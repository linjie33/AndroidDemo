package com.planetwalk.ponion.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.planetwalk.ponion.GlobalData;

import java.util.Collection;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.content.Context.INPUT_METHOD_SERVICE;

public final class CommonUtils {
    public static int randInt(int min, int max) {
        Random rand = new Random(System.currentTimeMillis());
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    public static void openKeyboard(final EditText editText) {
        //设置可获得焦点
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        //请求获得焦点
        editText.requestFocus();
        //调用系统输入法
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) editText
                        .getContext().getSystemService(
                                INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 100);
    }



    /**
     * 关闭键盘
     *
     * @param editText 操作的输入框
     */
    public static void closeKeyboard(EditText editText) {
        //关闭键盘
        InputMethodManager imm = (InputMethodManager) editText
                .getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

    }

    public static String getDeviceId(Context context) {
        String serial;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位

        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    public static void writeSharePreferenceString(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(GlobalData.PET_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void writeSharePreferenceLong(Context context, String key, long value) {
        SharedPreferences sharedPref = context.getSharedPreferences(GlobalData.PET_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(GlobalData.PET_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPref.getString("token", null);
    }

    public static long getLastMsgId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(GlobalData.PET_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPref.getLong("last_msg_id", 0);
    }

    public static void writeLastMsgId(Context context, long id) {
        writeSharePreferenceLong(context, "last_msg_id", id);
    }

    public static String getAccountName(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(GlobalData.PET_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPref.getString("name", null);
    }

    public static boolean isCollectionEmpty(Collection a) {
        return a != null && !a.isEmpty();
    }
}
