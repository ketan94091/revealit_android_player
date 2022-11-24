package com.Revealit.UserOnboardingProcess;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

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
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
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
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BIOMETRIC_STRONG | DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS && mSessionManager.getPreferenceBoolean(Constants.IS_ALLOW_BIOMETRIC )) {

            //OPEN BIOMETRIC PROMPT
            loadBiomatricPrompt();

        } else {

            //OPEN NO AUTHENTICATION DIALOGUE
            CommonMethods.openStartOverDialogue(mActivity);

        }


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
        paramObject.addProperty("name", mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));


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

                    //SAVE PUBLIC SETTINGS
                    if(response.body().getPuplic_settings() != null){
                        mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_API_VERSION, response.body().getPuplic_settings().getApi_version());
                        mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_VERSION, response.body().getPuplic_settings().getMinumum_acceptable_version());
                        mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_API_VERSION, response.body().getPuplic_settings().getMinumum_acceptable_api_version());
                        mSessionManager.updatePreferenceInteger(Constants.KEY_PUBLIC_SETTING_MINIMUM_PROFILE_REMINDER, response.body().getPuplic_settings().getProfile_update_reminder_period());
                        mSessionManager.updatePreferenceInteger(Constants.KEY_PUBLIC_SETTING_BACKUP_REMINDER, response.body().getPuplic_settings().getBackup_update_reminder_period());
                    }


                    //CHECK IF APPLICATION IS IN MAINTENANCE
                    if(response.body().getPuplic_settings() != null && response.body().getPuplic_settings().getMaintenance().equals("1")){
                        //MOVE TO MAINTENANCE SCREEN
                        Intent mIntent = new Intent(NewAuthBiomatricAuthenticationActivity.this, MaintanaceActivity.class);
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

                    displayAlertDialogue(getResources().getString(R.string.strUsernotfound));


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

    private void displayAlertDialogue(String strMessege) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.app_name));
        builder.setMessage(strMessege);
        builder.setNegativeButton(mContext.getResources().getString(R.string.strOk), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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

                    //SAVE PUBLIC SETTINGS
                    if(response.body().getPuplic_settings() != null){
                        mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_API_VERSION, response.body().getPuplic_settings().getApi_version());
                        mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_VERSION, response.body().getPuplic_settings().getMinumum_acceptable_version());
                        mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_API_VERSION, response.body().getPuplic_settings().getMinumum_acceptable_api_version());
                        mSessionManager.updatePreferenceInteger(Constants.KEY_PUBLIC_SETTING_MINIMUM_PROFILE_REMINDER, response.body().getPuplic_settings().getProfile_update_reminder_period());
                        mSessionManager.updatePreferenceInteger(Constants.KEY_PUBLIC_SETTING_BACKUP_REMINDER, response.body().getPuplic_settings().getBackup_update_reminder_period());
                    }

                    //CHECK IF APPLICATION IS IN MAINTENANCE
                    if(response.body().getPuplic_settings() != null && response.body().getPuplic_settings().getMaintenance().equals("1")){
                        //MOVE TO MAINTENANCE SCREEN
                        Intent mIntent = new Intent(NewAuthBiomatricAuthenticationActivity.this, MaintanaceActivity.class);
                        mIntent.putExtra(Constants.KEY_IS_FROM_CALLBACKAPI,false);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        finish();
                    }else {
                        goToNextActivity();
                    }


                }else {

                    displayAlertDialogue(getResources().getString(R.string.strUsernotfound));

                }
            }

            @Override
            public void onFailure(Call<NewAuthLogin> call, Throwable t) {


                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                CommonMethods.closeDialog();

            }
        });

    }


}

