package com.mariko.bistroread;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SpeedView extends LinearLayout {

    private static final int CHANGE_LONG_PRESS_DELAY = 350;

    private ReadController readController;

    private View speedMinus;
    private View speedPlus;
    private TextView speedValue;

    private Runnable changeRunnable;

    public SpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.speed, this);

        speedMinus = findViewById(R.id.speedMinus);
        speedPlus = findViewById(R.id.speedPlus);
        speedValue = (TextView) findViewById(R.id.speedValue);

        speedMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeValue(view, false);
            }
        });

        speedMinus.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                changeValue(view, false);
                return true;
            }
        });

        speedPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeValue(view, true);
            }
        });

        speedPlus.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                changeValue(view, true);
                return true;
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //nothing
            }
        });

    }

    private void changeValue(final View view, final boolean increase) {

        getHandler().removeCallbacks(changeRunnable);

        this.readController.changeWordsPerMinute(increase);

        updateSpeed(this.readController.getWordsPerMinute());

        changeRunnable = new Runnable() {
            @Override
            public void run() {
                if (view.isPressed()) {
                    changeValue(view, increase);
                }
            }
        };

        getHandler().postDelayed(changeRunnable, CHANGE_LONG_PRESS_DELAY);
    }

    @Override
    protected void onDetachedFromWindow() {

        getHandler().removeCallbacks(changeRunnable);
        super.onDetachedFromWindow();
    }

    private void updateSpeed(int value) {
        speedValue.setText(String.valueOf(value) + getResources().getString(R.string.wpm));
    }

    public void setReadController(ReadController readController) {
        this.readController = readController;

        updateSpeed(this.readController.getWordsPerMinute());
    }
}
