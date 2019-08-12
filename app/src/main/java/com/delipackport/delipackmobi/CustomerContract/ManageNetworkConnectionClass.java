package com.delipackport.delipackmobi.CustomerContract;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ManageNetworkConnectionClass {

    private  Context context;

    public ManageNetworkConnectionClass(Context context){
        this.context = context;
    }



    public  boolean checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            return  true;
//            Intent networkIntent = new Intent("networkConnectionCheck");
//            networkIntent.putExtra("connectionstatus", "connected");
//            LocalBroadcastManager.getInstance(context).sendBroadcast(networkIntent);
//            System.out.println("connection alive");
        } else {
            return false;
//            Intent networkIntent = new Intent("networkConnectionCheck");
//            networkIntent.putExtra("connectionstatus", "not connected");
//            LocalBroadcastManager.getInstance(context).sendBroadcast(networkIntent);
//            System.out.println("connection not alive");

        }
    }
}
