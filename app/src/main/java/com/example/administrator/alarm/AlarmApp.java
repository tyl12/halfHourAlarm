package com.example.administrator.alarm;

import android.app.Application;
import android.content.Context;

/**
 * Created by ylteng on 16-7-15.
 */
public class AlarmApp extends Application {
    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
