package com.example.ademi.galonovaversao.Classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by root on 22/04/16.
 */
public class CheckConnection {

    Context context;

    public CheckConnection(Context context) { this.context = context; }

    public boolean isOnline(){

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());

    }

}
