package com.Revealit.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.Revealit.Adapter.FragmentAdapter;
import com.Revealit.Adapter.SilosAvailableAccountsListAdapterPopup;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.CommonClasse.SwipeHelper;
import com.Revealit.CustomViews.CustomViewPager;
import com.Revealit.Fragments.ListenFragment;
import com.Revealit.Fragments.PlayFragment;
import com.Revealit.Fragments.ProfileFragmentContainer;
import com.Revealit.Fragments.WalletFragment;
import com.Revealit.Interfaces.DeleteVideoRevealsInterface;
import com.Revealit.Interfaces.IsSimulationModeIsActiveInterface;
import com.Revealit.ModelClasses.KeyStoreServerInstancesModel;
import com.Revealit.ModelClasses.NewAuthLogin;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.UserOnboardingProcess.AddRefferalAndEarnActivity;
import com.Revealit.UserOnboardingProcess.NewAuthGetStartedActivity;
import com.Revealit.UserOnboardingProcess.NewAuthSplashScreen;
import com.Revealit.services.NetworkChangeReceiver;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pusher.pushnotifications.PushNotificationReceivedListener;
import com.pusher.pushnotifications.PushNotifications;
import com.pusher.pushnotifications.PushNotificationsInstance;
import com.pusher.pushnotifications.auth.BeamsTokenProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreenTabLayout extends AppCompatActivity implements DeleteVideoRevealsInterface, IsSimulationModeIsActiveInterface, View.OnClickListener{

    private static final String TAG ="HomeScreenTabLayout" ;
    private CustomViewPager viewPager;
    private RelativeLayout relativeTab;
    private LinearLayout linearListenScreenControls;
    private TabLayout tabLayout;
    ArrayList<Fragment> fragments;
    Toolbar toolbar;
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private boolean isUserIsActive;
    private View viewBottomLine;
    private int REQUEST_CAMERA_PERMISSIONc=100;
    private BeamsTokenProvider tokenProvider;
    private Gson gson;
    private DeleteVideoRevealsInterface mDeleteVideoRevealsInterface;
    private IsSimulationModeIsActiveInterface mIsSimulationModeIsActiveInterface;
    private LinearLayout linearDelete;
    private RecyclerView recycleAccountList;
    private String strAccountName,strPrivateKey,strRevealitPrivateKey;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private BroadcastReceiver mNetworkReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tab_layout);


            setId();
            setOnClicks();

    }


    private void setId()  {

        gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();

        mActivity = HomeScreenTabLayout.this;
        mContext = HomeScreenTabLayout.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        mNetworkReceiver = new NetworkChangeReceiver(this);
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        //INITIALIZE INTERFACE FOR MULTI VIDEO DELETE FROM LISTEN SCREEN
        mDeleteVideoRevealsInterface = (DeleteVideoRevealsInterface) this;

        //INITIALIZE THE INTERFACE FOR CHANGING APP MODE LIVE MODE TO SIMULATION MODE
        mIsSimulationModeIsActiveInterface = (IsSimulationModeIsActiveInterface) this;


        viewPager = (CustomViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        linearDelete =(LinearLayout)findViewById(R.id.linearDelete);


        relativeTab =(RelativeLayout)findViewById(R.id.relativeTab);
        linearListenScreenControls =(LinearLayout)findViewById(R.id.linearListenScreenControls);

        viewBottomLine=(View)findViewById(R.id.viewBottomLine);

        //IS USER IS ACTIVE = TRUE -> MEANS USER IS NOT ACTIVE -> ACCESS RESTRICTED TO ONLY USER PROFILE SCREEN
        //FALSE -> USER IS ACTIVE AND ALLOW THEM TO ACCESS THE APP
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


        ImageView imgView= new ImageView(this);
        imgView.setPadding(5,5,5,5);


        //SET USER PROFILE
        Glide.with(mContext)
                .load("" + mSessionManager.getPreference(Constants.KEY_USERPROFILE_PIC))
                .placeholder(getResources().getDrawable(R.mipmap.icon_profile_home))
                .apply(RequestOptions.circleCropTransform())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(imgView);


        //tabLayout.getTabAt(3).setIcon(getResources().getDrawable(R.mipmap.icon_profile_home));
        tabLayout.getTabAt(3).setCustomView(imgView);

        //tabLayout.getTabAt(3).setText(getResources().getString(R.string.strLogout));

        //DEFAULT SELECT PROFILE SCREEN IF USER IS FROM REGISTRATION PAGE
        //IF USER IS NOT ACTIVATED
        //IF USER ACTIVATED BUT HE IS FROM REGISTRATION SCREEN - FIRST SHOW THE PROFILE SCREEN THAN USUALL PLAY SCREEN
        if (!isUserIsActive) {
            //SELECT USER PROFILE FRAGMENT
            selectPage(3);
            //DEFAULT ICON COLOR
            if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
                //tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorNewAppGreen));

            } else {
                tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
               // tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorBlue));
            }




        } else{
            //SELECT PLAY FRAGMENT
            selectPage(1);

            //DEFAULT ICON COLOR
            if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                //tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorNewAppGreen));

            } else {
                tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                //tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorBlue));
            }
        }



        if (!isUserIsActive) {

            //MAKE VIEW PAGER SCROLLABLE FALSE
            viewPager.disableScroll(true);

            //DISABLE BOTTOM BAR ICON CLICK IF USER IS NOT ACTIVE
            enableDisableBottomBar(false);

        }else{
            //MAKE VIEW PAGER SCROLLABLE TURE
            viewPager.disableScroll(true);

            //DISABLE BOTTOM BAR ICON CLICK IF USER IS NOT ACTIVE
            enableDisableBottomBar(true);
        }


        //CHANGE BOTTOM VIEW COLOR
        changeBottomViewColor();

        LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            int finalI = i;
            tabStrip.getChildAt(i).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(finalI == 3){
                        openBottomBarForSwitchAccount();
                    }
                    return false;
                }
            });
        }


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
                            //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            //tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorNewAppGreen));

                        } else {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            //tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorBlue));
                        }

                        break;
                    case 1:

                        if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            //tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorNewAppGreen));

                        } else {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                           // tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorBlue));
                        }


                        break;
                    case 2:
                        if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
                            //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            //tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorNewAppGreen));

                        } else {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
                            //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            //tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorBlue));
                        }
                        break;

                    case 3:
                        if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
                            //tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorNewAppGreen));

                        } else {
                            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                            //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
                            //tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey), getResources().getColor(R.color.colorBlue));
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

        //CREATE PUSHER TOKEN PROVIDER
        pusherTokenProvider();

        //SHOW BOTTOM BAR CONTROL
        isVideoDeleteMultiSelectionActive(false);

    }

    public void openBottomBarForSwitchAccount() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.activity_list_of_active_accounts_shortcut);
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setPeekHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        behavior.setHideable(false);

        ImageView imgCancel = bottomSheetDialog.findViewById(R.id.imgCancel);
        ImageView imgCreateNewUser = bottomSheetDialog.findViewById(R.id.imgCreateNewUser);
        LinearLayout linearImgCancel = (LinearLayout) bottomSheetDialog.findViewById(R.id.linearImgCancel);
        recycleAccountList = (RecyclerView) bottomSheetDialog.findViewById(R.id.recycleAccountList);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        recycleAccountList.setLayoutManager(mLinearLayoutManager);
        recycleAccountList.setAdapter(null);

        RelativeLayout relativeImportKey = (RelativeLayout) bottomSheetDialog.findViewById(R.id.relativeImportKey);

        //FETCH USER'S SELECTED SILOS DATA
        bindRecyclerView(bottomSheetDialog);

        relativeImportKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.cancel();

                Intent mIntent = new Intent(HomeScreenTabLayout.this, ImportAccountFromPrivateKey.class);
                startActivity(mIntent);

            }
        });
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.cancel();

            }
        });
        imgCreateNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CREATE NEW USER
                //LOGOUT CURRENT USER AND MOVE USER TO SIGN UP SCREEN
                //CLEAR PUSHER NOTIFICATION INTEREST
                //PushNotifications.clearAllState();

                logoutUserAndMoveToStartScreen();


            }
        });

        bottomSheetDialog.show();
    }
    private void bindRecyclerView(BottomSheetDialog bottomSheetDialog) {

        ArrayList<KeyStoreServerInstancesModel.Data> selectedSilosAccountsList  = null;
        try {
            selectedSilosAccountsList = new FetchDataFromAndroidKeyStoreTaskTabPopup().execute(mSessionManager).get();
            //CHECK IF THERE IS DATA AVAILABLE FOR SELECTED SILOS
            if(selectedSilosAccountsList != null && selectedSilosAccountsList.size() != 0){

                SilosAvailableAccountsListAdapterPopup mSilosAvailableAccountsListAdapter = new SilosAvailableAccountsListAdapterPopup(mContext, this, selectedSilosAccountsList, mSessionManager,mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));
                recycleAccountList.setAdapter(mSilosAvailableAccountsListAdapter);


                //SWIPE HELPER SHOULD ATTACH FIRST TIME
                ArrayList<KeyStoreServerInstancesModel.Data> mSelectedSilosAccountsList = selectedSilosAccountsList;

                new SwipeHelper(mActivity, recycleAccountList) {
                    @Override
                    public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {

                        underlayButtons.add(new SwipeHelper.UnderlayButton(
                                R.mipmap.icn_delete_with_text,
                                mActivity,
                                new SwipeHelper.UnderlayButtonClickListener() {
                                    @Override
                                    public void onClick(int pos) {

                                        bottomSheetDialog.cancel();

                                        displayWarningDialogue(mSelectedSilosAccountsList.get(pos).getSubmitProfileModel().getrevealit_private_key(), mSelectedSilosAccountsList.get(pos).getSubmitProfileModel().getProton().getPrivateKey(), mSelectedSilosAccountsList.get(pos).getSubmitProfileModel().getProton().getAccountName());

                                    }
                                }
                        ));

                    }
                };



            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void displayWarningDialogue(String revealitPrivateKey,String privateKey, String accountName) {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_user_warning_dailoague, null);
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBuilder.setView(dialogView);


        final AlertDialog mAlertDialog = dialogBuilder.create();
        TextView txtOk = (TextView) dialogView.findViewById(R.id.txtOk);
        TextView txtCancel = (TextView) dialogView.findViewById(R.id.txtCancel);
        LinearLayout linearCancel = (LinearLayout) dialogView.findViewById(R.id.linearCancel);


        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

                //OPEN BIOMETRIC PROMPT
                strRevealitPrivateKey = revealitPrivateKey;
                strPrivateKey = privateKey;
                strAccountName = accountName;
                loadBiomatricPrompt(revealitPrivateKey,privateKey,accountName);


            }
        });


        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

            }
        });

        linearCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

            }
        });
        mAlertDialog.show();
    }
    private void loadBiomatricPrompt(String revealitPrivateKey, String strPrivateKey, String strAccountName) {

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);


            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);


                //CALL AUTHENTICATION API
                //TO GET AUTH TOKEN
                callAuthenticationAPI(revealitPrivateKey, strAccountName, strPrivateKey);

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login to Reveailit")
                .setSubtitle("Log in using your biometric credential")
                // .setNegativeButtonText("Use account password or Pin or Pattern")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .setConfirmationRequired(false)
                .build();

        biometricPrompt.authenticate(promptInfo);

    }
    private void callAuthenticationAPI(String revealItPrivateKey, String username, String strPrivateKey) {

        //DISPLAY DIALOG
        CommonMethods.showDialog(mContext);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request original = chain.request();

                okhttp3.Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("revealit_private_key",revealItPrivateKey);
        paramObject.addProperty("name", username);


        Call<NewAuthLogin> call = patchService1.newAuthLogin(paramObject);

        call.enqueue(new Callback<NewAuthLogin>() {
            @Override
            public void onResponse(Call<NewAuthLogin> call, Response<NewAuthLogin> response) {

                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + response.code());

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + gson.toJson(response.body()));

                //CLOSE DIALOG
                CommonMethods.closeDialog();


                if (response.isSuccessful() && response.code() == Constants.API_CODE_200 && response.body().getToken() != null) {

                    //IF - USER FOUND THAN CALL REMOVE USER API -> THAN DELETE USER FROM LOCAL ANDROID KEYSTORE
                    //ELSE - DELETE DIRECTLY FROM LOCAL
                    callDeleteUserApi(response.body().getToken());

                }else {

                    try {
                        deleteAccountFromAndroidKeyStore();
                    } catch (ExecutionException |InterruptedException  e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NewAuthLogin> call, Throwable t) {


                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                CommonMethods.closeDialog();

            }
        });

    }
    private void callDeleteUserApi(String token) {

        //DISPLAY DIALOG
        CommonMethods.showDialog(mContext);


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request original = chain.request();

                okhttp3.Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + " " +token)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);


        Call<JsonElement> call = patchService1.removeUser();

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ callDeleteUserApi: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callDeleteUserApi: ", "" + response.code());

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ callDeleteUserApi: ", "" + gson.toJson(response.body()));


                //CLOSE DIALOG
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {
                    try {
                        deleteAccountFromAndroidKeyStore();
                    } catch (ExecutionException |InterruptedException  e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {


                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                CommonMethods.closeDialog();


            }
        });
    }

    private void deleteAccountFromAndroidKeyStore() throws ExecutionException, InterruptedException {

        if(new UpdateUserDataInAndroidKeyStoreTaskTabPopup(strPrivateKey,strAccountName).execute(mSessionManager).get()){

            if(mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME).equals(strAccountName)){
                logoutUserAndMoveToStartScreen();
            }else {
                openBottomBarForSwitchAccount();
            }
        }

    }
    private void logoutUserAndMoveToStartScreen() {

        //UPDATE LOGIN FLAG
        mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN,false);
        mSessionManager.updatePreferenceBoolean(Constants.KEY_ISFROM_LOGOUT,true);
        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN, false);
        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_CANCEL_REFERRAL, false);

        // SEND USER TO LANDING SCREEN
        Intent mIntent = new Intent(mActivity, NewAuthGetStartedActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mIntent);
        mActivity.finish();
    }


    private void showWelcomeDialogue(boolean isUserIsActive) {


        Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.user_welcome_dialogue);


        //final AlertDialog mAlertDialog = dialogBuilder.create();
        TextView txtGetStarted = (TextView) dialog.findViewById(R.id.txtGetStarted);
        TextView txtWaitListed = (TextView) dialog.findViewById(R.id.txtWaitListed);
        TextView txtLogout = (TextView) dialog.findViewById(R.id.txtLogout);
        ImageView imgCancel = (ImageView) dialog.findViewById(R.id.imgCancel);

        //HIDE SHOW BUTTON BASED ON USER ACTIVATION STATUS
        if(isUserIsActive){
            txtWaitListed.setVisibility(View.GONE);
            txtGetStarted.setVisibility(View.VISIBLE);
            txtLogout.setVisibility(View.GONE);
            imgCancel.setVisibility(View.GONE);
        }else{
            txtWaitListed.setVisibility(View.VISIBLE);
            txtGetStarted.setVisibility(View.GONE);
            txtLogout.setVisibility(View.GONE);
            imgCancel.setVisibility(View.VISIBLE);
        }



        txtGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                //UPDATE FIRST OPEN FLAG
                mSessionManager.updatePreferenceBoolean(Constants.IS_USER_OPEN_APP_FIRST_TIME,true);

                //UPDATE EDUCATIONAL VIDEO FLAG
                mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_EDUCATION_VIDEO_PLAYED, true);

                Intent mIntent = new Intent(mActivity, ExoPlayerActivity.class);
                mIntent.putExtra(Constants.MEDIA_URL, Constants.EDUCATION_VIDEO_URL);
                mIntent.putExtra(Constants.MEDIA_ID, "0");
                mIntent.putExtra(Constants.VIDEO_NAME, Constants.EDUCATION_VIDEO_TITLE);
                mIntent.putExtra(Constants.VIDEO_SEEK_TO,"0");
                mIntent.putExtra(Constants.IS_VIDEO_SEEK, false);
                mActivity.startActivity(mIntent);

            }
        });


        txtWaitListed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                //EXIT FROM THE APP UNTIL USER TURN TO ACTIVE FROM THE BACKEND
                Intent mIntent = new Intent(mActivity, AddRefferalAndEarnActivity.class);
                startActivity(mIntent);
                finish();


            }
        });
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                //CLEAR PUSHER NOTIFICATION INTEREST
                //PushNotifications.clearAllState();

                //UPDATE LOGIN FLAG
                mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN,false);
                mSessionManager.updatePreferenceBoolean(Constants.KEY_ISFROM_LOGOUT,true);
                mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN, false);
                mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_CANCEL_REFERRAL, false);

                // SEND USER TO LANDING SCREEN
                Intent mIntent = new Intent(mActivity, NewAuthSplashScreen.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mIntent);
                mActivity.finish();


            }
        });
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE APPLICATION BOTTOM BAR VISIBILITY FLAG
                //IF FLAG = TRUE -> ALLOW USER TO ONLY VIEW USER PROFILE SCREEN
                //IF FALSE -> ALLOW USER TO ACCESS ALL FOUR BOTTOM BAR PAGES
                mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_FROM_PLAY_SCREEN, true);

                dialog.dismiss();

                //EXIT FROM THE APP UNTIL USER TURN TO ACTIVE FROM THE BACKEND
                Intent mIntent = new Intent(mActivity, HomeScreenTabLayout.class);
                startActivity(mIntent);
                mActivity.finishAffinity();


            }
        });
        dialog.show();
    }




    private void setOnClicks() {

        linearDelete.setOnClickListener(this);
    }


    @Override
    public void onClick(View mView) {

        switch (mView.getId()){
            case R.id.linearDelete:
                if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                    ListenFragment.clearListenHistory(true);
                }else{
                    ListenFragment.clearListenHistory(false);
                }

                break;
        }

    }



    private void pusherTokenProvider() {

        //PUSHER SDK
        //IF -> SELECTED SERVER INSTANCE IS INTEGRATION
        //ELSE -> OTHER THAN INTEGRATION
//        if(mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID) == 6){
//            //PushNotifications.start(getApplicationContext(), Constants.PUSHER_INSTANCE_ID_INTERGRATION);
//            PushNotificationsInstance pushNotificationsIntegration =
//                    new PushNotificationsInstance(getApplicationContext(), Constants.PUSHER_INSTANCE_ID_INTERGRATION);
//            pushNotificationsIntegration.start();
//            pushNotificationsIntegration.addDeviceInterest(mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));
//
//        }else {
//            //PushNotifications.start(getApplicationContext(), Constants.PUSHER_INSTANCE_ID_PRODUCTION);
//            PushNotificationsInstance pushNotificationsProduction =
//                    new PushNotificationsInstance(getApplicationContext(), Constants.PUSHER_INSTANCE_ID_PRODUCTION);
//            pushNotificationsProduction.start();
//            pushNotificationsProduction.addDeviceInterest(mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));
//        }

        //REGISTER PUSHER DEVICE INTEREST
        //pushNotificationsProduction.addDeviceInterest(mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));

        if(!mSessionManager.getPreference(Constants.KEY_PUSHER_ID).isEmpty()){
            PushNotificationsInstance pushNotificationsIntegration =
                    new PushNotificationsInstance(getApplicationContext(),mSessionManager.getPreference(Constants.KEY_PUSHER_ID));
            pushNotificationsIntegration.start();
            pushNotificationsIntegration.addDeviceInterest(mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));

        }

    }

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



    @Override
    protected void onResume() {
        super.onResume();
        PushNotifications.setOnMessageReceivedListenerForVisibleActivity(this, new PushNotificationReceivedListener() {
            @Override
            public void onMessageReceived(RemoteMessage remoteMessage) {
                String messagePayload =gson.toJson(remoteMessage);
                if (remoteMessage.getData() != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            try {

                                //Log.e("PAYLOAD",messagePayload);

                                if(new JSONObject(messagePayload).getJSONObject("bundle").getJSONObject("mMap").get("gcm.notification.body").toString().contains("message_type")){

                                    JSONObject obj = new JSONObject(new JSONObject(messagePayload).getJSONObject("bundle").getJSONObject("mMap").get("gcm.notification.body").toString());

                                    if(obj.getString("message_type").replaceAll("^\"|\"$", "").replaceAll("u0027", "'").replaceAll("\\\\", "").equals("300")){
                                        openBottomBarForReward(messagePayload);
                                    }else{
                                        openBottomBarForAuth(messagePayload);
                                    }
                                }else{
                                    openBottomBarForAuth(messagePayload);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {

                    CommonMethods.displayToast(mContext,"Authentication failed!");
                }
            }
        });

        //UPDATE FIRST OPEN FLAG
        if(!isUserIsActive){
            mActivity.runOnUiThread(new Runnable() {
                public void run() {

                    if(!mSessionManager.getPreferenceBoolean(Constants.KEY_IS_FROM_PLAY_SCREEN)){
                        showWelcomeDialogue(false);
                    }else{
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_FROM_PLAY_SCREEN, false);
                    }


                }
            });
        }else{

        }
    }

    private void openBottomBarForReward(String messagePayload) throws JSONException {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_reward_push);
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setPeekHeight(CommonMethods.getScreenHeight(mActivity));
        behavior.setHideable(false);

        ImageView imgBackArrow = bottomSheetDialog.findViewById(R.id.imgBackArrow);
        TextView txtNotificationMsg = bottomSheetDialog.findViewById(R.id.txtNotificationMsg);
        TextView txtReferMoreFriends = bottomSheetDialog.findViewById(R.id.txtReferMoreFriends);



        //TEXT NOTIFICATION MSG
        txtNotificationMsg.setText(""+new JSONObject(messagePayload).getJSONObject("bundle").getJSONObject("mMap").get("gcm.notification.title"));


        //String pusherId =""+new JSONObject(new JSONObject(messagePayload).getJSONObject("bundle").getJSONObject("mMap").getString("pusher")).get("publishId");

        imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               bottomSheetDialog.cancel();

            }
        });

        txtReferMoreFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.cancel();

            }
        });
        bottomSheetDialog.show();
    }

    private void openBottomBarForAuth(String messagePayload) throws JSONException {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_auth_push);
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setPeekHeight(CommonMethods.getScreenHeight(mActivity));
        behavior.setHideable(false);

        ImageView imgBackArrow = bottomSheetDialog.findViewById(R.id.imgBackArrow);
        ImageView imgProfile = bottomSheetDialog.findViewById(R.id.imgProfile);
        TextView txtUserName = bottomSheetDialog.findViewById(R.id.txtUserName);
        TextView txtProtonName = bottomSheetDialog.findViewById(R.id.txtProtonName);
        TextView txtNotificationMsg = bottomSheetDialog.findViewById(R.id.txtNotificationMsg);
        TextView txtAuthorise = bottomSheetDialog.findViewById(R.id.txtAuthorise);

        //TEXT NOTIFICATION MSG
        txtNotificationMsg.setText(""+new JSONObject(messagePayload).getJSONObject("bundle").getJSONObject("mMap").get("gcm.notification.title"));

        //TEXT USERNAME
        txtProtonName.setText("@"+mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));

        String pusherId =""+new JSONObject(new JSONObject(messagePayload).getJSONObject("bundle").getJSONObject("mMap").getString("pusher")).get("publishId");

        imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //FIRST CLOSE DIALOGUE AND THEN CALL API
                callAuthorisationCancelApi(bottomSheetDialog,pusherId);



            }
        });

        txtAuthorise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callAuthorisationApi(bottomSheetDialog,pusherId);


            }
        });
        bottomSheetDialog.show();
    }

    private void callAuthorisationCancelApi(BottomSheetDialog bottomSheetDialog, String pusherId) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request original = chain.request();

                okhttp3.Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + " " + mSessionManager.getPreference(Constants.AUTH_TOKEN))
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("publish_id", pusherId);

        Call<JsonElement> call = patchService1.pushAuthorisationCancel(paramObject);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ callAuthorisationCancelApi: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callAuthorisationCancelApi: ", "" + response.code());


                bottomSheetDialog.cancel();

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

                    CommonMethods.displayToast(mActivity,"Sign in Cancel!");

                } else {

                    CommonMethods.displayToast(mActivity,"Sign in failed!");
                }

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                bottomSheetDialog.cancel();

                CommonMethods.displayToast(mActivity,"Sign in failed!");


            }
        });
    }


    private void callAuthorisationApi(BottomSheetDialog bottomSheetDialog, String pusherId) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request original = chain.request();

                okhttp3.Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + " " + mSessionManager.getPreference(Constants.AUTH_TOKEN))
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("publish_id", pusherId);

        Call<JsonElement> call = patchService1.pushAuthorisation(paramObject);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ callAuthorisationApi: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callAuthorisationApi: ", "" + response.code());


                bottomSheetDialog.cancel();

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

                    CommonMethods.displayToast(mActivity,"Sign in successfully!");

                } else {

                    CommonMethods.displayToast(mActivity,"Sign in failed!");
                }

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                bottomSheetDialog.cancel();

                CommonMethods.displayToast(mActivity,"Sign in failed!");


            }
        });

    }


    @Override
    public void isVideoDeleteMultiSelectionActive(boolean isChangeControls) {
        //CHANGE BOTTOM BAR TAB CONTROLS WITH LISTEN SCREEN CONTROLS
        if(isChangeControls){
            tabLayout.setVisibility(View.GONE);
            linearListenScreenControls.setVisibility(View.VISIBLE);
        }else{
            tabLayout.setVisibility(View.VISIBLE);
            linearListenScreenControls.setVisibility(View.GONE);
        }
    }

    @Override
    public void isSimulationModeActive(boolean isSimulationModeActive) {
        if(isSimulationModeActive){
            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
            //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
            //tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey) , getResources().getColor(R.color.colorNewAppGreen));
            viewBottomLine.setBackgroundColor(getResources().getColor(R.color.colorNewAppGreen));
        }else{
            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
            //tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
            //tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey) , getResources().getColor(R.color.colorBlue));
            viewBottomLine.setBackgroundColor(getResources().getColor(R.color.colorBlue));

        }

    }




}
class UpdateUserDataInAndroidKeyStoreTaskTabPopup extends AsyncTask<SessionManager, Integer, Boolean> {

    String strPrivateKey ="";
    String strUsername = "";

    public UpdateUserDataInAndroidKeyStoreTaskTabPopup(String privateKey, String strUsername) {

        this.strPrivateKey =privateKey;
        this.strUsername = strUsername;
    }
    @Override
    protected Boolean doInBackground(SessionManager... mSessionManager) {
        return CommonMethods.updateUserAccountActivationFlag(mSessionManager[0],strPrivateKey,strUsername);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Boolean searchResults) {
        super.onPostExecute(searchResults);

    }


}
class FetchDataFromAndroidKeyStoreTaskTabPopup extends AsyncTask<SessionManager, Integer, ArrayList<KeyStoreServerInstancesModel.Data>> {


    @Override
    protected ArrayList<KeyStoreServerInstancesModel.Data> doInBackground(SessionManager... mSessionManager) {
        return CommonMethods.fetchUserSelectedSilosDataInList(mSessionManager[0]);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(ArrayList<KeyStoreServerInstancesModel.Data> searchResults) {
        super.onPostExecute(searchResults);

    }
}