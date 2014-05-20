package com.mariko.bistroread;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    private HighlighTextView[] txt;
    private String[] text;
    private int index;
    private Timer timer;

    final int minValue = 250;
    final int maxValue = 1000;

    private int wordsPerMinute = minValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout seekBarDividers = (LinearLayout) findViewById(R.id.seekBarDividers);
        for(int i=minValue; i<=maxValue; i+=50){

            TextView textView = new TextView(this, null);
            textView.setText("" + i);

            seekBarDividers.addView(textView);

            ((LinearLayout.LayoutParams)textView.getLayoutParams()).weight = 1;
        }

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                wordsPerMinute = (maxValue - minValue)/100 * i + minValue;

                startTimer();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //txt = new HighlighTextView[] {(HighlighTextView) findViewById(R.id.txt1), (HighlighTextView) findViewById(R.id.txt2), (HighlighTextView) findViewById(R.id.txt3)};
        //txt = new HighlighTextView[] {(HighlighTextView) findViewById(R.id.txt1), (HighlighTextView) findViewById(R.id.txt2)};
        txt = new HighlighTextView[]{(HighlighTextView) findViewById(R.id.txt1)};


        text = getString(R.string.my_text).split(" ");

        startTimer();
    }

    private void startTimer(){

        if(timer != null){
            timer.cancel();
        }

        Log.d("ABC", "Word per minute " + wordsPerMinute);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                final String[] str = new String[txt.length];

                int j = 0;
                for (int i = index; i < text.length && j < str.length; i++) {

                    str[j] = text[i];
                    ++j;
                }

                index += str.length;

                if (index >= text.length) {

                    index = 0;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < str.length; i++) {
                            txt[i].setText(str[i]);
                        }
                    }
                });


            }
        }, 2000, (int) (60 * 1000 * 1.0f / wordsPerMinute)/ txt.length);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify highlight_text_view parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
