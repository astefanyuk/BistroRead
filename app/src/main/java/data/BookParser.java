package data;

import android.text.Html;
import android.text.TextUtils;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;

/**
 * Created by AStefaniuk on 6/16/2014.
 */
public class BookParser {

    public BookParserBase getBookParser(File file) {
        if (file.getName().toLowerCase().endsWith(".fb2")) {
            return new BookParserFb2(file);
        } else if (file.getName().toLowerCase().endsWith(".epub")) {
            return new BookParserFb2(file);
        } else {
            return new BookParserText(file);
        }
    }

    public Document parse(File file) {

        Document document = new Document();

        if (!file.exists() || file.length() <= 0) {
            return document;
        }

        try {

            document.read(file);

            if (document.book != null && !document.sections.isEmpty()) {
                return document;
            }

            BookParserBase bookParserBase = getBookParser(file);

            ActiveAndroid.beginTransaction();

            try {

                (new Delete()).from(BookContent.class).execute();
                (new Delete()).from(BookSection.class).execute();

                Book book = new Select().from(Book.class).and("path = ?", file.getAbsoluteFile()).executeSingle();

                if (book == null) {
                    book = new Book(file);
                }
                book.save2();

                bookParserBase.parseContent(book);

                ActiveAndroid.setTransactionSuccessful();

            } finally {
                ActiveAndroid.endTransaction();
            }


            document.read(file);

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return document;
    }
}
