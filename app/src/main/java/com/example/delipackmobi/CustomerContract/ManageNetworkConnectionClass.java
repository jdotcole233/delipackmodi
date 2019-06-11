package com.example.delipackmobi.CustomerContract;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.example.delipackmobi.Homedashboard_user;

public class ManageNetworkConnectionClass {

    private  Context context;

    public ManageNetworkConnectionClass(Context context){
        this.context = context;
    }



    public  boolean checkConnectivity() {
        Boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            isConnected = true;
//            Intent networkIntent = new Intent("networkConnectionCheck");
//            networkIntent.putExtra("connectionstatus", "connected");
//            LocalBroadcastManager.getInstance(context).sendBroadcast(networkIntent);
//            System.out.println("connection alive");
        } else {
            isConnected = false;
//            Intent networkIntent = new Intent("networkConnectionCheck");
//            networkIntent.putExtra("connectionstatus", "not connected");
//            LocalBroadcastManager.getInstance(context).sendBroadcast(networkIntent);
//            System.out.println("connection not alive");

        }

        return isConnected;
    }
}
