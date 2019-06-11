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
        historyThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
