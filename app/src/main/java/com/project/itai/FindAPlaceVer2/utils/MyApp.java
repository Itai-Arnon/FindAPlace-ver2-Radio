package com.project.itai.FindAPlaceVer2.utils;

import android.app.Application;
import android.content.Context;
//Application Class is in charge of communications between the app and the Android Framework
public class MyApp extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }


    public static Context getContext() {
        return context;

    }
}
