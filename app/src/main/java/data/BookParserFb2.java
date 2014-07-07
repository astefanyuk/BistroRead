package data;

import android.text.Html;
import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by AStefaniuk on 6/19/2014.
 */
public class BookParserFb2 extends BookParserBase {

    public BookParserFb2(File file) {
        super(file);
    }

    private void readEncoding() {
        try {
            //<?xml version="1.0" encoding="windows-1251"?>
            String xmlHeader = null;

            BufferedReader in = new BufferedReader(new FileReader(file));
            try {
                xmlHeader = in.readLine();
            } finally {
                in.close();
            }

            if (!TextUtils.isEmpty(xmlHeader)) {

                xmlHeader = xmlHeader.replace(" ", "");

                final String enc = "encoding=\"";
                int index = xmlHeader.toLowerCase().indexOf(enc);

                if (index >= 0) {
                    xmlHeader = xmlHeader.substring(index + enc.length());
                    int indexEnd = xmlHeader.indexOf("\"");


                    if (indexEnd > 0) {
                        encoding = xmlHeader.substring(0, indexEnd);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static String htmlToString(String html) {
        return Html.fromHtml(html.trim()).toString().replace("\n", "").replace("  ", " ");
    }

    @Override
    public void parseContent() {

        readEncoding();

        FileInputStream stream = null;

        try {
            stream = new FileInputStream(file);

            Book book = new Book(file);
            book.save();

            long start = 0;

            int bookContentPosition = -1;

            org.jsoup.nodes.Document doc = Jsoup.parse(stream, encoding, "");
            for (Element sectionElement : doc.select("section")) {

                ++bookContentPosition;

                StringBuffer content = new StringBuffer();

                BookSection bookSection = new BookSection();
                bookSection.bookId = book.getId();

                for (Node node : sectionElement.childNodes()) {
                    if ("title".equalsIgnoreCase(node.nodeName())) {
                        bookSection.title = htmlToString(((Element) node).text());
                    } else if ("section".equalsIgnoreCase(node.nodeName())) {
                        break;
                    } else {
                        if (content.length() > 0) {
                            content.append("\n");
                        }
                        content.append(htmlToString(node.outerHtml()));
                    }
                }

                bookSection.start = start;

                bookSection.end = start + content.length();

                bookSection.save();

                BookContent bookContent = saveContent(content.toString(), bookSection, bookContentPosition, start);

                if (bookContent != null) {
                    bookContentPosition = bookContent.position;
                    start = bookContent.start;
                }

            }

            book.maxContentPosition = bookContentPosition;
            book.save();

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
