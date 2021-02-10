package com.example.continuoustempsensor;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.threeten.bp.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class fragment_tab2 extends Fragment  {

    LineChart mChart;
    GraphPageAdapter graphPageAdapter;
    SimpleDateFormat time = new SimpleDateFormat("h:mm a", Locale.getDefault());
    SimpleDateFormat day = new SimpleDateFormat("EEE", Locale.getDefault());
    String dayOfWeek = day.format(Calendar.getInstance().getTime());
    LineDataSet set;
    LineData data;
    List<Entry> tempEntries = new ArrayList<>();
    int i = 0;
    File file;
    FileReader fileReader = null;
    BufferedReader bufferedReader = null;
    List<String> dayArray = new ArrayList<>();
    List<Item> daysList;
    MaterialCalendarView materialCalendarView;
    TabLayout tabLayout;
    TabLayout.Tab selectTab;
    List<CalendarDay> datesLeft = new ArrayList<>();
    List<CalendarDay> datesCenter = new ArrayList<>();
    List<CalendarDay> datesRight = new ArrayList<>();
    CalendarDay myDate = CalendarDay.today();
    LocalDate localDate;
    CalendarDay upToDay;
    WeekDecorator weekDecorator;
    CurrentDayDecorator currentDayDecorator;
    ViewPager report;
    ReportViewPageAdapter reportAdapter;
    List<Entry> tempList = new ArrayList<>();
    String avg = "Average: 94";
    String high = "High: 99.2";
    String low = "Low: 98.2";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_layout, container, false);
        materialCalendarView = view.findViewById(R.id.calendarView);
        tabLayout = view.findViewById(R.id.daytime);
        report = view.findViewById(R.id.graph_viewpager);
        tempList.add(new Entry(1, (float) 98.6));
        tempList.add(new Entry(2, (float) 98.8));
        tempList.add(new Entry(3, (float) 97.6));
        tempList.add(new Entry(4, (float) 98.2));
        tempList.add(new Entry(5, (float) 99.2));
        tempList.add(new Entry(6, (float) 98.3));
        tempList.add(new Entry(7, (float) 98.6));
        materialCalendarView.setDateSelected(myDate, true);
        materialCalendarView.addDecorator(new CurrentDayDecorator(myDate, true));
        selectTab = tabLayout.getTabAt(2);
        selectTab.select();
        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectTab = tabLayout.getTabAt(0);
            selectTab.select();
            materialCalendarView.removeDecorators();
            materialCalendarView.invalidateDecorators();
            if (date.equals(myDate)) {
                currentDayDecorator = new CurrentDayDecorator(myDate, true);
                materialCalendarView.addDecorator(currentDayDecorator);
                upToDay = myDate;
            } else {
                currentDayDecorator = new CurrentDayDecorator(myDate, false);
                materialCalendarView.addDecorator(currentDayDecorator);
                upToDay = date;
            }
            materialCalendarView.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit();
            reportAdapter = new ReportViewPageAdapter(requireContext(), tempList, date, true);
            report.setAdapter(reportAdapter);
            // day report stuff
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                materialCalendarView.removeDecorators();
                materialCalendarView.invalidateDecorators();
                if (tab.getPosition() == 1) {
                    report.setVisibility(View.VISIBLE);
                    materialCalendarView.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit();
                    CalendarDay date = materialCalendarView.getSelectedDate();
                    addDays(date);
                    reportAdapter = new ReportViewPageAdapter(requireContext(), tempList, date,false);
                    report.setAdapter(reportAdapter);
                    // weekly report
                } else if (tab.getPosition() == 2) {
                    if (upToDay == null || upToDay.equals(myDate)) {
                        currentDayDecorator = new CurrentDayDecorator(myDate, true);
                        materialCalendarView.addDecorator(currentDayDecorator);
                    } else {
                        currentDayDecorator = new CurrentDayDecorator(myDate, false);
                        materialCalendarView.addDecorator(currentDayDecorator);
                    }
                    report.setVisibility(View.GONE);
                    materialCalendarView.state().edit().setCalendarDisplayMode(CalendarMode.MONTHS).commit();
                } else {
                    report.setVisibility(View.VISIBLE);
                    CalendarDay date = materialCalendarView.getSelectedDate();
                    reportAdapter = new ReportViewPageAdapter(requireContext(), tempList, date,true);
                    report.setAdapter(reportAdapter);
                    materialCalendarView.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
////      viewPager = view.findViewById(R.id.viewpager2);
//        ItemAdapter itemAdapter = new ItemAdapter(requireContext(), buildItemList());
        String FILE_NAME = "temp.json";
        file = new File(requireContext().getFilesDir(), FILE_NAME);
//        tempEntries.add(new Entry(1, (float) 98.6));
//        tempEntries.add(new Entry(2, (float) 98.8));
//        tempEntries.add(new Entry(3, (float) 97.6));
//        tempEntries.add(new Entry(4, (float) 98.2));
//        tempEntries.add(new Entry(5, (float) 99.2));
//        tempEntries.add(new Entry(6, (float) 98.3));
//        tempEntries.add(new Entry(7, (float) 98.6));
//        try {
//            String key = "time0";
//            reading.put("temperature", "98.6");
//            reading.put("hour", "1:30");
//            obj.put(key, reading);
//            today.put(dayOfWeek, obj);
//            String userString = today.toString();
//            fileWriter = new FileWriter(file);
//            bufferedWriter = new BufferedWriter(fileWriter);
//            bufferedWriter.write(userString);
//            bufferedWriter.close();
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//        }
        return view;
    }

    private void addDays(CalendarDay date) {
        Calendar calendar = Calendar.getInstance();
        String bruh = String.valueOf(date);
        String bruhpt2 = bruh.substring(12, bruh.length()-1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            calendar.setTime(sdf.parse(bruhpt2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int c = calendar.get(Calendar.DAY_OF_WEEK);
        localDate = getLocalDate(bruhpt2);
        switch(c) {
            case 1:
                datesLeft.add(CalendarDay.from(localDate));
                for (int t = 1; t < 6; t++) {
                    datesCenter.add(CalendarDay.from(localDate.plusDays(t)));
                }
                datesRight.add(CalendarDay.from(localDate.plusDays(6)));
                break;
            case 2:
                datesLeft.add(CalendarDay.from(localDate.minusDays(1)));
                datesCenter.add(CalendarDay.from(localDate));
                for (int t = 1; t < 5; t++) {
                    datesCenter.add(CalendarDay.from(localDate.plusDays(t)));
                }
                datesRight.add(CalendarDay.from(localDate.plusDays(5)));
                break;
            case 3:
                datesLeft.add(CalendarDay.from(localDate.minusDays(2)));
                datesCenter.add(CalendarDay.from(localDate.minusDays(1)));
                datesCenter.add(CalendarDay.from(localDate));
                for (int t = 1; t < 4; t++) {
                    datesCenter.add(CalendarDay.from(localDate.plusDays(t)));
                }
                datesRight.add(CalendarDay.from(localDate.plusDays(4)));
                break;
            case 4:
                datesLeft.add(CalendarDay.from(localDate.minusDays(3)));
                datesCenter.add(CalendarDay.from(localDate.minusDays(2)));
                datesCenter.add(CalendarDay.from(localDate.minusDays(1)));
                datesCenter.add(CalendarDay.from(localDate));
                for (int t = 1; t < 3; t++) {
                    datesCenter.add(CalendarDay.from(localDate.plusDays(t)));
                }
                datesRight.add(CalendarDay.from(localDate.plusDays(3)));
                break;
            case 5:
                datesLeft.add(CalendarDay.from(localDate.minusDays(4)));
                datesCenter.add(CalendarDay.from(localDate.minusDays(3)));
                datesCenter.add(CalendarDay.from(localDate.minusDays(2)));
                datesCenter.add(CalendarDay.from(localDate.minusDays(1)));
                datesCenter.add(CalendarDay.from(localDate));
                for (int t = 1; t < 2; t++) {
                    datesCenter.add(CalendarDay.from(localDate.plusDays(t)));
                }
                datesRight.add(CalendarDay.from(localDate.plusDays(2)));
                break;
            case 6:
                datesLeft.add(CalendarDay.from(localDate.minusDays(5)));
                for (int t = 4; t > 0; t--) {
                    datesCenter.add(CalendarDay.from(localDate.minusDays(t)));
                }
                datesCenter.add(CalendarDay.from(localDate));
                datesRight.add(CalendarDay.from(localDate.plusDays(1)));
                break;
            case 7:
                datesLeft.add(CalendarDay.from(localDate.minusDays(6)));
                for (int t = 5; t > 0; t--) {
                    datesCenter.add(CalendarDay.from(localDate.minusDays(t)));
                }
                datesRight.add(CalendarDay.from(localDate));
                break;
        }

        setDecor(datesLeft, R.drawable.g_left);
        setDecor(datesCenter, R.drawable.g_center);
        setDecor(datesRight, R.drawable.g_right);
    }

    private LocalDate getLocalDate(String ld) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date input = sdf.parse(ld);
            Calendar cal = Calendar.getInstance();
            cal.setTime(input);
            return LocalDate.of(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        } catch (NullPointerException | ParseException e) {
            return null;
        }
    }

    private void setDecor(List<CalendarDay> calendarDayList, int drawable) {
        weekDecorator = new WeekDecorator(requireContext(), drawable, calendarDayList);
        materialCalendarView.addDecorators(weekDecorator);
    }

    private List<Item> buildItemList() {
        daysList = new ArrayList<>();
        for (int i=0; i<10; i++) {
            Item item = new Item("Week " + i);
            daysList.add(item);
        }
        return daysList;
    }

    @Override
    public void onResume() {
        super.onResume();
        String now = time.format(Calendar.getInstance().getTime());
        while (!now.equals("12:00 AM")) {
            dayArray.add(dayOfWeek);
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
                JSONObject jsonObject = new JSONObject(response);
                JSONObject mainObject = jsonObject.getJSONObject(MainActivity.jsonDate);
                JSONObject timeObject = mainObject.getJSONObject("time0");
                String x = timeObject.getString("temperature");
                tempEntries.add(new Entry(i, Float.parseFloat(x)));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            set = new LineDataSet(tempEntries, null);
            set.setDrawCircles(true);
            set.setFillAlpha(65);
            set.setFillColor(ColorTemplate.getHoloBlue());
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setLineWidth(3f);
            set.setColor(Color.MAGENTA);
            set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set.setCubicIntensity(0.2f);
            data = new LineData(set);
            data.setValueTextColor(Color.WHITE);
            List<LineData> dat = new ArrayList<>();
            dat.add(data);
            graphPageAdapter = new GraphPageAdapter(requireContext(), dat, mChart, dayArray);
//            viewPager.setAdapter(graphPageAdapter);
            break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        onResume();
    }
}
