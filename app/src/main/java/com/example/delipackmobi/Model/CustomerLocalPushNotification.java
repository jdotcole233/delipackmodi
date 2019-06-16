package com.example.delipackmobi.Model;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.delipackmobi.R;

public class CustomerLocalPushNotification {

    private Context context;
    NotificationCompat.Builder builder;
    NotificationChannel deliveryNotificationChannel;
    private String notificationChannelID = "delivertnotifications";
    private int notificationID = 001;

    @TargetApi(Build.VERSION_CODES.O)
    public CustomerLocalPushNotification(Context context){
        this.context = context;
    }

    public void publishNotification(String notificationTitle, String notificationMessage){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, notificationChannelID);
        builder.setSmallIcon(R.drawable.deli_s);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(notificationMessage);
        Intent notificationIntent = new Intent(context, context.getClass());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            deliveryNotificationChannel = new NotificationChannel(notificationChannelID, "deliveries", NotificationManager.IMPORTANCE_DEFAULT);
            deliveryNotificationChannel.setLightColor(Color.parseColor("#1565c0"));
            deliveryNotificationChannel.setShowBadge(true);
            deliveryNotificationChannel.setDescription("Deliveries description");
//            deliveryNotificationChannel.setSound();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(deliveryNotificationChannel);
            System.out.println("In something notification");

        } else {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationID, builder.build());
        }


    }
}
