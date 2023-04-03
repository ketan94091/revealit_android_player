package com.Revealit.services;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.Activities.InternetBrokenActivity;

public class NetworkChangeReceiver extends BroadcastReceiver
{
    HomeScreenTabLayout homeScreenTabLayout;

    public NetworkChangeReceiver(HomeScreenTabLayout homeScreenTabLayout) {
        this.homeScreenTabLayout= homeScreenTabLayout;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            if (!isOnline(context)) {

                Intent mIntent = new Intent(homeScreenTabLayout, InternetBrokenActivity.class);
                homeScreenTabLayout.startActivity(mIntent);
                homeScreenTabLayout.finishAffinity();

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                @SuppressLint("MissingPermission") NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }

            }
            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
