package data;

import android.util.Log;

import java.io.File;

/**
 * Created by AStefaniuk on 6/19/2014.
 */
public abstract class BookParserBase {

    protected final File file;
    protected String encoding = "UTF-8";

    private final int maxPackage = 5 * 1024;

    public BookParserBase(File file) {
        this.file = file;
    }

    public abstract void parseContent();

    protected BookContent saveContent(String s, BookSection section, int bookContentPosition, long start) {

        BookContent bookContent = null;

        for (int i = 0; i < s.length(); ) {

            bookContent = new BookContent();

            bookContent.position = bookContentPosition;

            ++bookContentPosition;

            bookContent.sectionId = section.getId();

            bookContent.content = s.substring(i, Math.min(s.length(), i + maxPackage));

            bookContent.start = start;
            bookContent.end = start + bookContent.content.length();

            start = bookContent.end + 1;

            bookContent.save();

            Log.d("ABC", bookContent.toString());

            i += maxPackage;
        }

        return bookContent;
    }

}
