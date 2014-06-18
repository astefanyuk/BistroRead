package com.mariko.bistroread;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import data.BookContent;

public class ReadContentTextView extends View {

    private final TextPaint paint;
    private StaticLayout layout;

    private ArrayList<TextLineInfo> lines = new ArrayList<TextLineInfo>();

    private boolean displayedOnTop;

    private ReadController.TextParams text;

    public static class TextLineInfo {

        int start;
        int end;

        public TextLineInfo(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return start + " " + end;
        }
    }

    public ReadContentTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new TextPaint();

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, GApp.sInstance.getResources().getDisplayMetrics()));

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {

            post(new Runnable() {
                @Override
                public void run() {
                    if (text != null && View.VISIBLE == getVisibility()) {
                        setText(text);
                    }
                }
            });

        }
    }

    public void setDisplayedOnTop(boolean value) {
        this.displayedOnTop = value;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (text != null && View.VISIBLE == visibility) {
            setText(text);
        }
    }

    public void setText(ReadController.TextParams text) {

        this.text = text;

        layout = null;
        lines.clear();

        if (getVisibility() != View.VISIBLE) {
            return;
        }

        int width = getMeasuredWidth();

        StaticLayout next = null;

        int maxHeight = getMeasuredHeight();

        if (displayedOnTop) {

            String buffer = "";

            for (int i = text.bookContentList.position; i >= 0; i--) {

                BookContent bookContent = text.bookContentList.getContent(i);

                if (bookContent == null) {
                    break;
                }

                int startPosition;

                if (i == text.bookContentList.position) {
                    startPosition = text.bookContentList.index - 1;
                } else {
                    startPosition = bookContent.text.length - 1;
                }

                for (int j = startPosition; j >= 0; j--) {

                    buffer = bookContent.text[j] + (buffer.length() == 0 ? "" : " ") + buffer;

                    next = new StaticLayout(buffer.toString(), paint, width, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);

                    if (next.getHeight() > maxHeight) {
                        break;
                    }

                    layout = next;

                    //lines.get(lines.size() - 1).start = i;
                }
            }

        } else {

            //lines.add(new TextLineInfo(index, index + 1));

            StringBuffer buffer = new StringBuffer();

            for (int i = text.bookContentList.position; ; i++) {

                BookContent bookContent = text.bookContentList.getContent(i);

                if (bookContent == null) {
                    break;
                }

                int startPosition;

                if (i == text.bookContentList.position) {
                    startPosition = text.bookContentList.index + 1;
                } else {
                    startPosition = 0;
                }


                for (int j = startPosition; j < bookContent.text.length; j++) {

                    buffer.append(" " + bookContent.text[j]);

                    next = new StaticLayout(buffer.toString(), paint, width, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);

                    if (next.getHeight() > maxHeight) {
                        break;
                    }

                    layout = next;

                    /*
                    if (layout.getLineCount() > lines.size()) {
                        lines.add(new TextLineInfo(lines.get(lines.size() - 1).end + 1, index + 1));
                    } else {
                        lines.get(lines.size() - 1).end = i;
                    }
                    */
                }
            }
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (layout != null) {
            layout.draw(canvas);
        }
    }

    public List<TextLineInfo> getLines() {
        return this.lines;
    }

}
