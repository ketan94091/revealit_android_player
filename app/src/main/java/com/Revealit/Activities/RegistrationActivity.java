package com.Revealit.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.CheckEmailModel;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "RegistrationActivity";


    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView imgCancel;
    private EditText edtPassword, edtEmailid;
    private LinearLayout linearEmailAlreadyRegistered, linearLongPasswordMsgs, linearWarningMsgs;
    private TextView txtLogin, txtTermsOfService, txtGetStartedEnabled, txtGetStartedDisabled;
    private boolean isEmailValid = false;
    private boolean isPasswordValid = false;
    private boolean isEmailVarified = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        setIds();
        setOnclick();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setIds() {

        mActivity = RegistrationActivity.this;
        mContext = RegistrationActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgCancel = (ImageView) findViewById(R.id.imgCancel);

        edtEmailid = (EditText) findViewById(R.id.edtEmailid);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        linearWarningMsgs = (LinearLayout) findViewById(R.id.linearWarningMsgs);
        linearLongPasswordMsgs = (LinearLayout) findViewById(R.id.linearLongPasswordMsgs);
        linearEmailAlreadyRegistered = (LinearLayout) findViewById(R.id.linearEmailAlreadyRegistered);

        txtGetStartedDisabled = (TextView) findViewById(R.id.txtGetStartedDisabled);
        txtGetStartedEnabled = (TextView) findViewById(R.id.txtGetStartedEnabled);
        txtTermsOfService = (TextView) findViewById(R.id.txtTermsOfService);
        txtLogin = (TextView) findViewById(R.id.txtLogin);

        //UNDERLINE LOGIN TEXTVIEW IF USER EMAIL ALREADY REGISTERED
        txtLogin.setPaintFlags(txtLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        //EMAIL VALIDATION
        edtEmailid.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                //HIDE EMAIL VALIDATAION MSG
                linearEmailAlreadyRegistered.setVisibility(View.GONE);

                //NO SPACE ALLOWED
                if (edtEmailid.getText().toString().contains(" ")) {

                    edtEmailid.setText(edtEmailid.getText().toString().replaceAll(" ", ""));
                    edtEmailid.setSelection(edtEmailid.getText().length());

                    CommonMethods.displayToast(mContext, getResources().getString(R.string.strNoSpaceAllowed));

                } else {
                    if (edtEmailid.getText().toString().matches(Constants.EMAIL_VALIDATION_REGEX) && s.length() > 0) {
                        edtEmailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.check, 0);

                        //CHECK WHETHER EMAIL IS ALREADY REGISTERED
                        checkEmailValidation();

                    } else {
                        edtEmailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.cross_circle, 0);

                        isEmailValid = false;
                        updateUI();
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


        //PASSWORD VALIDATION
        edtPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                //NO SPACE ALLOWED
                if (edtPassword.getText().toString().contains(" ")) {

                    edtPassword.setText(edtPassword.getText().toString().replaceAll(" ", ""));
                    edtPassword.setSelection(edtPassword.getText().length());

                    CommonMethods.displayToast(mContext, getResources().getString(R.string.strNoSpaceAllowed));

                } else {

                    if (edtPassword.getText().toString().matches(Constants.PASSWORD_VALIDATION_REGEX) && s.length() > 0) {
                        edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.check, 0);

                        linearLongPasswordMsgs.setVisibility(View.GONE);
                        linearWarningMsgs.setVisibility(View.GONE);

                        isPasswordValid = true;

                        updateUI();

                    } else {
                        edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.cross_circle, 0);

                        linearLongPasswordMsgs.setVisibility(View.VISIBLE);
                        linearWarningMsgs.setVisibility(View.VISIBLE);

                        isPasswordValid = false;

                        updateUI();
                    }

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        edtEmailid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    edtEmailid.setBackground(getResources().getDrawable(R.drawable.round_corner_focused_border));
                } else {
                    edtEmailid.setBackground(getResources().getDrawable(R.drawable.round_corner_border));
                }

            }
        });

        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    edtPassword.setBackground(getResources().getDrawable(R.drawable.round_corner_focused_border));
                } else {
                    edtPassword.setBackground(getResources().getDrawable(R.drawable.round_corner_border));
                }

            }
        });

    }


    private void setOnclick() {

        imgCancel.setOnClickListener(this);
        txtGetStartedEnabled.setOnClickListener(this);
        txtTermsOfService.setOnClickListener(this);
        txtLogin.setOnClickListener(this);

    }


    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {

            case R.id.imgCancel:

                finish();

                break;
            case R.id.txtGetStartedEnabled:


                //SEND VERIFICATION CODE ON CLICK OF GET STARTED BUTTON
                sendVerificationCode();


                break;
            case R.id.txtTermsOfService:


                break;


            case R.id.txtLogin:

                Intent mLoginIntent = new Intent(RegistrationActivity.this, LoginActivityActivity.class);
                startActivity(mLoginIntent);
                finish();


                break;
        }
    }


    private void updateUI() {

        if (isEmailValid && isPasswordValid) {

            //ENABLE BUTTON
            txtGetStartedDisabled.setVisibility(View.GONE);
            txtGetStartedEnabled.setVisibility(View.VISIBLE);

        } else {

            txtGetStartedDisabled.setVisibility(View.VISIBLE);
            txtGetStartedEnabled.setVisibility(View.GONE);
        }

    }


    private void checkEmailValidation() {


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
                .baseUrl(Constants.API_END_POINTS_REGISTRATION)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty(Constants.PROTON_EMAIL, edtEmailid.getText().toString());
        // paramObject.addProperty(Constants.AUTH_PASSWORD, edtPassword.getText().toString());

        Call<CheckEmailModel> call = patchService1.checkEmailid(paramObject);

        call.enqueue(new Callback<CheckEmailModel>() {
            @Override
            public void onResponse(Call<CheckEmailModel> call, Response<CheckEmailModel> response) {

                CommonMethods.printLogE("Response @ checkEmailValidation: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ checkEmailValidation: ", "" + response.code());


                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ checkEmailValidation: ", "" + gson.toJson(response.body()));


                    if (response.body().getStatus().equals("ok")) {


                        //UPDATE FLAG
                        isEmailValid = true;

                        linearEmailAlreadyRegistered.setVisibility(View.GONE);

                        //SAVE AUTHENTICATION DATA
                        mSessionManager.updatePreferenceString(Constants.PROTON_EMAIL, edtEmailid.getText().toString());
                        mSessionManager.updatePreferenceString(Constants.PROTON_PASSWORD, edtPassword.getText().toString());

                        //UPDATE UI
                        updateUI();


                    } else if (response.body().getStatus().equals("error")) {

                        //VISIBLE EMAIL ALREADY REGISTERED MSG
                        linearEmailAlreadyRegistered.setVisibility(View.VISIBLE);

                        //CHANGE THE DRAWBLE
                        edtEmailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.cross_circle, 0);

                        //UPDATE FLAG
                        isEmailValid = false;

                        //UPDATE UI
                        updateUI();
                    }

                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                    //VISIBLE EMAIL ALREADY REGISTERED MSG
                    linearEmailAlreadyRegistered.setVisibility(View.VISIBLE);

                    //CHANGE THE DRAWBLE
                    edtEmailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.cross_circle, 0);

                    //UPDATE FLAG
                    isEmailValid = false;

                    //UPDATE UI
                    updateUI();

                }
            }

            @Override
            public void onFailure(Call<CheckEmailModel> call, Throwable t) {


                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                //VISIBLE EMAIL ALREADY REGISTERED MSG
                linearEmailAlreadyRegistered.setVisibility(View.VISIBLE);

                //CHANGE THE DRAWBLE
                edtEmailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.cross_circle, 0);

                //UPDATE FLAG
                isEmailValid = false;

                //UPDATE UI
                updateUI();


            }
        });

    }

    private void sendVerificationCode() {

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
                .baseUrl(Constants.API_END_POINTS_REGISTRATION)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty(Constants.PROTON_EMAIL, edtEmailid.getText().toString());

        Call<CheckEmailModel> call = patchService1.sendVerificationEmail(paramObject);

        call.enqueue(new Callback<CheckEmailModel>() {
            @Override
            public void onResponse(Call<CheckEmailModel> call, Response<CheckEmailModel> response) {

                CommonMethods.printLogE("Response @ sendVerificationCode: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ sendVerificationCode: ", "" + response.code());

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ sendVerificationCode: ", "" + gson.toJson(response.body()));


                    if (response.body().getStatus().equals("ok")) {

                        //SAVE AUTHENTICATION DATA
                        mSessionManager.updatePreferenceString(Constants.PROTON_EMAIL, edtEmailid.getText().toString());
                        mSessionManager.updatePreferenceString(Constants.PROTON_PASSWORD, edtPassword.getText().toString());

                        Intent mIntent = new Intent(RegistrationActivity.this, VerificationCodeActivity.class);
                        startActivity(mIntent);

                    } else if (response.body().getStatus().equals("error")) {

                        CommonMethods.buildDialog(mContext, response.body().getMessage());

                    }

                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }
            }

            @Override
            public void onFailure(Call<CheckEmailModel> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });

    }


}

