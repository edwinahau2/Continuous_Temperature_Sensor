package com.example.continuoustempsensor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.mikephil.charting.charts.LineChart;

public class todayPageAdapter extends PagerAdapter {

    Context context;
    View layoutScreen;
    LineChart mChart;

    public todayPageAdapter(Context context, LineChart lineChart) {
        this.context = context;
        this.mChart = lineChart;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutScreen = inflater.inflate(R.layout.daily_report, null);
        mChart = layoutScreen.findViewById(R.id.lineChart);
        container.addView(layoutScreen);
        return layoutScreen;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
