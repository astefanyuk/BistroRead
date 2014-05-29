package com.mariko.bistroread;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity {

    private ReadController readController;

    private View pausedView;
    private SpeedView speedView;
    private HighlighTextView txt;
    private ReadContentTextView txtContentTop;
    private ReadContentTextView txtContentBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtContentTop = (ReadContentTextView) findViewById(R.id.txtContentTop);
        txtContentBottom = (ReadContentTextView) findViewById(R.id.txtContentBottom);

        pausedView = findViewById(R.id.paused);

        txtContentTop.setDisplayedOnTop(true);
        txtContentBottom.setDisplayedOnTop(false);

        speedView = (SpeedView) findViewById(R.id.speedView);

        txt = (HighlighTextView) findViewById(R.id.txt1);

        readController = new ReadController() {
            public void onTextChanged(final TextParams str) {
                if (isFinishing()) {
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtContentTop.setText(str.text, str.index);
                        txtContentBottom.setText(str.text, str.index);
                        txt.setText(str);
                    }
                });
            }

            @Override
            public void onTextLoaded(final String[] text) {

            }
        };

        readController.start();

        findViewById(R.id.paused).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readController.changePaused();
            }
        });

        speedView.setReadController(readController);

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                readController.changePaused();
                updateView();

                return true;
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
                Log.d("ABC", "onFling");
                return true;
            }
        });

        View root = findViewById(R.id.root);

        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        updateView();
    }

    private void updateView(){
        txtContentTop.setVisibility(readController.isPaused() ? View.VISIBLE : View.INVISIBLE);
        txtContentBottom.setVisibility(readController.isPaused() ? View.VISIBLE : View.INVISIBLE);
        speedView.setVisibility(readController.isPaused() ? View.VISIBLE : View.INVISIBLE);

        pausedView.setVisibility(readController.isPaused() ? View.VISIBLE : View.INVISIBLE);
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
