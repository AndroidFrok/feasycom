package com.feasycom.feasyblue.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class Uitls {

    private static Context mContext;

    public static void init(Context context){
        mContext = context;
    }

    public static Context getApp(){
        return mContext;
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * @param pxValue
     * @return
     */
    public static int px2dp( float pxValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转px
     * @param dpVal
     * @return
     */
    public static int dp2px( float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, mContext.getResources().getDisplayMetrics());
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * @param pxValue
     * @return
     */
    public static int px2sp( float pxValue) {
        float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param spValue
     * @return
     */
    public static int sp2px( float spValue) {
//        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
//        return (int) (spValue * fontScale + 0.5f);
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spValue, mContext.getResources().getDisplayMetrics());
    }

    /**
     * 获取版本号
     * @return
     */
    public static String getVersionName() {
        try {
            return mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    /**
     * 获取版本号
     * @return
     */
    public static int getVersionCode() {
        try {
            return mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return 0;
    }

    /**
     * 获取屏幕宽度，px
     * @return
     */
    public static float getScreenWidth() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度，px
     * @return
     */
    public static float getScreenHeight() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取屏幕像素密度
     * @return
     */
    public static float getDensity() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 获取scaledDensity
     * @return
     */
    public static float getScaledDensity() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.scaledDensity;
    }

    /**
     * 获取当前小时分钟24小时进制
     * @return
     */
    public static String getTime24Hours() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.CHINA);
        // 获取当前时间
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    /**
     * 将byte转换为一个长度为8的boolean数组（每bit代表一个boolean值）
     * @param b byte
     * @return boolean数组
     */
    public static boolean[] getBooleanArray(byte b) {
        boolean[] array = new boolean[8];
        //对于byte的每bit进行判定
        for (int i = 7; i >= 0; i--) {
            //判定byte的最后一位是否为1，若为1，则是true；否则是false
            array[i] = (b & 1) == 1;
            //将byte右移一位
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * 将byte转换为一个长度为8的boolean数组（每bit代表一个boolean值）
     * @param b byte
     * @return boolean数组
     */
    public static Boolean[] getBooleanCapArray(byte b) {
        Boolean[] array = new Boolean[8];
        //对于byte的每bit进行判定
        for (int i = 7; i >= 0; i--) {
            //判定byte的最后一位是否为1，若为1，则是true；否则是false
            array[i] = (b & 1) == 1;
            //将byte右移一位
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * 将一个长度为8的boolean数组（每bit代表一个boolean值）转换为byte
     * @param array
     * @return
     */
    public static byte getByte(boolean[] array) {
        if (array != null && array.length > 0) {
            byte b = 0;
            for (int i = 0; i <= 7; i++) {
                if (array[i]) {
                    int nn = (1 << (7 - i));
                    b += nn;
                }
            }
            return b;
        }
        return 0;
    }

    /**
     * 将一个长度为8的boolean数组（每bit代表一个boolean值）转换为byte
     * @param array
     * @return
     */
    public static byte getByte(List<Boolean> array) {
        if (array != null && array.size() > 0) {
            byte b = 0;
            for (int i = 0; i <= 7; i++) {
                if (array.get(i)) {
                    int nn = (1 << (7 - i));
                    b += nn;
                }
            }
            return b;
        }
        return 0;
    }

    /**
     * 将时间格式化，使其变成00:00:00形式
     * @param time
     * @return
     */
    public static String formatTimeRecord(int time) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int minute, second, hours;
        minute = time / 60;
        second = time % 60;
        hours = time / 3600;
        mFormatBuilder.setLength(0);
        return mFormatter.format("%02d:%02d:%02d", hours, minute, second).toString();
    }

    public static String getPageName(){
        return mContext.getPackageName();
    }
}
