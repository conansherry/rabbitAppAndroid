package com.lvfq.rabbit;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;

public class InitialApplication extends Application {
    private final static String TAG="InitialApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        // TODO Put your application initialization code here.
        Log.d(TAG, "onCreate");
    }
}