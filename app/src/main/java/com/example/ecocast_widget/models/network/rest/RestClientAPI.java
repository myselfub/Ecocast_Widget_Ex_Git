package com.example.ecocast_widget.models.network.rest;

import com.example.ecocast_widget.models.model.JsonResultModel;
import com.example.ecocast_widget.models.model.VentilationTimeModel;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface RestClientAPI {
//
//    @GET("/Prod/ranking/latest")
//    Observable<TopNewsResultREPO> getTopNews();
//
//    @GET("/Prod/news")
//    Observable<FeedResultREPO> getNewFeeds(@Query("keyword") String keyword);
//
//    @GET("/Prod/news")
//    Observable<FeedResultREPO> getNewFeeds(
//            @Query("keyword") String keyword,
//            @Query("last") String last);
//
//    @GET("/Prod/keywords/suggest")
//    Observable<SuggestKeywordResultREPO> getSuggestKeywords(@Query("keyword") String keyword);
//
//    @POST("/Prod/keywords")
//    Observable<KeywordParam> addKeywords(
//            @Body KeywordParam param
//    );

    @GET("air/ventilation/time")
//    Observable<TestREPO> test();
    Observable<JsonResultModel<VentilationTimeModel>> ventilationtime();


}