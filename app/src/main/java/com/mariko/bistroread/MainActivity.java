package com.mariko.bistroread;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListPopupWindow;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import data.Book;
import data.BookSection;

public class MainActivity extends Activity {

    private ReadController readController;

    private View pausedView;
    private TextView positionView;
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

        positionView = (TextView) findViewById(R.id.position);

        setPercent(0f);

        txtContentTop.setDisplayedOnTop(true);
        txtContentBottom.setDisplayedOnTop(false);

        speedView = (SpeedView) findViewById(R.id.speedView);

        txt = (HighlighTextView) findViewById(R.id.txt1);

        findViewById(R.id.paused).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readController.changePaused();
            }
        });

        //open recently
        Book book = new Select().from(Book.class).orderBy("OpenDate DESC").executeSingle();

        File file = book == null ? new File("") : new File(book.path);

        createReadController(file);

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {

                if (!readController.getDocument().isValid()) {
                    return true;
                }

                readController.changePaused();
                updateView();

                return true;
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {

                if (!readController.getDocument().isValid()) {
                    return true;
                }

                readController.pause(true);

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

        positionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.content_navigator, null);

                builder.setTitle(R.string.go_to);
                builder.setView(view);

                final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.percent);

                builder.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        long absPosition = (long) (readController.getDocument().book.contentSize / 100.0f * numberPicker.getValue());
                        readController.moveToAbsPosition(absPosition);
                    }
                });

                final Dialog dialog = builder.create();


                numberPicker.setMaxValue(100);
                numberPicker.setValue((int) readController.percent);

                view.findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        numberPicker.setValue(0);
                    }
                });

                view.findViewById(R.id.end).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        numberPicker.setValue(100);
                    }
                });

                dialog.show();

            }
        });


        updateView();

        fileChooser.listener = new FileChooser.Listener() {
            @Override
            public void fileSelected(File file) {

                drawer_layout.closeDrawers();

                createReadController(file);
            }
        };
    }

    private void createReadController(File file) {

        if (readController != null) {
            readController.close();
        }

        txtContentTop.setText(null);
        txtContentBottom.setText(null);
        positionView.setText("");
        txt.setText(null);

        readController = new ReadController(new File(file.getAbsolutePath())) {

            @Override
            protected void onLoading(final boolean started) {

                if (isFinishing()) {
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        setPercent(readController.percent);

                        if (started) {

                            progressLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            progressBarStatus.setText(GApp.sInstance.getString(R.string.loading_file, readController.getFile().getName()));
                        } else {

                            if (!readController.getDocument().isValid()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                progressLayout.setVisibility(View.VISIBLE);
                                progressBarStatus.setText(GApp.sInstance.getString(R.string.file_error, readController.getFile().getAbsolutePath()));
                            } else {
                                progressLayout.setVisibility(View.GONE);
                            }
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
//                        MainActivity.this.setTitle(readController.getDocument().book.path +  str.bookContentList.);
                        txtContentTop.setText(str);
                        txtContentBottom.setText(str);
                        txt.setText(str);

                        positionView.setText(new DecimalFormat("##.##").format(readController.percent) + " %");
                    }
                });
            }
        };

        readController.start();

        speedView.setReadController(readController);
    }

    private void setPercent(float value) {
        positionView.setText(new DecimalFormat("##.##").format(value) + " %");
    }

    private void updateView() {
        txtContentTop.setVisibility(readController.isPaused() ? View.VISIBLE : View.INVISIBLE);
        txtContentBottom.setVisibility(readController.isPaused() ? View.VISIBLE : View.INVISIBLE);
        speedView.setVisibility(readController.isPaused() ? View.VISIBLE : View.INVISIBLE);
        pausedView.setVisibility(readController.isPaused() ? View.VISIBLE : View.INVISIBLE);
        positionView.setVisibility(readController.isPaused() ? View.VISIBLE : View.INVISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        readController.pause(true);

        int id = item.getItemId();

        if (id == R.id.action_search_file) {

            drawer_layout.openDrawer(fileChooser);

            return true;
        }

        if (id == R.id.action_sections) {

            final ListPopupWindow popupWindow = new ListPopupWindow(MainActivity.this);

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

            popupWindow.setModal(true);
            popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    BookSection bookSection = readController.getDocument().sections.get(i);
                    readController.moveToAbsPosition(bookSection.start);

                    popupWindow.dismiss();
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
