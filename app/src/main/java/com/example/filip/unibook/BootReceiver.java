package com.example.filip.unibook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ludvig on 2018-03-20.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    //Startar MyService så att den körs i bakgrunden.
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceIntent = new Intent("com.example.filip.unibook");
            //serviceIntent.setPackage("com.example.filip.unibook");
            context.startService(serviceIntent);
        }
    }
}

