package com.mju.csmoa.util;

import android.app.Application;
import android.util.Log;

import lombok.Getter;


public final class MyApplication extends Application {

    // Create the instance
    private static Application INSTANCE;

    public static Application getInstance() {
        if (INSTANCE == null) {
            synchronized (Application.class) {
                if (INSTANCE == null)
                    INSTANCE = new Application();
            }
        }
        // Return the instance
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        Log.d("로그", "Application 시작");
    }
}




