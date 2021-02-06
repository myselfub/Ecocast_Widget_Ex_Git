package com.example.ecocast_widget.models.model;

import java.util.List;

import lombok.Data;

@Data
public class JsonResultModel<T> {
    private String code;
    private String status;
    private String message;
    private int count;
    private List<T> data;
}
