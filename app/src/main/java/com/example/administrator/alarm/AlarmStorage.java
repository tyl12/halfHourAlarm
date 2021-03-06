package com.example.administrator.alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/15.
 */
public class AlarmStorage {
    static final String TAG = "AlarmStorage";

    static private final String KEY_STATE="key_onoff";
    static private final String KEY_START_HOUR="key_start_hour";
    static private final String KEY_START_MIN="key_start_min";
    static private final String KEY_STOP_HOUR="key_stop_hour";
    static private final String KEY_STOP_MIN="key_stop_min";
    static private final String KEY_HALFHOUR_CHECKED = "key_halfhour_checked";

    public synchronized static void saveState(Context context, boolean enabled){
        Log.d(TAG, "saveState = " + enabled);
        SharedPreferences.Editor editor = context.getSharedPreferences("alarm", Context.MODE_PRIVATE).edit();
        if (enabled)
            editor.putString(KEY_STATE, "on");
        else
            editor.putString(KEY_STATE, "off");
        try {
            editor.commit();
        } catch (Exception e){
            Log.e(TAG, "saveState failed. " + e);
        }

    }
    public synchronized static boolean getState(Context context){
        boolean ret = false;
        SharedPreferences sp = context.getSharedPreferences("alarm", Context.MODE_PRIVATE);
        String state = sp.getString(KEY_STATE, "off");
        if ("on".equalsIgnoreCase(state))
            ret = true;
        Log.d(TAG, "getState = " + ret);
        return ret;
    }

    public synchronized static void saveAllSetting(Context context, int start_hour, int start_min, int stop_hour, int stop_min, int checked){
        Log.d(TAG, "saveAllSetting = ");
        SharedPreferences.Editor editor = context.getSharedPreferences("alarm", Context.MODE_PRIVATE).edit();
        editor.
                putInt(KEY_START_HOUR, start_hour).
                putInt(KEY_START_MIN, start_min).
                putInt(KEY_STOP_HOUR, stop_hour).
                putInt(KEY_STOP_MIN, stop_min).
                putInt(KEY_HALFHOUR_CHECKED, checked);
        try {
            editor.commit();
        } catch (Exception e){
            Log.e(TAG, "saveAllSetting failed. " + e);
        }
    }

    public synchronized static List<Integer> getAllSetting(Context context, List<Integer> list){
        Log.d(TAG, "getAllSetting = ");
        if (list == null)
            list = new ArrayList<Integer>();
        SharedPreferences sp = context.getSharedPreferences("alarm", Context.MODE_PRIVATE);
        list.add(sp.getInt(KEY_START_HOUR, 0));
        list.add(sp.getInt(KEY_START_MIN,0));
        list.add(sp.getInt(KEY_STOP_HOUR,23));
        list.add(sp.getInt(KEY_STOP_MIN,59));
        list.add(sp.getInt(KEY_HALFHOUR_CHECKED, 0));
        return list;
    }
}
