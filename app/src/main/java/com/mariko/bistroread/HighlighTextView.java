package com.mariko.bistroread;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by AStefaniuk on 5/19/2014.
 */
public class HighlighTextView extends LinearLayout {

    private TextView txtCenter;
    private TextView txtLeft;
    private TextView txtRight;

    private TextView[] txtList;

    private int MAX_FONT = 90;

    private int font;

    private Rect rect = new Rect();

    private final TextPaint textPaint;

    private int viewWidth;

    public HighlighTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.highlight_text_view, this);

        txtLeft = (TextView) findViewById(R.id.txtLeft);
        txtCenter = (TextView) findViewById(R.id.txtCenter);
        txtRight = (TextView) findViewById(R.id.txtRight);

        txtList = new TextView[]{txtLeft, txtCenter, txtRight};

        textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        setTextSize(MAX_FONT);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (changed) {
            Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);

            viewWidth = (int) (metrics.widthPixels - 100 * metrics.density);
        }


        super.onLayout(changed, l, t, r, b);
    }

    public void setTextSize(int font) {

        if (this.font == font) {
            return;
        }

        this.font = font;

        for (TextView txt : txtList) {
            txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, font);
        }
    }

    public void setText(ReadController.TextParams textParams) {

        for (TextView txt : txtList) {
            txt.setText("");
        }

        StringBuffer buffer = new StringBuffer();

        if (!TextUtils.isEmpty(textParams.left)) {
            buffer.append(textParams.left);
        }

        if (!TextUtils.isEmpty(textParams.center)) {
            buffer.append(textParams.center);
        }

        if (!TextUtils.isEmpty(textParams.right)) {
            buffer.append(textParams.right);
        }

        if (buffer.length() > 0) {

            int newFontSize = 0;

            for (int i = MAX_FONT; i > 0; i--) {

                newFontSize = i;

                textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, i, GApp.sInstance.getResources().getDisplayMetrics()));

                StaticLayout staticLayout = new StaticLayout(buffer.toString(), textPaint, viewWidth, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);

                if (staticLayout.getLineCount() <= 1) {
                    break;
                }
            }

            setTextSize(newFontSize);

        }

        if (!TextUtils.isEmpty(textParams.left)) {
            txtLeft.setText(textParams.left);
        }

        if (!TextUtils.isEmpty(textParams.center)) {
            txtCenter.setText(textParams.center);
        }

        if (!TextUtils.isEmpty(textParams.right)) {
            txtRight.setText(textParams.right);
        }
    }
}
