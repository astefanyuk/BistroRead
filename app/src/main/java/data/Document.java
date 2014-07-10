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
    public Book book = new Book();
    public List<BookSection> sections = new ArrayList<BookSection>();

    private static int CACHE_OFFSET_COUNT = 2;

    public boolean isValid() {
        return !sections.isEmpty();
    }

    public static class BookContentList {

        private LruCache<Integer, BookContent> bookContentList = new LruCache<Integer, BookContent>(CACHE_OFFSET_COUNT * 2 + 2);

        public int position;
        public int index;

        public String getText() {

            BookContent bookContent = getContent(position);

            if (bookContent != null) {

                if (bookContent.text != null && index >= 0 && index < bookContent.text.length) {
                    return bookContent.text[index];
                }
            }
            return null;
        }

        public BookContent getContent(int index) {
            return bookContentList.get(index);
        }
    }

    private BookContentList bookContentList = new BookContentList();

    public BookContentList getContentNextWord() {

        putContentIntoCache();

        BookContent current = bookContentList.bookContentList.get(bookContentList.position);

        if (current == null) {
            return bookContentList;
        }

        if (bookContentList.index < current.text.length) {

            if (bookContentList.index == current.text.length - 1 && current.position == book.maxContentPosition) {
                //last item
                return bookContentList;
            }

            ++bookContentList.index;

        } else {

            current = bookContentList.bookContentList.get(bookContentList.position + 1);

            if (current != null) {
                ++bookContentList.position;
                bookContentList.index = 0;
            }
        }


        return bookContentList;
    }

    private void putContentIntoCache() {

        for (int i = Math.max(0, bookContentList.position - CACHE_OFFSET_COUNT);
             i <= Math.min(book.maxContentPosition, bookContentList.position + CACHE_OFFSET_COUNT);
             i++) {

            if (bookContentList.bookContentList.get(i) != null) {
                continue;
            }

            BookContent bookContent = new Select()
                    .from(BookContent.class)
                    .and("position = ?", i)
                    .executeSingle();

            if (bookContent != null) {
                bookContent.parseContent();
                bookContentList.bookContentList.put(i, bookContent);
            }


        }
    }

    private void deleteByBookId(long id) {

        //TODO:
        (new Delete()).from(Book.class).execute();
        (new Delete()).from(BookContent.class).execute();
        (new Delete()).from(BookSection.class).execute();
    }

    public void read(File file) {

        this.book = new Select().from(Book.class).and("path = ?", file.getAbsoluteFile()).executeSingle();
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
