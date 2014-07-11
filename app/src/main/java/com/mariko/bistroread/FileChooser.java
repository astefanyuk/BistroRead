package com.mariko.bistroread;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;


public class FileChooser extends ExpandableListView {

    public static interface Listener {
        void fileSelected(File file);
    }

    public Listener listener;

    private static class FileGroup {
        public String title;
        public List<File> files;

        public FileGroup(String title, List<File> files) {
            this.title = title;
            this.files = files;
        }
    }

    private List<FileGroup> groupList = new ArrayList<FileGroup>();

    public FileChooser(Context context, AttributeSet attrs) {
        super(context, attrs);

        groupList = loadFiles();


        setAdapter(new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return groupList.size();
            }

            @Override
            public int getChildrenCount(int i) {
                return groupList.get(i).files.size();
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
                if (view == null) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.file_chooser_item_header, null);
                }
                TextView textView = (TextView) view.findViewById(R.id.txtTitle);
                textView.setText(groupList.get(i).title);

                return view;
            }

            @Override
            public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {

                if (view == null) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.file_chooser_item, null);
                }
                TextView textView = (TextView) view.findViewById(R.id.txtTitle);
                textView.setText(groupList.get(i).files.get(i2).getName());

                return view;
            }

            @Override
            public boolean isChildSelectable(int i, int i2) {
                return true;
            }
        });

        setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                listener.fileSelected(groupList.get(i).files.get((int) i2));

                return true;
            }
        });
    }

    private static List<FileGroup> loadFiles() {

        List<FileGroup> fileGroup = new ArrayList<FileGroup>();

        File file = Environment.getExternalStorageDirectory();

        if (file != null) {
            List<File> files = new ArrayList<File>();
            search(file.getAbsolutePath(), files);

            Hashtable<String, List<File>> hash = new Hashtable<String, List<File>>();
            for (File f : files) {
                String key = f.getParentFile().getAbsolutePath();
                List<File> fileCollection = hash.get(key);
                if (fileCollection == null) {
                    fileCollection = new ArrayList<File>();
                    hash.put(key, fileCollection);
                }
                fileCollection.add(f);
            }

            List<String> titles = new ArrayList<String>(hash.keySet());
            Collections.sort(titles);


            for (String s : titles) {
                fileGroup.add(new FileGroup(s, hash.get(s)));
            }

        }

        return fileGroup;
    }

    private static void search(String root, List<File> foundedFiles) {

        File file = new File(root);

        if (file.isHidden()) {
            return;
        }

        File[] files = file.listFiles();

        if (files == null || files.length == 0) {
            return;
        }

        for (File f : files) {

            if (f.isHidden()) {
                continue;
            }

            if (f.isDirectory()) {
                search(f.getAbsolutePath(), foundedFiles);
            } else {
                if (f.getName().toLowerCase().contains(".fb2")) {
                    foundedFiles.add(f);
                }
            }
        }

    }
}
