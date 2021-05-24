package com.Revealit.CommonClasse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppController extends MultiDexApplication {

    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        FacebookSdk.sdkInitialize(getApplicationContext());

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }

    public static synchronized AppController getInstance()
    {
        return mInstance;
    }







}