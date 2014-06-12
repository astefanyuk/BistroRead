package com.mariko.bistroread;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

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
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
