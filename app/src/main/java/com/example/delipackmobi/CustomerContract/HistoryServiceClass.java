package com.example.delipackmobi.CustomerContract;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class HistoryServiceClass extends Service {

    Context context = this;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread historyThread = new Thread(new ManageHistoryClass(context, intent.getStringExtra("customerID")));
//        Thread networkThread = new Thread(new ManageNetworkConnectionClass(context));
        historyThread.start();
//        networkThread.start();
        System.out.println("running");
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
