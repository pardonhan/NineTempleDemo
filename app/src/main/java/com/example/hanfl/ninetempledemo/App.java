package com.example.hanfl.ninetempledemo;

import android.app.Application;

import org.xutils.x;

/**
 * Created by HanFL on 2016/9/27.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
