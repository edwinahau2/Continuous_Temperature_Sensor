package com.example.continuoustempsensor;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import static java.lang.Math.round;

public class fragment_tab1 extends Fragment implements OnChartValueSelectedListener{
    public static final String TAG = "ONE";
    private static final Random RANDOM = new Random();
    private ArrayList<String> time = new ArrayList<>();
    private LineChart mChart;
    private TextView text;
    private Thread thread;
    private String temp;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat format = new SimpleDateFormat("h:mm:ss a");
    private int lastX = 1;
    private int key;
    private String tf;
    private boolean check;
    private String symbol;
    private ArrayAdapter<String> arrayAdapter;
//    private int i;
    SwipeFlingAdapterView flingContainer;
    private TextView counter;
    private ValueFormatter newTime = new DataValueFormatter();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_layout, container, false);
        text = view.findViewById(R.id.text);
        mChart = view.findViewById(R.id.sparkView);
        flingContainer = view.findViewById(R.id.frame);
        counter = view.findViewById(R.id.counter);
        if (MainActivity.i != 1) {
            MainActivity.addal("Notifications");
            MainActivity.addal("my");
            MainActivity.addal("name");
            MainActivity.addal("is");
            MainActivity.addal("Aryan");
            MainActivity.addal("Agarwal");
            MainActivity.i = 1;
        }
        final int[] number = {MainActivity.al.size()};
        counter.setText(String.valueOf(number[0]));
        arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.item, R.id.helloText, MainActivity.al);
        flingContainer.setAdapter(arrayAdapter);
        if (getArguments() != null) {
            boolean notif = MainActivity.hide;
            // boolean notif = getArguments().getBoolean("inApp");
            temp = MainActivity.temperature;
//            temp = getArguments().getString("temperature");
            key = MainActivity.num;
            // key = getArguments().getInt("key");
//            text.setText(temp);
            if (key == 1) {
                //        mChart.setHighlightPerDragEnabled(true);
                mChart.setTouchEnabled(true);
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(true);
                mChart.setDrawGridBackground(false);
                mChart.setPinchZoom(true);
                mChart.setDescription(null);
                mChart.setBackgroundColor(Color.TRANSPARENT);
                mChart.setHighlightPerTapEnabled(true);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);
                mChart.setData(data);
                mChart.setOnChartValueSelectedListener(this);
                XAxis xl = mChart.getXAxis();
                xl.setCenterAxisLabels(true);
                mChart.setVisibleXRangeMaximum(4);
//            xl.setLabelCount(3, true);
                mChart.setExtraBottomOffset(10f);
                xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                xl.setTextColor(Color.BLACK);
                xl.setDrawGridLines(false);
                xl.setAvoidFirstLastClipping(true);
                xl.setEnabled(true);
                YAxis yl = mChart.getAxisLeft();
                yl.setTextColor(Color.BLACK);
                yl.setAxisMaximum(100f);
                yl.setAxisMinimum(0f);
                yl.setDrawGridLines(false);
                yl.setLabelCount(10);
                YAxis y2 = mChart.getAxisRight();
                y2.setEnabled(false);
                mChart.setDrawBorders(false);
                Legend l = mChart.getLegend();
                l.setEnabled(false);
                feedMultiple();
//            handler.post(feedMultiple);
                mChart.invalidate();
                key = 2;
            } else if (key == 2) {
                mChart.setTouchEnabled(true);
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(true);
                mChart.setDrawGridBackground(false);
                mChart.setPinchZoom(true);
                mChart.setDescription(null);
                mChart.setBackgroundColor(Color.LTGRAY);
                mChart.setHighlightPerTapEnabled(true);
                mChart.setOnChartValueSelectedListener(this);
                MainActivity.data.setValueTextColor(Color.WHITE);
                mChart.setData(MainActivity.data);
                XAxis xl = mChart.getXAxis();
                xl.setCenterAxisLabels(true);
                mChart.setVisibleXRangeMaximum(4);
//            xl.setLabelCount(3, true);
                mChart.setExtraBottomOffset(10f);
                xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                xl.setTextColor(Color.WHITE);
                xl.setDrawGridLines(false);
                xl.setAvoidFirstLastClipping(true);
                xl.setEnabled(true);
                YAxis yl = mChart.getAxisLeft();
                yl.setTextColor(Color.WHITE);
                yl.setAxisMaximum(100f);
                yl.setAxisMinimum(0f);
                yl.setDrawGridLines(false);
                yl.setLabelCount(10);
                YAxis y2 = mChart.getAxisRight();
                y2.setEnabled(false);
                mChart.setDrawBorders(false);
                Legend l = mChart.getLegend();
                l.setEnabled(false);
                feedMultiple();
//            handler.post(feedMultiple);
                mChart.invalidate();
            } else {
//                text.setText("- - °F");
                //        mChart.setHighlightPerDragEnabled(true);
                mChart.setTouchEnabled(true);
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(true);
                mChart.setDrawGridBackground(false);
                mChart.setPinchZoom(true);
                mChart.setDescription(null);
                mChart.setBackgroundColor(Color.TRANSPARENT);
                mChart.setHighlightPerTapEnabled(true);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);
                mChart.setData(data);
                mChart.setOnChartValueSelectedListener(this);
                XAxis xl = mChart.getXAxis();
                xl.setCenterAxisLabels(true);
                mChart.setVisibleXRangeMaximum(4);
