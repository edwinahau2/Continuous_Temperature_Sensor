package com.example.continuoustempsensor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Random;

public class fragment_tab1 extends Fragment {
    private TextView text;
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private ArrayList<String> al;
    private ArrayAdapter<String> arrayAdapter;
    private int i;
    SwipeFlingAdapterView flingContainer;
    private TextView counter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_layout, container, false);
        text = view.findViewById(R.id.text);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            text.setText(bundle.getString("temperature"));
        }
        GraphView graph = view.findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMinY(0);
        viewport.setMaxY(10);
        viewport.setScrollable(true);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(true);
//        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
//        graph.getGridLabelRenderer().setVerticalAxisTitle("Temperature");
        flingContainer = view.findViewById(R.id.frame);
        counter = view.findViewById(R.id.counter);
        al = new ArrayList<>();
        al.add("MY");
        al.add("name");
        al.add("is");
        al.add("Aryan");
        al.add("Agarwal");
        al.add("Welcome");
        al.add("to");
        al.add("hell");
        final int[] number = {al.size()};
        counter.setText(String.valueOf(number[0]));
        arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.item, R.id.helloText, al);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object o) {
                number[0]--;
                counter.setText(String.valueOf(number[0]));
            }

            @Override
            public void onRightCardExit(Object o) {
                number[0]--;
                counter.setText(String.valueOf(number[0]));
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {
            }

            @Override
            public void onScroll(float v) {
            }
        });

        return view;
    }

    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            double y = RANDOM.nextDouble() * 10d;
                            series.appendData(new DataPoint(lastX++, y), true, 10);
                            String temp = String.valueOf(y);
                            temp = temp.substring(0, 3);
//                            text.setText(temp);
                            }
                        });
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }).start();
    }
}
