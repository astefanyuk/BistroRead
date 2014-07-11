package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by AStefaniuk on 6/19/2014.
 */
public class BookParserEpub extends BookParserBase {
    public BookParserEpub(File file) {
        super(file);
    }


    @Override
    public void parseContent(data.Book book) {

        /*
        FileInputStream inputStream = null;

        try {

            inputStream = new FileInputStream(file);

            Book book = (new EpubReader()).readEpub(inputStream);

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
            */
    }
}