//            xl.setLabelCount(3, true);
                mChart.setExtraBottomOffset(10f);
                xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                xl.setTextColor(Color.BLACK);
                xl.setDrawGridLines(false);
                xl.setAvoidFirstLastClipping(true);
                xl.setEnabled(true);
                YAxis yl = mChart.getAxisLeft();
                yl.setTextColor(Color.BLACK);
                yl.setAxisMaximum(100f);
                yl.setAxisMinimum(0f);
                yl.setDrawGridLines(false);
                yl.setLabelCount(10);
                YAxis y2 = mChart.getAxisRight();
                y2.setEnabled(false);
                mChart.setDrawBorders(false);
                Legend l = mChart.getLegend();
                l.setEnabled(false);
//                handler.post(feedMultiple);
                mChart.invalidate();
            }
//            if (MainActivity.data != null) {
//                //        mChart.setHighlightPerDragEnabled(true);
//                MainActivity.data.setValueTextColor(Color.WHITE);
//                mChart.setData(MainActivity.data);
//            } else {
//                text.setText("- - °F");
//                //        mChart.setHighlightPerDragEnabled(true);
//                LineData data = new LineData();
//                data.setValueTextColor(Color.WHITE);
//                mChart.setData(data);
//            }
//            if (notif) {
//                flingContainer.setVisibility(View.GONE);
//                counter.setVisibility(View.GONE);
//            } else {
//                flingContainer.setVisibility(View.VISIBLE);
//                counter.setVisibility(View.VISIBLE);
//            }
        } else {
//            text.setText("- - °F");
            //        mChart.setHighlightPerDragEnabled(true);
            mChart.setTouchEnabled(true);
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(true);
            mChart.setDrawGridBackground(false);
            mChart.setPinchZoom(true);
            mChart.setDescription(null);
            mChart.setBackgroundColor(Color.TRANSPARENT);
            mChart.setHighlightPerTapEnabled(true);
            LineData data = new LineData();
            data.setValueTextColor(Color.WHITE);
            mChart.setData(data);
            mChart.setOnChartValueSelectedListener(this);
            XAxis xl = mChart.getXAxis();
            xl.setCenterAxisLabels(true);
//            xl.setLabelCount(3, true);
            mChart.setExtraBottomOffset(10f);
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            xl.setTextColor(Color.BLACK);
            xl.setDrawGridLines(false);
            xl.setAvoidFirstLastClipping(true);
            xl.setEnabled(true);
            YAxis yl = mChart.getAxisLeft();
            yl.setTextColor(Color.BLACK);
            yl.setAxisMaximum(100f);
            yl.setAxisMinimum(0f);
            yl.setDrawGridLines(false);
            yl.setLabelCount(10);
            YAxis y2 = mChart.getAxisRight();
            y2.setEnabled(false);
            mChart.setDrawBorders(false);
            Legend l = mChart.getLegend();
            l.setEnabled(false);
            feedMultiple();
//            handler.post(feedMultiple);
            mChart.invalidate();
        }
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                MainActivity.al.remove(0);
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

    private void addEntry(float y) {
//        float e = Float.parseFloat(time);
//        String r = Float.toString(e);
        MainActivity.data = mChart.getData();
        if (MainActivity.data != null) {
            ILineDataSet set = MainActivity.data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                MainActivity.data.addDataSet(set);
            }
            MainActivity.coordinate(set.getEntryCount(), y);
            MainActivity.data.addEntry(new Entry(set.getEntryCount(), y), 0);
            XAxis xl = mChart.getXAxis();
            xl.setValueFormatter(new IndexAxisValueFormatter(time));
            MainActivity.data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(10);
            mChart.moveViewToX(MainActivity.data.getEntryCount());
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, null);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.rgb(27, 157, 255));
        set.setHighlightEnabled(true);
        set.setDrawValues(false);
        set.setDrawCircles(true);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        mChart.invalidate();
        return set;
    }

    private void feedMultiple() {
        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (tf != null) {
                    text.setText(tf);
//                    String tf = temp.substring(0, temp.length() - 4);
//                    float y = Float.parseFloat(tf);
//                    addEntry(Math.round(y));
//                    String clock = format.format(Calendar.getInstance().getTime());
//                    time.add(clock);
                }
            }
        });
    }

