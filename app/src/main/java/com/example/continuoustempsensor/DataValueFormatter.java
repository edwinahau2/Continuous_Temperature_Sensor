package com.example.continuoustempsensor;

import android.annotation.SuppressLint;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DataValueFormatter extends ValueFormatter {


    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("a");

    @Override
    public String getFormattedValue(float value) {
        String test = Float.toString(value);
        String test2 = test.substring(0, test.indexOf("."));
        String test1 = test2.replace(",", "");

        int o = test1.length();
        if (o == 6) {
            String newTest = test1.substring(0, 2) + ":" + test1.substring(3, 5) + ":" + test1.substring(4, 6);
            String meridian = sdf.format(Calendar.getInstance().getTime());
            return newTest + " " + meridian;
        } else {
            String newTest = test1.substring(0,1) + ":" + test1.substring(2,4) + ":" + test1.substring(3, 5);
            String meridian = sdf.format(Calendar.getInstance().getTime());
            return newTest + " " + meridian;
        }
    }

}
