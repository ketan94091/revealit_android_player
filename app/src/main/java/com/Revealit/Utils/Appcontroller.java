package com.Revealit.Utils;

import android.app.Application;

import com.droidnet.DroidNet;

public class Appcontroller extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DroidNet.init(this);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        DroidNet.getInstance().removeAllInternetConnectivityChangeListeners();
    }
}
