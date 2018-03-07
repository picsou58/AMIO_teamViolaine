package com.example.root.project_amio;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.MalformedURLException;
import java.util.Timer;
import java.util.TimerTask;

public class RestService extends Service{
    RestTask restTask;
    Timer timer;
    TimerTask timerTask;

    public RestService() {
    }
    @Override
    public void onCreate() {
        Log.i("Service onCreate", "restService");


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        startTimer();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                try {
                    restTask = new RestTask("http://iotlab.telecomnancy.eu/rest/data/1/light1/last", getApplicationContext());
                    restTask.execute();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        };
    }







}
