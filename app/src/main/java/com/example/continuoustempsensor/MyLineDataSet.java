package com.example.continuoustempsensor;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

public class MyLineDataSet extends LineDataSet {

    public MyLineDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public int getColor(int index) {
        return mColors.get(0);
    }
}
