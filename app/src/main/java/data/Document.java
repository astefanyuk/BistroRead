package data;

import android.util.LruCache;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AStefaniuk on 6/16/2014.
 */
public class Document {
    public Book book;
    public List<BookSection> sections = new ArrayList<BookSection>();

    private static int CACHE_OFFSET_COUNT = 2;

    public static class BookContentList {

        private LruCache<Integer, BookContent> bookContentList = new LruCache<Integer, BookContent>(CACHE_OFFSET_COUNT * 2 + 2);

        public int position;
        public int index;

        public String getText() {
            return bookContentList.get(position).text[index];
        }
    }

    private BookContentList bookContentList = new BookContentList();

    public BookContentList getContentNextWord() {

        putContentIntoCache();

        BookContent current = bookContentList.bookContentList.get(bookContentList.position);

        if (bookContentList.index < current.text.length) {
            ++bookContentList.index;

            return bookContentList;
        }

        current = bookContentList.bookContentList.get(bookContentList.position + 1);

        if (current != null) {
            ++bookContentList.position;
            bookContentList.index = 0;
        }


        return bookContentList;
    }

    private void putContentIntoCache() {

        for (int i = bookContentList.position - CACHE_OFFSET_COUNT; i <= bookContentList.position + CACHE_OFFSET_COUNT; i++) {

            if (i < 0 || i >= book.maxContentPosition || bookContentList.bookContentList.get(i) != null) {
                continue;
            }

            BookContent bookContent = new Select()
                    .from(BookContent.class)
                    .and("position = ", i)
                    .executeSingle();

            bookContentList.bookContentList.put(i, bookContent);
        }
    }

    private void deleteByBookId(long id) {

        //TODO:
        (new Delete()).from(Book.class).execute();
        (new Delete()).from(BookContent.class).execute();
        (new Delete()).from(BookSection.class).execute();
    }

    public void read(File file) {

        this.book = new Select().from(Book.class).and("path=", file.getAbsoluteFile()).executeSingle();
        if (this.book != null) {
            if (file.length() != this.book.size || file.lastModified() != this.book.modifiedDate) {
                deleteByBookId(this.book.getId());
                this.book = null;
            }
        }
        if (this.book != null) {
            this.sections = new Select().from(BookSection.class).and("bookId = ?", this.book.getId()).orderBy("start ASC ").execute();

        }
    }

    private void load(long position) {

        bookContentList = new BookContentList();

        BookContent bookContent = new Select().from(BookContent.class).and("Start <= ", position).and("End >= ", position).executeSingle();

        bookContentList.bookContentList.put(bookContent.position, bookContent);

        putContentIntoCache();

    }

}