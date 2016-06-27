package com.example.ademi.galonovaversao.Classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.ademi.galonovaversao.Activities.MainActivity;

/**
 * Created by root on 28/04/16.
 */
public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
