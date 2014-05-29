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

public class ReadContentTextView extends View {

    private final TextPaint paint;
    private StaticLayout layout;

    private ArrayList<TextLineInfo> lines = new ArrayList<TextLineInfo>();

    private boolean displayedOnTop;

    private String [] text;
    private int index;

    public static class TextLineInfo {
        int start;
        int end;

        public TextLineInfo(int start, int end){
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

    public void setDisplayedOnTop(boolean value){
        this.displayedOnTop = value;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if(text != null && View.VISIBLE == visibility){
            setText(text, index);
        }
    }

    public void setText(String [] text, int index ){

        this.text = text;
        this.index = index;

        layout = null;
        lines.clear();

        if(getVisibility() != View.VISIBLE){
            return;
        }

        int maxLines = 3;//displayedOnTop ? 2 : 4;

        int width = getMeasuredWidth();

        /*
        if(layout != null &&
                !lines.isEmpty() &&
                lines.get(0).start <= index && lines.get(lines.size() -1).end >= index ){
            //no changes
            return;
        }
        */

        StaticLayout next = null;

        if(displayedOnTop){

            String buffer = "";

            lines.add(new TextLineInfo(index -2, index -2));

            for (int i = index -2; i >=0; i--) {

                buffer = text[i] + (buffer.length() == 0 ? "" : " ")  + buffer;

                next = new StaticLayout(buffer.toString(), paint, width, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);

                if (next.getLineCount() > maxLines) {
                    break;
                }

                layout = next;

                lines.get(lines.size() - 1).start = i;
            }

        }else {

            StringBuffer buffer = new StringBuffer();

            lines.add(new TextLineInfo(index, index + 1));

            for (int i = index; i < text.length; i++) {

                buffer.append(" " + text[i]);

                next = new StaticLayout(buffer.toString(), paint, width, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);

                if (next.getLineCount() > maxLines) {
                    break;
                }

                layout = next;

                if (layout.getLineCount() > lines.size()) {
                    lines.add(new TextLineInfo(lines.get(lines.size() - 1).end + 1, index + 1));
                } else {
                    lines.get(lines.size() - 1).end = i;
                }

            }
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(layout != null){
            layout.draw(canvas);
        }
    }

    public List<TextLineInfo> getLines(){
        return this.lines;
    }

}
