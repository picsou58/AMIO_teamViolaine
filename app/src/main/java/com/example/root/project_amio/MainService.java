package com.example.root.project_amio;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainService extends Service {
    Timer timer;
    TimerTask timerTask;
    RestTask restTask;
    Context context;
    SharedPreferences sharedPrefs;
    public static final String CHANNEL_ID = "1";
    private ArrayList<LumData> lumData = new ArrayList<>();
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Double value1 = intent.getDoubleExtra("value1", 20);
            Long timestamp1 = intent.getLongExtra("timestamp1", 20);
            String label1 = intent.getStringExtra("label1");
            Double mote1 = intent.getDoubleExtra("mote1", 0.0);
            Double value2 = intent.getDoubleExtra("value2", 20);
            Long timestamp2 = intent.getLongExtra("timestamp2", 20);
            String label2 = intent.getStringExtra("label2");
            Double mote2 = intent.getDoubleExtra("mote2", 0.0);

            LumData data1 = new LumData(value1, mote1,new Date(new Timestamp(timestamp1).getTime()), label1 );
            LumData data2 = new LumData(value2, mote2,new Date(new Timestamp(timestamp2).getTime()), label2 );
            Calendar cal = Calendar.getInstance();
            cal.setTime(data1.getTime());
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int startNHW = (int) Integer.parseInt(sharedPrefs.getString("startHourNotifWeek", "19"));
            int endNHW = (int) Integer.parseInt(sharedPrefs.getString("endHourNotifWeek", "23"));
            int startMHW = (int) Integer.parseInt(sharedPrefs.getString("startHourMailWeek", "23"));
            int endMHW = (int) Integer.parseInt(sharedPrefs.getString("endHourMailWeek", "6"));
            int startMHWE = (int) Integer.parseInt(sharedPrefs.getString("startHourMailWeekEnd", "19"));
            int endMHWE = (int) Integer.parseInt(sharedPrefs.getString("endHourMailWeekEnd", "23"));
            String fromEmail = sharedPrefs.getString("from", "tom.barat@telecomnancy.net");
            String toEmail = sharedPrefs.getString("to", "t.barat@orange.fr");
            boolean orderNW = true;
            boolean orderMW = false;
            boolean orderMWE = true;
            if(startNHW>endNHW){
                orderNW = false;
            }else{
                orderNW = true;
            }
            if(startMHW<endMHW){
                orderMW = true;
            }else{
                orderMW = false;
            }
            if(startMHWE>endMHWE){
                orderMWE = false;
            }else{
                orderMWE = true;
            }

            for(int i=0;i<lumData.size();i++){
                if(data1.getMote().equals(lumData.get(i).getMote())){
                    if(lumData.get(i).getValue()<250 && data1.getValue()>250){
                        if( day!=Calendar.SATURDAY && day!=Calendar.SUNDAY && ((orderNW && data1.getTime().getHours()>=startNHW && data1.getTime().getHours()<=endNHW)|| (!orderNW && (data1.getTime().getHours()>=startNHW || data1.getTime().getHours()<=endNHW)))){
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                                    .setContentTitle("Mote Stat Change")
                                    .setContentText("Mote : "+data1.getMote()+" changed state.")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

// notificationId is a unique int for each notification that you must define
                            notificationManager.notify(1, mBuilder.build());
                        }
                        if( day!=Calendar.SATURDAY && day!=Calendar.SUNDAY && ((orderMW && data1.getTime().getHours()>=startMHW && data1.getTime().getHours()<=endMHW)|| (!orderMW && (data1.getTime().getHours()>=startMHW || data1.getTime().getHours()<=endMHW)))) {

                            Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.fromParts(
                                    "mailto", fromEmail, null));
                            emailIntent.setType("text/plain");
                            //emailIntent.setData(Uri.parse("mailto:"));
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mote changed state");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "Mote " + data1.getMote() + " changed state.");
                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        }
                        if( (day==Calendar.SATURDAY || day==Calendar.SUNDAY) && ((orderMWE && data1.getTime().getHours()>=startMHWE && data1.getTime().getHours()<=endMHWE)|| (!orderMWE && (data1.getTime().getHours()>=startMHWE || data1.getTime().getHours()<=endMHWE)))) {
                            Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.fromParts(
                                    "mailto", fromEmail, null));
                            emailIntent.setType("text/plain");
                            //emailIntent.setData(Uri.parse("mailto:"));
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mote changed state");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "Mote " + data1.getMote() + " changed state.");
                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        }
                    }
                    lumData.get(i).update(data1);
                }
                if(lumData.get(i).getMote().equals(data2.getMote())){
                    if(lumData.get(i).getValue()<250 && data2.getValue()>250){
                        if( day!=Calendar.SATURDAY && day!=Calendar.SUNDAY && ((orderNW && data2.getTime().getHours()>=startNHW && data2.getTime().getHours()<=endNHW)|| (!orderNW && (data2.getTime().getHours()>=startNHW || data2.getTime().getHours()<=endNHW)))){
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                                    .setContentTitle("Mote Stat Change")
                                    .setContentText("Mote : "+data2.getMote()+" changed state.")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

// notificationId is a unique int for each notification that you must define
                            notificationManager.notify(1, mBuilder.build());
                        }
                        if( day!=Calendar.SATURDAY && day!=Calendar.SUNDAY && ((orderMW && data2.getTime().getHours()>=startMHW && data2.getTime().getHours()<=endMHW)|| (!orderMW && (data2.getTime().getHours()>=startMHW || data2.getTime().getHours()<=endMHW)))) {

                            Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.fromParts(
                                    "mailto", fromEmail, null));
                            emailIntent.setType("text/plain");
                            //emailIntent.setData(Uri.parse("mailto:"));
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mote changed state");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "Mote " + data2.getMote() + " changed state.");
                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        }
                        if( (day==Calendar.SATURDAY || day==Calendar.SUNDAY) && ((orderMWE && data2.getTime().getHours()>=startMHWE && data2.getTime().getHours()<=endMHWE)|| (!orderMWE && (data2.getTime().getHours()>=startMHWE || data2.getTime().getHours()<=endMHWE)))) {
                            Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.fromParts(
                                    "mailto", fromEmail, null));
                            emailIntent.setType("text/plain");
                            //emailIntent.setData(Uri.parse("mailto:"));
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mote changed state");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "Mote " + data2.getMote() + " changed state.");
                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        }
                    }
                    lumData.get(i).update(data2);
                }
            }
            if(lumData.isEmpty()){

                lumData.add(data1);
                lumData.add(data2);
            }
        }
    };
    public MainService() {
        this.context = this;


    }
    @Override
    public void onCreate() {
        sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        Log.i("Service onCreate", "main Service");
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("updateHttp"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            NotificationManager notificationManager1 = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            notificationManager1.createNotificationChannel(channel);

        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
                try {
                    View view = View.inflate(getApplicationContext(),R.layout.content_main, null);
                    restTask = new RestTask("http://iotlab.telecomnancy.eu/rest/data/1/light1/last", getApplicationContext());
                    restTask.execute();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy(){

        Log.i("mainService Stop", "main service was stopped");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        timer.cancel();
    }

}
