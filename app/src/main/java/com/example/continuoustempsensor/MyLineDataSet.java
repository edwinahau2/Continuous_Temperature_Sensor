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
        if (getEntryForIndex(index).getY() < 100.3 || getEntryForIndex(index).getY() < 37.9) {
            return mColors.get(0);
        } else if ((getEntryForIndex(index).getY() <= 103 && getEntryForIndex(index).getY() >= 100.4) || (getEntryForIndex(index).getY() <= 39.4 && getEntryForIndex(index).getY() >= 38)) {
            return mColors.get(1);
        } else {
            return mColors.get(2);
        }
    }
}
