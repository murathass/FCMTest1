package com.biotek.mobil.fcmtest1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

public class SharedPref  {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SharedPref(Context context){
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = preferences.edit();
    }



    public void putLoginInfo(String user){
        editor.putString("user",user);
        editor.commit();
    }

    public String getLoginInfo() {
        return preferences.getString("user","0");
    }
}
