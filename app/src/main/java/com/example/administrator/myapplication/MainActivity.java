package com.example.administrator.myapplication;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.List;

public class MainActivity extends Activity/*implements OnClickListener*/{

    private ToggleButton mToggleBtn = null;
    private Button mStartBtn = null;
    private Button mStopBtn = null;
    private TextView mIntervalText = null;
    private TextView mToText = null;
    private CheckBox mCheckBtn = null;
    private Button mTestBtn = null;

    private int start_hourOfDay = -1;
    private int start_min = -1;
    private int stop_hourOfDay = -1;
    private int stop_min = -1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("##@@##", "onCreate E");
        setContentView(R.layout.activity_main);

        //checkbtn
        mToggleBtn = (ToggleButton)findViewById(R.id.tglSound);
        mToggleBtn.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("##@@##", "checkbtn=" + isChecked);
                if (isChecked){
                    recoverAllSetting(); //recover all previous setting when enabled again
                    enableBtn(true);
                }
                else {
                    //save all settings when unchecked
                    AlarmStorage.saveState(MainActivity.this, isChecked);
                    AlarmStorage.saveAllSetting(MainActivity.this, start_hourOfDay, start_min, stop_hourOfDay, stop_min, mCheckBtn.isChecked()?1:0);
                    enableBtn(false);
                }
                updateAlarm();
            }
        });
        //test btn
        mTestBtn = (Button)findViewById(R.id.test);
        mTestBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建Intent对象
                Intent intent = new Intent();
                // 设置Intent的Action属性
                intent.setAction("android.intent.action.ALARM_RECEIVER");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.putExtra("msg", "测试广播");
                //发送广播
                sendBroadcast(intent);
                Toast.makeText(MainActivity.this, "发送测试广播", Toast.LENGTH_SHORT).show();
            }
        });
        //start/stop btn
        mIntervalText = (TextView)findViewById(R.id.timeInterval);
        mToText = (TextView)findViewById(R.id.to);
        mStartBtn = (Button)findViewById(R.id.start);
        mStopBtn = (Button)findViewById(R.id.stop);
        mStartBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mStartBtn.setText(String.format("%d:%d",hourOfDay,minute));
                        start_hourOfDay = hourOfDay;
                        start_min = minute;
                        updateAlarm();
                    }
                    //0,0指的是时间，true表示是否为24小时，true为24小时制
                },0,0,true).show();
            }
        });
        mStopBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mStopBtn.setText(String.format("%d:%d",hourOfDay,minute));
                        stop_hourOfDay = hourOfDay;
                        stop_min = minute;
                        updateAlarm();
                    }
                    //0,0指的是时间，true表示是否为24小时，true为24小时制
                },23,59,true).show();
            }
        });

        //checkbox
        mCheckBtn = (CheckBox)findViewById(R.id.checkbox);
        mCheckBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Toast.makeText(MainActivity.this, "halfHour alarm", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Hour alarm", Toast.LENGTH_SHORT).show();
                updateAlarm();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("##@@##", "onResume E");
        boolean enabled = AlarmStorage.getState(this);
        if (enabled)
            enableBtn(true);
        else
            enableBtn(false);
        mToggleBtn.setChecked(enabled);
        recoverAllSetting();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("##@@##", "onPause E");
        AlarmStorage.saveState(this, mToggleBtn.isEnabled());
        AlarmStorage.saveAllSetting(this, start_hourOfDay, start_min, stop_hourOfDay, stop_min, mCheckBtn.isChecked()?1:0);
    }

    private void recoverAllSetting(){
        Log.d("##@@##", "recoverAllSetting E");
        List<Integer> list = AlarmStorage.getAllSetting(this, null);
        if (list != null){
            start_hourOfDay = list.get(0);
            start_min = list.get(1);
            stop_hourOfDay = list.get(2);
            stop_min = list.get(3);

            mStartBtn.setText(String.format("%d:%d",start_hourOfDay, start_min));
            mStopBtn.setText(String.format("%d:%d",stop_hourOfDay, stop_min));
            mCheckBtn.setChecked( (list.get(4)>0) ? true : false);
        }
    }

    private void enableBtn(boolean enable){
        Log.d("##@@##", "enableBtn  = " + enable);
        mTestBtn.setEnabled(enable);
        mStartBtn.setEnabled(enable);
        mStopBtn.setEnabled(enable);
        mIntervalText.setEnabled(enable);
        mToText.setEnabled(enable);
        mCheckBtn.setEnabled(enable);
    }

    private void updateAlarm(){
        synchronized (this) {
            //save all states first;
            AlarmStorage.saveState(this, mToggleBtn.isEnabled());
            AlarmStorage.saveAllSetting(this, start_hourOfDay, start_min, stop_hourOfDay, stop_min, mCheckBtn.isChecked()?1:0);
            //alarmcontrol will read info from sharepreference directly.
            AlarmControl.cancelAlarm(this);
            if (mToggleBtn.isChecked()) {
                AlarmControl.updateAlarm(this);
            }
        }
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.clock:// 设置一次性闹钟
//                alarmManager.cancel(operation);
//
//                c = Calendar.getInstance();
//                c.setTimeInMillis(System.currentTimeMillis() + 5000);
//                int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
//
//                Log.d("##@@##", "hour r = " + hourOfDay);
//
//                /*
//                //设置小时分钟，秒和毫秒都设置为0
//                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                c.set(Calendar.MINUTE, 0);
//                c.set(Calendar.SECOND, 0);
//                c.set(Calendar.MILLISECOND, 0);
//                */
//
//                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), operation);
//                break;
//            case R.id.repeating_clock:// 设置重复闹钟
//                alarmManager.cancel(operation);
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 5000, 20000, operation);
//                break;
//            case R.id.cancel_clock:// 取消闹钟
//                alarmManager.cancel(operation);
//                break;
//            default:
//                break;
//        }
//    }
}
