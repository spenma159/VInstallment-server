package com.example.vinstallment.Service;

import static android.content.ContentValues.TAG;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.vinstallment.R;

import java.util.Locale;

public class ThreeDaysAfterService extends Service {
    Handler handler;
    Runnable runnable;
//    MediaPlayer mediaPlayer;
    TextToSpeech textToSpeech;
    @Override
    public void onCreate() {
        super.onCreate();
//        mediaPlayer = MediaPlayer.create(this, R.raw.bayarcicilansound);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.i("TTS", "Language is not supported");
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private void speak(String message){
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "Reminder");
    }
}
