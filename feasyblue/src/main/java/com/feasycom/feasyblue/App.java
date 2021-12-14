package com.feasycom.feasyblue;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("ly","开启bugly");
        context = getApplicationContext();
        CrashReport.initCrashReport(getApplicationContext(), "8557035c67", false);
    }


    public static Context getContext() {
        return context;
    }
}
