package com.lvpf.samckdemo.base;

import android.app.Application;

public class LMApplication extends Application {
    private static LMApplication instance;

    public static LMApplication getInstance() {
        // TODO Auto-generated method stub
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
