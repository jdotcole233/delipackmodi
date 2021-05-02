package com.delipackport.delipackmobi.CustomerContract;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NetworkAllowanceCheck extends ConnectivityManager.NetworkCallback {

        private NetworkRequest networkRequest;
        private Activity activity;

        public NetworkAllowanceCheck(Activity activity){
            this.activity = activity;
            networkRequest = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
        }

        public void enable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.registerNetworkCallback(networkRequest , this);
        }

        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            activity.finish();

        }

}
