package com.example.continuoustempsensor;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class fragment_tab2 extends Fragment {

    Button add;
    RecyclerView daRecycle;
    ViewPager viewPager;
    LineChart mChart;
    GraphPageAdapter graphPageAdapter;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat time = new SimpleDateFormat("h:mm a");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat day = new SimpleDateFormat("EEE");
    String dayOfWeek = day.format(Calendar.getInstance().getTime());
    LineDataSet set;
    LineData data;
    List<Entry> tempEntries = new ArrayList<>();
    int i = 0;
    File file;
    FileReader fileReader = null;
    BufferedReader bufferedReader = null;
    List<String> dayArray = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_layout, container, false);
        add = view.findViewById(R.id.button2);
        daRecycle = view.findViewById(R.id.alphaRV);
        viewPager = view.findViewById(R.id.viewpager2);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemAdapter itemAdapter = new ItemAdapter(buildItemList());
                daRecycle.setAdapter(itemAdapter);
                daRecycle.setLayoutManager(layoutManager);
            }
        });
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

    private List<Item> buildItemList() {
        List<Item> daysList = new ArrayList<>();
        for (int i=0; i<10; i++) {
            Item item = new Item("Item "+i, buildSubItemList());
            daysList.add(item);
        }
        return daysList;
    }

    private List<SubItem> buildSubItemList() {
        List<SubItem> subItemList = new ArrayList<>();
        for (int i=0; i<3; i++) {
            SubItem subItem = new SubItem("Sub Item "+i, "Description "+i);
            subItemList.add(subItem);
        }
        return subItemList;
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
            viewPager.setAdapter(graphPageAdapter);
            break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        onResume();
    }
}
