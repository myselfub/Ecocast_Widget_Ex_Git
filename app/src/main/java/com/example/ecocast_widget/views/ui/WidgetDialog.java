package com.example.ecocast_widget.views.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecocast_widget.R;

public class WidgetDialog extends AppCompatActivity {

    private Button appWidgetDialogYes;
    private Button appWidgetDialogNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("백그라운드 데이터 제한");
        setContentView(R.layout.app_widget_dialog);

        appWidgetDialogYes = findViewById(R.id.btn_app_widget_dialog_yes);
        appWidgetDialogNo = findViewById(R.id.btn_app_widget_dialog_no);
        appWidgetDialogYes.setOnClickListener(appWidgetDialogYesListener);
        appWidgetDialogNo.setOnClickListener(appWidgetDialogNoListener);
    }

    Button.OnClickListener appWidgetDialogYesListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    };

    Button.OnClickListener appWidgetDialogNoListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}