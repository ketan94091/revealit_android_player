package com.Revealit.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.UserDetailsModel;
import com.Revealit.ModelClasses.UserRegistrationModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
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

public class RevealitNameActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "RevealitNameActivity";


    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView imgGoBack;
    private EditText edtUsername;
    private TextView txtGetNextDisabled,txtNextEnabled;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_revealit);


        setIds();
        setOnclick();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setIds() {

        mActivity = RevealitNameActivity.this;
        mContext = RevealitNameActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgGoBack =(ImageView)findViewById(R.id.imgGoBack);

        edtUsername = (EditText)findViewById(R.id.edtUsername);

        txtGetNextDisabled =(TextView)findViewById(R.id.txtGetNextDisabled);
        txtNextEnabled =(TextView)findViewById(R.id.txtNextEnabled);

        //SET USERNAME FROM EMAIL
        String strUserEMail = mSessionManager.getPreference(Constants.PROTON_EMAIL);
        String[] parts = strUserEMail.split("@");
        edtUsername.setText(""+parts[0].replaceAll("[^a-zA-Z0-9]", ""));



        edtUsername.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if (s.toString().length() == 0){
                    edtUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.cross_circle, 0);
                    txtGetNextDisabled.setVisibility(View.VISIBLE);
                    txtNextEnabled.setVisibility(View.GONE);
                }else {
                    edtUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.check, 0);
                    txtGetNextDisabled.setVisibility(View.GONE);
                    txtNextEnabled.setVisibility(View.VISIBLE);
                }

            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        edtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    edtUsername.setBackground(getResources().getDrawable(R.drawable.round_corner_focused_border));
                } else {
                    edtUsername.setBackground(getResources().getDrawable(R.drawable.round_corner_border));
                }

            }
        });


    }


    private void setOnclick() {

        imgGoBack.setOnClickListener(this);
        txtNextEnabled.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.imgGoBack:

                finish();

                break;
            case R.id.txtNextEnabled:


                openBiomatricPermissionDialog();


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
                mSessionManager.updatePreferenceBoolean(Constants.IS_ALLOW_BIOMETRIC , false);

                mAlertDialog.dismiss();

                userRegistrationAPI();

            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE FLAG IF USER ALLOW BIOMETRIC AUTHENTICATION
                mSessionManager.updatePreferenceBoolean(Constants.IS_ALLOW_BIOMETRIC , true);

                mAlertDialog.dismiss();

                userRegistrationAPI();

            }
        });
        mAlertDialog.show();

    }

    private void userRegistrationAPI() {

        //OPEN DIALOGUE
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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_REGISTRATION_KEY))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty(Constants.PROTON_EMAIL, mSessionManager.getPreference(Constants.PROTON_EMAIL));
        paramObject.addProperty(Constants.PROTON_PASSWORD, mSessionManager.getPreference(Constants.PROTON_PASSWORD));
        paramObject.addProperty(Constants.PROTON_VERIFICATION_CODE, mSessionManager.getPreference(Constants.PROTON_VERIFICATION_CODE));
        paramObject.addProperty(Constants.PROTON_USERNAME, edtUsername.getText().toString());

        Call<UserRegistrationModel> call = patchService1.userRegistration(paramObject);

        call.enqueue(new Callback<UserRegistrationModel>() {
            @Override
            public void onResponse(Call<UserRegistrationModel> call, Response<UserRegistrationModel> response) {

                CommonMethods.printLogE("Response @ userRegistrationAPI: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ userRegistrationAPI: ", "" + response.code());

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ userRegistrationAPI: ", "" + gson.toJson(response.body()));

                    if (response.body().getStatus().equals("error")){

                        CommonMethods.buildDialog(mContext, response.body().getMessage());


                    } else if (response.body().getStatus().equals("ok") && response.body().getData() != null) {


                        //SAVE AUTHENTICATION DATA
                        mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN ,response.body().getData().getAccessToken());
                        mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE ,response.body().getData().getTokenType());
                        mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_EXPIRES_IN ,response.body().getData().getExpiresIn());
                        mSessionManager.updatePreferenceString(Constants.PROTON_ACCOUNT_NAME ,response.body().getData().getProtonAccountName());
                        mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN ,true);
                        mSessionManager.updatePreferenceBoolean(Constants.IS_FIRST_LOGIN ,true);


                        //GET USER DETAILS FROM TOKEN
                        getUserDetails();




                    }

                } else {

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }
            }

            @Override
            public void onFailure(Call<UserRegistrationModel> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });

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
                    Intent mIntent = new Intent(RevealitNameActivity.this, HomeScreenTabLayout.class);
                    mIntent.putExtra(Constants.KEY_IS_FROM_REGISTRATION_SCREEN,false);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    //FINISH THE CURRENT ACTIVITY
                    startActivity(mIntent);

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

