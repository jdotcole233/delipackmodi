package com.example.delipackmobi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NetworkConnectionView extends AppCompatActivity {

    private Button networkConnection;
    private NetworkAllowanceCheck networkAllowanceCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_connection_view);

        networkConnection = findViewById(R.id.checkconnectionbutton);
        networkAllowanceCheck = new NetworkAllowanceCheck(this);

        networkConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkAllowanceCheck.enable(NetworkConnectionView.this);

            }
        });
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class NetworkAllowanceCheck extends ConnectivityManager.NetworkCallback{
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
}
