package com.example.continuoustempsensor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import java.text.DecimalFormat;
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
    Toast toast;
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
//        Calendar calendar = Calendar.getInstance();
//        String bruh = String.valueOf(date);
//        String bruhpt2 = bruh.substring(12, bruh.length()-1);
//        SimpleDateFormat first_sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        try {
//            dateObj = first_sdf.parse(bruhpt2);
//            calendar.setTime(dateObj);
//            String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
//            String year = String.valueOf(calendar.get(Calendar.YEAR));
//            int month = (calendar.get(Calendar.MONTH)) + 1;
//            String Month;
//            if (month < 10) {
//                Month = "0" + month;
//            } else {
//                Month = String.valueOf(month);
//            }
//            int day = (calendar.get(Calendar.DAY_OF_MONTH));
//            String Day;
//            if (day < 10) {
//                Day = "0"+day;
//            } else {
//                Day = String.valueOf(day);
//            }
//            time = dayOfWeek + "." + year + "." + Month + "." + Day;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        if (daily) {
            layoutScreen = inflater.inflate(R.layout.daily_report, null);
            try {
//                fileReader = new FileReader(file);
//                bufferedReader = new BufferedReader(fileReader);
//                StringBuilder stringBuilder = new StringBuilder();
//                String line = bufferedReader.readLine();
//                while (line != null) {
//                    stringBuilder.append(line).append("\n");
//                    line = bufferedReader.readLine();
//                }
//                bufferedReader.close();
//                String response = stringBuilder.toString();
//                int index = response.indexOf(time);
//                if (index < 0) {
//                    toast = Toast.makeText(context, "No data found for this day", Toast.LENGTH_SHORT);
//                    setToast();
//                } else {
//                    String object = response.substring(index - 2);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject values = (JSONObject) jsonObject.get(date);
                    List<String[]> array = new ArrayList<>();
                    for (int i = 0; i < values.names().length(); i++) {
                        String time = "time" + i;
                        JSONObject obj = values.getJSONObject(time);
                        array.add(new String[]{obj.getString("temperature"), obj.getString("hour"), obj.getString("unit")});
                    }
                    ArrayList<Float> temp = new ArrayList<>();
                    int k = 0;
                    float x;
                    String unit;
                    if (MainActivity.f) {
                        unit = "°F";
                    } else {
                        unit = "°C";
                    }
                    for (String[] ignored : array) {
                        if (MainActivity.f) {
                            if (array.get(k)[2].equals("°C")) {
                                x = ((Float.parseFloat(array.get(k)[0])) * 9) / 5 + 32;
                            } else {
                                x = Float.parseFloat(array.get(k)[0]);
                            }
                        } else {
                            if (array.get(k)[2].equals("°F")) {
                                x = ((Float.parseFloat(array.get(k)[0])) - 32) * 5 / 9;
                            } else {
                                x = Float.parseFloat(array.get(k)[0]);
                            }
                        }
                        temp.add(x);
                        k++;
                    }
                    float max = Collections.max(temp);
                    float min = Collections.min(temp);
//                    for (int w = 0; w < temp.size(); w++) {
//                        if (temp.get(w) > max) {
//                            max = temp.get(w);
//                        }
//                    }
//
//                    for (int q = 0; q < temp.size(); q++) {
//                        if (temp.get(q) < min) {
//                            min = temp.get(q);
//                        }
//                    }
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
                    float total = 0;
                    for (int l = 0; l < temp.size(); l++) {
                        total = total + temp.get(l);
                    }
                    DecimalFormat df = new DecimalFormat("#.#");
                    float avgTemp = Float.parseFloat(df.format(total / temp.size()));

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
                            mySet.setColors(ContextCompat.getColor(context, R.color.green), ContextCompat.getColor(context, R.color.yellow), ContextCompat.getColor(context, R.color.red));
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
                time = date;
                calendar.setTime(sdf.parse(date));
                int c = calendar.get(Calendar.DAY_OF_WEEK);
                List<String[]> array = new ArrayList<>();
                ArrayList<Float> avgTemp = new ArrayList<>();
                switch(c) {
                    case 1:
                        for (int j = 0; j < 7; j++) {
                            if (j != 0) {
                                calendar.setTime(sdf.parse(time));
                                calendar.add(Calendar.DATE, +j);
                                date = sdf.format(calendar.getTime());
                            }
                            String response = stringBuilder.toString();
                            int index = response.indexOf(date);
                            String object = response.substring(index - 2);
                            JSONObject jsonObject = new JSONObject(object);
                            JSONObject values = (JSONObject) jsonObject.get(date);
                            for (int i = 0; i < values.names().length(); i++) {
                                String time = "time" + i;
                                JSONObject obj = values.getJSONObject(time);
                                array.add(new String[]{obj.getString("temperature"), obj.getString("unit")});
                            }
                            avgTemp.add(averageCalc(array, MainActivity.f));
                        }
                        break;
                    case 2:
                        for (int j = 1; j < 8; j++) {
                            if (j != 2) {
                                if (j < 2) {
                                    calendar.setTime(sdf.parse(time));
                                    calendar.add(Calendar.DATE, -j);
                                    date = sdf.format(calendar.getTime());
                                } else {
                                    calendar.setTime(sdf.parse(time));
                                    calendar.add(Calendar.DATE, +(j - 2));
                                    date = sdf.format(calendar.getTime());
                                }
                            }
                            String response = stringBuilder.toString();
                            int index = response.indexOf(date);
                            String object = response.substring(index - 2);
                            JSONObject jsonObject = new JSONObject(object);
                            JSONObject values = (JSONObject) jsonObject.get(date);
                            for (int i = 0; i < values.names().length(); i++) {
                                String time = "time" + i;
                                JSONObject obj = values.getJSONObject(time);
                                array.add(new String[]{obj.getString("temperature"), obj.getString("unit")});
                            }
                            avgTemp.add(averageCalc(array, MainActivity.f));
                        }
                        break;
                    case 3:
                        for (int j = 1; j < 8; j++) {
                            if (j != 3) {
                                if (j < 3) {
                                    calendar.setTime(sdf.parse(time));
                                    calendar.add(Calendar.DATE, -(3-j));
                                    date = sdf.format(calendar.getTime());
                                } else {
                                    calendar.setTime(sdf.parse(time));
                                    calendar.add(Calendar.DATE, +(j - 3));
                                    date = sdf.format(calendar.getTime());
                                }
                            }
                            String response = stringBuilder.toString();
                            int index = response.indexOf(date);
                            String object = response.substring(index - 2);
                            JSONObject jsonObject = new JSONObject(object);
                            JSONObject values = (JSONObject) jsonObject.get(date);
                            for (int i = 0; i < values.names().length(); i++) {
                                String time = "time" + i;
                                JSONObject obj = values.getJSONObject(time);
                                array.add(new String[]{obj.getString("temperature"), obj.getString("unit")});
                            }
                            avgTemp.add(averageCalc(array, MainActivity.f));
                        }
                        break;
                    case 4:
                        for (int j = 1; j < 8; j++) {
                            if (j != 4) {
                                if (j < 4) {
                                    calendar.setTime(sdf.parse(time));
                                    calendar.add(Calendar.DATE, -(4-j));
                                    date = sdf.format(calendar.getTime());
                                } else {
                                    calendar.setTime(sdf.parse(time));
                                    calendar.add(Calendar.DATE, +(j - 4));
                                    date = sdf.format(calendar.getTime());
                                }
                            }
                            String response = stringBuilder.toString();
                            int index = response.indexOf(date);
                            String object = response.substring(index - 2);
                            JSONObject jsonObject = new JSONObject(object);
                            JSONObject values = (JSONObject) jsonObject.get(date);
                            for (int i = 0; i < values.names().length(); i++) {
                                String time = "time" + i;
                                JSONObject obj = values.getJSONObject(time);
                                array.add(new String[]{obj.getString("temperature"), obj.getString("unit")});
                            }
                            avgTemp.add(averageCalc(array, MainActivity.f));
                        }
                        break;
                    case 5:
                        for (int j = 1; j < 8; j++) {
                            if (j != 5) {
                                if (j < 5) {
                                    calendar.setTime(sdf.parse(time));
                                    calendar.add(Calendar.DATE, -(5-j));
                                    date = sdf.format(calendar.getTime());
                                } else {
                                    calendar.setTime(sdf.parse(time));
                                    calendar.add(Calendar.DATE, +(j - 5));
                                    date = sdf.format(calendar.getTime());
                                }
                            }
                            String response = stringBuilder.toString();
                            int index = response.indexOf(date);
                            String object = response.substring(index - 2);
                            JSONObject jsonObject = new JSONObject(object);
                            JSONObject values = (JSONObject) jsonObject.get(date);
                            for (int i = 0; i < values.names().length(); i++) {
                                String time = "time" + i;
                                JSONObject obj = values.getJSONObject(time);
                                array.add(new String[]{obj.getString("temperature"), obj.getString("unit")});
                            }
                            avgTemp.add(averageCalc(array, MainActivity.f));
                        }
                        break;
                    case 6:
                        for (int j = 1; j < 8; j++) {
                            if (j != 6) {
                                if (j < 6) {
                                    calendar.setTime(sdf.parse(time));
                                    calendar.add(Calendar.DATE, -(6-j));
                                    date = sdf.format(calendar.getTime());
                                } else {
                                    calendar.setTime(sdf.parse(time));
                                    calendar.add(Calendar.DATE, +(j - 6));
                                    date = sdf.format(calendar.getTime());
                                }
                            }
                            String response = stringBuilder.toString();
                            int index = response.indexOf(date);
                            String object = response.substring(index - 2);
                            JSONObject jsonObject = new JSONObject(object);
                            JSONObject values = (JSONObject) jsonObject.get(date);
                            for (int i = 0; i < values.names().length(); i++) {
                                String time = "time" + i;
                                JSONObject obj = values.getJSONObject(time);
                                array.add(new String[]{obj.getString("temperature"), obj.getString("unit")});
                            }
                            avgTemp.add(averageCalc(array, MainActivity.f));
                        }
                        break;
                    case 7:
                        for (int j = 12; j > 5 ; j--) {
                            if (j != 6) {
                                calendar.setTime(sdf.parse(time));
                                calendar.add(Calendar.DATE, -(13-j));
                                date = sdf.format(calendar.getTime());
                            }
                            String response = stringBuilder.toString();
                            int index = response.indexOf(date);
                            String object = response.substring(index - 2);
                            JSONObject jsonObject = new JSONObject(object);
                            JSONObject values = (JSONObject) jsonObject.get(date);
                            for (int i = 0; i < values.names().length(); i++) {
                                String time = "time" + i;
                                JSONObject obj = values.getJSONObject(time);
                                array.add(new String[]{obj.getString("temperature"), obj.getString("unit")});
                            }
                            avgTemp.add(averageCalc(array, MainActivity.f));
                        }
                        break;
                }
//                ArrayList daysOfWeek = new ArrayList();
//                daysOfWeek.add("SUN");
//                daysOfWeek.add("MON");
//                daysOfWeek.add("SUN");
//                daysOfWeek.add("MON");
//                daysOfWeek.add("SUN");
//                daysOfWeek.add("MON");
//                daysOfWeek.add("SUN");
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

    public void setToast() {
        toast.setGravity(Gravity.BOTTOM, 0, 180);
        toast.show();
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