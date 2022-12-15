package com.Revealit.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.Revealit.Adapter.FragmentAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.CustomViews.CustomViewPager;
import com.Revealit.Fragments.ListenFragment;
import com.Revealit.Fragments.PlayFragment;
import com.Revealit.Fragments.ProfileFragmentContainer;
import com.Revealit.Fragments.WalletFragment;
import com.Revealit.Interfaces.DeleteVideoRevealsInterface;
import com.Revealit.Interfaces.IsSimulationModeIsActiveInterface;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
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
import com.pusher.pushnotifications.auth.BeamsTokenProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreenTabLayout extends AppCompatActivity implements DeleteVideoRevealsInterface, IsSimulationModeIsActiveInterface, View.OnClickListener {

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
    private boolean isUserIsActive,isFromRegistrationScreen;
    private View viewBottomLine;
    private int REQUEST_CAMERA_PERMISSIONc=100;
    private BeamsTokenProvider tokenProvider;
    private Gson gson;
    private DeleteVideoRevealsInterface mDeleteVideoRevealsInterface;
    private IsSimulationModeIsActiveInterface mIsSimulationModeIsActiveInterface;
    private LinearLayout linearDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tab_layout);


            setId();
            setOnClicks();

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
        if (!isUserIsActive) {
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
            viewPager.disableScroll(true);

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

        //CREATE PUSHER TOKEN PROVIDER
        pusherTokenProvider();

        //SHOW BOTTOM BAR CONTROL
        isVideoDeleteMultiSelectionActive(false);

    }


    private void pusherTokenProvider() {

        //PUSHER SDK
        //IF -> SELECTED SERVER INSTANCE IS INTEGRATION
        //ELSE -> OTHER THAN INTEGRATION
        if(mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID) == 2){
            PushNotifications.start(getApplicationContext(), Constants.PUSHER_INSTANCE_ID_INTERGRATION);
        }else {
            PushNotifications.start(getApplicationContext(), Constants.PUSHER_INSTANCE_ID_PRODUCTION);
        }

        //REGISTER PUSHER DEVICE INTEREST
        PushNotifications.addDeviceInterest(mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));

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
                                openBottomBarForAuth(messagePayload);
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
            tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
            tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey) , getResources().getColor(R.color.colorNewAppGreen));
            viewBottomLine.setBackgroundColor(getResources().getColor(R.color.colorNewAppGreen));
        }else{
            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_IN);
            tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey) , getResources().getColor(R.color.colorBlue));
            viewBottomLine.setBackgroundColor(getResources().getColor(R.color.colorBlue));

        }

    }

}