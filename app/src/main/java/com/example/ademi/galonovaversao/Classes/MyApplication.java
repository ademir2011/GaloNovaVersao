package com.example.ademi.galonovaversao.Classes;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by ademi on 05/07/2016.
 */
public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private Handler handler;
    private Runnable runLogout = new Runnable() {
        @Override
        public void run() {
            //logoutUser()
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(this);
        handler = new Handler(getMainLooper());

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        handler.removeCallbacks(runLogout);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        handler.removeCallbacks(runLogout);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        handler.removeCallbacks(runLogout);
//        restartApp(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        handler.postDelayed(runLogout, 1000);
//        restartApp(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
//        restartApp(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
//        restartApp(activity);
    }

    public static void restartApp(final Context context) {
        Intent restart = context
                .getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(restart);
    }
}
