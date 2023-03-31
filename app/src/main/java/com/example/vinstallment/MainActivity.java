package com.example.vinstallment;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.vinstallment.Service.AIDLVInstallmentService;

public class MainActivity extends AppCompatActivity {
    ServiceConnection serviceCon;
    IVInstallmentInterface ivInstallmentService;
    Button btnReminder, btnOneDayAfter, btnTwoDayAfter, btnThreeDayAfter, btnClearPunishment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiateService();
        btnOnClick();
    }

    private void initiateService(){
        serviceCon = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                ivInstallmentService = IVInstallmentInterface.Stub.asInterface(iBinder);
                Log.i(TAG, "onServiceConnected: Connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i(TAG, "onServiceConnected: Disconnected");
            }
        };
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.vinstallment","com.example.vinstallment.Service.AIDLVInstallmentService"));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(intent);
        }else{
            startService(intent);
        }
        bindService(intent,serviceCon,BIND_AUTO_CREATE);
    }

    private void btnOnClick(){
        btnReminder = findViewById(R.id.btn_notify);
        btnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ivInstallmentService.reminderInstallment();
                    Log.i(TAG, "onClick: ");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnOneDayAfter = findViewById(R.id.btn_one_day_after);
        btnOneDayAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ivInstallmentService.oneDayAfter();
                    Log.i(TAG, "onClick: ");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnTwoDayAfter = findViewById(R.id.btn_two_day_after);
        btnTwoDayAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ivInstallmentService.twoDayAfter();
                    Log.i(TAG, "onClick: ");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        btnThreeDayAfter = findViewById(R.id.btn_three_day_after);
        btnThreeDayAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ivInstallmentService.threeDayAfter();
                    Log.i(TAG, "onClick: ");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        btnClearPunishment = findViewById(R.id.btn_clear_punishment);
        btnClearPunishment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ivInstallmentService.clearPunishment();
                    Log.i(TAG, "onClick: ");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}