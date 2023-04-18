package com.Revealit.UserOnboardingProcess;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.Activities.InternetBrokenActivity;
import com.Revealit.Activities.MaintanaceActivity;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.InviteModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

public class NewAuthSplashScreen extends AppCompatActivity {

    private Handler handler;
    private NewAuthSplashScreen mActivity;
    private NewAuthSplashScreen mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private Gson mGson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_new_auth_splash_screen);

        mActivity = NewAuthSplashScreen.this;
        mContext = NewAuthSplashScreen.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();


        //CHECK IF INTERNET IS AVAILABLE
        if(!CommonMethods.isInternetAvailable(mContext)){
            Intent mIntent = new Intent(this, InternetBrokenActivity.class);
            startActivity(mIntent);
            finish();
        }


        //GET DATA FROM THE CAMERA QR CODE
        //IT MUST BE IN THE FORM OF REVEALIT
        Intent intent = getIntent();
        Uri data = intent.getData();
        if(data != null && data.toString().contains("revealit")){
            mSessionManager.updatePreferenceBoolean(Constants.KEY_QR_CODE_FROM_CAMERA, true);
            mSessionManager.updatePreferenceString(Constants.KEY_QR_CODE_FROM_CAMERA_VALUE,data.toString() );
        }else{
            mSessionManager.updatePreferenceBoolean(Constants.KEY_QR_CODE_FROM_CAMERA, false);
            mSessionManager.updatePreferenceString(Constants.KEY_QR_CODE_FROM_CAMERA_VALUE,"");
        }

        mGson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                    .serializeNulls()
                    .create();




        //FALSE == APP OPEN FIRST TIME
        //TRUE  == APP NOT OPEN FIRST TIME
        if (!mSessionManager.getPreferenceBoolean(Constants.IS_APP_OPEN_FIRST_TIME)) {

            //UPDATE DEFAULT BLOCK PRODUCERS
            //CREATE ARRAY LIST FOR LOAD PRODUCERS
            ArrayList<String> mBlockProducerArray = new ArrayList<>();
            mBlockProducerArray.add(Constants.GET_PROTON_ACCOUNT_NAME_BASE_URL);

            //SAVE THIS TO SESSION MANAGER
            mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_BLOCK_PRODUCERS, mGson.toJson(mBlockProducerArray));



            //SAVE TESTING END POINTS
            //CHANGE API END POINT TO ALPHA T CURATOR
            mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, Constants.API_END_POINTS_MOBILE_B_CURATOR);
            mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, Constants.API_END_POINTS_REGISTRATION_B_CURATOR);
            mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, mActivity.getResources().getString(R.string.strBeta));


            switch (Constants.API_END_POINTS_MOBILE_B_CURATOR) {

                case Constants.API_END_POINTS_MOBILE_B_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 1);
                    break;
                case Constants.API_END_POINTS_MOBILE_S_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 2);
                    break;
                case Constants.API_END_POINTS_MOBILE_T1_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 3);
                    break;
                case Constants.API_END_POINTS_MOBILE_T2_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 4);
                    break;
                case Constants.API_END_POINTS_MOBILE_T3_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 5);
                    break;
                case Constants.API_END_POINTS_MOBILE_INTEGRATION_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 6);
                    break;
                case Constants.API_END_POINTS_MOBILE_DEMO_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 7);
                    break;
                case Constants.API_END_POINTS_MOBILE_ANDROID_M1_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 8);
                    break;
            }

            //CHANGE THE VALUE OF APP OPEN
            mSessionManager.updatePreferenceBoolean(Constants.IS_APP_OPEN_FIRST_TIME, true);
        }



        //API SETTINGS DATA CALL
        apiInviteSettings();

        }

    private void openNextActivity(InviteModel mInviteModel) {


        //UPDATE PREFERENCE FOR APP SETTINGS
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_MSG ,""+mInviteModel.getInvitation_message());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_COPY_CLIPBOARD ,""+mInviteModel.getInvitation_message_clipboard());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_BIOMETRIC_PERMISSION ,""+getResources().getString(R.string.strBiomatricPermissionTwo));
        mSessionManager.updatePreferenceString(Constants.KEY_CALL_FOR_INVITE_MSG ,""+mInviteModel.getInvitation_message());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_CYPTO_CURRNCY ,""+mInviteModel.getCrypto_currency());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_CURRNCY ,""+mInviteModel.getCurrency());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_CURRNCY_AMOUNT ,""+mInviteModel.getCurrency_amount());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_PLACEHOLDER ,""+mInviteModel.getPlace_holder());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_CURRENCY_ICON ,""+mInviteModel.getCurrency_icon_url());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_QUESTION ,""+mInviteModel.getQuestion());

        //INTENT
        //CHECK IF USER IS ALREADY LOGGED IN OR NOT
        if (!mSessionManager.getPreferenceBoolean(Constants.USER_LOGGED_IN)) {
            Intent mIntent = new Intent(NewAuthSplashScreen.this, NewAuthGetStartedActivity.class);
            startActivity(mIntent);
            finishAffinity();
        }else {
            //CALL CALLBACK API
            Intent mIntent = new Intent(NewAuthSplashScreen.this, HomeScreenTabLayout.class);
            mIntent.putExtra(Constants.KEY_ISFROM_LOGIN, false);
            startActivity(mIntent);
            finishAffinity();

        }


    }

    private void apiInviteSettings(){


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
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
                .client(client.newBuilder().connectTimeout(3000, TimeUnit.SECONDS).readTimeout(3000, TimeUnit.SECONDS).writeTimeout(3000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        String url=" ";


        Call<InviteModel> call = patchService1.getCampaignDetails(Constants.API_NEW_AUTH_INVITE_SETTINGS+url);

        call.enqueue(new Callback<InviteModel>() {
            @Override
            public void onResponse(Call<InviteModel> call, Response<InviteModel> response) {

                CommonMethods.printLogE("Response @ apiSendInvites: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiSendInvites: ", "" + response.code());
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ apiSendInvites: ", "" + gson.toJson(response.body()));


                if(response.code() == 200){
                    //UPDATE INVITE UI
                    openNextActivity(response.body());
                }else
                {
                    openMaintenanceActivity();
                }

            }

            @Override
            public void onFailure(Call<InviteModel> call, Throwable t) {

                openMaintenanceActivity();


            }
        });


    }

    private void openMaintenanceActivity() {
        Intent mIntent = new Intent(NewAuthSplashScreen.this, MaintanaceActivity.class);
        startActivity(mIntent);
        finishAffinity();
    }


}