package com.example.administrator.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver{

    private AlarmManager alarmManager;
    private PendingIntent operation;
    private Context mContext;

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        mContext = arg0;
        //此处可以添加闹钟铃声
        System.out.println("我是闹钟，我要叫醒你...");
        Toast.makeText(arg0, "我是闹钟，我要叫醒你...", Toast.LENGTH_SHORT).show();

        sound();
        reInstallAlarm();
    }

    private void reInstallAlarm(){
        AlarmControl.cancelAlarm(mContext);
        AlarmControl.updateAlarm(mContext);
        /*
        // 获取AlarmManager对象
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        // 创建Intent对象，action为android.intent.action.ALARM_RECEIVER
        Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
        operation = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        alarmManager.cancel(operation);
        Calendar c = null;
        c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis() + 10000);
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        Log.d("##@@##", "hour x = " + hourOfDay);
        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), operation);
        */
    }

    MediaPlayer mMediaPlayer = new MediaPlayer();

    private void sound(){
        Log.d("##@@##" , "sound x");
        //获取alarm uri
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        //创建media player
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mContext, alert);
            final AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                Log.d("##@@##" , "stream found");
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(false);
                mMediaPlayer.prepare();
                mMediaPlayer.start();


                Thread thread=new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.e("##@@##", "111111111");
                        // TODO Auto-generated method stub
                        Message message=new Message();
                        message.what=1;
                        mHandler.sendMessageDelayed(message, 2000);
                    }
                });
                thread.start();
                Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);



            }
        } catch (Exception e) {
            Log.d("##@@##" , "Exception: "  + e.toString());
        }
        Log.d("##@@##" , "stream play done");
    }


    public Handler mHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:
                    mMediaPlayer.stop();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}