package com.example.continuoustempsensor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.acl.LastOwnerException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ReportViewPageAdapter extends PagerAdapter {

    Context context;
    List<Entry> tempList;
    View layoutScreen;
    Boolean daily;
    CalendarDay date;
    String FILE_NAME = "temp.json";
    File file;
    FileReader fileReader = null;
    BufferedReader bufferedReader = null;
    String time;
    LineChart mChart;
    BarChart barChart;

    public ReportViewPageAdapter(Context mContext, List<Entry> list, CalendarDay date, Boolean daily) {
        this.context = mContext;
        this.tempList = list;
        this.daily = daily;
        this.date = date;
        file = new File(mContext.getFilesDir(), FILE_NAME);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (daily) {
            layoutScreen = inflater.inflate(R.layout.daily_report, null);
            Calendar calendar = Calendar.getInstance();
            String bruh = String.valueOf(date);
            String bruhpt2 = bruh.substring(12, bruh.length()-1);
            SimpleDateFormat first_sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdf = new SimpleDateFormat("EEE.yyyy.MM.dd", Locale.getDefault());
            try {
                Date dateObj = first_sdf.parse(bruhpt2);
                calendar.setTime(dateObj);
                String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
                String year = String.valueOf(calendar.get(Calendar.YEAR));
                int month = (calendar.get(Calendar.MONTH)) + 1;
                String Month;
                if (month < 10) {
                    Month = "0" + month;
                } else {
                    Month = String.valueOf(month);
                }
                int day = (calendar.get(Calendar.DAY_OF_MONTH));
                String Day;
                if (day < 10) {
                    Day = "0"+day;
                } else {
                    Day = String.valueOf(day);
                }
                time = dayOfWeek + "." + year + "." + Month + "." + Day;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
                String response = stringBuilder.toString();
                JSONObject jsonObject  = new JSONObject(response);
                JSONObject values = (JSONObject) jsonObject.get(time);
                List<String[]> array = new ArrayList<>();
                for (int i = 0; i < values.names().length(); i++) {
                    String time = "time" + i;
                    JSONObject obj = values.getJSONObject(time);
                    array.add(new String[] {obj.getString("temperature"), obj.getString("hour")});
                }
                ArrayList<Float> temp = new ArrayList<>();
                int k = 0;
                for (String[] row : array) {
                    float x = Float.parseFloat(array.get(k)[0]);
                    temp.add(x);
                }
                float max = temp.get(0);
                float min = temp.get(0);
                for (int w = 0; w < temp.size(); w++) {
                    if (temp.get(w) > max) {
                        max = temp.get(w);
                    } else {
                        min = temp.get(w);
                    }
                }
                float total = 0;
                for (int l = 0; l < temp.size(); l++) {
                    total = total + temp.get(l);
                }
                float avgTemp = total / temp.size();
                TextView dailyAvg = layoutScreen.findViewById(R.id.average);
                dailyAvg.setText(String.valueOf(avgTemp));
                TextView dailyHigh = layoutScreen.findViewById(R.id.high);
                dailyHigh.setText(String.valueOf(max));
                TextView dailyLow = layoutScreen.findViewById(R.id.low);
                dailyLow.setText(String.valueOf(min));
                mChart = layoutScreen.findViewById(R.id.lineChart);
                mChart.setVisibility(View.VISIBLE);
                mChart.setDescription(null);
                mChart.setTouchEnabled(true);
                mChart.setExtraBottomOffset(10f);
                mChart.setBackgroundColor(Color.TRANSPARENT);
                mChart.setHighlightPerTapEnabled(true);
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(true);
                mChart.setDrawGridBackground(false);
                mChart.setPinchZoom(true);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);
                mChart.setData(data);
                mChart.setMaxHighlightDistance(20);

                XAxis xl = mChart.getXAxis();
                xl.setTextColor(Color.BLACK);
                xl.setAvoidFirstLastClipping(true);
                xl.setEnabled(true);
                xl.setDrawGridLines(false);
                xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                xl.setLabelCount(4, true);

                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.setTextColor(Color.BLACK);
                leftAxis.setDrawGridLines(false);
                leftAxis.setAxisMaximum(100f);
                leftAxis.setAxisMinimum(0f);
                leftAxis.setDrawGridLines(false);
                leftAxis.setEnabled(true);

                YAxis rightAxis = mChart.getAxisRight();
                rightAxis.setEnabled(false);

                mChart.getLegend().setEnabled(false);
                mChart.setDrawBorders(false);
                mChart.invalidate();
                LineData lineData = mChart.getData();
                if (lineData != null) {
                    LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
                    if (set == null) {
                        set = new LineDataSet(null, null);
                        set.setDrawCircles(true);
                        set.setFillAlpha(65);
                        set.setFillColor(ColorTemplate.getHoloBlue());
                        set.setAxisDependency(YAxis.AxisDependency.LEFT);
                        set.setLineWidth(3f);
                        set.setColor(Color.MAGENTA);
                        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        set.setCubicIntensity(0.2f);
                        data.addDataSet(set);
                    }
                    for (int r = 0; r < array.size(); r++) {
                        float y = Float.parseFloat(array.get(r)[0]);
                        data.addEntry(new Entry(set.getEntryCount(), y), 0);
                        xl.setValueFormatter(new IndexAxisValueFormatter(Collections.singleton(array.get(r)[1])));
                        lineData.notifyDataChanged();
                        mChart.notifyDataSetChanged();
                        mChart.setVisibleXRangeMaximum(6);
                        mChart.moveViewToX(lineData.getEntryCount());
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            layoutScreen = inflater.inflate(R.layout.weekly_report, null);
        }
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
