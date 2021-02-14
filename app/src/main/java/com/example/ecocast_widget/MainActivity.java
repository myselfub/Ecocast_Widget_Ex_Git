package com.example.ecocast_widget;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ecocast_widget.utils.PermissionsCheck;

public class MainActivity extends AppCompatActivity {

    private PermissionsCheck permissionsCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionsCheck = new PermissionsCheck(this);

        if (!permissionsCheck.allPermissions()) {
            Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, permissionsCheck.getPERMISSIONS(), permissionsCheck.getPERMISSIONS_REQUEST_CODE());
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsCheck.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}