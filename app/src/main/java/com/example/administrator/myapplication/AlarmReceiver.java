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
import android.provider.MediaStore;
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
    private boolean mIsHalfHour = false;


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
        Log.d("##@@##", "handleAlarmIntent: minute = " + min);
        if (min<15 || min>45)
            halfHour = false;
        else
            halfHour = true;
        sound_vibrate(); //halfHour);
        mIsHalfHour = halfHour;
        Log.d("##@@##", "mIsHalfHour = " + mIsHalfHour);
        reInstallAlarm();
    }

    private void reInstallAlarm(){
        AlarmControl.cancelAlarm(mContext);
        AlarmControl.updateAlarm(mContext);
    }

    MediaPlayer mMediaPlayer; //= new MediaPlayer();
    private int mPlayCnt = 0;
    private Uri mAlert;
    private void sound_vibrate() { //final boolean halfHour){
        //获取alarm uri
        mAlert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //InputStream stream = mContext.getResources().openRawResource(R.raw.pixiedust);
        //Uri music = Uri.parse("android.resource://com.example.administrator.myapplication/raw/pixiedust.ogg");
        //File file= new File(video.toString());
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mContext, mAlert);
            //mMediaPlayer.setDataSource(mContext, music);
            final AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                Log.d("##@@##", "stream found");
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(false);
                mPlayCnt = 0;

                //let handler play sound
                Message message = new Message();
                message.what = PLAY_SOUND;
                mHandler.sendMessage(message);
            }
        }catch (Exception e) {
            Log.d("##@@##" , "Exception: "  + e.toString());
        }
        //triggerVibrate();
    }

    private static final int PLAY_SOUND = 1;
    private static final int STOP_SOUND = 2;
    private static final int SOUND_DURATION = 1000; //ms
    private static final int SOUND_INTERVAL = 1500; //ms

    /*
    private void triggerVibrate() {
        //vibrate
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (mIsHalfHour)
            v.vibrate(new long[] {0,200}, -1);
        else
            v.vibrate(new long[] {0,200,1000,200}, -1);
    }
    */

    public Handler mHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case PLAY_SOUND:
                    mPlayCnt++;
                    Log.d("##@@##", "PLAY_SOUND cnt = " + mPlayCnt);
                    try {
                        mMediaPlayer.prepare();
                        mMediaPlayer.seekTo(0);
                        mMediaPlayer.start();
                        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(new long[] {0,200}, -1);
                    } catch (Exception e){}
                    Message msg_stop = new Message();
                    msg_stop.what = STOP_SOUND;
                    mHandler.sendMessageDelayed(msg_stop, SOUND_DURATION); // play sound for SOUND_DURATION ms
                    break;
                case STOP_SOUND:
                    mMediaPlayer.stop(); //stop sound
                    Log.d("##@@##", "STOP_SOUND cnt = " + mPlayCnt);
                    if (!mIsHalfHour && mPlayCnt < 2) {
                        Message msg_start = new Message();
                        msg_start.what = PLAY_SOUND;
                        mHandler.sendMessageDelayed(msg_start, SOUND_INTERVAL);//pause for SOUND_DURATION ms and play sound for the second time.
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}