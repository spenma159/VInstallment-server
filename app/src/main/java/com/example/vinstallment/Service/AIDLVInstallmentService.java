package com.example.vinstallment.Service;

import static android.content.ContentValues.TAG;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.vinstallment.IVInstallmentInterface;
import com.example.vinstallment.Notification.NotificationReminder;
import com.example.vinstallment.Receiver.DeviceAdminReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AIDLVInstallmentService extends Service {
    NotificationReminder notificationReminder;
    DevicePolicyManager dpm;
    ComponentName mAdminComponentName;
    TextToSpeech textToSpeech;
    Handler handler;
    Runnable runnable;

    public AIDLVInstallmentService(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationReminder = new NotificationReminder(this);
        mAdminComponentName = new ComponentName(getApplicationContext(), DeviceAdminReceiver.class);
        dpm = getSystemService(DevicePolicyManager.class);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(new Locale("id", "ID"));
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.i("TTS", "Language is not supported");
                    }
                }
            }
        },"com.google.android.tts");
        Log.i(TAG, "onCreate: HERE");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: HERE");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final IVInstallmentInterface.Stub binder = new IVInstallmentInterface.Stub() {
        @Override
        public void reminderInstallment() throws RemoteException {
            notificationReminder.reminderNotify();
            Log.i(TAG, "Package Name : " + Arrays.toString(getAllApplication()));
            Log.i(TAG, "Package Name : " + Arrays.toString(getCameraApp()));
        }

        @Override
        public void oneDayAfter() throws RemoteException {
            startForeground(2, notificationReminder.oneDayAfterNotify());
        }

        @Override
        public void twoDayAfter() throws RemoteException {
            setSuspendApp(getCameraApp(), true);
            setShortSupportMessage("Silahkan lakukan pembayaran cicilan anda");
            setLongSupportMessage("Mohon maaf anda tidak dapat mengakses aplikasi ini, jika anda ingin mengakses aplikasi ini silahkan bayar cicilan anda");
        }

        @Override
        public void threeDayAfter() throws RemoteException {
//            Intent intent = new Intent(AIDLVInstallmentService.this, ThreeDaysAfterService.class);
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                startForegroundService(intent);
//            }else{
//                startService(intent);
//            }
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
            setSuspendApp(getAllApplication(), true);
            setShortSupportMessage("Silahkan lakukan pembayaran cicilan anda");
            setLongSupportMessage("Mohon maaf anda tidak dapat mengakses aplikasi ini, jika anda ingin mengakses aplikasi ini silahkan bayar cicilan anda");
        }
        @Override
        public void clearPunishment() throws RemoteException {
//            Intent threeDayAfterIntent = new Intent(AIDLVInstallmentService.this, ThreeDaysAfterService.class);
            setSuspendApp(getAllApplication(), false);
            setSuspendApp(getCameraApp(), false);
//            stopService(threeDayAfterIntent);
            stopForeground(true);
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
                handler.removeCallbacks(runnable);
            }
        }
    };
    public void reminderInstallment() throws RemoteException {
        notificationReminder.reminderNotify();
        Log.i(TAG, "Package Name : " + Arrays.toString(getAllApplication()));
        Log.i(TAG, "Package Name : " + Arrays.toString(getCameraApp()));
    }
    private String[] getAllApplication(){
//        final PackageManager packageManager = getPackageManager();
//        List<ApplicationInfo> installedApplications =
//                packageManager.getInstalledApplications(0);
//        List<String> packageNames = new ArrayList<>();
//        for (ApplicationInfo appInfo : installedApplications) {
//
//            packageNames.add(appInfo.packageName);
//        }
//
//        packageNames.remove("com.example.vinstallment");
//        packageNames.remove("com.samsung.android.messaging");
//        packageNames.remove("com.google.android.apps.messaging");
//        packageNames.remove("com.whatsapp");
//        packageNames.remove("com.android.settings");
//        packageNames.remove("android.speech.tts");
////        packageNames.remove("android.speech");
////        packageNames.remove("android.media");
////        packageNames.remove("android.media.audiofx");
//        packageNames.remove("com.samsung.SMT");

        final Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> launcherIntentResolvers = getPackageManager()
                .queryIntentActivities(launcherIntent, 0);
        Collections.sort(launcherIntentResolvers,
                new ResolveInfo.DisplayNameComparator(getPackageManager()));
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
    private void setSuspendApp(String[] packages, boolean enabled){
        dpm.setPackagesSuspended(mAdminComponentName, packages, enabled);
    }
    private String[] getCameraApp(){
        final PackageManager packageManager = getPackageManager();
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
    private void speak(String message){
        textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, "Reminder");
    }
    private void setShortSupportMessage(String message){
        dpm.setShortSupportMessage(mAdminComponentName, message);
    }
    private void setLongSupportMessage(String message){
        dpm.setLongSupportMessage(mAdminComponentName, message);
    }

}
