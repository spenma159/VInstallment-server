package com.example.vinstallment.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.vinstallment.Notification.NotificationReminder;
import com.example.vinstallment.Policy.PolicyManager;

public class ThreeDaysAfterService extends Service {
    PolicyManager policyManager;
    NotificationReminder notificationReminder;
    @Override
    public void onCreate() {
        super.onCreate();
        policyManager = new PolicyManager(this);
        notificationReminder = new NotificationReminder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(2, notificationReminder.oneDayAfterNotify());
        policyManager.startSpeak();
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
        policyManager.stopSpeak();
    }
}
