package com.example.administrator.alarm;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ylteng on 16-7-15.
 */
public class AlarmLog {
    private final static AlarmLog mLog = new AlarmLog();
    private final static String TAG = "AlarmLog";

    private final static String filePath = AlarmApp.getContext().getFilesDir() + "/alarmlog.txt";

    private static FileOutputStream mOutputStream = null;
    private AlarmLog(){
    }
    public synchronized static void log(String tag, String info){
        File f = new File(filePath);
        if (f.exists() && f.length()/1024 > 64) { // keep file size <= 64k
            f.delete();
            Log.d(TAG, "delete logfile");
        }
        try {
            mOutputStream = AlarmApp.getContext().openFileOutput("alarmlog.txt", Context.MODE_PRIVATE | Context.MODE_APPEND);
            StringBuilder builder = new StringBuilder();
            builder.append("@"+getTime() + " :");
            builder.append(tag);
            builder.append(": ");
            builder.append(info);
            builder.append("\r\n");
            mOutputStream.write(builder.toString().getBytes());
        }catch(Exception e){}

    }
    public static String getTime(){
        Calendar c = Calendar.getInstance();
        Date cDate = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(cDate);
    }
}