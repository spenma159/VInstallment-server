package com.example.vinstallment.Policy;

import static android.content.ContentValues.TAG;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.example.vinstallment.EventBus.SetLunasEvent;
import com.example.vinstallment.EventBus.UpdateUIEvent;
import com.example.vinstallment.Notification.NotificationReminder;
import com.example.vinstallment.Receiver.DeviceAdminReceiver;
import com.example.vinstallment.Service.OneDayAfterService;
import com.example.vinstallment.Service.ThreeDaysAfterService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PolicyManager {
    NotificationReminder notificationReminder;
    DevicePolicyManager dpm;
    ComponentName mAdminComponentName;
    TextToSpeech textToSpeech;
    Handler handler;
    Runnable runnable;
    Context context;

    public PolicyManager(Context context) {
        this.context = context;
        notificationReminder = new NotificationReminder(context);
        mAdminComponentName = new ComponentName(context, DeviceAdminReceiver.class);
        dpm = context.getSystemService(DevicePolicyManager.class);
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(new Locale("id", "ID"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.i("TTS", "Language is not supported");
                    }
                }
            }
        }, "com.google.android.tts");
    }

    public boolean isDeviceOwner() {
        return dpm.isAdminActive(mAdminComponentName) || dpm.isDeviceOwnerApp("com.example.vinstallment");
    }

    public void reminderInstallment() throws RemoteException {
        if (!isDeviceOwner()) return;
        notificationReminder.reminderNotify();
        Log.i(TAG, "Package Name : " + Arrays.toString(getAllApplication()));
        Log.i(TAG, "Package Name : " + Arrays.toString(getCameraApp()));
    }


    public void oneDayAfter() throws RemoteException {
        if (!isDeviceOwner()) return;
        Intent intent = new Intent(context, OneDayAfterService.class);
        if (!isServiceRunning(ThreeDaysAfterService.class)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startActivity(intent);
            }
        }
        EventBus.getDefault().post(new UpdateUIEvent(0, true));
    }


    public void twoDayAfter() throws RemoteException {
        if (!isDeviceOwner()) return;
        setSuspendApp(getCameraApp(), true);
        setShortSupportMessage("Silahkan lakukan pembayaran cicilan anda");
        setLongSupportMessage("Mohon maaf anda tidak dapat mengakses aplikasi ini, jika anda ingin mengakses aplikasi ini silahkan bayar cicilan anda");
        EventBus.getDefault().post(new UpdateUIEvent(1, true));
    }


    public void threeDayAfter() throws RemoteException {
        if (!isDeviceOwner()) return;
        Intent intent = new Intent(context, ThreeDaysAfterService.class);
        if (!isServiceRunning(ThreeDaysAfterService.class)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }
//        handler = new Handler();
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                // kode yang dijalankan setiap kali proses berjalan
////                mediaPlayer.start();
//                speak("Silahkan bayar cicilan anda");
//                Log.i("Handler", "Process is running...");
//                handler.postDelayed(this, 10000); // mengulang proses setiap 10 detik
//            }
//        };
//        handler.post(runnable);
        setSuspendApp(getAllApplication(), true);
        setShortSupportMessage("Silahkan lakukan pembayaran cicilan anda");
        setLongSupportMessage("Mohon maaf anda tidak dapat mengakses aplikasi ini, jika anda ingin mengakses aplikasi ini silahkan bayar cicilan anda");
        EventBus.getDefault().post(new UpdateUIEvent(2, true));
    }

    public void clearPunishment() throws RemoteException {
        if (!isDeviceOwner()) return;
        setSuspendApp(getAllApplication(), false);
        setSuspendApp(getCameraApp(), false);
        if (isServiceRunning(OneDayAfterService.class)) {
            Intent oneDayAfterIntent = new Intent(context, OneDayAfterService.class);
            context.stopService(oneDayAfterIntent);
        }
        if (isServiceRunning(ThreeDaysAfterService.class)) {
            Intent threeDayAfterIntent = new Intent(context, ThreeDaysAfterService.class);
            context.stopService(threeDayAfterIntent);
        }
        EventBus.getDefault().post(new UpdateUIEvent(0, false));
        EventBus.getDefault().post(new UpdateUIEvent(1, false));
        EventBus.getDefault().post(new UpdateUIEvent(2, false));
    }

    public void setLunas() {
        EventBus.getDefault().post(new SetLunasEvent());
    }

    private String[] getAllApplication() {

        final Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> launcherIntentResolvers = context.getPackageManager()
                .queryIntentActivities(launcherIntent, 0);
        Collections.sort(launcherIntentResolvers,
                new ResolveInfo.DisplayNameComparator(context.getPackageManager()));
        List<String> packageNames = new ArrayList<>();
        for (ResolveInfo appInfo : launcherIntentResolvers) {
            packageNames.add(appInfo.activityInfo.packageName);
        }
        packageNames.remove("com.example.vinstallment");
        packageNames.remove("com.samsung.android.messaging");
        packageNames.remove("com.google.android.apps.messaging");
        packageNames.remove("com.whatsapp");
        packageNames.remove("com.android.settings");
        return packageNames.toArray(new String[0]);
    }

    private void setSuspendApp(String[] packages, boolean enabled) {
        dpm.setPackagesSuspended(mAdminComponentName, packages, enabled);
    }

    private String[] getCameraApp() {
        final PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApplications =
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<String> packageNames = new ArrayList<>();
        for (ApplicationInfo appInfo : installedApplications) {
            if (appInfo.packageName.contains("app.camera")) {
                packageNames.add(appInfo.packageName);
            }
        }
        return packageNames.toArray(new String[0]);
    }

    public void startSpeak() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // kode yang dijalankan setiap kali proses berjalan
//                mediaPlayer.start();
                speak("Silahkan bayar cicilan anda");
                Log.i("Handler", "Process is running...");
                handler.postDelayed(this, 10000); // mengulang proses setiap 10 detik
            }
        };
        handler.post(runnable);
    }

    public void stopSpeak() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            handler.removeCallbacks(runnable);
        }
    }

    private void speak(String message) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, "Reminder");
    }

    private void setShortSupportMessage(String message) {
        dpm.setShortSupportMessage(mAdminComponentName, message);
    }

    private void setLongSupportMessage(String message) {
        dpm.setLongSupportMessage(mAdminComponentName, message);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
