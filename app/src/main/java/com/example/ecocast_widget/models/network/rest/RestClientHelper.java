package com.example.ecocast_widget.models.network.rest;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sunua on 2017. 7. 15..
 */


public class RestClientHelper {

    private Map<String, RestClientAPI> restClientAPIHashMap;
    private OkHttpClient okHttpClient;

    private RestClientHelper() {
    }

    private final String SERVER_URL = "http://10.0.2.2:8000/";

    private static class Singleton {
        private static final RestClientHelper instance = new RestClientHelper();
    }

    public static RestClientHelper getInstance() {
        return Singleton.instance;
    }

    public RestClientAPI getRestClientAPI(String url) {
        if (restClientAPIHashMap == null) {
            restClientAPIHashMap = new HashMap<>();
        }

        if (url == null) {
            url = SERVER_URL;
        }
        if (restClientAPIHashMap.containsKey(url)) {
            return restClientAPIHashMap.get(url);
        } else {
            RestClientAPI restClientAPI = createRestClientAPI(url);
            restClientAPIHashMap.put(url, restClientAPI);

            return restClientAPI;
        }
    }

    private OkHttpClient getOkHttpClient() {
        if (okHttpClient == null)
            okHttpClient = createOkHttpClient();
        return okHttpClient;
    }

    private OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        return builder.build();
    }

    private RestClientAPI createRestClientAPI(String url) {
        RestClientAPI restClientAPI = new Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .build().create(RestClientAPI.class);

        return restClientAPI;
    }
}