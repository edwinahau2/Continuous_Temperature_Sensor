package com.example.continuoustempsensor;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashSet;
import java.util.List;

public class WeekDecorator implements DayViewDecorator {

    Context context;
    private int drawable;
    private HashSet<CalendarDay> dates;

    public WeekDecorator(Context context, int drawable, List<CalendarDay> calendarDay1) {
        this.context = context;
        this.drawable = drawable;
        this.dates = new HashSet<>(calendarDay1);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        Drawable d = ContextCompat.getDrawable(context, drawable);
        view.setBackgroundDrawable(d);
    }
}
