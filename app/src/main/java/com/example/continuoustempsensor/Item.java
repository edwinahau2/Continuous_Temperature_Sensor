package com.example.continuoustempsensor;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Item {
   private String Week;

    public Item(String week) {
        this.Week = week;
    }

    public String getWeek() {
        return Week;
    }
}
