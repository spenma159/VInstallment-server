package com.example.vinstallment.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.vinstallment.Notification.NotificationReminder;

public class OneDayAfterService extends Service {
    NotificationReminder notificationReminder;
    @Override
    public void onCreate() {
        super.onCreate();
        notificationReminder = new NotificationReminder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(2, notificationReminder.oneDayAfterNotify());
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
