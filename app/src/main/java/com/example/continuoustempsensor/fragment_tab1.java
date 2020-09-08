package com.example.continuoustempsensor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class fragment_tab1 extends Fragment {
    private TextView text;
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;

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
                                addEntry();
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

    private void addEntry() {
        double y = RANDOM.nextDouble() * 10d;
        series.appendData(new DataPoint(lastX++, y), true, 10);
        String temp = String.valueOf(y);
        temp = temp.substring(0, 3);
        text.setText(temp);
    }
}
