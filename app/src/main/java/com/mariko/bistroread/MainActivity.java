package com.mariko.bistroread;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import org.jsoup.nodes.Document;

import java.io.File;
import java.util.List;

public class MainActivity extends Activity {

    private ReadController readController;

    private View pausedView;
    private SpeedView speedView;
    private HighlighTextView txt;
    private ReadContentTextView txtContentTop;
    private ReadContentTextView txtContentBottom;

    private DrawerLayout drawer_layout;

    private FileChooser fileChooser;

    private View progressLayout;
    private View progressBar;
    private TextView progressBarStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ((FrameLayout) findViewById(R.id.content_frame)).addView(LayoutInflater.from(this).inflate(R.layout.content, null));

        progressLayout = findViewById(R.id.progressLayout);
        progressBar = findViewById(R.id.progressBar);
        progressBarStatus = (TextView) findViewById(R.id.progressBarStatus);

        progressLayout.setVisibility(View.GONE);

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fileChooser = (FileChooser) findViewById(R.id.file_chooser);

        txtContentTop = (ReadContentTextView) findViewById(R.id.txtContentTop);
        txtContentBottom = (ReadContentTextView) findViewById(R.id.txtContentBottom);

        pausedView = findViewById(R.id.paused);

        txtContentTop.setDisplayedOnTop(true);
        txtContentBottom.setDisplayedOnTop(false);

        speedView = (SpeedView) findViewById(R.id.speedView);

        txt = (HighlighTextView) findViewById(R.id.txt1);

        readController = new ReadController(new File("/mnt/sdcard/Download/01_Harry_Potter_i_Filosovskij_Kamen.fb2")) {

            @Override
            protected void onLoading(final boolean started) {

                if (isFinishing()) {
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (started) {
                            progressLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            progressBarStatus.setText(GApp.sInstance.getString(R.string.loading_file, readController.getFile().getName()));
                        } else {
                            progressLayout.setVisibility(View.GONE);
                        }

                    }
                });
            }

            @Override
            public void onTextChanged(final TextParams str) {
                if (isFinishing()) {
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtContentTop.setText(str);
                        txtContentBottom.setText(str);
                        txt.setText(str);
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

                readController.pause();

                int max = -1;

                List<ReadContentTextView.TextLineInfo> lines = txtContentTop.getLines();

                if (!lines.isEmpty()) {
                    max = Math.max(max, lines.get(lines.size() - 1).end);
                }

                lines = txtContentBottom.getLines();

                if (!lines.isEmpty()) {
                    max = Math.max(max, lines.get(lines.size() - 1).end);
                }

                if (max >= 0) {
                    //readController.setCurrentIndex(max);
                }


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

    private void updateView() {
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

        if (id == R.id.action_search_file) {

            drawer_layout.openDrawer(fileChooser);

            return true;
        }

        if (id == R.id.action_sections) {

            ListPopupWindow popupWindow = new ListPopupWindow(MainActivity.this);

            popupWindow.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return readController.getDocument().sections.size();
                }

                @Override
                public Object getItem(int i) {
                    return null;
                }

                @Override
                public long getItemId(int i) {
                    return 0;
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    if (view == null) {
                        view = LayoutInflater.from(MainActivity.this).inflate(R.layout.file_chooser_item, null);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.txtTitle);
                    textView.setText(readController.getDocument().sections.get(i).title);

                    return view;

                }
            });

            popupWindow.setAnchorView(findViewById(R.id.action_sections));
            popupWindow.setContentWidth(getResources().getDimensionPixelOffset(R.dimen.popup_min_width));
            popupWindow.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
