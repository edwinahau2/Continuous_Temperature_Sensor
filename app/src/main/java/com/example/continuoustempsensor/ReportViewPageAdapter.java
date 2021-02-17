package com.example.continuoustempsensor;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
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
                    for (String[] row : array) {
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
                    dailyAvg.setText(avgTemp + " " + unit);
                    TextView dailyHigh = layoutScreen.findViewById(R.id.high);
                    dailyHigh.setText(max + " " + unit);
                    TextView dailyLow = layoutScreen.findViewById(R.id.low);
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
                barChart.invalidate();
                XAxis xl = barChart.getXAxis();
                xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                xl.setLabelCount(7);
                xl.setDrawGridLines(false);
                xl.setCenterAxisLabels(true);
                xl.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        if (((int) value) > -1 && ((int) value) < 7) {
                            return daysOfWeek[((int) value)];
                        }
                        return "";
                    }
                });
//                YAxis yAxis = barChart.getAxisLeft();
//                yAxis.setEnabled(true);
//                yAxis.setDrawGridLines(false);
//                yAxis.setAxisMinimum(90f);
//                yAxis.setAxisMinimum(100f);

                ArrayList<BarEntry> barValues = new ArrayList<>();
                for (int d = 0; d < 7; d++) {
                    barValues.add(new BarEntry(d, avgTemp.get(d)));
                }
                BarDataSet set = new BarDataSet(barValues, null);
                BarData barData = new BarData(set);
                barChart.setData(barData);
                barChart.notifyDataSetChanged();
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
        for (String[] row : array) {
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
        return total / temp.size();
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
