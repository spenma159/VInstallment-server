package com.example.vinstallment.Activity;

import static android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_MODE;
import static android.app.admin.DevicePolicyManager.PROVISIONING_MODE_FULLY_MANAGED_DEVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.vinstallment.R;

import org.w3c.dom.Text;

public class GetProvisioningModeActivity extends Activity {
    Button btnAccept;
    TextView textCompanyName;
    private PersistableBundle persistableBundle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_provisioning_mode);
        btnAccept = findViewById(R.id.btn_accept);
        textCompanyName = findViewById(R.id.text_company_name);
        persistableBundle = getIntent().getParcelableExtra(DevicePolicyManager.EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE);
        textCompanyName.setText(persistableBundle.get("company").toString());

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    onDoButtonClick();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void onDoButtonClick() {
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_PROVISIONING_MODE, PROVISIONING_MODE_FULLY_MANAGED_DEVICE);
        finishWithIntent(intent);
    }

    private void finishWithIntent(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }


}
