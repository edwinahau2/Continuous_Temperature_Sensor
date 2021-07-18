package com.example.continuoustempsensor;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

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
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

    File file;
    FileReader fileReader = null;
    BufferedReader bufferedReader = null;
    MaterialCalendarView materialCalendarView;
    TabLayout tabLayout;
    TabLayout.Tab selectTab;
    CalendarDay myDate = CalendarDay.today();
    LocalDate localDate;
    CalendarDay upToDay;
    CurrentDayDecorator currentDayDecorator;
    ViewPager report;
    ReportViewPageAdapter reportAdapter;
    todayPageAdapter todayAdapter;
    String current;
    Toast toast;
    String response;
    int index;
    int itab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_layout, container, false);
        materialCalendarView = view.findViewById(R.id.calendarView);
        String FILE_NAME = "temp.json";
        file = new File(requireContext().getFilesDir(), FILE_NAME);
        tabLayout = view.findViewById(R.id.daytime);
        report = view.findViewById(R.id.graph_viewpager);
        Display display = requireActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        ViewGroup.LayoutParams params = report.getLayoutParams();
        report.setLayoutParams(params);
        materialCalendarView.setDateSelected(myDate, true);
        materialCalendarView.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit();
        materialCalendarView.addDecorator(new CurrentDayDecorator(myDate, true));
        selectTab = tabLayout.getTabAt(itab);
        selectTab.select();
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                TextView tabTextView = new TextView(requireContext());
                tab.setCustomView(tabTextView);
                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.setText(tab.getText());
                if (i == 0) {
                    tabTextView.setTypeface(Typeface.DEFAULT_BOLD);
                    tabTextView.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
        }
        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            params.height = (int) (size.y*0.95);
            current = convertCalendar(date);
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
            current = convertCalendar(upToDay);
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
                response = stringBuilder.toString();
                index = response.indexOf(current);
                if (index < 0) {
                    toast = Toast.makeText(getContext(), "No data found for this day", Toast.LENGTH_SHORT);
                    setToast();
                    report.setVisibility(View.GONE);
                } else {
                    if (itab != 0) {
                        selectTab = tabLayout.getTabAt(0);
                        selectTab.select();
                    } else {
                        tabSelect();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        materialCalendarView.setOnMonthChangedListener((widget, date) -> {
            params.height = (int) (size.y*0.95);
            if (itab == 1) {
                boolean verify = check(date);
                if (verify) {
                    toast = Toast.makeText(getContext(), "No data found for this week", Toast.LENGTH_SHORT);
                    setToast();
                    report.setVisibility(View.GONE);
                } else {
                    current = convertCalendar(date);
                    materialCalendarView.removeDecorators();
                    reportAdapter = new ReportViewPageAdapter(requireContext(), current, null, false);
                    report.setVisibility(View.VISIBLE);
                    report.setAdapter(reportAdapter);
                }
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        itab = tab.getPosition();
                        TextView text = (TextView) tab.getCustomView();
                        text.setTypeface(Typeface.DEFAULT_BOLD);
                        text.setTextColor(Color.parseColor("#FFFFFF"));
                        materialCalendarView.removeDecorators();
                        materialCalendarView.invalidateDecorators();
                        CalendarDay date = materialCalendarView.getSelectedDate();
                        boolean verify = check(date);
//                } else if (tab.getPosition() == 0) {
//                    if (date == myDate) {
//                        todayAdapter = new todayPageAdapter(requireContext(), MainActivity.mChart);
//                        report.setAdapter(todayAdapter);
//                    } else {
//                        report.setVisibility(View.VISIBLE);
//                        String object = response.substring(index - 2);
//                        reportAdapter = new ReportViewPageAdapter(requireContext(), current, object, true);
//                        report.setAdapter(reportAdapter);
//                    }
//                }
                        if (itab == 1) {
                            if (verify) {
                                toast = Toast.makeText(getContext(), "No data found for this week", Toast.LENGTH_SHORT);
                                setToast();
                                report.setVisibility(View.GONE);
                            } else {
                                current = convertCalendar(date);
                                materialCalendarView.removeDecorators();
                                reportAdapter = new ReportViewPageAdapter(requireContext(), current, null, false);
                                report.setVisibility(View.VISIBLE);
                                report.setAdapter(reportAdapter);
                            }
                        } else {
                            tabSelect();
                        }
//                if (tab.getPosition() == 1) {
//                    if (verify) {
//                        toast = Toast.makeText(getContext(), "No data found for this day", Toast.LENGTH_SHORT);
//                        setToast();
//                        report.setVisibility(View.GONE);
//                    } else {
//                        addDays(date);
//                        current = convertCalendar(date);
//                        reportAdapter = new ReportViewPageAdapter(requireContext(), current, null, false);
//                        report.setVisibility(View.VISIBLE);
//                        materialCalendarView.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit();
//                        report.setAdapter(reportAdapter);
//                    }
//                } else if (tab.getPosition() == 2) {
//                    if (upToDay == null || upToDay.equals(myDate)) {
//                        currentDayDecorator = new CurrentDayDecorator(myDate, true);
//                        materialCalendarView.addDecorator(currentDayDecorator);
//                    } else {
//                        currentDayDecorator = new CurrentDayDecorator(myDate, false);
//                        materialCalendarView.addDecorator(currentDayDecorator);
//                    }
//                    report.setVisibility(View.GONE);
//                    materialCalendarView.state().edit().setCalendarDisplayMode(CalendarMode.MONTHS).commit();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        TextView text = (TextView) tab.getCustomView();
                        text.setTypeface(Typeface.DEFAULT);
                        text.setTextColor(Color.parseColor("#9e9e9e"));
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
        return view;
    }

    private void tabSelect() {
        materialCalendarView.removeDecorators();
        materialCalendarView.invalidateDecorators();
        CalendarDay date = materialCalendarView.getSelectedDate();
        boolean verify = check(date);
        if (verify) {
            toast = Toast.makeText(getContext(), "No data found for this day", Toast.LENGTH_SHORT);
            setToast();
            report.setVisibility(View.GONE);
        } else {
            if (date == myDate) {
                todayAdapter = new todayPageAdapter(requireContext(), MainActivity.mChart);
                report.setAdapter(todayAdapter);
            } else {
                report.setVisibility(View.VISIBLE);
                String object = response.substring(index - 2);
                reportAdapter = new ReportViewPageAdapter(requireContext(), current, object, true);
                report.setAdapter(reportAdapter);
            }
        }
    }

    private boolean check(CalendarDay date) {
        String words = convertCalendar(date);
        boolean verify = false;
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
            response = stringBuilder.toString();
            index = response.indexOf(words);
            verify = index < 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return verify;
    }


    private String convertCalendar(CalendarDay date) {
        Calendar calendar = Calendar.getInstance();
        String bruh = String.valueOf(date);
        String bruhpt2 = bruh.substring(12, bruh.length()-1);
        SimpleDateFormat first_sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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
                Day = "0" + day;
            } else {
                Day = String.valueOf(day);
            }
            current = dayOfWeek + "." + year + "." + Month + "." + Day;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return current;
    }

//    private void addDays(CalendarDay date) {
//        Calendar calendar = Calendar.getInstance();
//        String bruh = String.valueOf(date);
//        String bruhpt2 = bruh.substring(12, bruh.length()-1);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//        try {
//            calendar.setTime(sdf.parse(bruhpt2));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        int c = calendar.get(Calendar.DAY_OF_WEEK);
//        localDate = getLocalDate(bruhpt2);
//        switch(c) {
//            case 1:
//                datesLeft.add(CalendarDay.from(localDate));
//                for (int t = 1; t < 6; t++) {
//                    datesCenter.add(CalendarDay.from(localDate.plusDays(t)));
//                }
//                datesRight.add(CalendarDay.from(localDate.plusDays(6)));
//                break;
//            case 2:
//                datesLeft.add(CalendarDay.from(localDate.minusDays(1)));
//                datesCenter.add(CalendarDay.from(localDate));
//                for (int t = 1; t < 5; t++) {
//                    datesCenter.add(CalendarDay.from(localDate.plusDays(t)));
//                }
//                datesRight.add(CalendarDay.from(localDate.plusDays(5)));
//                break;
//            case 3:
//                datesLeft.add(CalendarDay.from(localDate.minusDays(2)));
//                datesCenter.add(CalendarDay.from(localDate.minusDays(1)));
//                datesCenter.add(CalendarDay.from(localDate));
//                for (int t = 1; t < 4; t++) {
//                    datesCenter.add(CalendarDay.from(localDate.plusDays(t)));
//                }
//                datesRight.add(CalendarDay.from(localDate.plusDays(4)));
//                break;
//            case 4:
//                datesLeft.add(CalendarDay.from(localDate.minusDays(3)));
//                datesCenter.add(CalendarDay.from(localDate.minusDays(2)));
//                datesCenter.add(CalendarDay.from(localDate.minusDays(1)));
//                datesCenter.add(CalendarDay.from(localDate));
//                for (int t = 1; t < 3; t++) {
//                    datesCenter.add(CalendarDay.from(localDate.plusDays(t)));
//                }
//                datesRight.add(CalendarDay.from(localDate.plusDays(3)));
//                break;
//            case 5:
//                datesLeft.add(CalendarDay.from(localDate.minusDays(4)));
//                datesCenter.add(CalendarDay.from(localDate.minusDays(3)));
//                datesCenter.add(CalendarDay.from(localDate.minusDays(2)));
//                datesCenter.add(CalendarDay.from(localDate.minusDays(1)));
//                datesCenter.add(CalendarDay.from(localDate));
//                for (int t = 1; t < 2; t++) {
//                    datesCenter.add(CalendarDay.from(localDate.plusDays(t)));
//                }
//                datesRight.add(CalendarDay.from(localDate.plusDays(2)));
//                break;
//            case 6:
//                datesLeft.add(CalendarDay.from(localDate.minusDays(5)));
//                for (int t = 4; t > 0; t--) {
//                    datesCenter.add(CalendarDay.from(localDate.minusDays(t)));
//                }
//                datesCenter.add(CalendarDay.from(localDate));
//                datesRight.add(CalendarDay.from(localDate.plusDays(1)));
//                break;
//            case 7:
//                datesLeft.add(CalendarDay.from(localDate.minusDays(6)));
//                for (int t = 5; t > 0; t--) {
//                    datesCenter.add(CalendarDay.from(localDate.minusDays(t)));
//                }
//                datesRight.add(CalendarDay.from(localDate));
//                break;
//        }
//
//        setDecor(datesLeft, R.drawable.g_left);
//        setDecor(datesCenter, R.drawable.g_center);
//        setDecor(datesRight, R.drawable.g_right);
//    }

//    private LocalDate getLocalDate(String ld) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//        try {
//            Date input = sdf.parse(ld);
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(input);
//            return LocalDate.of(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
//        } catch (NullPointerException | ParseException e) {
//            return null;
//        }
//    }

//    private void setDecor(List<CalendarDay> calendarDayList, int drawable) {
//        weekDecorator = new WeekDecorator(requireContext(), drawable, calendarDayList);
//        materialCalendarView.addDecorators(weekDecorator);
//    }

    public void setToast() {
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.show();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        onResume();
    }
}
