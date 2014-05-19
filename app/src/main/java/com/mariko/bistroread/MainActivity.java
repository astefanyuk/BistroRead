package com.mariko.bistroread;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MainActivity extends Activity {

    private HighlighTextView[]  txt;
    private String[] text;
    private int index;
    private Timer timer;
    private long t = (int)(60 * 1000 * 1.0f / 500);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //txt = new HighlighTextView[] {(HighlighTextView) findViewById(R.id.txt1), (HighlighTextView) findViewById(R.id.txt2), (HighlighTextView) findViewById(R.id.txt3)};
        //txt = new HighlighTextView[] {(HighlighTextView) findViewById(R.id.txt1), (HighlighTextView) findViewById(R.id.txt2)};
        txt = new HighlighTextView[] {(HighlighTextView) findViewById(R.id.txt1)};


        text = getString(R.string.my_text).split(" ");

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                final String[] str = new String[txt.length];

                int j =0;
                for(int i=index; i<text.length && j < str.length; i++){

                    str[j] = text[i];
                    ++j;
                }

                index+= str.length;

                if(index >= text.length){
                    index = 0;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0; i<str.length; i++){
                            txt[i].setText(str[i]);
                        }
                    }
                });


            }
        }, 0, t/txt.length);


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
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
