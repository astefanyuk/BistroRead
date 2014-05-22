package com.mariko.bistroread;

import android.app.Application;

/**
 * Created by AStefaniuk on 5/22/2014.
 */
public class GApp extends Application {

    public static GApp sInstance;

    public GApp() {
        sInstance = this;
    }
}
