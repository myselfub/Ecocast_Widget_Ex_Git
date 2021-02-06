package com.example.ecocast_widget.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ecocast_widget.R;
import com.example.ecocast_widget.models.model.VentilationTimeModel;

import java.util.ArrayList;

public class VentilationTimeAdapter extends BaseAdapter {
    private ArrayList<VentilationTimeModel> ventilation_list = null;
    private LayoutInflater inflater;

    public VentilationTimeAdapter(ArrayList<VentilationTimeModel> ventilation_list, LayoutInflater inflater) {
        this.ventilation_list = ventilation_list;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return ventilation_list.size();
    }

    @Override
    public Object getItem(int position) {
        return ventilation_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.widget, parent, false);
        }

        TextView tvTitleText = convertView.findViewById(R.id.tv_title_text);
        TextView tvDustGradeText = convertView.findViewById(R.id.tv_dust_grade_text);
        TextView tvFineDustValue = convertView.findViewById(R.id.tv_fine_dust_value);
        TextView tvUltraFineDustValue = convertView.findViewById(R.id.tv_ultra_fine_dust_value);
        TextView tvAnnounceTime = convertView.findViewById(R.id.tv_announce);

        VentilationTimeModel ventilationTimeModel = ventilation_list.get(position);
        tvTitleText.setText(ventilationTimeModel.getMessage());
        tvDustGradeText.setText(ventilationTimeModel.getMessage());
        tvFineDustValue.setText(String.valueOf(ventilationTimeModel.getPm_10_value()));
        tvUltraFineDustValue.setText(String.valueOf(ventilationTimeModel.getMessage()));
        String announceTime = ventilationTimeModel.getTime();
        announceTime = announceTime.substring(2, announceTime.length() - 3).replace("-", ".");
        String announce = ventilationTimeModel.getCity_name() + " " + ventilationTimeModel.getStation_name() + "(" + announceTime + " 발표)";
        tvAnnounceTime.setText(announce);

        return convertView;
    }
}
