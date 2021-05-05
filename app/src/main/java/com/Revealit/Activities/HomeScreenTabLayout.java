package com.Revealit.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.Revealit.Adapter.FragmentAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.Fragments.HomeFragment;
import com.Revealit.Fragments.ListenFragment;
import com.Revealit.Fragments.PlayFragment;
import com.Revealit.Fragments.WalletFragment;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.security.Key;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreenTabLayout extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    ArrayList<Fragment> fragments;
    Toolbar toolbar;
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tab_layout);


        setId();


    }

    private void setId() {

        mActivity = HomeScreenTabLayout.this;
        mContext = HomeScreenTabLayout.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        fragments = new ArrayList<>();

        fragments.add(new ListenFragment());
        fragments.add(new PlayFragment());
        fragments.add(new WalletFragment());

        FragmentAdapter pagerAdapter = new FragmentAdapter(getSupportFragmentManager(), getApplicationContext(), fragments);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        //DEFAULT SELECT PLAY FRAGMENT
        tabLayout.getTabAt(1).select();

        tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.mic));
        tabLayout.getTabAt(0).setText(getResources().getString(R.string.strListen));

        tabLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.watch));
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.strPlay));

        tabLayout.getTabAt(2).setIcon(getResources().getDrawable(R.drawable.wallet));
        tabLayout.getTabAt(2).setText(getResources().getString(R.string.strWallet));

        //DEFAULT ICON COLOR
        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorGrayBottomBar), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBlueBottomBar), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorGrayBottomBar), PorterDuff.Mode.SRC_IN);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {


                    case 0:

                        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBlueBottomBar), PorterDuff.Mode.SRC_IN);
                        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorGrayBottomBar), PorterDuff.Mode.SRC_IN);
                        tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorGrayBottomBar), PorterDuff.Mode.SRC_IN);

                        break;
                    case 1:

                        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorGrayBottomBar), PorterDuff.Mode.SRC_IN);
                        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBlueBottomBar), PorterDuff.Mode.SRC_IN);
                        tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorGrayBottomBar), PorterDuff.Mode.SRC_IN);

                        break;
                    case 2:
                        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorGrayBottomBar), PorterDuff.Mode.SRC_IN);
                        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorGrayBottomBar), PorterDuff.Mode.SRC_IN);
                        tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBlueBottomBar), PorterDuff.Mode.SRC_IN);

                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

}