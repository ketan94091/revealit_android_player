package com.Revealit.UserOnboardingProcess;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.Revealit.Activities.AppUpgradeActivity;
import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.Activities.MaintanaceActivity;
import com.Revealit.Activities.QrCodeScannerActivity;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.NewAuthLogin;
import com.Revealit.ModelClasses.NewAuthLoginCallbackModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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

public class NewAuthBiomatricAuthenticationActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "NewAuthBiomatricAuthenticationActivity";


    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private RelativeLayout relativeMain;
    private boolean isFromLoginScreen;
    private AlertDialog mAlertDialog;
    private Gson mGson;
    //FOR MAINTANANCE
    boolean isAppInMaintainance = false;
    boolean isAppVersionIsNotOk = false;
    private String userRole,username,privatekey;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_biomatric_authentication);


        setIds();
        setOnclick();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setIds() {

        mActivity = NewAuthBiomatricAuthenticationActivity.this;
        mContext = NewAuthBiomatricAuthenticationActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        relativeMain= (RelativeLayout)findViewById(R.id.relativeMain);

        isFromLoginScreen = getIntent().getBooleanExtra(Constants.KEY_ISFROM_LOGIN, false);


        //CHECK IF BIOMETRIC HARDWARE AVAILABLE OR NOT
        //ALSO USER ALLOW TO USE BIOMETRIC WHILE REGISTRAION OR FIRST LOGIN
        BiometricManager biometricManager = BiometricManager.from(mContext);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BIOMETRIC_STRONG | DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {
        //if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BIOMETRIC_STRONG | DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS && mSessionManager.getPreferenceBoolean(Constants.IS_ALLOW_BIOMETRIC )) {

            //OPEN BIOMETRIC PROMPT
            loadBiomatricPrompt();

        } else {

            //OPEN NO AUTHENTICATION DIALOGUE
            CommonMethods.openBiometricActivatioDailogue(mActivity);

        }

        mGson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();



    }


    private void setOnclick() {

    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {

            case R.id.imgGoBack:

                finish();

                break;
            case R.id.txtNextEnabled:


                break;
        }
    }

    private void loadBiomatricPrompt() {

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(NewAuthBiomatricAuthenticationActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);


                finishAffinity();


            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                if(isFromLoginScreen){
                    callAuthenticationAPI();
                }else{
                    callCallBackAPI();

                }


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

    private void callCallBackAPI() {

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
        //paramObject.addProperty("name", mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));


        Call<NewAuthLoginCallbackModel> call = patchService1.newAuthCallback(paramObject);

        call.enqueue(new Callback<NewAuthLoginCallbackModel>() {
            @Override
            public void onResponse(Call<NewAuthLoginCallbackModel> call, Response<NewAuthLoginCallbackModel> response) {

                CommonMethods.printLogE("Response @ callCallBackAPI: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callCallBackAPI: ", "" + response.code());

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ callCallBackAPI: ", "" + gson.toJson(response.body()));


                //CLOSE DIALOG
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200 && response.body().getToken() != null) {


                    //SAVE AUTHENTICATION DATA
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN, response.body().getToken());
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE, response.body().getToken_type());
                    mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN, true);
                    mSessionManager.updatePreferenceBoolean(Constants.IS_FIRST_LOGIN, true);

                    //UPDATE FLAG IF USER IS ADMIN OR NOT
                    if(response.body().getRole().equals(getResources().getString(R.string.strAdmin))){
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,true);
                    }else{
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,false);
                    }

                    //UPDATE ACTIVE FLAG
                    if(response.body().getIs_activated().equals("1")){
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,true);
                    }else{
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,false);
                    }

                    //CHECK IF USERROLE IS CHANGED
                    String privatekey = getIntent().getStringExtra(Constants.KEY_PRIVATE_KEY);
                    String username = getIntent().getStringExtra(Constants.KEY_PROTON_ACCOUNTNAME);
                    String userRole = getIntent().getStringExtra(Constants.KEY_USER_ROLE);
                    if(privatekey != null && username != null && userRole != null &&  !userRole.equals(response.body().getRole())){
                        try {
                            if(new UpdateUserRoleInAndroidKeyStoreTask(privatekey,username,response.body().getRole()).execute(mSessionManager).get()){}
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }


                    //FOR MAINTANANCE
                    boolean isAppInMaintainance = false;
                    boolean isAppVersionIsNotOk = false;
                    for(int i =0; i <response.body().getPublic_settings().size(); i++ ){

                        //SAVE PUBLIC SETTINGS
                        if(response.body().getPublic_settings().get(i).getOs().equals("Android")){
                            mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_API_VERSION, response.body().getPublic_settings().get(i).getApi_version());
                            mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_VERSION, response.body().getPublic_settings().get(i).getMinimum_acceptable_version());
                            mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_API_VERSION, response.body().getPublic_settings().get(i).getMinimum_acceptable_api_version());
                            mSessionManager.updatePreferenceInteger(Constants.KEY_PUBLIC_SETTING_MINIMUM_PROFILE_REMINDER, Integer.valueOf(response.body().getPublic_settings().get(i).getProfile_update_reminder_period()));
                            mSessionManager.updatePreferenceInteger(Constants.KEY_PUBLIC_SETTING_BACKUP_REMINDER, Integer.valueOf(response.body().getPublic_settings().get(i).getBackup_update_reminder_period()));

                           if(response.body().getPublic_settings().get(i).getMaintenance() == "1"){
                               isAppInMaintainance = true;
                           }

                           if(CommonMethods.calculateAcceptableVersion(response.body().getPublic_settings().get(i).getMinimum_acceptable_version() )){
                               isAppVersionIsNotOk = true;
                           }

                            //GET PUSHER SETTINGS
                            //SAVE TO SHARE PREFERENCE
                            if(response.body().getPublic_settings().get(i).getApp_settings().getPusher() != null){

                            }else {
                                mSessionManager.updatePreferenceString(Constants.KEY_PUSHER_ID, "");
                                mSessionManager.updatePreferenceString(Constants.KEY_PUSHER_SERVER_KEY,"");
                            }


                        }
                    }


                    //CHECK IF APPLICATION IS IN MAINTENANCE
                    if(isAppInMaintainance){
                        //MOVE TO MAINTENANCE SCREEN
                        Intent mIntent = new Intent(NewAuthBiomatricAuthenticationActivity.this, MaintanaceActivity.class);
                        mIntent.putExtra(Constants.KEY_IS_FROM_CALLBACKAPI,false);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        finish();
                    }else if(isAppVersionIsNotOk){
                        //MOVE TO MAINTENANCE SCREEN
                        Intent mIntent = new Intent(NewAuthBiomatricAuthenticationActivity.this, AppUpgradeActivity.class);
                        mIntent.putExtra(Constants.KEY_IS_FROM_CALLBACKAPI,false);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        finish();
                    }else {
                        goToNextActivity();
                    }



                }else if (response.code() == Constants.API_CODE_401) {

                    //CLOSE DIALOG
                    CommonMethods.closeDialog();

                    //IF JWT TOKEN IS IN VALID CALL AUTH LOGIN API AND GENERATE NEW TOKEN
                    callAuthenticationAPI();

                }else {
                    if(response.body().getError_code() ==  604){
                        displayUserNotFoundDialogue();
                    }else{
                        CommonMethods.buildDialog(mContext,"Error : "+response.body().getError_msg());
                    }
                }



            }

            @Override
            public void onFailure(Call<NewAuthLoginCallbackModel> call, Throwable t) {


                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                CommonMethods.closeDialog();

            }
        });

    }



    private void goToNextActivity() {


        //IF THE USER IS FROM QR CODE
        //ELSE MOVE TO HOME SCREEN
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_QR_CODE_FROM_CAMERA)){
            Intent mIntentQrCode = new Intent(this, QrCodeScannerActivity.class);
            startActivity(mIntentQrCode);
        }else{
            //MOVE TO HOME SCREEN
            Intent mIntent = new Intent(NewAuthBiomatricAuthenticationActivity.this, HomeScreenTabLayout.class);
            mIntent.putExtra(Constants.KEY_IS_FROM_REGISTRATION_SCREEN, false);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
            finish();
        }

    }

    private void displayAlertDialogue() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.no_user_available_for_this_name, null);
        dialogBuilder.setView(dialogView);

        mAlertDialog = dialogBuilder.create();
        mAlertDialog.setCancelable(true);

        TextView txtOk = (TextView)dialogView. findViewById(R.id.txtOk);

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CLOSE DIALOGUE
                mAlertDialog.cancel();

                //GO TO SIGN IN OR SIGN UP ACTIVITY
                Intent mIntent = new Intent(NewAuthBiomatricAuthenticationActivity.this, NewAuthGetStartedActivity.class);
                startActivity(mIntent);
                finishAffinity();


            }
        });

        mAlertDialog.show();
    }

    private void callAuthenticationAPI() {

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
        paramObject.addProperty("revealit_private_key",mSessionManager.getPreference(Constants.KEY_REVEALIT_PRIVATE_KEY));
        paramObject.addProperty("name", mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));

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


                    //SAVE AUTHENTICATION DATA
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN, response.body().getToken());
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE, response.body().getToken_type());
                    mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN, true);
                    mSessionManager.updatePreferenceBoolean(Constants.IS_FIRST_LOGIN, true);

                    //UPDATE FLAG IF USER IS ADMIN OR NOT
                    if(response.body().getRole().equals(getResources().getString(R.string.strAdmin))){
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,true);
                    }else{
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,false);
                    }

                    //UPDATE ACTIVE FLAG
                    if(response.body().getIs_activated().equals("1")){
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,true);
                    }else{
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,false);
                    }



                    //CHECK IF USER ROLE IS CHANGED
                     privatekey = getIntent().getStringExtra(Constants.KEY_PRIVATE_KEY);
                     username = getIntent().getStringExtra(Constants.KEY_PROTON_ACCOUNTNAME);
                     userRole = getIntent().getStringExtra(Constants.KEY_USER_ROLE);
                    if(privatekey != null && username != null && userRole != null &&  !userRole.equals(response.body().getRole())){
                        try {
                            if(new UpdateUserRoleInAndroidKeyStoreTask(privatekey,username,response.body().getRole()).execute(mSessionManager).get()){}
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }


                    for(int i =0; i <response.body().getPublic_settings().size(); i++ ){

                        //SAVE PUBLIC SETTINGS
                        if(response.body().getPublic_settings().get(i).getOs().equals("Android")){
                            mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_API_VERSION, response.body().getPublic_settings().get(i).getApi_version());
                            mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_VERSION, response.body().getPublic_settings().get(i).getMinimum_acceptable_version());
                            mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_API_VERSION, response.body().getPublic_settings().get(i).getMinimum_acceptable_api_version());
                            mSessionManager.updatePreferenceInteger(Constants.KEY_PUBLIC_SETTING_MINIMUM_PROFILE_REMINDER, Integer.valueOf(response.body().getPublic_settings().get(i).getProfile_update_reminder_period()));
                            mSessionManager.updatePreferenceInteger(Constants.KEY_PUBLIC_SETTING_BACKUP_REMINDER, Integer.valueOf(response.body().getPublic_settings().get(i).getBackup_update_reminder_period()));

                            //CREATE ARRAY LIST FOR LOAD PRODUCERS
                            ArrayList<String> mBlockProducerArray = new ArrayList<>();
                            for (int j=0;j< response.body().getPublic_settings().get(i).getApp_settings().getBlock_producers().size();j++){
                                mBlockProducerArray.add(response.body().getPublic_settings().get(i).getApp_settings().getBlock_producers().get(j).getUrl());
                            }

                            //SAVE THIS TO SESSION MANAGER
                            mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_BLOCK_PRODUCERS, mGson.toJson(mBlockProducerArray));



                            if(response.body().getPublic_settings().get(i).getMaintenance() == "1"){
                                isAppInMaintainance = true;
                            }

                            if(CommonMethods.calculateAcceptableVersion(response.body().getPublic_settings().get(i).getMinimum_acceptable_version() )){
                                isAppVersionIsNotOk = true;
                            }

                            //GET PUSHER SETTINGS
                            //SAVE TO SHARE PREFERENCE
                              if(response.body().getPublic_settings().get(i).getApp_settings().getPusher() != null) {
                                  mSessionManager.updatePreferenceString(Constants.KEY_PUSHER_ID, response.body().getPublic_settings().get(i).getApp_settings().getPusher().getInstance_id());
                                  mSessionManager.updatePreferenceString(Constants.KEY_PUSHER_SERVER_KEY, response.body().getPublic_settings().get(i).getApp_settings().getPusher().getSecret_key());
                              }else {
                                  mSessionManager.updatePreferenceString(Constants.KEY_PUSHER_ID, "");
                                  mSessionManager.updatePreferenceString(Constants.KEY_PUSHER_SERVER_KEY,"");
                              }

                        }
                    }

                    //GET USER DETAILS API
                    getUserDetails();

                }else {

                    if(response.code() ==  404 ){
                        displayUserNotFoundDialogue();
                    }else{
                        openAppMaintenanceScreen();
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

    private void getUserDetails() {


        //DISPLAY PROGRESSBAR
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

        Call<JsonElement> call = patchService1.getUser(Constants.API_GET_USER);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {




                CommonMethods.printLogE("Response @ getUserDetails: ", "" + response.isSuccessful());
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ getUserDetails: ", "" + gson.toJson(response.body().getAsJsonObject().get("data")));


                //CLOSE DIALGOUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {


                    if(privatekey != null && username != null){
                        try {
                            if(new UpdateUserUserDetailsToKeyStoreTask(privatekey,username,response.body().getAsJsonObject().get("data").toString()).execute(mSessionManager).get()){}
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }


                    //CHECK IF APPLICATION IS IN MAINTENANCE
                    if(isAppInMaintainance){
                       openAppMaintenanceScreen();
                    }else if(isAppVersionIsNotOk){
                        //MOVE TO MAINTENANCE SCREEN
                        Intent mIntent = new Intent(NewAuthBiomatricAuthenticationActivity.this, AppUpgradeActivity.class);
                        mIntent.putExtra(Constants.KEY_IS_FROM_CALLBACKAPI,false);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        finish();
                    }else {
                        goToNextActivity();
                    }

                }else{

                    openAppMaintenanceScreen();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                //CLOSE DIALGOUE
                CommonMethods.closeDialog();

                openAppMaintenanceScreen();
            }
        });


    }

    private void openAppMaintenanceScreen() {

        //MOVE TO MAINTENANCE SCREEN
        Intent mIntent = new Intent(NewAuthBiomatricAuthenticationActivity.this, MaintanaceActivity.class);
        mIntent.putExtra(Constants.KEY_IS_FROM_CALLBACKAPI,false);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mIntent);
        finish();
    }

    private void displayUserNotFoundDialogue() {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.import_key_user_notfound, null);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBuilder.setView(dialogView);

        final AlertDialog mAlertDialog = dialogBuilder.create();
        TextView txtContinue = (TextView) dialogView.findViewById(R.id.txtContinue);
        LinearLayout linearCancel = (LinearLayout) dialogView.findViewById(R.id.linearCancel);


        linearCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

            }
        });


        txtContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //OPEN ENTER MOBILE NUMBER APP
                Intent mIntent = new Intent(NewAuthBiomatricAuthenticationActivity.this,NewAuthMobileAndPromoActivity.class);
                mIntent.putExtra(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY, true);
                startActivity(mIntent);


                mAlertDialog.dismiss();

            }
        });

        mAlertDialog.show();

    }


}
class UpdateUserRoleInAndroidKeyStoreTask extends AsyncTask<SessionManager, Integer, Boolean> {

