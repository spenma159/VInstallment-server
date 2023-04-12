package com.example.vinstallment.Receiver;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import com.example.vinstallment.IVInstallmentInterface;
import com.example.vinstallment.Policy.PolicyManager;
import com.example.vinstallment.Service.AIDLVInstallmentService;

public class OneDayAfterReceiver extends BroadcastReceiver {
    PolicyManager policyManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        policyManager = new PolicyManager(context);
        try {
            policyManager.oneDayAfter();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
