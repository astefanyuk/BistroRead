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

    public class BookContentList {

        private LruCache<Long, BookContent> bookContentList = new LruCache<Long, BookContent>(CACHE_OFFSET_COUNT * 2 + 2);

        public String getText() {

            BookContent bookContent = getContent(book.contentPosition);

            if (bookContent != null) {

                if (bookContent.text != null && book.contentIndex >= 0 && book.contentIndex < bookContent.text.length) {
                    return bookContent.text[book.contentIndex];
                }
            }
            return null;
        }

        public BookContent getContent(long index) {
            return bookContentList.get(index);
        }
    }

    private BookContentList bookContentList = new BookContentList();

    public BookContentList getContentWord(boolean next) {

        putContentIntoCache();

        BookContent current = bookContentList.bookContentList.get(book.contentPosition);

        if (current == null) {
            return bookContentList;
        }

        if (book.contentIndex < current.text.length) {

            if (book.contentIndex == current.text.length - 1 && current.position == book.maxContentPosition) {
                //last item
                return bookContentList;
            }

            if (next) {
                ++book.contentIndex;
            }


        } else {

            current = bookContentList.bookContentList.get(book.contentPosition + 1);

            if (current != null && next) {

                ++book.contentPosition;
                book.contentIndex = 0;

                book.save2();

            }
        }


        return bookContentList;
    }

    private void putContentIntoCache() {

        for (long i = Math.max(0, book.contentPosition - CACHE_OFFSET_COUNT);
             i <= Math.min(book.maxContentPosition, book.contentPosition + CACHE_OFFSET_COUNT);
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

        (new Delete()).from(Book.class).and("id=?", id).execute();
        (new Delete()).from(BookContent.class).execute();
        (new Delete()).from(BookSection.class).execute();
    }

    public void read(File file) {

        this.book = new Select().from(Book.class).and("path = ?", file.getAbsoluteFile()).executeSingle();
        if (this.book != null) {

            this.sections = new Select().from(BookSection.class).and("bookId = ?", this.book.getId()).orderBy("start ASC ").execute();

            if (file.length() != this.book.size || file.lastModified() != this.book.modifiedDate) {
                deleteByBookId(this.book.getId());
                this.book = null;
                this.sections.clear();
                return;
            }
        }
    }

    public boolean isValid() {
        return !sections.isEmpty();
    }

    public void moveToAbsPosition(long position) {

        BookContent bookContent = new Select().from(BookContent.class).and("Start <= ?", position).and("End >= ?", position).executeSingle();

        book.contentPosition = bookContent.position;
        book.contentIndex = 0;

        book.save2();
    }

}
