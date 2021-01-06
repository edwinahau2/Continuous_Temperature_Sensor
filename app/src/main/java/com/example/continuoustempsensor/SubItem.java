package com.example.continuoustempsensor;

public class SubItem {
    String Week, Temp;
    public SubItem(String week, String temp) {
        Week = week;
        Temp = temp;
    }

    public void setString(String week, String temp) {
        this.Week = week;
        this.Temp = temp;
    }

    public String getWeekDay() {
        return Week;
    }

    public String getTemp() {
        return Temp;
    }
}
