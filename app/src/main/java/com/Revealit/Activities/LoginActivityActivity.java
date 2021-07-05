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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.LoginAuthModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivityActivity extends YouTubeBaseActivity implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "LoginActivityActivity";

    private View mView;

    public NavController navController;

    public NavigationView navigationView;

    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private WebView webView;
    private EditText edtPassword, edtUsername;
    private TextView txtLogin;


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


    }


    private void setOnclick() {

        txtLogin.setOnClickListener(this);
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
        }
    }

    private void openBiomatricPermissionDialog() {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_biomatric_permission_dailoague, null);
        dialogBuilder.setView(dialogView);


        final AlertDialog mAlertDialog = dialogBuilder.create();
        TextView txtDontAllow = (TextView) dialogView.findViewById(R.id.txtDontAllow);
        TextView txtOk = (TextView) dialogView.findViewById(R.id.txtOk);

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
                .baseUrl(Constants.API_END_POINTS_MOBILE)
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
                   // mSessionManager.updatePreferenceString(Constants.PROTON_ACCOUNT_NAME ,response.body().getProton_account_name());
                    mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN, true);
                    mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN, true);
                    mSessionManager.updatePreferenceBoolean(Constants.IS_FIRST_LOGIN, true);

                    Intent mIntent = new Intent(LoginActivityActivity.this, HomeScreenTabLayout.class);
                    startActivity(mIntent);
                    finish();

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

    private boolean checkValidation() {

        if (edtUsername.getText().toString().isEmpty()) {
            CommonMethods.displayToast(mContext, getResources().getString(R.string.strEnterUserName));
            return false;
        } else if (edtPassword.getText().toString().isEmpty()) {
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
}

