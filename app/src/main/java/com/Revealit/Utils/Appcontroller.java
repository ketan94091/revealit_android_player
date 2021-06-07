package com.Revealit.Utils;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.Revealit.R;
import com.facebook.FacebookSdk;
import com.testfairy.TestFairy;

public class Appcontroller extends MultiDexApplication {
    public static final String TAG = Appcontroller.class.getSimpleName();
    private static Appcontroller mInstance;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //MULTIDEX
        MultiDex.install(this);

        //FACEBOOK SDK
        //FacebookSdk.sdkInitialize(this);


    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;


        //TEST FAIRY SDK
        TestFairy.begin(this, "SDK-5f7VDJMC");

        //SHAKE FEATURE
        TestFairy.enableFeedbackForm("shake");


    }

    public static synchronized Appcontroller getInstance() {
        return mInstance;
    }
}
