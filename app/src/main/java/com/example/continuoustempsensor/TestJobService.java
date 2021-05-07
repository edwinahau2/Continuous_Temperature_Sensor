package com.example.continuoustempsensor;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;


public class TestJobService extends JobService {

    private static final String TAG = "ExampleJobService";
    private boolean jobCancelled = false;
    JobParameters param;
    String address;
//Aryan's code that I commented out
/*
    @Override
    public boolean onStartJob(JobParameters params) {
        this.param = params;
        Log.d(TAG, "onStartJob");
        PersistableBundle bundle = params.getExtras();
        address = bundle.getString("address");
        Log.d(TAG, "address: " + address);
        int counter = getCounter();
        Log.d(TAG, "Work Done: counter = " + counter);
        setCounter(counter + 1);
        jobFinished(param, false);
        Log.d(TAG, "-----------------------");
//        new MyWorker(getApplicationContext(), this, params).execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job Cancelled");
        return true;
    }

    private void setCounter(int counter) {
        SharedPreferences sp = this.getSharedPreferences("getCounter", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("counter", counter);
        editor.apply();
    }

    private int getCounter() {
        SharedPreferences shp = this.getSharedPreferences("getCounter", MODE_PRIVATE);
        return shp.getInt("counter", 0);
    }
*/

// my code
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);

        return true; // do you want to reschedule
    }

    private void doBackgroundWork(JobParameters params){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 10; i++){ // fake background work
                    Log.d(TAG,"run:" + i);
                    if(jobCancelled){
                        return; // if we cancelled job then will exit and stop doing background work
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true; // do you want to reschedule
    }
    //Aryan's code I commented out
/*

    private void setCounter(int counter) {
        SharedPreferences sp = this.getSharedPreferences("getCounter", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("counter", counter);
        editor.apply();
    }

    private int getCounter() {
        SharedPreferences shp = this.getSharedPreferences("getCounter", MODE_PRIVATE);
        return shp.getInt("counter", 0);
    }
*/

//Aryan's code that was already commented out
//    private static class MyWorker extends AsyncTask<Void, Void, Boolean> {
//
//        private final Context mContext;
//        @SuppressLint("StaticFieldLeak")
//        private final TestJobService tjs;
//        private final JobParameters jp;
//
//        public MyWorker(Context context, TestJobService testJobService, JobParameters jobParameters) {
//            mContext = context;
//            tjs = testJobService;
//            jp = jobParameters;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void...voids) {
//            Log.d(TAG, "Work Start");
//            int counter = getCounter();
//            Log.d(TAG, "Work Done: counter = " + counter);
//            setCounter(counter + 1);
////            Log.d(TAG, "address: " + );
//            return false;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean) {
//            tjs.jobFinished(jp, false);
//            Log.d(TAG, "-----------------------");
//        }
//
//        private void setCounter(int counter) {
//            SharedPreferences sp = mContext.getSharedPreferences("getCounter", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putInt("counter", counter);
//            editor.apply();
//        }
//
//        private int getCounter() {
//            SharedPreferences shp = mContext.getSharedPreferences("getCounter", MODE_PRIVATE);
//            return shp.getInt("counter", 0);
//        }
//    }

}
