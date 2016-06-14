package com.example.administrator.myapplication;

/*
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
*/


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

    private AlarmManager alarmManager;
    private PendingIntent operation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化按钮，并绑定监听事件
        findViewById(R.id.clock).setOnClickListener(this);
        findViewById(R.id.repeating_clock).setOnClickListener(this);
        findViewById(R.id.cancel_clock).setOnClickListener(this);

        // 获取AlarmManager对象
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // 创建Intent对象，action为android.intent.action.ALARM_RECEIVER
        Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
        operation = PendingIntent.getBroadcast(this, 0, intent, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clock:// 设置一次性闹钟
                alarmManager.set(AlarmManager.RTC_WAKEUP, 10000, operation);
                break;
            case R.id.repeating_clock:// 设置重复闹钟
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 5000, 10000,
                        operation);
                break;
            case R.id.cancel_clock:// 取消闹钟
                alarmManager.cancel(operation);
                break;
            default:
                break;
        }
    }
}
