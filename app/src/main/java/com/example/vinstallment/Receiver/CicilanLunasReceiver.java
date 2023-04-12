package com.example.vinstallment.Receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import com.example.vinstallment.Policy.PolicyManager;

public class CicilanLunasReceiver extends BroadcastReceiver {
    PolicyManager policyManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        policyManager = new PolicyManager(context);
        policyManager.setLunas();
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        dpm.clearDeviceOwnerApp(context.getPackageName());
    }
}
