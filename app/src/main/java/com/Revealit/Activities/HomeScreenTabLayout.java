package com.Revealit.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.Revealit.Adapter.FragmentAdapter;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.CustomViews.CustomViewPager;
import com.Revealit.Fragments.ListenFragment;
import com.Revealit.Fragments.PlayFragment;
import com.Revealit.Fragments.ProfileFragmentContainer;
import com.Revealit.Fragments.WalletFragment;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class HomeScreenTabLayout extends AppCompatActivity {

    public static CustomViewPager viewPager;
    public static TabLayout tabLayout;
    ArrayList<Fragment> fragments;
    Toolbar toolbar;
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private boolean isUserIsActive,isFromRegistrationScreen;
    public static View viewBottomLine;
    private int REQUEST_CAMERA_PERMISSIONc=100;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tab_layout);

        setId();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setId() {

        mActivity = HomeScreenTabLayout.this;
        mContext = HomeScreenTabLayout.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (CustomViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        viewBottomLine=(View)findViewById(R.id.viewBottomLine);

        //GET INTENT DATA
        isFromRegistrationScreen = getIntent().getBooleanExtra(Constants.KEY_IS_FROM_REGISTRATION_SCREEN, false);
        isUserIsActive = mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_ACTIVE);

        //INITIALISE FRAGMENTS FOR VIEW PAGER
        fragments = new ArrayList<>();

        //ADD FRAGMENTS TO VIEW PAGER
        fragments.add(new ListenFragment(this));
        fragments.add(new PlayFragment(this));
        fragments.add(new WalletFragment(this));
        fragments.add(new ProfileFragmentContainer(this));
        //fragments.add(new UserProfileFragment(this));

        //ATTACHED ADAPTER
        FragmentAdapter pagerAdapter = new FragmentAdapter(getSupportFragmentManager(), getApplicationContext(), fragments);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        //SET PAGER OFFSET LIMIT - LOADING ALL PAGES
        viewPager.setOffscreenPageLimit(3);



        tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.mipmap.icon_mic_home));
        //tabLayout.getTabAt(0).setText(getResources().getString(R.string.strListen));

        tabLayout.getTabAt(1).setIcon(getResources().getDrawable(R.mipmap.icon_play_home));
        //tabLayout.getTabAt(1).setText(getResources().getString(R.string.strPlay));

        tabLayout.getTabAt(2).setIcon(getResources().getDrawable(R.mipmap.icon_wallet_home));
        //tabLayout.getTabAt(2).setText(getResources().getString(R.string.strWallet));

        tabLayout.getTabAt(3).setIcon(getResources().getDrawable(R.mipmap.icon_profile_home));
        //tabLayout.getTabAt(3).setText(getResources().getString(R.string.strLogout));

        //DEFAULT SELECT PROFILE SCREEN IF USER IS FROM REGISTRATION PAGE
        //IF USER IS NOT ACTIVATED
        //IF USER ACTIVATED BUT HE IS FROM REGISTRATION SCREEN - FIRST SHOW THE PROFILE SCREEN THAN USUALL PLAY SCREEN
        if (!isUserIsActive  || isUserIsActive && isFromRegistrationScreen) {
            //SELECT USER PROFILE FRAGMENT
            selectPage(3);
            //DEFAULT ICON COLOR
            if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
                tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorNewAppGreen));

            } else {
                tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
                tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorBlue));
            }


        } else{
            //SELECT PLAY FRAGMENT
            selectPage(1);

            //DEFAULT ICON COLOR
            if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorNewAppGreen));

            } else {
                tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorBlue));
            }
        }



        if (!isUserIsActive) {

            //MAKE VIEW PAGER SCROLLABLE FALSE
            viewPager.disableScroll(true);

            //DISABLE BOTTOM BAR ICON CLICK IF USER IS NOT ACTIVE
            enableDisableBottomBar(false);

        }else{
            //MAKE VIEW PAGER SCROLLABLE TURE
            viewPager.disableScroll(false);

            //DISABLE BOTTOM BAR ICON CLICK IF USER IS NOT ACTIVE
            enableDisableBottomBar(true);
        }



        //CHANGE BOTTOM VIEW COLOR
        changeBottomViewColor();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {

                    case 0:
                        if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorNewAppGreen));

                        } else {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorBlue));
                        }

                        break;
                    case 1:

                        if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorNewAppGreen));

                        } else {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorBlue));
                        }


                        break;
                    case 2:
                        if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorNewAppGreen));

                        } else {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorBlue));
                        }
                        break;

                    case 3:
                        if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorNewAppGreen));

                        } else {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorBlue));
                        }

                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //ASK FOR THE CAMARA PERMISSION
        requestCamaraPermission();


    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCamaraPermission() {

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    private void changeBottomViewColor() {
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)){
            viewBottomLine.setBackgroundColor(getResources().getColor(R.color.colorNewAppGreen));
        }else{
            viewBottomLine.setBackgroundColor(getResources().getColor(R.color.colorBlue));
        }

    }

    private void enableDisableBottomBar(boolean shouldDisable) {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);

        for(int i =0 ;i< 4 ;i++){
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(i);
            vgTab.setEnabled(shouldDisable);
        }
    }

    void selectPage(int pageIndex){
        tabLayout.setScrollPosition(pageIndex,0f,true);
        viewPager.setCurrentItem(pageIndex);
    }
}