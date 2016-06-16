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
    //currentTimeMs: 系统UTC基准的当前时间， ms
    //start_min：24小时内的闹钟起点时间，min
    //stop_min: 24小时内的闹钟结束时间，min
    //halfHour: 是否以30min作为step，否则60min
    //return: 接下来半点（整点）闹钟时间，UTC基准，ms
    private static long getnextHalfHourAroundAlarm(long currentTimeMs, int start_min, int stop_min, boolean halfHour){
        long stepMin = halfHour ? 30:60;
        long currentTimeMin = currentTimeMs/(1000*60);
        long count = 24*60 / stepMin;
        for (int i = 1; i <= count; i++) {
            long nextTimeInMin = currentTimeMin + i * stepMin;
            long nextTimeHalfHourRoundMin = ((long)(nextTimeInMin / stepMin)) * stepMin;
            //把闹钟时间归一化到一天时间范围内
            long nextTimeToCheck_normalized_min = (nextTimeHalfHourRoundMin % (24*60));
            Log.d("##@@##", "currentms = " + currentTimeMs+ " currentTimeMin = " + currentTimeMin + " nextTimeInMin=" + nextTimeInMin + " nextTimeHalfHourRoundMin=" + nextTimeHalfHourRoundMin + " round_normalized_min = " + nextTimeToCheck_normalized_min);
            if (isInRange(start_min, stop_min, nextTimeToCheck_normalized_min))
                return nextTimeHalfHourRoundMin*60*1000;
        }
        return -1;
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
