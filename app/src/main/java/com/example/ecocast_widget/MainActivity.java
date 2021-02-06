package com.example.ecocast_widget;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecocast_widget.models.model.JsonResultModel;
import com.example.ecocast_widget.models.model.VentilationTimeModel;
import com.example.ecocast_widget.models.network.JsonResultCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    final String TAG_C = "RetrofitService - ";
    VentilationTimeModel ventilationTimeModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ventilationTimeModel = RetrofitHttpConnection.getInstance().request(ventilationTime, "ventilationtime", null);
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