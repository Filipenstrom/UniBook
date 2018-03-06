package com.example.filip.unibook;

import android.content.Context;
import android.preference.PreferenceManager;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Ludvig on 2018-03-06.
 */

public class SharedPreferences {

    private android.content.SharedPreferences prefs;

    public SharedPreferences(Context ctx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public void setusername(String username) {
        prefs.edit().putString("username", username).commit();
    }

    public String getusername() {
        String usename = prefs.getString("username","");
        //new GetUrlContentTask().execute(usename);
        return usename;
    }
}
