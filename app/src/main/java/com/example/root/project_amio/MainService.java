package com.example.root.project_amio;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MainService extends Service {
    Timer timer;
    TimerTask timerTask;
    public MainService() {
    }
    @Override
    public void onCreate() {
        Log.i("Service onCreate", "infoService 36-15");


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        startTimer();
        return START_STICKY;
    }
    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, 10000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Truc périodique", "le truc qu'on fait périodiquement");
            }
        };
    }

    private void handleCommand(Intent intent) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy(){

        Log.i("serviceStop", "le 36-15 n'est plus en ligne, boloss");
        timer.cancel();
    }

}
