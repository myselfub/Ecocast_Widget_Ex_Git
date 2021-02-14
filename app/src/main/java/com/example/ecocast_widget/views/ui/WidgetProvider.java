package com.example.ecocast_widget.views.ui;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.ecocast_widget.MainActivity;
import com.example.ecocast_widget.R;
import com.example.ecocast_widget.models.model.JsonResultModel;
import com.example.ecocast_widget.models.model.VentilationTimeModel;
import com.example.ecocast_widget.models.network.rest.RestClientAPI;
import com.example.ecocast_widget.models.network.rest.RestClientHelper;
import com.example.ecocast_widget.models.repository.LocalRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WidgetProvider extends AppWidgetProvider implements LocationListener {
    private static final String TAG = WidgetProvider.class.getSimpleName();
    private static int repetition = 0;
    private static final String ACTION_REFRESH = "android.appwidget.REFRESH";
    private static final String ACTION_OPEN_MAIN_ACTIVITY = "android.appwidget.OPEN_MAIN_ACTIVITY";
    private static final String ACTION_OPEN_CONFIG = "android.appwidget.OPEN_CONFIG";
    private static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
    private final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // meter
    private final long MIN_TIME_BW_UPDATES = 1000 * 5; // millisecond
    private CompositeDisposable disposable;
    private LocationManager locationManager;
    private RemoteViews views;
    private LocalRepository localRepository;
    private boolean isLocationUpdated = false;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        if (localRepository == null) {
            localRepository = new LocalRepository(context);
        }
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (localRepository == null) {
            localRepository = new LocalRepository(context);
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName appWidgetId = new ComponentName(context, WidgetProvider.class);
        String action = intent.getAction();
        widgetScreenUpdate(context, appWidgetManager);
        if (action != null && action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            widgetUpdate(context, appWidgetManager, appWidgetId);
        } else if (action != null && action.equals(ACTION_REFRESH)) {
            if (repetition > 0) {
                Toast.makeText(context, repetition + "초 후에 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            repetition = (int) (MIN_TIME_BW_UPDATES / 1000);
            widgetUpdate(context, appWidgetManager, appWidgetId);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    repetition--;
                    if (repetition <= 0) {
                        this.cancel();
                    }
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 0, 1000);
        } else if (action != null && action.equals(ACTION_OPEN_MAIN_ACTIVITY)) {
            Intent main_intent = new Intent(context, MainActivity.class);
            context.startActivity(main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (action != null && action.equals(ACTION_OPEN_CONFIG)) {
            Intent config_intent = new Intent();
            config_intent.setAction(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            config_intent.setData(uri);
            context.startActivity(config_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (action != null && action.equals(ACTION_SCREEN_ON)) {
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void widgetScreenUpdate(Context context, AppWidgetManager appWidgetManager) {
        views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        int appWidgetId;
        for (int widgetId : appWidgetIds) {
            appWidgetId = widgetId;
            Intent refreshIntent = new Intent(context, this.getClass());
            refreshIntent.setAction(ACTION_REFRESH);
            refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent refreshPending = PendingIntent.getBroadcast(context, appWidgetId, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.btn_app_widget_redo, refreshPending);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            Intent mainIntent = new Intent(context, this.getClass());
            mainIntent.setAction(ACTION_OPEN_MAIN_ACTIVITY);
            mainIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent mainPending = PendingIntent.getBroadcast(context, appWidgetId, mainIntent, 0);
            views.setOnClickPendingIntent(R.id.ll_app_widget_main_background, mainPending);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_SCREEN_ON);
            context.getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public void widgetUpdate(Context context, AppWidgetManager appWidgetManager, ComponentName appWidgetId) {
        if (localRepository == null) {
            localRepository = new LocalRepository(context);
        }
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(context, "네트워크를 켜주세요.", Toast.LENGTH_LONG).show();
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            Intent main_intent = new Intent(context, MainActivity.class);
            context.startActivity(main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean checkGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!checkGPS) {
            Toast.makeText(context, "GPS를 켜야합니다.", Toast.LENGTH_LONG).show();
            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting() || !connectivityManager.isDefaultNetworkActive()) {
            boolean background = false;
            if (Build.VERSION.SDK_INT >= 24) {
                if (connectivityManager.getRestrictBackgroundStatus() == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED) {
                    background = true;
                }
            } else {
                if (networkInfo != null && networkInfo.getDetailedState().equals(NetworkInfo.DetailedState.BLOCKED)) {
                    background = true;
                }
            }
            if (background) {
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidgetId);
                for (int widgetId : appWidgetIds) {
                    views = new RemoteViews(context.getPackageName(), R.layout.app_widget_network);
                    Intent configIntent = new Intent(context, this.getClass());
                    configIntent.setAction(ACTION_OPEN_CONFIG);
                    configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    PendingIntent configPending = PendingIntent.getBroadcast(context, widgetId, configIntent, 0);
                    views.setOnClickPendingIntent(R.id.ll_app_widget_config, configPending);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                    Intent dialog_intent = new Intent(context, WidgetDialog.class);
                    context.startActivity(dialog_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    repetition = 0;
                }
            } else {
                Toast.makeText(context, R.string.error_network, Toast.LENGTH_LONG).show();
            }
            return;
        }

        double[] latlon = getMyLastLocation(context);
        if (latlon == null) {
            return;
        }
        double lat = 0.0;
        double lon = 0.0;
        lat = latlon[0];
        lon = latlon[1];

        if (disposable == null || disposable.isDisposed()) {
//            disposable.clear();
            disposable = new CompositeDisposable();
        }

        RestClientAPI restClientAPI = RestClientHelper.getInstance().getRestClientAPI(null);
        disposable.add(restClientAPI.ventilationtime()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> loadData(context, appWidgetManager, appWidgetId, result),
                        e -> showError(e), () -> completed()));
    }

    public void loadData(Context context, AppWidgetManager appWidgetManager, ComponentName
            appWidgetId, JsonResultModel<VentilationTimeModel> jsonResultModel) {
        if (localRepository == null) {
            localRepository = new LocalRepository(context);
        }
        VentilationTimeModel ventilationTimeModel = jsonResultModel.getData().get(0);
        double find_dust_value = ventilationTimeModel.getPm_10_value();
        double ultra_find_dust_value = ventilationTimeModel.getPm_25_value();
        String grade_name = "좋음";
        String title_text = "분만 외출하세요.";
        double find_dust_grade = ventilationTimeModel.getPm_10_grade_num();
        double ultra_find_dust_grade = ventilationTimeModel.getPm_25_grade_num();
        int grade = 0;
        if (find_dust_grade >= ultra_find_dust_grade) {
            grade = (int) Math.round(find_dust_grade);
        } else {
            grade = (int) Math.round(ultra_find_dust_grade);
        }
        if (grade <= 1) {
        } else if (grade <= 2) {
            grade_name = "보통";
        } else if (grade <= 3) {
            grade_name = "나쁨";
        } else {
            grade_name = "매우나쁨";
        }
        int grade_color = R.drawable.round_corner_all_blue;
        int grade_icon = R.drawable.app_widget_level_good_icon;
        SimpleDateFormat sdf = new SimpleDateFormat(" (yy-MM-dd HH:mm 발표)");
        String announceTime = ventilationTimeModel.getTime();
        String announce = ventilationTimeModel.getCity_name() + " " + ventilationTimeModel.getStation_name() + announceTime;
        String find_dust = find_dust_value + " ㎍/m³";
        String ultra_find_dust = ultra_find_dust_value + " ㎍/m³";
        switch (grade_name) {
            case "좋음":
                break;
            case "보통":
                grade_color = R.drawable.round_corner_all_green;
                grade_icon = R.drawable.app_widget_level_normal_icon;
                break;
            case "나쁨":
                grade_color = R.drawable.round_corner_all_yellow;
                grade_icon = R.drawable.app_widget_level_bad_icon;
                break;
            case "매우나쁨":
                grade_color = R.drawable.round_corner_all_red;
                grade_icon = R.drawable.app_widget_level_awful_icon;
                break;
        }

        title_text = ventilationTimeModel.getMessage();

        views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.tv_app_widget_title, title_text);
        views.setTextViewText(R.id.tv_app_widget_dust_grade, grade_name);
        views.setImageViewResource(R.id.iv_app_widget_dust_grade, grade_icon);
        views.setInt(R.id.widget_background, "setBackgroundResource", grade_color);
//        views.setInt(R.id.iv_dust_grade_img, "setColorFilter", grade_color);
//        views.setTextColor(R.id.tv_dust_grade_text, grade_color);
//        views.setTextViewText(R.id.tv_fine_dust_value, find_dust);
//        views.setTextViewText(R.id.tv_ultra_fine_dust_value, ultra_find_dust);
        views.setTextViewText(R.id.tv_app_widget_announce, announce);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public void showError(Throwable t) {
        Log.e(TAG, t.toString());
    }

    public void completed() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String latlon = location.getLatitude() + "," + location.getLongitude();
        localRepository.setLatLon(latlon);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WidgetProvider.this.onReceive(context, intent);
        }
    };

    private double[] getMyLastLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            Intent main_intent = new Intent(context, MainActivity.class);
            context.startActivity(main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return null;
        }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean checkNetGPS = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!checkGPS || !checkNetGPS) {
            Toast.makeText(context, "GPS를 켜야합니다.", Toast.LENGTH_LONG).show();
            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); // REQUEST_CODE_TURN_ON_GPS
            return null;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        isLocationUpdated = false;
//        if (localRepository == null) {
//            if (location == null || location.getTime() + MIN_TIME_BW_UPDATES < Calendar.getInstance().getTimeInMillis()) {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                if (location == null) {
//                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                    if (location == null) {
//                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                    }
//                }
//            }
//        }

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setNumUpdates(1); // 한 번만 가져옴
//
//        LocationCallback locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                isLocationUpdated = true;
//                List<Location> locationList = locationResult.getLocations();
//                if (locationList != null && locationList.size() > 0) {
//                    Location location = locationList.get(locationList.size() - 1);
//                    String latlon = location.getLatitude() + "," + location.getLongitude();
//                    localRepository.setLastMyLocation(latlon);
//                }
//            }
//        };
//
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//        // timeout.
//        new Handler(Looper.myLooper()).postDelayed(() -> {
//            if (!isLocationUpdated) {
//                Toast.makeText(context, R.string.error_detect_location, Toast.LENGTH_LONG).show();
//                fusedLocationClient.removeLocationUpdates(locationCallback);
//            }
//        }, 3000);
//        String[] latlon = localRepository.getLastMyLocation().split(",");
//        double[] latlonD = new double[]{Double.valueOf(latlon[0]), Double.valueOf(latlon[1])};

        double[] latlonD = null;
        if (location == null || location.getTime() + MIN_TIME_BW_UPDATES < Calendar.getInstance().getTimeInMillis()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this, Looper.myLooper());
            try {
                String[] latlon = localRepository.getLatLon();
                latlonD = new double[]{Double.valueOf(latlon[0]), Double.valueOf(latlon[1])};
            } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                Toast.makeText(context, "일정시간 후에 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                repetition = 0;
                return null;
            }
        } else {
            latlonD = new double[]{location.getLatitude(), location.getLongitude()};
        }
        return latlonD;
    }

}
