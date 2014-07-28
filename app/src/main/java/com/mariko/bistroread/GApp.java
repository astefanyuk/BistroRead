package com.mariko.bistroread;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by AStefaniuk on 5/22/2014.
 */
public class GApp extends Application {

    public static GApp sInstance;

    public GApp() {
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);

        dumpDb();
    }

    private static void dumpDb() {
        try {
            copyFile(new File(ActiveAndroid.getDatabase().getPath()), new File("/mnt/sdcard/mydb_jul28_2014.db"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(File sourceFile, File destinationFile) throws IOException {

        destinationFile.delete();
        destinationFile.getParentFile().mkdirs();

        int FILE_COPY_BUFFER_SIZE = 20 * 1024;

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(destinationFile);
            input = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count;
            while (pos < size) {
                count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        } finally {
            closeStream(output);
            closeStream(fos);
            closeStream(input);
            closeStream(fis);
        }
    }

    private static void closeStream(java.io.Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Throwable ignored) {
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
