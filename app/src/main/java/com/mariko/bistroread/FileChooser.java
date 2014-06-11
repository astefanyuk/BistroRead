package com.mariko.bistroread;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;


public class FileChooser extends ExpandableListView {

    public FileChooser(Context context, AttributeSet attrs) {
        super(context, attrs);


        File file = Environment.getExternalStorageDirectory();
        if(file != null){
            search(file.getAbsolutePath());
        }

        setAdapter(new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return 2;
            }

            @Override
            public int getChildrenCount(int i) {
                return 5;
            }

            @Override
            public Object getGroup(int i) {
                return null;
            }

            @Override
            public Object getChild(int i, int i2) {
                return null;
            }

            @Override
            public long getGroupId(int i) {
                return 0;
            }

            @Override
            public long getChildId(int i, int i2) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
                TextView textView = new TextView(getContext(), null);
                textView.setText("Hello");
                return textView;
            }

            @Override
            public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
                TextView textView = new TextView(getContext(), null);
                textView.setText("Hello");
                return textView;
            }

            @Override
            public boolean isChildSelectable(int i, int i2) {
                return false;
            }
        });
    }

    private void search(String root){

        File file = new File(root);

        if(file.isHidden()){
            return;
        }

        File [] files = file.listFiles();

        if(files == null || files.length ==0){
            return;
        }

        for(File f : files){

            if(f.isHidden()){
                continue;
            }

            if(f.isDirectory()){
                    search(f.getAbsolutePath());
            }else{
                if(f.getName().toLowerCase().contains(".fb2")){
                    Log.d("ABC", "Added " + f.getAbsolutePath());
                }
            }
        }

    }
}
