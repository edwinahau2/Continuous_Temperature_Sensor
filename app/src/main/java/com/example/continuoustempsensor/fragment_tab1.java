package com.example.continuoustempsensor;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import static java.lang.Math.round;

public class fragment_tab1 extends Fragment {
    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;
    //public final int request_code;
    private static final Random RANDOM = new Random();
    private LineChart mChart;
    private TextView text;
    private Thread thread;
    private LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
    private Calendar calendar = Calendar.getInstance();
    private String temp;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat formatHour = new SimpleDateFormat("h");
    private SimpleDateFormat formatMinute = new SimpleDateFormat("mm");
    private SimpleDateFormat formatSecond = new SimpleDateFormat("ss");
    String[] time;
    private int lastX = 1;
    private int newX;
    private ArrayAdapter<String> arrayAdapter;
//    private int i;
    SwipeFlingAdapterView flingContainer;
    private TextView counter;
    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;
    private ValueFormatter newTime = new DataValueFormatter();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_layout, container, false);
        text = view.findViewById(R.id.text);
        mChart = view.findViewById(R.id.sparkView);
//        String currentDateTime = java.text.DateFormat.getDateTimeInstance().format(new Date());
//        GraphView graph = view.findViewById(R.id.graph);
//        time = formatter.format(calendar.getTime());
//        graph.addSeries(series);
//        Viewport viewport = graph.getViewport();
//        viewport.setYAxisBoundsManual(true);
//        viewport.setXAxisBoundsManual(true);
////        viewport.setMinX(0);
//        viewport.setMinY(0);
//        viewport.setMaxY(10);
//        viewport.setScrollable(true);
//        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
//        graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);
//        graph.getGridLabelRenderer().setVerticalLabelsVisible(true);
//        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
//        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
//            @Override
//            public String formatLabel(double value, boolean isValueX) {
//                if (isValueX) {
//                     return formatter.format(new Date((long) value));
//                } else {
//                    return super.formatLabel(value, isValueX);
//                }
//            }
//        });
//        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
//        graph.getGridLabelRenderer().setVerticalAxisTitle("Temperature");
        flingContainer = view.findViewById(R.id.frame);
        counter = view.findViewById(R.id.counter);
        if (MainActivity.i != 1) {
            MainActivity.addal("Hello");
            MainActivity.addal("my");
            MainActivity.addal("name");
            MainActivity.addal("is");
            MainActivity.addal("Aryan");
            MainActivity.addal("Agarwal");
            MainActivity.i = 1;
        }
        final int[] number = {MainActivity.al.size()};
        counter.setText(String.valueOf(number[0]));
        arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.item, R.id.helloText, MainActivity.al);
        flingContainer.setAdapter(arrayAdapter);
        if (getArguments() != null) {
            boolean notif = getArguments().getBoolean("inApp");
            temp = getArguments().getString("temperature");
            text.setText(temp);
            //        mChart.setHighlightPerDragEnabled(true);
            mChart.setTouchEnabled(true);
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(true);
            mChart.setDrawGridBackground(false);
            mChart.setPinchZoom(true);
            mChart.setDescription(null);
            mChart.setBackgroundColor(Color.LTGRAY);
            MainActivity.data.setValueTextColor(Color.WHITE);
            mChart.setData(MainActivity.data);
            XAxis xl = mChart.getXAxis();
            xl.setCenterAxisLabels(true);
            xl.setLabelCount(3, true);
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
            YAxis y2 = mChart.getAxisRight();
            y2.setEnabled(false);
            mChart.setDrawBorders(false);
            Legend l = mChart.getLegend();
            l.setEnabled(false);
            handler.post(feedMultiple);
            mChart.invalidate();
            if (notif) {
                flingContainer.setVisibility(View.GONE);
                counter.setVisibility(View.GONE);
            } else {
                flingContainer.setVisibility(View.VISIBLE);
                counter.setVisibility(View.VISIBLE);
            }
        } else {
            text.setText("- - °F");
            //        mChart.setHighlightPerDragEnabled(true);
            mChart.setTouchEnabled(true);
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(true);
            mChart.setDrawGridBackground(false);
            mChart.setPinchZoom(true);
            mChart.setDescription(null);
            mChart.setBackgroundColor(Color.LTGRAY);
            LineData data = new LineData();
            data.setValueTextColor(Color.WHITE);
            mChart.setData(data);
            XAxis xl = mChart.getXAxis();
            xl.setCenterAxisLabels(true);
            xl.setLabelCount(3, true);
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
            YAxis y2 = mChart.getAxisRight();
            y2.setEnabled(false);
            mChart.setDrawBorders(false);
            Legend l = mChart.getLegend();
            l.setEnabled(false);
            handler.post(feedMultiple);
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
//        final Context that = this.getContext();
//        notificationManager = NotificationManagerCompat.from(this.getContext());
//        editTextTitle = view.findViewById(R.id.edit_text_title);
//        editTextMessage = view.findViewById(R.id.edit_text_message);
//        Button warningButton = view.findViewById(R.id.urgent_warning);
//        warningButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View w) {
//                String title = editTextTitle.getText().toString();
//                String message = editTextMessage.getText().toString();
//                assert that != null;
//
//                Intent activityIntent = new Intent(that, MainActivity.class);
//                PendingIntent contentIntent = PendingIntent.getActivity(that,0, activityIntent, 0);
//
//                Intent broadcastIntent = new Intent(that, NotificationReceiver.class);
//                broadcastIntent.putExtra("openApp", message);
//                PendingIntent actionIntent = PendingIntent.getBroadcast(that,0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                android.app.Notification notification = new NotificationCompat.Builder(that, notifications.CHANNEL_1_ID)
//                        .setSmallIcon(R.drawable.warning)
//                        .setContentTitle(title)
//                        .setContentText(message)
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                        .setCategory(NotificationCompat.CATEGORY_ALARM)
//                        .setColor(Color.BLUE)
//                        .setContentIntent(contentIntent)
//                        .setAutoCancel(true)
//                        //.setOnlyAlertOnce(true) will only make sound and popup the first time we show it
//                        .addAction(R.mipmap.ic_launcher, "Open App", actionIntent)
//                        .build();
//                notificationManager.notify(1, notification);
//            }
//        });
//
//        Button updateButton = view.findViewById(R.id.send_update);
//        updateButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View w) {
//                String title = editTextTitle.getText().toString();
//                String message = editTextMessage.getText().toString();
//                assert that != null;
//                android.app.Notification notification = new NotificationCompat.Builder(that, notifications.CHANNEL_2_ID)
//                        .setSmallIcon(R.drawable.announcement)
//                        .setContentTitle(title)
//                        .setContentText(message)
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                        .setCategory(NotificationCompat.CATEGORY_EVENT)
//                        .build();
//                notificationManager.notify(2, notification);
//            }
//        });

        return view;
    }

    private void addEntry(float y, String time) {
        float e = Float.parseFloat(time);
        String r = Float.toString(e);
        text.setText(r);
        MainActivity.data = mChart.getData();
        if (MainActivity.data != null) {
            ILineDataSet set = MainActivity.data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                MainActivity.data.addDataSet(set);
            }
            MainActivity.coordinate(set.getEntryCount(), y);
            MainActivity.data.addEntry(new Entry(Float.parseFloat(time), y), 0);
            XAxis xl = mChart.getXAxis();
            xl.setValueFormatter(newTime);
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
        set.setColor(Color.MAGENTA);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(true);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        mChart.invalidate();
        return set;
    }

    private final Runnable feedMultiple = new Runnable() {
        @Override
        public void run() {
            float y = (float) (Math.random() * 80 + 10f);
            String time = formatHour.format(Calendar.getInstance().getTime()) +
                    formatMinute.format(Calendar.getInstance().getTime()) + formatSecond.format(Calendar.getInstance().getTime());
            addEntry(Math.round(y), time);
            handler.postDelayed(feedMultiple, 3000);
        }
    };


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_layout, container, false);

        final Context that = this.getContext(); // used "that" because in buttons can't reference parent context from innerclass line 37 and 54 instead of using "this"
        notificationManager = NotificationManagerCompat.from(this.getContext());
        editTextTitle = view.findViewById(R.id.edit_text_title);
        editTextMessage = view.findViewById(R.id.edit_text_message);

        Button warningButton = view.findViewById(R.id.urgent_warning);
        warningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                String title = editTextTitle.getText().toString();
                String message = editTextMessage.getText().toString();
                assert that != null;

                Intent activityIntent = new Intent(that, MainActivity.class); // opens the app at fragment 1 when notification clicked
                PendingIntent contentIntent = PendingIntent.getActivity(that,
                        0, activityIntent, 0);

                Intent broadcastIntent = new Intent(that, NotificationReceiver.class);
                broadcastIntent.putExtra("ButtonUnderneath", message);
                PendingIntent actionIntent = PendingIntent.getBroadcast(that,
                        0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                //update current means that when we create a new pendingintent, it will update putextra

                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.temp_spike);

                android.app.Notification notification = new NotificationCompat.Builder(that, notifications.CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.warning)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setLargeIcon(largeIcon)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getString(R.string.temp_spike_message))
                                .setBigContentTitle("Fever Temperatures Detected")
                                .setSummaryText("Summary Text"))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setColor(Color.BLUE)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true) //when tapped the notification will go away
                        //.setOnlyAlertOnce(true) will only make sound and popup the first time we show it
                        .addAction(R.mipmap.ic_launcher, "Notify Others", actionIntent) // button at the moment sends toast, but want to send it to notify supervisor etc.
                        //can add up to 3 action buttons
                        .build();
                notificationManager.notify(1, notification);
            }
        });

        Button updateButton = view.findViewById(R.id.send_update);
        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                String title = editTextTitle.getText().toString();
                String message = editTextMessage.getText().toString();
                assert that != null;

                Intent activityIntent = new Intent(that, MainActivity.class); // opens the app at fragment 1 when notification clicked
                PendingIntent contentIntent = PendingIntent.getActivity(that,
                        1, activityIntent, 0);

                android.app.Notification notification = new NotificationCompat.Builder(that, notifications.CHANNEL_2_ID)
                        .setSmallIcon(R.drawable.announcement)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine("Notification message 1") // can add up to 7 lines
                                .addLine("Notification message 2")
                                .addLine("Notification message 3")
                        )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setCategory(NotificationCompat.CATEGORY_EVENT)
                        .setColor(Color.BLUE)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .build();
                notificationManager.notify(2, notification);
            }
        });

        return view;
    }
}