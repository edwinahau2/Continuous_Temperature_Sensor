package com.example.continuoustempsensor;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class MyBarDataSet extends BarDataSet {

    public MyBarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);
    }

    public int getColors(int index) {
        if(getEntryForIndex(index).getY() < 100.3 || getEntryForIndex(index).getY() < 37.9) {
            return mColors.get(0);
        } else if ((getEntryForIndex(index).getY() <= 103 && getEntryForIndex(index).getY() >= 100.4) || (getEntryForIndex(index).getY() <= 39.4 && getEntryForIndex(index).getY() >= 38)) {
            return mColors.get(1);
        } else {
            return mColors.get(2);
        }
    }
}
