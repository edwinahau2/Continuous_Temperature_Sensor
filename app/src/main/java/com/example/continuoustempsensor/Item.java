package com.example.continuoustempsensor;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Item {
   private String Week;
   private List<SubItem> subItemList;

    public Item(String week, List<SubItem> subItemList) {
        this.Week = week;
        this.subItemList = subItemList;
    }

    public String getWeek() {
        return Week;
    }

    public List<SubItem> getSubItemList(){
        return subItemList;
    }

    public void setSubItemList(List<SubItem> subItemList) {
        this.subItemList = subItemList;
    }
}
