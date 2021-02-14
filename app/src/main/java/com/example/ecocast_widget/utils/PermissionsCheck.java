package com.example.ecocast_widget.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

public class PermissionsCheck {
    private static final String TAG = PermissionsCheck.class.getSimpleName();
    private Activity activity;
    private final int PERMISSIONS_REQUEST_CODE;
    private final String[] PERMISSIONS;

    private PermissionsCheck() {
        PERMISSIONS_REQUEST_CODE = 1001;
        PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    }

    public PermissionsCheck(Activity activity) {
        this.activity = activity;
        PERMISSIONS_REQUEST_CODE = 1001;
        PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    }

    public PermissionsCheck(Activity activity, int permissions_request_code, String[] permissions) {
        this.activity = activity;
        PERMISSIONS_REQUEST_CODE = permissions_request_code;
        if (permissions == null) {
            PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        } else {
            PERMISSIONS = permissions;
        }
    }

    public boolean allPermissions() {
        boolean hasPermissions = true;
        for (String permission : PERMISSIONS) {
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                hasPermissions = false;
                break;
            }
        }
        return hasPermissions;
    }

    public void showDialogForPermission(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                activity.finish();
            }
        });
        builder.create().show();
    }

    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean grantBoolean = true;
        switch (requestCode) {
            case 1001:
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            grantBoolean = false;
                            break;
                        }
                    }
                    if (!grantBoolean) {
                        showDialogForPermission("앱을 실행하려면 권한이 필요합니다. ");
                    }
                }
                break;
        }
        return grantBoolean;
    }

    public int getPERMISSIONS_REQUEST_CODE() {
        return PERMISSIONS_REQUEST_CODE;
    }

    public final String[] getPERMISSIONS() {
        return PERMISSIONS;
    }
}
