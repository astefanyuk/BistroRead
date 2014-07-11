package com.mariko.bistroread;

import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.File;
import java.util.HashSet;

import data.BookContent;
import data.BookParser;
import data.Document;

/**
 * Created by AStefaniuk on 5/21/2014.
 */
public abstract class ReadController {

    public static final String PREF_WORDS_PER_MINUTE = "words_per_minute";
    public int LONG_WORD = 15;
    public static final int WORD_END_PAUSE = 500;

    private Boolean isCanceled = false;
    private Thread thread;

    public static final int WORDS_PER_MINUTE_MIN = 250;
    public static final int WORDS_PER_MINUTE_MAX = 1000;

    private int wordsPerMinute = 250;

    private final static HashSet<String> prefferedCharacters;

    private final Object STATE = new Object();
    private Boolean isPaused = true;

    private final SharedPreferences pref;

    public data.Document getDocument() {
        return document;
    }

    public float percent;

    private data.Document document = new data.Document();

    private final File file;

    public ReadController(File file) {
        pref = GApp.sInstance.getSharedPreferences("settings", 0);

        wordsPerMinute = pref.getInt(PREF_WORDS_PER_MINUTE, WORDS_PER_MINUTE_MIN);

        this.file = file;
    }

    public void changePaused() {
        pause(!isPaused);
    }

    public void pause(boolean paused) {
        synchronized (STATE) {
            isPaused = paused;
            STATE.notifyAll();
        }

        saveBook();
    }

    private void saveBook() {
        if (getDocument().book != null) {
            getDocument().book.save2();
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getWordsPerMinute() {
        return wordsPerMinute;
    }

    public void changeWordsPerMinute(boolean increase) {

        int value = wordsPerMinute;

        if (increase) {
            value += 50;
        } else {
            value -= 50;
        }

        value = Math.max(value, WORDS_PER_MINUTE_MIN);
        value = Math.min(value, WORDS_PER_MINUTE_MAX);

        if (wordsPerMinute != value) {
            wordsPerMinute = value;
            pref.edit().putInt(PREF_WORDS_PER_MINUTE, value).commit();
        }
    }

    public File getFile() {
        return file;
    }

    public static class TextParams {
        public String left;
        public String center;
        public String right;

        public long position;
        public int index;

        public data.Document.BookContentList bookContentList;
    }

    static {
        prefferedCharacters = new HashSet<String>();

        for (String s : GApp.sInstance.getResources().getStringArray(R.array.preffered_characters)) {
            for (int i = 0; i < s.length(); i++) {
                prefferedCharacters.add(s.substring(i, i + 1).toUpperCase());
            }
        }
    }

    public void close() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    private String cleanText(String text) {
        if (!TextUtils.isEmpty(text)) {
            return text.replace("\n", "").trim();
        }
        return text;
    }

    private boolean firstTextDisplayed;

    public void start() {

        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                onLoading(true);

                try {
                    document = (new BookParser()).parse(file);
                } finally {
                    onLoading(false);
                }

                if (!document.isValid()) {
                    return;
                }

                while (!isCanceled) {

                    if (firstTextDisplayed) {

                        while (!isCanceled) {

                            boolean isInPaused;

                            try {
                                synchronized (STATE) {
                                    if (isPaused) {
                                        STATE.wait();
                                    }
                                    isInPaused = isPaused;
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                                return;
                            }

                            if (!isInPaused || !firstTextDisplayed) {
                                break;
                            }
                        }

                    }

                    final int step = 10;

                    int speed = (int) (60 * 1000 * 1.0f / wordsPerMinute);

                    for (int i = 0; i < speed; i += step) {

                        synchronized (isCanceled) {
                            try {
                                isCanceled.wait(step);
                            } catch (Throwable e) {
                                e.printStackTrace();
                                return;
                            }
                            if (isCanceled) {
                                return;
                            }
                        }
                    }

                    data.Document.BookContentList bookContentList = document.getContentWord(firstTextDisplayed);

                    firstTextDisplayed = true;

                    String value = bookContentList.getText();

                    TextParams textParams = new TextParams();

                    textParams.position = document.book.contentPosition;
                    textParams.index = document.book.contentIndex;

                    if (!TextUtils.isEmpty(value)) {

                        BookContent bookContent = bookContentList.getContent(document.book.contentPosition);
                        long start = bookContent.start;
                        for (int i = 0; i < bookContent.text.length && i <= document.book.contentIndex; i++) {
                            start += bookContent.text[i].length() + 1 /* space*/;
                        }

                        percent = (start * 100.0f) / document.book.contentSize;

                        int highlightIndex = getHighlightIndex(value);

                        textParams.center = cleanText(value.substring(highlightIndex, highlightIndex + 1));

                        if (highlightIndex > 0) {
                            textParams.left = cleanText(value.substring(0, highlightIndex));
                        }

                        if (highlightIndex < value.length()) {
                            textParams.right = cleanText(value.substring(highlightIndex + 1));
                        }
                    }

                    textParams.bookContentList = bookContentList;

                    onTextChanged(textParams);

                    if (!TextUtils.isEmpty(value)) {

                        //word is long or in the end of block
                        Character c = value.charAt(value.length() - 1);

                        if (value.length() > LONG_WORD || (!Character.isLetter(c) && !Character.isDigit(c))) {

                            try {
                                Thread.sleep(WORD_END_PAUSE);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                }
            }
        });

        thread.setDaemon(true);
        thread.start();


    }

    protected abstract void onLoading(boolean started);

    private int getHighlightIndex(String value) {

        int length = value.length();

        //ignore bad characters contains in the end
        while (length > 0) {
            if (Character.isLetter(value.charAt(length - 1)) ||
                    Character.isDigit(value.codePointAt(length - 1))) {
                break;
            }

            --length;
        }

        int index = length / 2;


        String center = value.substring(index, index + 1).toUpperCase();

        for (int i : new int[]{index - 1, index + 1}) {

            if (i > 0 && i < value.length()) {

                //neither letter or digit or equals
                if ((!Character.isLetter(center.charAt(0)) && !Character.isDigit(center.codePointAt(0)) ||
                        center.equalsIgnoreCase(value.substring(i, i + 1)))) {
                    index = i;
                    break;
                }

                /*
                if (prefferedCharacters.contains(center)) {
                    index = i;
                    break;
                }
                */
            }
        }

        /*
        if (!prefferedCharacters.contains(center) && Character.isLetter(center.codePointAt(0))) {

            for (int i : new int[]{index - 1, index + 1}) {

                if (i > 0 && i < value.length()) {

                    center = value.substring(i, i + 1).toUpperCase();

                    if (prefferedCharacters.contains(center)) {
                        index = i;
                        break;
                    }
                }
            }
        }
        */

        return index;
    }

    public abstract void onTextChanged(TextParams textParams);

    public void moveToAbsPosition(long start) {

        pause(true);

        getDocument().moveToAbsPosition(start);
        firstTextDisplayed = false;

        synchronized (STATE) {
            STATE.notifyAll();
        }
    }
}
