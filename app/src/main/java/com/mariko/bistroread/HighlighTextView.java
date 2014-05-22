package com.mariko.bistroread;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Property;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.animation.DecelerateInterpolator;
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

    public HighlighTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.highlight_text_view, this);

        txtLeft = (TextView) findViewById(R.id.txtLeft);
        txtCenter = (TextView) findViewById(R.id.txtCenter);
        txtRight = (TextView) findViewById(R.id.txtRight);

        txtList = new TextView[]{txtLeft, txtCenter, txtRight};

        setTextSize(90);

    }

    public void setTextSize(int size) {
        for (TextView txt : txtList) {
            txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        }
    }

    public void setText(ReadController.TextParams textParams) {

        for (TextView txt : txtList) {
            txt.setText("");

            /*
            if(txt != txtCenter){
                ObjectAnimator visToInvis = ObjectAnimator.ofFloat(txt, "alpha", 0f, 1f);
                visToInvis.setDuration(20);
                visToInvis.setInterpolator(new AccelerateDecelerateInterpolator());
                visToInvis.start();
            }
            */

            if (txt != txtCenter) {
                final TextView textView = txt;
                textView.setTextColor(getContext().getResources().getColor(R.color.text_start));

                final Property<TextView, Integer> property = new Property<TextView, Integer>(int.class, "textColor") {
                    @Override
                    public Integer get(TextView object) {
                        return object.getCurrentTextColor();
                    }

                    @Override
                    public void set(TextView object, Integer value) {
                        object.setTextColor(value);
                    }
                };

                final ObjectAnimator animator = ObjectAnimator.ofInt(textView, property, getContext().getResources().getColor(R.color.text_end));
                animator.setDuration(333L);
                animator.setEvaluator(new ArgbEvaluator());
                animator.setInterpolator(new DecelerateInterpolator(2));
                animator.start();
            }
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
