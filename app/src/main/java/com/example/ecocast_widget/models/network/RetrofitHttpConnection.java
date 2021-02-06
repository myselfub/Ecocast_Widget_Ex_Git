package com.example.ecocast_widget.models.network;

import com.example.ecocast_widget.models.model.JsonResultModel;
import com.example.ecocast_widget.models.model.VentilationTimeModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHttpConnection {
    private static RetrofitHttpConnection retrofitHttpConnection = new RetrofitHttpConnection();
    private RetrofitService retrofitService;
    private Gson gson;
    private Retrofit retrofit;
    private final String SERVER_URL = "http://10.0.2.2:8000/";

    private RetrofitHttpConnection() {
        if (gson == null) {
            gson = new GsonBuilder().setLenient().create();
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(SERVER_URL).addConverterFactory(GsonConverterFactory.create()).addConverterFactory(GsonConverterFactory.create(gson)).build();
        }

        if (retrofitService == null) {
            retrofitService = retrofit.create(RetrofitService.class);
        }

    }

    public static RetrofitHttpConnection getInstance() {
        return retrofitHttpConnection;
    }

    public RetrofitService getService() {
        return retrofitService;
    }

    public <T> List<T> requestList(JsonResultCallback callback, String methodName, HashMap<String, Object> params) {
        List<T> list = null;
        if (methodName.equals("ventilationtime")) {
            Call<JsonResultModel<VentilationTimeModel>> call = retrofitService.ventilationtime();
            call.enqueue(callback);
            list = callback.getModelList();
        }

        return list;
    }

    public <T> T request(JsonResultCallback callback, String methodName, HashMap<String, Object> params) {
        T model = null;
        if (methodName.equals("ventilationtime")) {
            Call<JsonResultModel<VentilationTimeModel>> call = retrofitService.ventilationtime();
            call.enqueue(callback);
            model = (T) callback.getModel();
        }

        return model;
    }

    public <T> List<T> conversion(String json, Class<T> clazz) {
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        List<T> result = gson.fromJson(reader, new TypeToken<List<T>>() {
        }.getType());
        return result;
    }
}
