package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.NewAuthStatusModel;
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

public class NewAuthEnterOTPActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NewAuthEnterOTPActivity";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView imgBackArrow, imgCancel, imgLogo;
    private TextView txtVerifiedSuccessully,txtInvalidOTP,txtContinueEnabled, txtContinueDisable, txtMobileNumber;
    private LinearLayout linearResendCode, linearClickToEdit;
    private EditText edtFive, edtSix, edtFour, edtThree, edtTwo, edtOne;
    private boolean isOtpVarified;
    private String strCountryCode,strMobileNumber,strCampaignId,strRefferalId,strInvitename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_auth_enter_otpactivity);

        setIds();
        setOnClicks();
    }

    private void setIds() {

        mActivity = NewAuthEnterOTPActivity.this;
        mContext = NewAuthEnterOTPActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        imgCancel = (ImageView) findViewById(R.id.imgCancel);
        imgBackArrow = (ImageView) findViewById(R.id.imgBackArrow);

        txtMobileNumber = (TextView) findViewById(R.id.txtMobileNumber);
        txtContinueDisable = (TextView) findViewById(R.id.txtContinueDisable);
        txtContinueEnabled = (TextView) findViewById(R.id.txtContinueEnabled);
        txtInvalidOTP = (TextView) findViewById(R.id.txtInvalidOTP);
        txtVerifiedSuccessully = (TextView) findViewById(R.id.txtVerifiedSuccessully);

        linearClickToEdit = (LinearLayout) findViewById(R.id.linearClickToEdit);
        linearResendCode = (LinearLayout) findViewById(R.id.linearResendCode);

        edtOne = (EditText) findViewById(R.id.edtOne);
        edtTwo = (EditText) findViewById(R.id.edtTwo);
        edtThree = (EditText) findViewById(R.id.edtThree);
        edtFour = (EditText) findViewById(R.id.edtFour);
        edtFive = (EditText) findViewById(R.id.edtFive);
        edtSix = (EditText) findViewById(R.id.edtSix);

        //GET INTENT DATA
         strMobileNumber =getIntent().getStringExtra(Constants.KEY_MOBILE_NUMBER);
         strCountryCode =getIntent().getStringExtra(Constants.KEY_COUNTRY_CODE);
         strCampaignId =getIntent().getStringExtra(Constants.KEY_CAMPAIGNID);
         strRefferalId =getIntent().getStringExtra(Constants.KEY_REFFERALID);
         strInvitename =getIntent().getStringExtra(Constants.KEY_NAMEOFINVITE);

        //SET MOBILE NUMBER
        txtMobileNumber.setText(getString(R.string.strWehaveRecieved) + " (+" + strCountryCode+" "+ strMobileNumber + ") " + getString(R.string.strEnterTheCodeBelow));


        edtOne.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edtOne.getText().toString().length() == 1)     //size as per your requirement
                {
                    edtTwo.requestFocus();
                } else {
                    edtOne.requestFocus();
                }
                //UPDATE CONTINUE BUTTON
                checkIfAllDigitEntered();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        edtTwo.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edtTwo.getText().toString().length() == 1)     //size as per your requirement
                {
                    edtThree.requestFocus();
                } else {
                    edtOne.requestFocus();
                }

                //UPDATE CONTINUE BUTTON
                checkIfAllDigitEntered();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        edtThree.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edtThree.getText().toString().length() == 1)     //size as per your requirement
                {
                    edtFour.requestFocus();
                } else {
                    edtTwo.requestFocus();
                }
                //UPDATE CONTINUE BUTTON
                checkIfAllDigitEntered();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
        edtFour.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edtFour.getText().toString().length() == 1)     //size as per your requirement
                {
                    edtFive.requestFocus();
                } else {
                    edtThree.requestFocus();
                }

                //UPDATE CONTINUE BUTTON
                checkIfAllDigitEntered();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        edtFive.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edtFive.getText().toString().length() == 1)     //size as per your requirement
                {
                    edtSix.requestFocus();

                } else {
                    edtFour.requestFocus();
                }

                //UPDATE CONTINUE BUTTON
                checkIfAllDigitEntered();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }



        });

        edtSix.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edtSix.getText().toString().length() == 1)     //size as per your requirement
                {

                } else {
                    edtFive.requestFocus();
                }

                //UPDATE CONTINUE BUTTON
                checkIfAllDigitEntered();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
    }

    private void checkIfAllDigitEntered() {

        if (!edtOne.getText().toString().isEmpty() && !edtTwo.getText().toString().isEmpty() && !edtThree.getText().toString().isEmpty() && !edtFour.getText().toString().isEmpty() && !edtFive.getText().toString().isEmpty() && !edtSix.getText().toString().isEmpty()) {
           apiCallVerifyOTPMobile();
        }else{
            txtContinueEnabled.setVisibility(View.INVISIBLE);
            txtContinueDisable.setVisibility(View.VISIBLE);
            txtInvalidOTP.setVisibility(View.VISIBLE);
            txtVerifiedSuccessully.setVisibility(View.GONE);
        }
    }

    private void setOnClicks() {

        imgCancel.setOnClickListener(this);
        imgBackArrow.setOnClickListener(this);
        imgLogo.setOnClickListener(this);
        linearClickToEdit.setOnClickListener(this);
        linearResendCode.setOnClickListener(this);
        txtContinueEnabled.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {

            case R.id.imgLogo:

                break;
            case R.id.imgCancel:
                 finish();
                break;
            case R.id.imgBackArrow:
                finish();
                break;
            case R.id.linearClickToEdit:
                finish();
                break;
            case R.id.linearResendCode:


                //CLEAR OTP FIELDS
                edtOne.setText("");
                edtTwo.setText("");
                edtThree.setText("");
                edtFour.setText("");
                edtFive.setText("");
                edtSix.setText("");
                txtInvalidOTP.setVisibility(View.INVISIBLE);
                txtVerifiedSuccessully.setVisibility(View.INVISIBLE);

                edtOne.setTextColor(getColor(R.color.colorBlack));
                edtTwo.setTextColor(getColor(R.color.colorBlack));
                edtThree.setTextColor(getColor(R.color.colorBlack));
                edtFour.setTextColor(getColor(R.color.colorBlack));
                edtFive.setTextColor(getColor(R.color.colorBlack));
                edtSix.setTextColor(getColor(R.color.colorBlack));



                //CALL GET OTP
                apiSendOTPtoMobile();

                break;
            case R.id.txtContinueEnabled:
                if(isOtpVarified) {
                    Intent mIntent = new Intent(NewAuthEnterOTPActivity.this, NewAuthEnterUserNameActivity.class);
                    mIntent.putExtra(Constants.KEY_MOBILE_NUMBER ,strMobileNumber);
                    mIntent.putExtra(Constants.KEY_COUNTRY_CODE ,strCountryCode);
                    mIntent.putExtra(Constants.KEY_CAMPAIGNID ,strCampaignId);
                    mIntent.putExtra(Constants.KEY_REFFERALID ,strRefferalId);
                    mIntent.putExtra(Constants.KEY_NAMEOFINVITE ,strInvitename);
                    startActivity(mIntent);
                }else{
                    CommonMethods.displayToast(mContext,getString(R.string.strInvalidCode));
                }

                break;
        }

    }

    private void apiSendOTPtoMobile(){

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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("receiver_number", strMobileNumber);
        paramObject.addProperty("country_code",strCountryCode);

        Call<NewAuthStatusModel> call = patchService1.verifyPhone(paramObject);

        call.enqueue(new Callback<NewAuthStatusModel>() {
            @Override
            public void onResponse(Call<NewAuthStatusModel> call, Response<NewAuthStatusModel> response) {

                CommonMethods.printLogE("Response @ apiCallToGetOTP: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiCallToGetOTP: ", "" + response.code());

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ apiCallToGetOTP: ", "" + gson.toJson(response.body()));


                    CommonMethods.displayToast(mContext, "Please check your inbox, We sent you an OTP");


                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }
            }

            @Override
            public void onFailure(Call<NewAuthStatusModel> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });


    }

    private void apiCallVerifyOTPMobile() {

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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("receiver_number", strMobileNumber);
        paramObject.addProperty("country_code", strCountryCode);
        paramObject.addProperty("code", edtOne.getText().toString()+edtTwo.getText().toString()+edtThree.getText().toString()+edtFour.getText().toString()+edtFive.getText().toString()+edtSix.getText().toString());

        Call<NewAuthStatusModel> call = patchService1.verifyOTPPhone(paramObject);

        call.enqueue(new Callback<NewAuthStatusModel>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<NewAuthStatusModel> call, Response<NewAuthStatusModel> response) {

                CommonMethods.printLogE("Response @ apiCallVerifyOTPMobile: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiCallVerifyOTPMobile: ", "" + response.code());
                CommonMethods.printLogE("Response @ apiCallVerifyOTPMobile: ", "" + gson.toJson(response.body()));

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.code() == 200 && response.body().getStatus().toString().contains("approved")) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();



                    CommonMethods.displayToast(mContext, response.body().getStatus());

                    txtContinueEnabled.setVisibility(View.VISIBLE);
                    txtContinueDisable.setVisibility(View.INVISIBLE);
                    txtInvalidOTP.setVisibility(View.GONE);
                    txtVerifiedSuccessully.setVisibility(View.VISIBLE);


                    isOtpVarified = true;

                    //HIDE KEY BOARD
                    CommonMethods.hideKeyboard(mActivity);


                } else {

                    txtContinueEnabled.setVisibility(View.INVISIBLE);
                    txtContinueDisable.setVisibility(View.VISIBLE);
                    txtInvalidOTP.setVisibility(View.VISIBLE);
                    txtVerifiedSuccessully.setVisibility(View.GONE);

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strOTPwrong));

                    isOtpVarified = false;

                    edtOne.setTextColor(getColor(R.color.colorCuratorRedError));
                    edtTwo.setTextColor(getColor(R.color.colorCuratorRedError));
                    edtThree.setTextColor(getColor(R.color.colorCuratorRedError));
                    edtFour.setTextColor(getColor(R.color.colorCuratorRedError));
                    edtFive.setTextColor(getColor(R.color.colorCuratorRedError));
                    edtSix.setTextColor(getColor(R.color.colorCuratorRedError));

                }
            }

            @Override
            public void onFailure(Call<NewAuthStatusModel> call, Throwable t) {

                isOtpVarified = false;

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });

    }

}