    String strPrivateKey ="";
    String strUsername = "";
    String strUserRole = "";

    public UpdateUserRoleInAndroidKeyStoreTask(String privateKey, String strUsername,String role) {

        this.strPrivateKey =privateKey;
        this.strUsername = strUsername;
        this.strUserRole = role;
    }
    @Override
    protected Boolean doInBackground(SessionManager... mSessionManager) {
        return CommonMethods.updateUserRoleToKeyChain(mSessionManager[0],strPrivateKey,strUsername,strUserRole);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Boolean searchResults) {
        super.onPostExecute(searchResults);

    }
}

class UpdateUserUserDetailsToKeyStoreTask extends AsyncTask<SessionManager, Integer, Boolean> {

    String strPrivateKey ="";
    String strUsername = "";
    String strUserDetails = "";

    public UpdateUserUserDetailsToKeyStoreTask(String privateKey, String strUsername,String role) {

        this.strPrivateKey =privateKey;
        this.strUsername = strUsername;
        this.strUserDetails = role;
    }
    @Override
    protected Boolean doInBackground(SessionManager... mSessionManager) {
        return CommonMethods.updateUserDetailsToKeyChain(mSessionManager[0],strPrivateKey,strUsername,strUserDetails);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Boolean searchResults) {
        super.onPostExecute(searchResults);

    }
}

