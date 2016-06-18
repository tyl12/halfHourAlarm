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

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver{

//    private AlarmManager alarmManager;
//    private PendingIntent operation;
    private Context mContext;


    @Override
    public void onReceive(Context arg0, Intent intent) {
        mContext = arg0;
        //此处可以添加闹钟铃声
//        System.out.println("我是闹钟，我要叫醒你...");
//        Toast.makeText(arg0, "我是闹钟，我要叫醒你...", Toast.LENGTH_SHORT).show();

        String action = intent.getAction();
        if (AlarmControl.INTENT_ALARM_RECEIVER.equals(action)) {
            Log.d("##@@##" , "INTENT_ALARM_RECEIVER received");
            handleAlarmIntent();
        }
        else if (Intent.ACTION_TIMEZONE_CHANGED.equals(action) ||
                Intent.ACTION_TIME_CHANGED.equals(action) ||
                Intent.ACTION_DATE_CHANGED.equals(action)){
            Log.d("##@@##" , "system time changed, ajust next alarm");
            reInstallAlarm();
        }
        else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Log.d("##@@##" , "ACTION_BOOT_COMPLETED received");
            reInstallAlarm();
        }
    }

    private void handleAlarmIntent(){
        boolean halfHour;
        Calendar c = Calendar.getInstance();
        Date cDate = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Alarm triggered @ " + df.format(cDate));
        Toast.makeText(mContext, df.format(cDate), Toast.LENGTH_SHORT).show();

        int min = c.get(Calendar.MINUTE);
        if (min<5 || min>55)
            halfHour = false;
        else
            halfHour = true;
        sound(halfHour);
        reInstallAlarm();
    }

    private void reInstallAlarm(){
        AlarmControl.cancelAlarm(mContext);
        AlarmControl.updateAlarm(mContext);
    }

    MediaPlayer mMediaPlayer = new MediaPlayer();

    private void sound(final boolean halfHour){
        Log.d("##@@##" , "sound x");
        //获取alarm uri
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //InputStream stream = mContext.getResources().openRawResource(R.raw.pixiedust);
        //Uri music = Uri.parse("android.resource://com.example.administrator.myapplication/raw/pixiedust.ogg");
        //File file= new File(video.toString());

        //创建media player
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mContext, alert);
            //mMediaPlayer.setDataSource(mContext, music);
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
                        Log.e("##@@##", "run");
                        int durationMs = halfHour?1500:3000;
                        Message message=new Message();
                        message.what=1;
                        mHandler.sendMessageDelayed(message, durationMs);
                    }
                });
                thread.start();
                Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                if (halfHour)
                    v.vibrate(new long[] {0,200,0}, -1);
                else
                    v.vibrate(new long[] {0,200,1000,200,1000,200}, -1);
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