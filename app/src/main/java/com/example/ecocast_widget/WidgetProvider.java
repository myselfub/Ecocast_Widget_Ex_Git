package com.example.ecocast_widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.ecocast_widget.models.model.JsonResultModel;
import com.example.ecocast_widget.models.model.VentilationTimeModel;
import com.example.ecocast_widget.models.network.JsonResultCallback;
import com.example.ecocast_widget.models.network.RetrofitHttpConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class WidgetProvider extends AppWidgetProvider {
    final String TAG_C = "RetrofitService - ";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
//            Intent intent = new Intent(context, MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RetrofitHttpConnection.getInstance().request(ventilationTime, "ventilationtime", null);


            // Get the layout for the App Widget and attach an on-click listener to the button
            String title_text = "외출해도\n좋아요";
            String grade_name = "좋음";
            int find_dust_value = 25;
            int ultra_find_dust_value = 10;
            int grade_color = Color.argb(255, 0, 0, 0);
            String announce = "서초구 서초동 (20.11.05 18:00 발표)";

            String find_dust = find_dust_value + " ㎍/m³";
            String ultra_find_dust = ultra_find_dust_value + " ㎍/m³";
            if (grade_name.equals("좋음")) {
                grade_color = Color.BLUE;
            } else if (grade_name.equals("보통")) {
                grade_color = Color.GREEN;
            } else if (grade_name.equals("나쁨")) {
                grade_color = Color.YELLOW;
            } else if (grade_name.equals("매우나쁨")) {
                grade_color = Color.RED;
                grade_name = "매우\n나쁨";
            }
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setTextViewText(R.id.tv_title_text, title_text);
            views.setTextViewText(R.id.tv_dust_grade_text, grade_name);
            views.setInt(R.id.iv_dust_grade_img, "setColorFilter", grade_color);
            views.setTextColor(R.id.tv_dust_grade_text, grade_color);
            views.setTextViewText(R.id.tv_fine_dust_value, find_dust);
            views.setTextViewText(R.id.tv_ultra_fine_dust_value, ultra_find_dust);
            views.setTextViewText(R.id.tv_announce, announce);
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    JsonResultCallback ventilationTime = new JsonResultCallback<VentilationTimeModel>() {

        String Tag = TAG_C + "ventilationTime()";
        VentilationTimeModel ventilationTimeModel = null;
        ArrayList<VentilationTimeModel> ventilationTimeModelList = null;

        @Override
        public void onResponse(Call<JsonResultModel<VentilationTimeModel>> call, Response<JsonResultModel<VentilationTimeModel>> response) {
            if (response.isSuccessful()) {
                JsonResultModel<VentilationTimeModel> jsonResultModel = response.body();
                if (jsonResultModel.getStatus().equals("Success")) {
                    ArrayList<VentilationTimeModel> ventilationTimeModelList_ = (ArrayList<VentilationTimeModel>) jsonResultModel.getData();
                    if (!ventilationTimeModelList_.isEmpty()) {
                        ventilationTimeModelList = ventilationTimeModelList_;
                        ventilationTimeModel = ventilationTimeModelList.get(0);
                        Log.d("1111", ventilationTimeModel.toString());
                    } else {
                        ventilationTimeModelList = new ArrayList<>();
                        ventilationTimeModel = new VentilationTimeModel();
                    }
                } else {
                    Log.e("TAG", Tag + " - isNotSuccessful");
                }
            } else {
                Log.e("TAG", Tag + " - isNotSuccessful");
            }
        }

        @Override
        public void onFailure(Call<JsonResultModel<VentilationTimeModel>> call, Throwable t) {
            Log.e("TAG", Tag + " - onFailure");
        }

        @Override
        public List<VentilationTimeModel> getModelList() {
            return ventilationTimeModelList;
        }

        @Override
        public VentilationTimeModel getModel() {
            return ventilationTimeModel;
        }
    };
}
