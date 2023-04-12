package com.example.vinstallment.Activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vinstallment.Adapter.RecylerViewPunishmentItemAdapter;
import com.example.vinstallment.EventBus.SetLunasEvent;
import com.example.vinstallment.EventBus.UpdateUIEvent;
import com.example.vinstallment.Policy.PolicyManager;
import com.example.vinstallment.PunishmentItem;
import com.example.vinstallment.R;
import com.example.vinstallment.Receiver.DeviceAdminReceiver;
import com.example.vinstallment.Service.AIDLVInstallmentService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
//    ServiceConnection serviceCon;
//    IVInstallmentInterface ivInstallmentService;
//    Button btnReminder, btnOneDayAfter, btnTwoDayAfter, btnThreeDayAfter, btnClearPunishment;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    SharedPreferences sharedPreferences;
    private ArrayList<PunishmentItem> punishmentItems;
    TextView textStatusCicilan;
    Button btnUninstallApp;
    PolicyManager policyManager;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        policyManager = new PolicyManager(this);
        loadPunishmentData();
        recyclerView = findViewById(R.id.recycler_view_punishment);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new RecylerViewPunishmentItemAdapter(punishmentItems);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        textStatusCicilan = findViewById(R.id.text_status_cicilan);
        btnUninstallApp = findViewById(R.id.btn_uninstall_app);

        setStatusCicilan();
        if(!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);


//        initiateService();
//        btnOnClick();
    }

    private void setStatusCicilan(){
        if(policyManager.isDeviceOwner()){
            textStatusCicilan.setText("Status : Belum Lunas");
            btnUninstallApp.setEnabled(false);
        }else{
            textStatusCicilan.setText("Status : Sudah Lunas");
            btnUninstallApp.setEnabled(true);
            btnUninstallApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse("package:com.example.vinstallment"));
                    startActivity(intent);
                }
            });
        }
    }

    private void generatePunishment(){
        if(punishmentItems == null){
            punishmentItems = new ArrayList<>();
            punishmentItems.add(new PunishmentItem(1, "Hukuman terlambat bayar satu hari", "Hukuman ini akan memberikan notifikasi terhadap pengguna dan tidak bisa dihapus", false));
            punishmentItems.add(new PunishmentItem(2, "Hukuman terlambat bayar dua hari", "Hukuman ini akan menonaktifkan aplikasi kamera", false));
            punishmentItems.add(new PunishmentItem(3, "Hukuman terlambat bayar tiga hari", "Hukuman ini akan menonaktifkan semua aplikasi kecuali aplikasi komunikasi seperti whatsapp, telepon, sms. Device juga akan mengeluarkan suara setiap satu jam", false));
        }
    }

    private void savePunishmentData(){
        sharedPreferences = getSharedPreferences("punishment item", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(punishmentItems);
        editor.putString("punishment list", json);
        editor.apply();
    }

    private void loadPunishmentData(){
        sharedPreferences = getSharedPreferences("punishment item", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("punishment list", null);
        Type type = new TypeToken<ArrayList<PunishmentItem>>() {}.getType();
        punishmentItems = gson.fromJson(json, type);
        generatePunishment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateUIEvent event) {
        loadPunishmentData();
        String title = "";
        String des = "";
        switch (event.index) {
            case 0:
                title = "Hukuman terlambat bayar satu hari";
                des = "Hukuman ini akan memberikan notifikasi terhadap pengguna dan tidak bisa dihapus";
                break;
            case 1:
                title = "Hukuman terlambat bayar dua hari";
                des = "Hukuman ini akan menonaktifkan aplikasi kamera";
                break;
            case 2:
                title = "Hukuman terlambat bayar tiga hari";
                des = "Hukuman ini akan menonaktifkan semua aplikasi kecuali aplikasi komunikasi seperti whatsapp, telepon, sms. Device juga akan mengeluarkan suara setiap satu jam";
                break;
            default:
                Log.i(TAG, "updatePolicyUIEnabled: ERROR");
                break;
        }
        punishmentItems.set(event.index, new PunishmentItem(event.index + 1, title, des, event.enabled));
        savePunishmentData();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new RecylerViewPunishmentItemAdapter(punishmentItems);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyItemChanged(event.index, punishmentItems.get(event.index));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetLunasEvent event) {
        setStatusCicilan();
    }


//    private void initiateService(){
//        serviceCon = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//                ivInstallmentService = IVInstallmentInterface.Stub.asInterface(iBinder);
//                Log.i(TAG, "onServiceConnected: Connected");
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName componentName) {
//                Log.i(TAG, "onServiceConnected: Disconnected");
//            }
//        };
//        Intent intent = new Intent();
//        intent.setComponent(new ComponentName("com.example.vinstallment","com.example.vinstallment.Service.AIDLVInstallmentService"));
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            startForegroundService(intent);
//        }else{
//            startService(intent);
//        }
//        bindService(intent,serviceCon,BIND_AUTO_CREATE);
//    }
//
//    private void btnOnClick(){
//        btnReminder = findViewById(R.id.btn_notify);
//        btnReminder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    ivInstallmentService.reminderInstallment();
//                    Log.i(TAG, "onClick: ");
//                } catch (RemoteException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//
//        btnOneDayAfter = findViewById(R.id.btn_one_day_after);
//        btnOneDayAfter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    ivInstallmentService.oneDayAfter();
//                    Log.i(TAG, "onClick: ");
//                } catch (RemoteException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//
//        btnTwoDayAfter = findViewById(R.id.btn_two_day_after);
//        btnTwoDayAfter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    ivInstallmentService.twoDayAfter();
//                    Log.i(TAG, "onClick: ");
//                } catch (RemoteException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//        btnThreeDayAfter = findViewById(R.id.btn_three_day_after);
//        btnThreeDayAfter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    ivInstallmentService.threeDayAfter();
//                    Log.i(TAG, "onClick: ");
//                } catch (RemoteException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//        btnClearPunishment = findViewById(R.id.btn_clear_punishment);
//        btnClearPunishment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    ivInstallmentService.clearPunishment();
//                    Log.i(TAG, "onClick: ");
//                } catch (RemoteException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//    }

}