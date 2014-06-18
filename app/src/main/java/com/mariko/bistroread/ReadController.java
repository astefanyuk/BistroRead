package com.mariko.bistroread;

import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.File;
import java.util.HashSet;

import data.BookParser;

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

    private Boolean isPaused = false;

    private final SharedPreferences pref;

    public data.Document getDocument() {
        return document;
    }

    private data.Document document = new data.Document();

    private final File file;

    public ReadController(File file) {
        pref = GApp.sInstance.getSharedPreferences("settings", 0);

        wordsPerMinute = pref.getInt(PREF_WORDS_PER_MINUTE, WORDS_PER_MINUTE_MIN);

        this.file = file;
    }

    public void changePaused() {
        synchronized (isPaused) {
            isPaused = !isPaused;
        }
    }

    public void pause() {
        synchronized (isPaused) {
            isPaused = true;
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

                while (true) {

                    boolean isInPaused = false;

                    synchronized (isPaused) {
                        isInPaused = isPaused;
                    }

                    if (isInPaused) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
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

                    data.Document.BookContentList bookContentList = document.getContentNextWord();

                    String value = bookContentList.getText();

                    TextParams textParams = new TextParams();

                    if (!TextUtils.isEmpty(value)) {

                        int highlightIndex = getHighlightIndex(value);

                        textParams.center = value.substring(highlightIndex, highlightIndex + 1);

                        if (highlightIndex > 0) {
                            textParams.left = value.substring(0, highlightIndex);
                        }

                        if (highlightIndex < value.length()) {
                            textParams.right = value.substring(highlightIndex + 1);
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
}
