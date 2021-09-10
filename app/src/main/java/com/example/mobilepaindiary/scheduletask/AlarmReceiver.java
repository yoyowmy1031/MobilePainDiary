package com.example.mobilepaindiary.scheduletask;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){
        Log.e("alert", "Alarm intent is received: " + intent.getExtras().getString("alert"));
        Toast.makeText(context, "Alarm: " + intent.getExtras().getString("alert"), Toast.LENGTH_LONG).show();
    }
}
