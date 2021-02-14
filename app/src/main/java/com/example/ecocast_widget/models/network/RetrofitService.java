package com.example.ecocast_widget.models.network;

import com.example.ecocast_widget.models.model.JsonResultModel;
import com.example.ecocast_widget.models.model.VentilationTimeModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitService {
    @GET("air/ventilation/time")
    Call<JsonResultModel<VentilationTimeModel>> ventilationtime();
}
