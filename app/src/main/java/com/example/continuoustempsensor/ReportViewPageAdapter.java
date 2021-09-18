package com.example.continuoustempsensor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    String yellow = "#ffdd00";
    String green = "#00B050";
    String orange = "#FB710B";
    String blue = "#00B0F0";
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
                DecimalFormat df = new DecimalFormat("#.#");
                float avgTemp = Float.parseFloat(df.format(threeParams.get(0)));
                float max = Float.parseFloat(df.format(threeParams.get(1)));
                float min = Float.parseFloat(df.format(threeParams.get(2)));
                TextView dailyAvg = layoutScreen.findViewById(R.id.avgTemp);
                TextView dailyHigh = layoutScreen.findViewById(R.id.highTemp);
                TextView dailyLow = layoutScreen.findViewById(R.id.lowTemp);
                ImageView pic = layoutScreen.findViewById(R.id.tempImage);
                String unit = " °F";
                if (!MainActivity.restoreTempUnit(context).equals(" °F")) {
                    unit = " °C";
                    avgTemp = (float) ((avgTemp - 32) * 5 / 9.0);
                    avgTemp = Float.parseFloat(df.format(avgTemp));
                    max = (float) ((max - 32) * 5 / 9.0);
                    max = Float.parseFloat(df.format(max));
                    min = (float) ((min - 32) * 5 / 9.0);
                    min = Float.parseFloat(df.format(min));
                    if (max <= 37.7f) {
                        dailyHigh.setTextColor(Color.parseColor(green));
                    } else if (max <= 38f && max >= 37.8f) {
                        dailyHigh.setTextColor(Color.parseColor(yellow));
                    } else if (max > 38f && max <= 39.4f) {
                        dailyHigh.setTextColor(Color.parseColor(orange));
                    } else if (max > 39.4f) {
                        dailyHigh.setTextColor(Color.parseColor(red));
                    } else {
                        dailyHigh.setText("--");
                        dailyHigh.setTextColor(Color.parseColor(blue));
                    }

                    if (min <= 37.7f) {
                        dailyLow.setTextColor(Color.parseColor(green));
                    } else if (min <= 38f && min >= 37.8f) {
                        dailyLow.setTextColor(Color.parseColor(yellow));
                    } else if (min > 38f && min <= 39.4f) {
                        dailyLow.setTextColor(Color.parseColor(orange));
                    } else if (min > 39.4f) {
                        dailyLow.setTextColor(Color.parseColor(red));
                    } else {
                        dailyLow.setText("--");
                        dailyLow.setTextColor(Color.parseColor(blue));
                    }

                    if (avgTemp <= 37.7f) {
                        dailyAvg.setTextColor(Color.parseColor(green));
                        pic.setImageResource(R.drawable.temp_green);
                    } else if (avgTemp <= 38f && avgTemp >= 37.8f) {
                        dailyAvg.setTextColor(Color.parseColor(yellow));
                    } else if (avgTemp > 38f && avgTemp <= 39.4f) {
                        dailyAvg.setTextColor(Color.parseColor(orange));
                        pic.setImageResource(R.drawable.temp_orange);
                    } else if (avgTemp > 39.4f) {
                        dailyAvg.setTextColor(Color.parseColor(red));
                        pic.setImageResource(R.drawable.temp_red);
                    } else {
                        dailyAvg.setText("--");
                        dailyAvg.setTextColor(Color.parseColor(blue));
                    }

                } else {
                    if (max <= 99.9f) {
                        dailyHigh.setTextColor(Color.parseColor(green));
                    } else if (max < 100.4 && max >= 100) {
                        dailyHigh.setTextColor(Color.parseColor(yellow));
                    } else if (max >= 100.4 && max <= 102.9) {
                        dailyHigh.setTextColor(Color.parseColor(orange));
                    } else if (max >= 103) {
                        dailyHigh.setTextColor(Color.parseColor(red));
                    } else {
                        dailyHigh.setText("--");
                        dailyHigh.setTextColor(Color.parseColor(blue));
                    }

                    if (min <= 99.9f) {
                        dailyLow.setTextColor(Color.parseColor(green));
                    } else if (min < 100.4f && min >= 100f) {
                        dailyLow.setTextColor(Color.parseColor(yellow));
                    } else if (min >= 100.4f && min <= 102.9f) {
                        dailyLow.setTextColor(Color.parseColor(orange));
                    } else if (min >= 103f) {
                        dailyLow.setTextColor(Color.parseColor(red));
                    } else {
                        dailyLow.setText("--");
                        dailyLow.setTextColor(Color.parseColor(blue));
                    }

                    if (avgTemp <= 99.9f) {
                        dailyAvg.setTextColor(Color.parseColor(green));
                        pic.setImageResource(R.drawable.temp_green);
                    } else if (avgTemp < 100.4f && avgTemp >= 100f) {
                        dailyAvg.setTextColor(Color.parseColor(yellow));
                    } else if (avgTemp >= 100.4f && avgTemp <= 102.9f) {
                        dailyAvg.setTextColor(Color.parseColor(orange));
                        pic.setImageResource(R.drawable.temp_orange);
                    } else if (avgTemp >= 103f) {
                        dailyAvg.setTextColor(Color.parseColor(red));
                        pic.setImageResource(R.drawable.temp_red);
                    } else {
                        dailyAvg.setText("--");
                        dailyAvg.setTextColor(Color.parseColor(blue));
                    }

                }

                dailyAvg.setText(avgTemp + unit);
                dailyHigh.setText(max + unit);
                dailyLow.setText(min + unit);
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

                float limitVal = 100.4f;
                if (!MainActivity.restoreTempUnit(context).equals(" °F")) {
                    limitVal = (float) ((limitVal - 32) * 5 / 9.0);
                    limitVal = Float.parseFloat(df.format(limitVal));
                }
                LimitLine limitLine = new LimitLine(limitVal, null);
                limitLine.setLineColor(Color.RED);
                limitLine.enableDashedLine(10f, 10f, 0);

                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.setDrawLimitLinesBehindData(true);
                leftAxis.removeAllLimitLines();
                leftAxis.addLimitLine(limitLine);
                leftAxis.setTextColor(Color.BLACK);
                leftAxis.setDrawGridLines(false);
                leftAxis.setAxisMaximum(Math.round(max + 1));
                leftAxis.setAxisMinimum(Math.round(min-1));
                leftAxis.setDrawGridLines(false);
                leftAxis.setEnabled(true);

                YAxis rightAxis = mChart.getAxisRight();
                rightAxis.setEnabled(false);

                mChart.getLegend().setEnabled(false);
                mChart.setDrawBorders(false);
                mChart.invalidate();

                LineData lineData = mChart.getData();
                if (lineData != null) {
                    MyLineDataSet mySet = (MyLineDataSet) data.getDataSetByIndex(0);
                    if (mySet == null) {
                        mySet = new MyLineDataSet(null, null);
                        mySet.setDrawCircles(true);
                        mySet.setCircleRadius(4f);
                        mySet.setDrawValues(false);
                        mySet.setFillAlpha(65);
                        mySet.setCircleHoleColor(Color.WHITE);
                        mySet.setCircleColor(Color.parseColor(blue));
                        mySet.setAxisDependency(YAxis.AxisDependency.LEFT);
                        mySet.setLineWidth(3f);
                        mySet.setMode(LineDataSet.Mode.LINEAR);
                        mySet.setColors(ContextCompat.getColor(context, R.color.blue));
                        data.addDataSet(mySet);
                    }
                    ArrayList<String> xTime = new ArrayList<>();
                    for (int r = 0; r < array.size(); r++) {
                        xTime.add(array.get(r)[1]);
                    }
                    for (int r = 0; r < array.size(); r++) {
                        float yVals = Float.parseFloat(array.get(r)[0]);
                        if (!MainActivity.restoreTempUnit(context).equals(" °F")) {
                            yVals = (float) ((yVals - 32) * 5 / 9.0);
                        }
                        data.addEntry(new Entry(mySet.getEntryCount(), yVals), 0);
                        xl.setValueFormatter(new IndexAxisValueFormatter(xTime));
                        xl.setGranularityEnabled(true);
                        xl.setGranularity(1f);
                        xl.setSpaceMax(0.1f);
                        lineData.notifyDataChanged();
                        mChart.notifyDataSetChanged();
                        mChart.setVisibleXRangeMaximum(6);
                        mChart.moveViewToX(lineData.getEntryCount());
                    }
                }
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
                    String bracket = String.valueOf(obj.charAt(0));
                    if (!bracket.equals("{")) {
                        obj = obj.replaceFirst(",", "{");
                    }
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
                xl.setCenterAxisLabels(false);
                xl.setGranularity(1f);
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
                DecimalFormat df = new DecimalFormat("#.#");

                float limitVal = 100.4f;
                if (!MainActivity.restoreTempUnit(context).equals(" °F")) {
                    limitVal = (float) ((limitVal - 32) * 5 / 9.0);
                    limitVal = Float.parseFloat(df.format(limitVal));
                }

                LimitLine limitLine = new LimitLine(limitVal, null);
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
                ArrayList<BarEntry> barValues = new ArrayList<>();
                for (int d = 0; d < 7; d++) {
                    float yVals = avgTemp.get(d);
                    if (!MainActivity.restoreTempUnit(context).equals(" °F")) {
                        yVals = (float) ((yVals - 32) * 5 / 9.0);
                    }
                    barValues.add(new BarEntry(d, Float.parseFloat(df.format(yVals))));
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
            total = total + temp.get(l);
        }
        float avgTemp = total / temp.size();
        if (!MainActivity.restoreTempUnit(context).equals(" °F")) {
            avgTemp = (float) ((avgTemp - 32) * 5 / 9.0);
            if (avgTemp <= 37.7f) {
                colorSet.add(ContextCompat.getColor(context, R.color.green));
            } else if (avgTemp <= 38f && avgTemp >= 37.8f) {
                colorSet.add(ContextCompat.getColor(context, R.color.yellow));
            } else if (avgTemp > 38f && avgTemp <= 39.4f) {
                colorSet.add(ContextCompat.getColor(context, R.color.orange));
            } else if (avgTemp > 39.4f) {
                colorSet.add(ContextCompat.getColor(context, R.color.red));
            }
        } else {
            if (avgTemp <= 99.9f) {
                colorSet.add(ContextCompat.getColor(context, R.color.green));
            } else if (avgTemp < 100.4f && avgTemp >= 100f) {
                colorSet.add(ContextCompat.getColor(context, R.color.yellow));
            } else if (avgTemp >= 100.4f && avgTemp <= 102.9f) {
                colorSet.add(ContextCompat.getColor(context, R.color.orange));
            } else if (avgTemp >= 103f) {
                colorSet.add(ContextCompat.getColor(context, R.color.red));
            }
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
