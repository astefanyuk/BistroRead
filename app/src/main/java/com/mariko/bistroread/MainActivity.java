package com.mariko.bistroread;

import android.app.Activity;
import android.os.Bundle;
import android.text.StaticLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    private ReadController readController;

    private HighlighTextView txt;
    private TextView txtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtContent = (TextView) findViewById(R.id.txtContent);

        LinearLayout seekBarDividers = (LinearLayout) findViewById(R.id.seekBarDividers);
        for (int i = ReadController.WORDS_PER_MINUTE_MIN; i <= ReadController.WORDS_PER_MINUTE_MAX; i += 50) {

            TextView textView = new TextView(this, null);
            textView.setText("" + i);

            seekBarDividers.addView(textView);

            ((LinearLayout.LayoutParams) textView.getLayoutParams()).weight = 1;
        }

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                readController.setWordsPerMinute((ReadController.WORDS_PER_MINUTE_MAX - ReadController.WORDS_PER_MINUTE_MIN) / 100 * i + ReadController.WORDS_PER_MINUTE_MIN);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        txt = (HighlighTextView) findViewById(R.id.txt1);

        readController = new ReadController() {
            public void onTextChanged(final TextParams str) {
                if (isFinishing()) {
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txt.setText(str);
                    }
                });
            }

            @Override
            public void onTextLoaded(final String[] text) {

                final StringBuffer b = new StringBuffer();

                for(String s : text){
                    b.append(s);
                    b.append(" ");

                    if(b.length() > 300){
                        break;
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtContent.setText(b.toString());
                    }
                });
            }
        };

        readController.start();

        findViewById(R.id.paused).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readController.changePaused();
            }
        });
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
