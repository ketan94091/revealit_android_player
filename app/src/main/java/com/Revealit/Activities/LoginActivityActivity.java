package com.Revealit.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.BuildConfig;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.LoginAuthModel;
import com.Revealit.ModelClasses.UserDetailsModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivityActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "LoginActivityActivity";


    public NavigationView navigationView;

    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private WebView webView;
    private EditText edtPassword, edtUsername;
    private TextView txtSwappingSilo,txtLogin,txtOk ,txtDontAllow;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setIds();
        setOnclick();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setIds() {

        mActivity = LoginActivityActivity.this;
        mContext = LoginActivityActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        txtLogin = (TextView) findViewById(R.id.txtLogin);
        txtSwappingSilo = (TextView) findViewById(R.id.txtSwappingSilo);

        //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
        txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);


    }


    private void setOnclick() {

        txtLogin.setOnClickListener(this);
        txtSwappingSilo.setOnClickListener(this);
    }


    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {

            case R.id.txtLogin:


                if (checkValidation()) {

                    //IF FIRST LOGIN DONE = DONT ASK FOR BIOMETRIC
                    //ELSE ASK FOR BIO METRIC ONLY ONCE
                    if (!mSessionManager.getPreferenceBoolean(Constants.IS_FIRST_LOGIN)) {
                        openBiomatricPermissionDialog();
                    } else {
                        callAuthenticationAPI();
                    }


                }

                break;
            case R.id.txtSwappingSilo:


                //OPEN END POINT SELECTION DIALOG
                openEndPointSelectionDialog();

                break;
        }
    }

    private void openBiomatricPermissionDialog() {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_biomatric_permission_dailoague, null);
        dialogBuilder.setView(dialogView);


        final AlertDialog mAlertDialog = dialogBuilder.create();
        txtDontAllow = (TextView) dialogView.findViewById(R.id.txtDontAllow);
        txtOk = (TextView) dialogView.findViewById(R.id.txtOk);

        txtDontAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE FLAG IF USER ALLOW BIOMETRIC AUTHENTICATION
                mSessionManager.updatePreferenceBoolean(Constants.IS_ALLOW_BIOMETRIC, false);

                mAlertDialog.dismiss();

                callAuthenticationAPI();

            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE FLAG IF USER ALLOW BIOMETRIC AUTHENTICATION
                mSessionManager.updatePreferenceBoolean(Constants.IS_ALLOW_BIOMETRIC, true);

                mAlertDialog.dismiss();

                callAuthenticationAPI();

            }
        });
        mAlertDialog.show();

    }

    private void callAuthenticationAPI() {

        //DISPLAY DIALOG
        CommonMethods.showDialog(mContext);


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
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty(Constants.AUTH_USERNAME, edtUsername.getText().toString());
        paramObject.addProperty(Constants.AUTH_PASSWORD, edtPassword.getText().toString());

        Call<LoginAuthModel> call = patchService1.loginAuth(paramObject);

        call.enqueue(new Callback<LoginAuthModel>() {
            @Override
            public void onResponse(Call<LoginAuthModel> call, Response<LoginAuthModel> response) {

                CommonMethods.printLogE("Response @ Login: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ Login: ", "" + response.code());

                //CLOSE DIALOG
                CommonMethods.closeDialog();


                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ Login: ", "" + gson.toJson(response.body()));

                    //SAVE AUTHENTICATION DATA
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN, response.body().getAccessToken());
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE, response.body().getTokenType());
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_EXPIRES_IN, response.body().getExpiresIn());
                    mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN, true);
                    mSessionManager.updatePreferenceBoolean(Constants.IS_FIRST_LOGIN, true);

                    //SAVE AUTHENTICATION DATA
                    mSessionManager.updatePreferenceString(Constants.PROTON_EMAIL, edtUsername.getText().toString());
                    mSessionManager.updatePreferenceString(Constants.PROTON_PASSWORD, edtPassword.getText().toString());


                    //GET USER DETAILS FROM TOKEN AND SAVE IT
                    getUserDetails();


                } else {

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strUsernamePasswordWrong));
                }
            }

            @Override
            public void onFailure(Call<LoginAuthModel> call, Throwable t) {


                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strUsernamePasswordWrong));


                CommonMethods.closeDialog();

            }
        });

    }


    public boolean checkValidation() {

        if (edtUsername.getText().toString().isEmpty()) {
            CommonMethods.displayToast(mContext, getResources().getString(R.string.strEnterUserName));
            return false;
        }else if (edtPassword.getText().toString().isEmpty()) {
            CommonMethods.displayToast(mContext, getResources().getString(R.string.strEnterPassword));
            return false;
        } else {
            return true;
        }
    }

    public boolean checkValidation(String username , String password) {

        if (username.isEmpty()) {
          CommonMethods.displayToast(mContext, getResources().getString(R.string.strEnterUserName));
            return false;
        } else if (password.isEmpty()) {
            CommonMethods.displayToast(mContext, getResources().getString(R.string.strEnterPassword));
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    private void openEndPointSelectionDialog() {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_endpoint_selection, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog mAlertDialog = dialogBuilder.create();
        mAlertDialog.setCancelable(true);

        //SET CURRENT PROGRESSBAR
        ImageView imgCloseDailoge = (ImageView) dialogView.findViewById(R.id.imgCloseDailoge);

        LinearLayout linearBetacurator = (LinearLayout) dialogView.findViewById(R.id.linearBetacurator);
        LinearLayout linearStaging = (LinearLayout) dialogView.findViewById(R.id.linearStaging);
        LinearLayout linearTesting1 = (LinearLayout) dialogView.findViewById(R.id.linearTesting1);
        LinearLayout linearTesting2 = (LinearLayout) dialogView.findViewById(R.id.linearTesting2);
        LinearLayout linearIntegration = (LinearLayout) dialogView.findViewById(R.id.linearIntegration);

        TextView txtBetaCuratorMobile = (TextView) dialogView.findViewById(R.id.txtBetaCuratorMobile);
        TextView txtBetaCuratorRegistration = (TextView) dialogView.findViewById(R.id.txtBetaCuratorRegistration);

        TextView txtStagingMobile = (TextView) dialogView.findViewById(R.id.txtStagingMobile);
        TextView txtStagingRegistration = (TextView) dialogView.findViewById(R.id.txtStagingRegistration);

        TextView txtTesting1Mobile = (TextView) dialogView.findViewById(R.id.txtTesting1Mobile);
        TextView txtTesting1Registration = (TextView) dialogView.findViewById(R.id.txtTesting1Registration);

        TextView txtTesting2Mobile = (TextView) dialogView.findViewById(R.id.txtTesting2Mobile);
        TextView txtTesting2Registration = (TextView) dialogView.findViewById(R.id.txtTesting2Registration);

        TextView txtIntegrationMobile = (TextView) dialogView.findViewById(R.id.txtIntegrationMobile);
        TextView txtIntegrationRegistration = (TextView) dialogView.findViewById(R.id.txtIntegrationRegistration);



        switch (mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID)) {
            case 1:

                linearBetacurator.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                txtBetaCuratorMobile.setTextColor(mContext.getResources().getColor(R.color.colorCurrency));
                txtBetaCuratorRegistration.setTextColor(mContext.getResources().getColor(R.color.colorCurrencyNameSelected));


                break;
            case 2:

                linearStaging.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                txtStagingMobile.setTextColor(mContext.getResources().getColor(R.color.colorCurrency));
                txtStagingRegistration.setTextColor(mContext.getResources().getColor(R.color.colorCurrencyNameSelected));


                break;

            case 3:

                linearTesting1.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                txtTesting1Mobile.setTextColor(mContext.getResources().getColor(R.color.colorCurrency));
                txtTesting1Registration.setTextColor(mContext.getResources().getColor(R.color.colorCurrencyNameSelected));

                break;

            case 4:

                linearTesting2.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                txtTesting2Mobile.setTextColor(mContext.getResources().getColor(R.color.colorCurrency));
                txtTesting2Registration.setTextColor(mContext.getResources().getColor(R.color.colorCurrencyNameSelected));


                break;

            case 5:

                linearIntegration.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                txtIntegrationMobile.setTextColor(mContext.getResources().getColor(R.color.colorCurrency));
                txtIntegrationRegistration.setTextColor(mContext.getResources().getColor(R.color.colorCurrencyNameSelected));


                break;
        }

        imgCloseDailoge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();
            }
        });


        linearBetacurator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CHANGE API END POINT TO BETA
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, Constants.API_END_POINTS_MOBILE_B_CURATOR);
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, Constants.API_END_POINTS_REGISTRATION_B_CURATOR);
                mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 1);

                //UPDATE END POINT NAME
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, mActivity.getResources().getString(R.string.strBeta));


                //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
                txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);


                mAlertDialog.dismiss();
            }
        });
        linearStaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //STAGING CURATOR
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, Constants.API_END_POINTS_MOBILE_S_CURATOR);
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, Constants.API_END_POINTS_REGISTRATION_S_CURATOR);
                mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 2);

                //UPDATE END POINT NAME
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, mActivity.getResources().getString(R.string.strStaging));


                //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
                txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);


                mAlertDialog.dismiss();
            }
        });
        linearTesting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TESTING1 CURATOR
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, Constants.API_END_POINTS_MOBILE_T1_CURATOR);
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, Constants.API_END_POINTS_REGISTRATION_T1_CURATOR);
                mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 3);

                //UPDATE END POINT NAME
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, mActivity.getResources().getString(R.string.strTesting1));


                //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
                txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);


                mAlertDialog.dismiss();
            }
        });

        linearTesting2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TESTING2 CURATOR
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, Constants.API_END_POINTS_MOBILE_T2_CURATOR);
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, Constants.API_END_POINTS_REGISTRATION_T2_CURATOR);
                mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 4);


                //UPDATE END POINT NAME
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, mActivity.getResources().getString(R.string.strTesting2));


                //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
                txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);

                mAlertDialog.dismiss();
            }
        });

        linearIntegration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //INTEGRATION CURATOR
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, Constants.API_END_POINTS_MOBILE_INTEGRATION_CURATOR);
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, Constants.API_END_POINTS_REGISTRATION_INTEGRATION_CURATOR);
                mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 5);

                //UPDATE END POINT NAME
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, mActivity.getResources().getString(R.string.strIntegration));


                //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
                txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);


                mAlertDialog.dismiss();
            }
        });


        mAlertDialog.show();


    }
    private void getUserDetails() {

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

        Call<UserDetailsModel> call = patchService1.getUserDetails(Constants.API_GET_USER_DETAILS);

        call.enqueue(new Callback<UserDetailsModel>() {
            @Override
            public void onResponse(Call<UserDetailsModel> call, Response<UserDetailsModel> response) {

                CommonMethods.printLogE("Response @ getUserDetails: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ getUserDetails: ", "" + response.body().first_name);


                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    //SAVE DATA
                    mSessionManager.updatePreferenceString(Constants.PROTON_ACCOUNT_NAME ,response.body().getProton_account_name());
                    mSessionManager.updatePreferenceString(Constants.KEY_USERNAME ,response.body().getName());

                    //UPDATE FLAG FOR APPLICATION MODE
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);


                    //MOVE TO HOME SCREEN
                    Intent mIntent = new Intent(LoginActivityActivity.this, HomeScreenTabLayout.class);
                    startActivity(mIntent);

                    //FINISH THE CURRENT ACTIVITY
                    finish();

                }else{
                    CommonMethods.printLogE("Response @ Error", "Something went wrong!");

                }

            }

            @Override
            public void onFailure(Call<UserDetailsModel> call, Throwable t) {

                CommonMethods.printLogE("Response @ getUserDetails Error", "" +t.getMessage());

            }
        });

    }

}

