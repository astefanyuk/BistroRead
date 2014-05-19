package com.mariko.bistroread;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by AStefaniuk on 5/19/2014.
 */
public class HighlighTextView extends LinearLayout {

    private TextView txtCenter;
    private TextView txtLeft;
    private TextView txtRight;

    public HighlighTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.a, this);

        txtCenter = (TextView) findViewById(R.id.txtCenter);
        txtLeft = (TextView) findViewById(R.id.txtLeft);
        txtRight = (TextView) findViewById(R.id.txtRight);
    }

    public void setText(String text){

        txtCenter.setText("");
        txtLeft.setText("");
        txtRight.setText("");

        if(!TextUtils.isEmpty(text)){
            int highlightIndex = text.length()/2;

            txtCenter.setText(text.substring(highlightIndex, highlightIndex + 1));

            if(highlightIndex >0){
                txtLeft.setText(text.substring(0, highlightIndex));
            }

            if(highlightIndex < text.length()){
                txtRight.setText(text.substring(highlightIndex + 1));
            }
        }
    }
}
