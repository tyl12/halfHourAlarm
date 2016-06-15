package com.example.administrator.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/6/15.
 */
public class AlarmControl {

    /*
    private AlarmManager alarmManager;
    private PendingIntent operation;
    */
    public static void cancelAlarm(Context context) {
        // 获取AlarmManager对象
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
        PendingIntent operation = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(operation);
    }

    public static void updateAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
        PendingIntent operation = PendingIntent.getBroadcast(context, 0, intent, 0);
        /*
        alarmManager.cancel(operation);
        */
        List<Integer> list = AlarmStorage.getAllSetting(context, null);
        int start_hourOfDay = 0;
        int start_min = 0;
        int stop_hourOfDay = 23;
        int stop_min = 59;
        boolean halfHour = true;
        if (list != null) {
            start_hourOfDay = list.get(0);
            start_min = list.get(1);
            stop_hourOfDay = list.get(2);
            stop_min = list.get(3);
            halfHour = (list.get(4) > 0 ? true:false);
        }
        Log.d("##@@##", " updateAlarm: halfHour=" + halfHour);
        long nextAlarmMs = getnextHalfHourAroundAlarm(System.currentTimeMillis(), start_hourOfDay*60+start_min, stop_hourOfDay*60+stop_min, halfHour);
        if (nextAlarmMs > 0){
            Calendar c = Calendar.getInstance();
            Date cDate = c.getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Calendar n = Calendar.getInstance();
            n.setTimeInMillis(nextAlarmMs);
            Date nDate = n.getTime();
            Log.d("##@@##", "currentTime=" + df.format(cDate) + " NextAlarm=" + df.format(nDate));

            alarmManager.set(AlarmManager.RTC_WAKEUP,nextAlarmMs,operation);
        }
    }
    private static long getnextHalfHourAroundAlarm(long baseTimeMs, int start_min, int stop_min, boolean halfHour){
        long roundRange = halfHour ? 30*60*1000 : 60*60*1000;
        long count = 24*60*60*1000 / roundRange;
        for (int i = 0; i < count; i++) {
            long nextTimeToCheckInMs = getNextHalfHourAroundInMs(baseTimeMs + i * roundRange, halfHour);
            //把闹钟时间归一化到一天时间范围内
            long nextTimeToCheck_normalized_min = ((long)(nextTimeToCheckInMs / (60*1000) )) % (24*60);
            if (isInRange(start_min, stop_min, nextTimeToCheck_normalized_min))
                return nextTimeToCheckInMs;
        }
        return -1;
    }
    private static long getNextHalfHourAroundInMs(long baseTimeMs, boolean halfHour){
        long roundRange = halfHour ? 30*60*1000 : 60*60*1000;
        long nextMs = ((long)(baseTimeMs + roundRange)/roundRange) * roundRange;
        return nextMs;
    }
    private static boolean isInRange(long start_min, long stop_min, long target_min){ //input: hour*60+min
        //boolean flip = (start_hourOfDay * 60 + start_min > stop_hourOfDay * 60 + stop_min) ? true : false;
        boolean flip = (start_min > stop_min) ? true : false;
        boolean inRange = false;

        if (!flip) {
            if (target_min >= start_min && target_min <= stop_min){
                inRange = true;
            } else {
                inRange = false;
            }
        } else {
            if (target_min >= start_min || target_min <= stop_min){
                inRange = true;
            } else {
                inRange = false;
            }
        }
        Log.d("##@@##", "isInRange: start_min=" + start_min + " stop_min=" + stop_min + " target_min=" + target_min + " flip=" + flip +" inRange=" + inRange);
        return inRange;
    }

}
