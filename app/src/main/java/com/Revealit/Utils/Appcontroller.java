package com.Revealit.Utils;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.Revealit.CommonClasse.Constants;
import com.pusher.pushnotifications.PushNotifications;

public class Appcontroller extends MultiDexApplication {
    public static final String TAG = Appcontroller.class.getSimpleName();
    private static Appcontroller mInstance;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //MULTIDEX
        MultiDex.install(this);






    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;


//        //TEST FAIRY SDK
//        TestFairy.begin(this, "SDK-5f7VDJMC");
//
//        //SHAKE FEATURE
//        TestFairy.enableFeedbackForm("shake");




    }

    public static synchronized Appcontroller getInstance() {
        return mInstance;
    }
}
