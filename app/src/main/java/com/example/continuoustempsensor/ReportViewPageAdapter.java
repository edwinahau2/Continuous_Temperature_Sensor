package com.example.continuoustempsensor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.blure.complexview.ComplexView;
import com.blure.complexview.Shadow;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ReportViewPageAdapter extends PagerAdapter {

    Context context;
    View layoutScreen;
    Boolean daily;
    String date;
    String FILE_NAME = "temp.json";
    File file;
    FileReader fileReader = null;
    BufferedReader bufferedReader = null;
    String time;
    LineChart mChart;
    BarChart barChart;
    String response;
    String red = "#FF0000";
    String yellow = "#FB710B";
    String green = "#00B050";
    String blue = "#00B0F0";
    ComplexView avgCard, highCard, lowCard;
    ArrayList<Integer> colorSet = new ArrayList<>();

    public ReportViewPageAdapter(Context mContext, String date, String response, Boolean daily) {
        this.context = mContext;
        this.daily = daily;
        this.date = date;
        this.response = response;
        file = new File(mContext.getFilesDir(), FILE_NAME);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (daily) {
            layoutScreen = inflater.inflate(R.layout.daily_report, null);
            try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject values = (JSONObject) jsonObject.get(date);
                    List<String[]> array = new ArrayList<>();
                    for (int i = 0; i < values.names().length(); i++) {
                        String time = "time" + i;
                        JSONObject obj = values.getJSONObject(time);
                        array.add(new String[]{obj.getString("temperature"), obj.getString("hour"), obj.getString("unit")});
                    }
                    ArrayList<Float> threeParams = threeParams(array, MainActivity.restoreTempUnit(context).equals(" °F"));
                    String unit;
                    if (MainActivity.restoreTempUnit(context).equals(" °F")) {
                        unit = "°F";
                    } else {
                        unit = "°C";
                    }
                    DecimalFormat df = new DecimalFormat("#.#");
                    float avgTemp = Float.parseFloat(df.format(threeParams.get(0)));
                    float max = threeParams.get(1);
                    float min = threeParams.get(2);
                    TextView dailyAvg = layoutScreen.findViewById(R.id.average);
//                    dailyAvg.setTextSize(10);
                    TextView dailyHigh = layoutScreen.findViewById(R.id.high);
//                    dailyHigh.setTextSize(10);
                    TextView dailyLow = layoutScreen.findViewById(R.id.low);
//                    dailyLow.setTextSize(10);
                    avgCard = layoutScreen.findViewById(R.id.averageCard);
                    highCard = layoutScreen.findViewById(R.id.highCard);
                    lowCard = layoutScreen.findViewById(R.id.lowCard);
                    float[] radii = {20, 20, 20, 20, 20, 20, 20, 20};
                    if (max <= 100.3 || max <= 37.9) {
                        dailyHigh.setTextColor(Color.parseColor(green));
                        highCard.setShadow(new Shadow(2, 100, green, GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
                    } else if ((max >= 100.4 && max < 103) || (max < 38 && max > 39.4)) {
                        dailyHigh.setTextColor(Color.parseColor(yellow));
                        highCard.setShadow(new Shadow(2, 100, yellow, GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
                    } else if ((max >= 103) || (max >= 39.4)) {
                        dailyHigh.setTextColor(Color.parseColor(red));
                        highCard.setShadow(new Shadow(2, 100, red, GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
                    } else {
                        dailyHigh.setTextColor(Color.parseColor(blue));
                        highCard.setShadow(new Shadow(2, 100, blue, GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
                    }

                    if (min <= 100.3 || min <= 37.9) {
                        dailyLow.setTextColor(Color.parseColor(green));
                        lowCard.setShadow(new Shadow(2, 100, green, GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
                    } else if ((min >= 100.4 && min < 103) || (min < 38 && min > 39.4)) {
                        dailyLow.setTextColor(Color.parseColor(yellow));
                        lowCard.setShadow(new Shadow(2, 100, yellow, GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
                    } else if ((min >= 103) || (min >= 39.4)) {
                        dailyLow.setTextColor(Color.parseColor(red));
                        lowCard.setShadow(new Shadow(2, 100, red, GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
                    } else {
                        dailyLow.setTextColor(Color.parseColor(blue));
                        lowCard.setShadow(new Shadow(2, 100, blue, GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
                    }
                    if (avgTemp <= 100.3 || avgTemp <= 37.9) {
                        dailyAvg.setTextColor(Color.parseColor(green));
                        avgCard.setShadow(new Shadow(2, 100, green, GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
                    } else if ((avgTemp >= 100.4 && avgTemp < 103) || (avgTemp < 38 && avgTemp > 39.4)) {
                        dailyAvg.setTextColor(Color.parseColor(yellow));
                        avgCard.setShadow(new Shadow(2, 100, yellow, GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
                    } else if ((avgTemp >= 103) || (avgTemp >= 39.4)) {
                        dailyAvg.setTextColor(Color.parseColor(red));
                        avgCard.setShadow(new Shadow(2, 100, red, GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
                    } else {
                        dailyAvg.setTextColor(Color.parseColor(blue));
                        avgCard.setShadow(new Shadow(2, 100, blue, GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
                    }
                    dailyAvg.setText(avgTemp + " " + unit);
                    dailyHigh.setText(max + " " + unit);
                    dailyLow.setText(min + " " + unit);
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
                    mChart.setPinchZoom(false);
                    mChart.setDoubleTapToZoomEnabled(false);
                    LineData data = new LineData();
                    data.setValueTextColor(Color.WHITE);
                    mChart.setData(data);
                    mChart.setMaxHighlightDistance(20);
                    MyMarkerView mv = new MyMarkerView(context, R.layout.marker);
                    mChart.setDrawMarkers(true);
                    mChart.setMarker(mv);

                    XAxis xl = mChart.getXAxis();
                    xl.setTextColor(Color.BLACK);
                    xl.setAvoidFirstLastClipping(true);
                    xl.setEnabled(true);
                    xl.setDrawGridLines(false);
                    xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xl.setLabelCount(4, true);

                    LimitLine limitLine = new LimitLine(100.4f, null);
                    limitLine.setLineColor(Color.RED);
                    limitLine.enableDashedLine(10f, 10f, 0);

                    YAxis leftAxis = mChart.getAxisLeft();
                    leftAxis.setDrawLimitLinesBehindData(true);
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(limitLine);
                    leftAxis.setTextColor(Color.BLACK);
                    leftAxis.setDrawGridLines(false);
                    leftAxis.setAxisMaximum(Math.round(max + 3));
                    leftAxis.setAxisMinimum(Math.round(min-3));
                    leftAxis.setDrawGridLines(false);
                    leftAxis.setEnabled(true);

                    YAxis rightAxis = mChart.getAxisRight();
                    rightAxis.setEnabled(false);

                    mChart.getLegend().setEnabled(false);
                    mChart.setDrawBorders(false);
                    mChart.invalidate();

                    ArrayList<Entry> yVals = new ArrayList<>();
                    for (int o = 0; o < array.size(); o++) {
                        yVals.add(new Entry(o, Float.parseFloat(array.get(o)[0])));
                    }

                    LineData lineData = mChart.getData();
                    if (lineData != null) {
                        MyLineDataSet mySet = (MyLineDataSet) data.getDataSetByIndex(0);
//                        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
                        if (mySet == null) {
                            mySet = new MyLineDataSet(yVals, null);
                            mySet.setDrawCircles(true);
                            mySet.setDrawValues(false);
//                            mySet.setFillAlpha(100);
//                            mySet.setFillColor(ColorTemplate.getHoloBlue());
                            mySet.setAxisDependency(YAxis.AxisDependency.LEFT);
                            mySet.setLineWidth(3f);
                            mySet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                            mySet.setCubicIntensity(0.2f);
                            mySet.setColors(colorSet);
//                            data.addDataSet(set);
                            data.addDataSet(mySet);
                        }
                        for (int r = 0; r < array.size(); r++) {
                            float y = Float.parseFloat(array.get(r)[0]);
                            data.addEntry(new Entry(mySet.getEntryCount(), y), 0);
                            xl.setValueFormatter(new IndexAxisValueFormatter(Collections.singleton(array.get(r)[1])));
                            lineData.notifyDataChanged();
                            mChart.notifyDataSetChanged();
                            mChart.setVisibleXRangeMaximum(6);
                            mChart.moveViewToX(lineData.getEntryCount());
                        }
                    }
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            layoutScreen = inflater.inflate(R.layout.weekly_report, null);
            barChart = layoutScreen.findViewById(R.id.barChart);
            String line;
            try {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                StringBuilder stringBuilder = new StringBuilder();
                line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE.yyyy.MM.dd");
                calendar.setTime(sdf.parse(date));
                int c = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                calendar.add(Calendar.DATE, -c);
                time = sdf.format(calendar.getTime());
                List<String[]> array = new ArrayList<>();
                ArrayList<Float> avgTemp = new ArrayList<>();
                String response = stringBuilder.toString();
                int index = response.indexOf(time);
                String object = response.substring(index - 2);
                JSONObject jsonObject = new JSONObject(object);
                JSONObject values = (JSONObject) jsonObject.get(time);
                for (int i = 0; i < values.names().length(); i++) {
                    String timeIdx = "time" + i;
                    JSONObject obj = values.getJSONObject(timeIdx);
                    array.add(new String[]{obj.getString("temperature"), obj.getString("unit")});
                }
                avgTemp.add(averageCalc(array, MainActivity.restoreTempUnit(context).equals(" °F")));
                for (int j = 1; j < 7; j++) {
                    List<String[]> arr = new ArrayList<>();
                    calendar.setTime(sdf.parse(time));
                    calendar.add(Calendar.DATE, +j);
                    String newDate = sdf.format(calendar.getTime());
                    String result = stringBuilder.toString();
                    int idx = result.indexOf(newDate);
                    String obj = result.substring(idx - 2);
                    JSONObject jsonObj = new JSONObject(obj);
                    JSONObject vals = (JSONObject) jsonObj.get(newDate);
                    for (int i = 0; i < vals.names().length(); i++) {
                        String timeIdx = "time" + i;
                        JSONObject objSet = vals.getJSONObject(timeIdx);
                        arr.add(new String[]{objSet.getString("temperature"), objSet.getString("unit")});
                    }
                    avgTemp.add(averageCalc(arr, MainActivity.restoreTempUnit(context).equals(" °F")));
                }
                String[] daysOfWeek = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
                barChart.setVisibility(View.VISIBLE);
                barChart.setPinchZoom(false);
                barChart.setDragEnabled(false);
                barChart.setDrawBarShadow(false);
                barChart.setDrawGridBackground(false);
                barChart.getDescription().setEnabled(false);
                barChart.setDrawValueAboveBar(false);
                barChart.setTouchEnabled(false);
                barChart.setHighlightFullBarEnabled(false);
                barChart.getAxisRight().setEnabled(false);
                barChart.getLegend().setEnabled(false);
                XAxis xl = barChart.getXAxis();
                xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                xl.setDrawGridLines(false);
                xl.setCenterAxisLabels(true);
                xl.setCenterAxisLabels(true);
                xl.setTextColor(Color.BLACK);
                xl.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        if (((int) value) > -1 && ((int) value) < 7) {
                            return daysOfWeek[((int) value)];
                        }
                        return "";
                    }
                });

                LimitLine limitLine = new LimitLine(100.4f, null);
                limitLine.setLineColor(Color.RED);
                limitLine.enableDashedLine(10f, 10f, 0);

                YAxis yAxis = barChart.getAxisLeft();
                yAxis.setDrawGridLines(false);
                yAxis.setTextColor(Color.BLACK);
                yAxis.setAxisMinimum(Collections.min(avgTemp) - 2);
                yAxis.setAxisMaximum(Collections.max(avgTemp) + 2);
                yAxis.setDrawLimitLinesBehindData(true);
                yAxis.removeAllLimitLines();
                yAxis.addLimitLine(limitLine);

                Legend legend = barChart.getLegend();
                legend.setEnabled(false);
                DecimalFormat df = new DecimalFormat("#.#");
                ArrayList<BarEntry> barValues = new ArrayList<>();
                for (int d = 0; d < 7; d++) {
                    barValues.add(new BarEntry(d, Float.parseFloat(df.format(avgTemp.get(d)))));
                }
                BarDataSet set = new BarDataSet(barValues, null);
                set.setColors(colorSet);
                set.setDrawValues(false);
//                ArrayList<BarDataSet> dataSets = new ArrayList<>();
//                dataSets.add(set);
                BarData barData = new BarData(set);
                barChart.setData(barData);
                barChart.setFitBars(true);
                barChart.notifyDataSetChanged();
                barChart.invalidate();
            } catch (IOException | JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
        container.addView(layoutScreen);
        return layoutScreen;
    }

    private Float averageCalc(List<String[]> array, boolean f) {
        ArrayList<Float> temp = new ArrayList<>();
        int k = 0;
        float x;
        for (String[] ignored : array) {
            if (f) {
                if (array.get(k)[1].equals("°C")) {
                    x = ((Float.parseFloat(array.get(k)[0]))*9)/5 + 32;
                } else {
                    x  = Float.parseFloat(array.get(k)[0]);
                }
            } else {
                if (array.get(k)[1].equals("°F")) {
                    x = ((Float.parseFloat(array.get(k)[0]))-32)*5/9;
                } else {
                    x  = Float.parseFloat(array.get(k)[0]);
                }
            }
            temp.add(x);
            k++;
        }
        float total = 0;
        for (int l = 0; l < temp.size(); l++) {
            Log.d("averageTemp", "value: " + temp.get(l));
            total = total + temp.get(l);
        }
        float avgTemp = total / temp.size();
        if(avgTemp < 100.3 || avgTemp < 37.9) {
            colorSet.add(ContextCompat.getColor(context, R.color.green));
        } else if ((avgTemp <= 103 && avgTemp >= 100.4) || (avgTemp <= 39.4 && avgTemp >= 38)) {
            colorSet.add(ContextCompat.getColor(context, R.color.yellow));
        } else {
            colorSet.add(ContextCompat.getColor(context, R.color.red));
        }
        return avgTemp;
    }

    private ArrayList<Float> threeParams(List<String[]> array, boolean f) {
        ArrayList<Float> temp = new ArrayList<>();
        ArrayList<Float> threeParams = new ArrayList<>();
        int k = 0;
        float x;
        for (String[] ignored : array) {
            if (f) {
                if (array.get(k)[1].equals("°C")) {
                    x = ((Float.parseFloat(array.get(k)[0]))*9)/5 + 32;
                } else {
                    x  = Float.parseFloat(array.get(k)[0]);
                }
            } else {
                if (array.get(k)[1].equals("°F")) {
                    x = ((Float.parseFloat(array.get(k)[0]))-32)*5/9;
                } else {
                    x  = Float.parseFloat(array.get(k)[0]);
                }
            }
            temp.add(x);
            k++;
        }
        float max = Collections.max(temp);
        float min = Collections.min(temp);
        float total = 0;
        for (int l = 0; l < temp.size(); l++) {
            total = total + temp.get(l);
        }
        float avgTemp = total / temp.size();
        if(avgTemp < 100.3 || avgTemp < 37.9) {
            colorSet.add(ContextCompat.getColor(context, R.color.green));
        } else if ((avgTemp <= 103 && avgTemp >= 100.4) || (avgTemp <= 39.4 && avgTemp >= 38)) {
            colorSet.add(ContextCompat.getColor(context, R.color.yellow));
        } else {
            colorSet.add(ContextCompat.getColor(context, R.color.red));
        }
        threeParams.add(avgTemp);
        threeParams.add(max);
        threeParams.add(min);
        return threeParams;
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
