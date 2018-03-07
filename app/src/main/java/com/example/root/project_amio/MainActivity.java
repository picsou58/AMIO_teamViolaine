package com.example.root.project_amio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private boolean serviceStat = false;
    private Intent serviceIntent ;
    private Intent reqIntent;
    private SharedPreferences sharedPreferences;
    private TextView httpResult;
    private TextView mote1Name;
    private TextView mote2Name;
    private TextView mote1Value;
    private TextView mote2Value;

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
            lumData.clear();
            lumData.add(new LumData(value1, mote1,new Date(new Timestamp(timestamp1).getTime()), label1 ));
            lumData.add(new LumData(value2, mote2,new Date(new Timestamp(timestamp2).getTime()), label2 ));

            Timestamp ts = new Timestamp(timestamp1);
            Date date = new Date(ts.getTime());
            httpResult.setText(""+value1+", measured : "+date);
            mote1Name.setText("Mote : "+lumData.get(0).getMote());
            mote2Name.setText("Mote : "+lumData.get(1).getMote());
            mote1Value.setText("Value : "+lumData.get(0).getValue());
            mote2Value.setText("Value : "+lumData.get(1).getValue());
            if(lumData.get(0).getValue()>250){
                mote1Value.setText(mote1Value.getText()+", Lit");
            }
            if(lumData.get(1).getValue()>250){
                mote1Value.setText(mote2Value.getText()+", Lit");
            }
        }
    };
    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                    mMessageReceiver, new IntentFilter("updateHttp"));



        serviceIntent = new Intent(this, MainService.class);
        reqIntent = new Intent(this, RestService.class);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton3);
        mote1Name = findViewById(R.id.textView5);
        mote2Name = findViewById(R.id.textView7);
        mote1Value = findViewById(R.id.textView6);
        mote2Value = findViewById(R.id.textView11);
        httpResult = (TextView) findViewById(R.id.textView12);
        Button reqButton = (Button) findViewById(R.id.button);
        reqButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                   startService(reqIntent);

                    }
                }
        );
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Log.i("state change ", "state change");
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putBoolean("startBoot", b);
                        editor.commit();
                    }
                }
        );
        final TextView serviceStatus = (TextView) findViewById(R.id.textView9);
        toggleButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        serviceStat = b;
                        if(serviceStat){
                            startService(serviceIntent);
                            serviceStatus.setText("Running...");
                        }else{
                            stopService(serviceIntent);
                            serviceStatus.setText("Press Enable Service to Start");
                        }

                    }
                    }

        );



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

   static public void updateHttpResult(Object value, Object timestamp) {

//         Timestamp ts = new Timestamp((Long)timestamp);
//         Date date = new Date(ts.getTime());
//         Log.i("textview exists", httpResult.getText().toString());
//        int count = 3;
//         httpResult.setText(count);
//        Log.i("textview exists", httpResult.getText().toString());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsAMIOActivity.class);
            MainActivity.this.startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopService(new Intent(this, MainService.class));
        stopService(new Intent(this,RestService.class));
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }




}
