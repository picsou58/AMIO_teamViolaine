package com.example.root.project_amio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by root on 29/01/18.
 */

public class MyBootBroadcastReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;


    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("startBoot",true)){
            Log.i("boolean fetched", "true");
        }else{
            Log.i("boolean fetched", "false");
        }
    }
}
