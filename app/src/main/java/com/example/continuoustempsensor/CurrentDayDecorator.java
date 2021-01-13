package com.example.continuoustempsensor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class CurrentDayDecorator implements DayViewDecorator {

    CalendarDay currentDay;
    Boolean white;

    public CurrentDayDecorator(CalendarDay currentDay, boolean white) {
        this.currentDay = currentDay;
        this.white = white;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(currentDay);
    }

    @Override
    public void decorate(DayViewFacade view) {
        if (white) {
            view.addSpan(new ForegroundColorSpan(Color.WHITE));
        } else {
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }
}
