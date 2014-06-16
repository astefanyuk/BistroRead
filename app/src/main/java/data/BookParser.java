package data;

import android.text.Html;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by AStefaniuk on 6/16/2014.
 */
public class BookParser {

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

            //InputStream stream = GApp.sInstance.getResources().getAssets().open("test/sample.fb2");
            InputStream stream = null;

            try {

                stream = new FileInputStream(file);

                ActiveAndroid.beginTransaction();

                try {

                    (new Delete()).from(Book.class).execute();
                    (new Delete()).from(BookContent.class).execute();
                    (new Delete()).from(BookSection.class).execute();


                    Book book = new Book(file);
                    book.save();

                    long start = 0;

                    long bookContentPosition = 0;

                    org.jsoup.nodes.Document doc = Jsoup.parse(stream, "windows-1251", "");
                    for (Element sectionElement : doc.select("section")) {

                        BookSection bookSection = new BookSection();

                        bookSection.bookId = book.getId();

                        bookSection.title = Html.fromHtml(sectionElement.select("title").text()).toString().replace("\n", "").replace("  ", " ");

                        bookSection.start = start;

                        String s = Html.fromHtml(sectionElement.text()).toString().replace("\n", "").replace("  ", " ");

                        bookSection.end = start + s.length();

                        Long sectionId = bookSection.save();

                        int maxPackage = 5 * 1024;

                        for (int i = 0; i < s.length(); ) {

                            BookContent bookContent = new BookContent();

                            bookContent.position = bookContentPosition;

                            ++bookContentPosition;

                            bookContent.sectionId = sectionId;

                            bookContent.content = s.substring(i, Math.min(s.length(), i + maxPackage));

                            bookContent.start = start;

                            start += s.length();

                            bookContent.end = start;

                            bookContent.save();

                            i += maxPackage;
                        }

                    }

                    ActiveAndroid.setTransactionSuccessful();

                } finally {
                    ActiveAndroid.endTransaction();
                }


                document.read(file);

            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable e) {

                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return document;
    }
}
