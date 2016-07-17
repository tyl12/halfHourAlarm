package com.example.administrator.alarm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ylteng on 16-7-15.
 */
public class AlarmService extends IntentService {
    static final String TAG = "AlarmService";
    private Context mContext = null;
    private boolean mIsHalfHour = false;
    public AlarmService()
    {
        super("AlarmService");
        mContext = this;
        Log.d(TAG, " create");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d(TAG, "onHandleIntent");
        handleAlarmIntent();
    }

    private void handleAlarmIntent(){
        boolean halfHour;
        Calendar c = Calendar.getInstance();
        final Date cDate = c.getTime();
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Alarm triggered @ " + df.format(cDate));
        AlarmLog.log(TAG, "Alarm triggered @ " + df.format(cDate));
        //Toast.makeText(mContext, df.format(cDate), Toast.LENGTH_SHORT).show();
        // create a handler to post messages to the main thread
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, df.format(cDate), Toast.LENGTH_SHORT).show();
            }
        });

        int min = c.get(Calendar.MINUTE);
        Log.d(TAG, "handleAlarmIntent: minute = " + min);
        if (min<15 || min>45)
            halfHour = false;
        else
            halfHour = true;
        mIsHalfHour = halfHour;
        Log.d(TAG, "mIsHalfHour = " + mIsHalfHour);
        sound_vibrate(); //halfHour);
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
                Log.d(TAG, "stream found");
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(false);
                mPlayCnt = (mIsHalfHour)?1:2;

                Log.d(TAG, "PLAY_SOUND cnt = " + mPlayCnt);
                Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                while(mPlayCnt-- > 0) {
                    try {
                        AlarmLog.log(TAG, " trigger sound");
                        mMediaPlayer.prepare();
                        mMediaPlayer.seekTo(0);
                        mMediaPlayer.start();
                        v.vibrate(new long[]{0, 200}, -1);
                        Thread.sleep(SOUND_DURATION);
                        mMediaPlayer.stop();
                        Thread.sleep(SOUND_INTERVAL);
                    } catch (Exception e) {}
                }
            }
        }catch (Exception e) {
            Log.d(TAG , "Exception: "  + e.toString());
        }
    }

    private static final int SOUND_DURATION = 1000; //ms
    private static final int SOUND_INTERVAL = 1500; //ms

}
