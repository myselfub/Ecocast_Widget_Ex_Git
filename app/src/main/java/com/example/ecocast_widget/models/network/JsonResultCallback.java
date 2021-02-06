package com.example.ecocast_widget.models.network;

import com.example.ecocast_widget.models.model.JsonResultModel;

import java.util.List;

import retrofit2.Callback;

public interface JsonResultCallback<T> extends Callback<JsonResultModel<T>> {

    List<T> getModelList();

    T getModel();
}