//    private final Runnable feedMultiple = new Runnable() {
//        @Override
//        public void run() {
//            if (temp != null) {
//                String tf = temp.substring(0, temp.length() - 4);
//                float y = Float.parseFloat(tf);
//                addEntry(Math.round(y));
//                String clock = format.format(Calendar.getInstance().getTime());
//                time.add(clock);
//            }
////            float y = (float) (Math.random() * 80 + 10f);
////            String time = formatHour.format(Calendar.getInstance().getTime()) +
////                    formatMinute.format(Calendar.getInstance().getTime()) + formatSecond.format(Calendar.getInstance().getTime());
////            handler.postDelayed(feedMultiple, 3000);
//        }
//    };

    @SuppressLint("SetTextI18n")
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        String currentTime = time.get((int) e.getX());
//        text.setText("(" + currentTime + ", " + e.getY() + ")");
//        mChart.centerViewTo(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataIndex()).getAxisDependency());
    }

    @Override
    public void onResume() {
        super.onResume();
        File file = new File(requireContext().getFilesDir(), "temp.json");
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            String num = stringBuilder.toString();

            JSONObject jsonObject = new JSONObject(num);
            tf = jsonObject.getString("temperature");
            check = jsonObject.getBoolean("check");
//            if (check) {
//                flingContainer.setVisibility(View.GONE);
//                counter.setVisibility(View.GONE);
//            } else {
//                flingContainer.setVisibility(View.VISIBLE);
//                counter.setVisibility(View.VISIBLE);
//            }
            key = jsonObject.getInt("key");
            text.setText(tf);
            feedMultiple();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        if (MainActivity.hide) {
            flingContainer.setVisibility(View.GONE);
            counter.setVisibility(View.GONE);
        } else {
            flingContainer.setVisibility(View.VISIBLE);
            counter.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        File file = new File(requireContext().getFilesDir(), "temp.json");
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            String num = stringBuilder.toString();

            JSONObject jsonObject = new JSONObject(num);
            tf = jsonObject.getString("temperature");
            check = jsonObject.getBoolean("check");
//            if (check) {
//                flingContainer.setVisibility(View.GONE);
//                counter.setVisibility(View.GONE);
//            } else {
//                flingContainer.setVisibility(View.VISIBLE);
//                counter.setVisibility(View.VISIBLE);
//            }
            key = jsonObject.getInt("key");
//            text.setText(tf);
            feedMultiple();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        if (MainActivity.hide) {
            flingContainer.setVisibility(View.GONE);
            counter.setVisibility(View.GONE);
        } else {
            flingContainer.setVisibility(View.VISIBLE);
            counter.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onNothingSelected() {

    }


//    private void feedMultiple() {
//        if (thread != null) {
//            thread.interrupt();
//        }



//        thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    float y = (float) (Math.random() * 80 + 10f);
//                    String time = formatHour.format(Calendar.getInstance().getTime()) +
//                            formatMinute.format(Calendar.getInstance().getTime()) + formatSecond.format(Calendar.getInstance().getTime());
//                    addEntry(Math.round(y), time);
////                    try {
////                        Thread.sleep(3000);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
//                }
//            }
//        }, 1000);
//    }
}
