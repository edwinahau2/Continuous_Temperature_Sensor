package com.example.continuoustempsensor;

import android.util.Log;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NotifTimer extends TimerTask {

    @Override
    public void run() {
        //System.out.println("Timer task started at:"+new Date());
        Date date = new Date();
        format(DateTimeFormatter date)
        Log.i("Timer task started at:"+ toString(date));
        completeTask();
        //System.out.println("Timer task finished at:"+new Date());
        Log.i("Timer task finished at:"+new Date());
    }

    private void completeTask() {
        try {
            //assuming it takes 20 secs to complete the task
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        TimerTask timerTask = new NotifTimer();
        //running timer task as daemon thread
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 10*1000);
        System.out.println("TimerTask started");

        if(fevertemp or generalupdate) {
            // depending on the situation, there will be a different time for which each will trigger
            // fevertemp timer will be used so that it won't retrigger for a certain amount of time
            // generalupdate timer will be used that it will regularly check and trigger a notification
            //cancel after sometime
            try {
                Thread.sleep(120000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timer.cancel();
            System.out.println("TimerTask cancelled");
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
