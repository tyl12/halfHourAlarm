package com.example.administrator.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        //此处可以添加闹钟铃声
        System.out.println("我是闹钟，我要叫醒你...");
        Toast.makeText(arg0, "我是闹钟，我要叫醒你...", Toast.LENGTH_SHORT).show();
    }

}