package com.example.administrator.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver{

//    private AlarmManager alarmManager;
//    private PendingIntent operation;
    private Context mContext;
    private boolean mIsHalfHour = false;
    static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context arg0, Intent intent) {
        mContext = arg0;

        String action = intent.getAction();
        AlarmLog.log(TAG, "intent:" + intent + " received. action:" + action);

        boolean enabled = AlarmStorage.getState(mContext);
        if (!enabled) {
            Log.d(TAG, "onReceived: alarm previously disabled");
            AlarmLog.log(TAG, "alarm disabled");
            return;
        }
        if (AlarmControl.INTENT_ALARM_RECEIVER.equals(action)) {
            Log.d(TAG , "INTENT_ALARM_RECEIVER received");
            AlarmLog.log(TAG, "--> start serviceIntent");
            Intent serviceIntent = new Intent(mContext, AlarmService.class);
            mContext.startService(serviceIntent);
        }
        else if (Intent.ACTION_TIMEZONE_CHANGED.equals(action) ||
                Intent.ACTION_SCREEN_OFF.equals(action) ||
                Intent.ACTION_BOOT_COMPLETED.equals(action) ||
                Intent.ACTION_TIME_CHANGED.equals(action) ||
                Intent.ACTION_DATE_CHANGED.equals(action)){
            Log.d(TAG , "--> system time changed, adjust next alarm");
            AlarmLog.log(TAG, "--> reInstallAlarm");
            reInstallAlarm();
        }
    }

    private void reInstallAlarm(){
        AlarmControl.cancelAlarm(mContext);
        AlarmControl.updateAlarm(mContext);
    }

/*
    private void handleAlarmIntent(){
        boolean halfHour;
        Calendar c = Calendar.getInstance();
        Date cDate = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Alarm triggered @ " + df.format(cDate));
        Toast.makeText(mContext, df.format(cDate), Toast.LENGTH_SHORT).show();

        int min = c.get(Calendar.MINUTE);
        Log.d(TAG, "handleAlarmIntent: minute = " + min);
        if (min<15 || min>45)
            halfHour = false;
        else
            halfHour = true;
        sound_vibrate(); //halfHour);
        mIsHalfHour = halfHour;
        Log.d(TAG, "mIsHalfHour = " + mIsHalfHour);
        reInstallAlarm();
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
                mPlayCnt = 0;

                //let handler play sound
                Message message = new Message();
                message.what = PLAY_SOUND;
                mHandler.sendMessage(message);
            }
        }catch (Exception e) {
            Log.d(TAG , "Exception: "  + e.toString());
        }
        //triggerVibrate();
    }

    private static final int PLAY_SOUND = 1;
    private static final int STOP_SOUND = 2;
    private static final int SOUND_DURATION = 1000; //ms
    private static final int SOUND_INTERVAL = 1500; //ms

    public Handler mHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case PLAY_SOUND:
                    mPlayCnt++;
                    Log.d(TAG, "PLAY_SOUND cnt = " + mPlayCnt);
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
                    Log.d(TAG, "STOP_SOUND cnt = " + mPlayCnt);
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
   */
}