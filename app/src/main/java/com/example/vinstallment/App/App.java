package com.example.vinstallment.App;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

public class App extends Application {
    public static final String CHANNEL_ID = "channel_reminder";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel vInstallmentChannel =  new NotificationChannel(
                    CHANNEL_ID,
                    "VInstallment",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            vInstallmentChannel.setDescription("This is channel for reminder");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(vInstallmentChannel);
        }
    }
}
