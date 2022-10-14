package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.CheckUserNameStatusModel;
import com.Revealit.ModelClasses.SubmitProfileModel;
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

public class NewAuthEnterUserNameActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NewAuthEnterUserNameActivity";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView imgUsernameStutusFalse,imgUsernameStutusTrue,imgBackArrow, imgCancel, imgLogo;
    private TextView txtContinueEnabled, txtUsernameInUse,txtContinueDisable, txtMobileNumber;
    private LinearLayout linearUsernameTrue, linearUsernameWarnings;
    private EditText edtUsername;
    private String strCountryCode,strMobileNumber,strCampaignId,strRefferalId,strInvitename;


    long delay = 5000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    private Runnable input_username = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                apiCheckIfUsernameExist();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_auth_enter_user_name);
        setIds();
        setOnClicks();
    }

    private void setIds() {

        mActivity = NewAuthEnterUserNameActivity.this;
        mContext = NewAuthEnterUserNameActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        imgCancel = (ImageView) findViewById(R.id.imgCancel);
        imgUsernameStutusTrue = (ImageView) findViewById(R.id.imgUsernameStutusTrue);
        imgUsernameStutusFalse = (ImageView) findViewById(R.id.imgUsernameStutusFalse);

        txtContinueEnabled = (TextView) findViewById(R.id.txtContinueEnabled);
        txtContinueDisable = (TextView) findViewById(R.id.txtContinueDisable);
        txtUsernameInUse = (TextView) findViewById(R.id.txtUsernameInUse);

        linearUsernameWarnings = (LinearLayout) findViewById(R.id.linearUsernameWarnings);
        linearUsernameTrue = (LinearLayout) findViewById(R.id.linearUsernameTrue);

        edtUsername = (EditText) findViewById(R.id.edtUsername);

        //GET INTENT DATA
        strMobileNumber =getIntent().getStringExtra(Constants.KEY_MOBILE_NUMBER);
        strCountryCode =getIntent().getStringExtra(Constants.KEY_COUNTRY_CODE);
        strCampaignId =getIntent().getStringExtra(Constants.KEY_CAMPAIGNID);
        strRefferalId =getIntent().getStringExtra(Constants.KEY_REFFERALID);
        strInvitename =getIntent().getStringExtra(Constants.KEY_NAMEOFINVITE);


        edtUsername.addTextChangedListener(new TextWatcher() {
                                                   @Override
                                                   public void beforeTextChanged(CharSequence s, int start, int count,
                                                                                 int after) {
                                                   }

                                                   @Override
                                                   public void onTextChanged(final CharSequence s, int start, int before,
                                                                             int count) {
                                                       //You need to remove this to run only once
                                                       handler.removeCallbacks(input_username);

                                                   }

                                                   @Override
                                                   public void afterTextChanged(final Editable s) {
                                                       //avoid triggering event when text is empty
                                                       if (s.length() > 3) {
                                                           last_text_edit = System.currentTimeMillis();
                                                           handler.postDelayed(input_username, delay);
                                                       } else if(s.length() == 0){
                                                           linearUsernameWarnings.setVisibility(View.INVISIBLE);
                                                           linearUsernameTrue.setVisibility(View.INVISIBLE);
                                                           imgUsernameStutusFalse.setVisibility(View.INVISIBLE);
                                                           imgUsernameStutusTrue.setVisibility(View.INVISIBLE);
                                                           txtUsernameInUse.setVisibility(View.GONE);
                                                       }else{
                                                           linearUsernameWarnings.setVisibility(View.VISIBLE);
                                                           linearUsernameTrue.setVisibility(View.INVISIBLE);
                                                           imgUsernameStutusFalse.setVisibility(View.VISIBLE);
                                                           imgUsernameStutusTrue.setVisibility(View.INVISIBLE);
                                                           txtUsernameInUse.setVisibility(View.GONE);
                                                       }
                                                   }
                                               }

        );
    }

    private void setOnClicks() {

        imgCancel.setOnClickListener(this);
        imgLogo.setOnClickListener(this);
        txtContinueEnabled.setOnClickListener(this);
    }

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
            case R.id.txtContinueEnabled:

               apiSubmitProfile();

                break;
        }

    }

    private void apiSubmitProfile(){

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
        paramObject.addProperty("campaign_id",Long.valueOf(strCampaignId));
        paramObject.addProperty("referral_id",Long.valueOf(strRefferalId));
        paramObject.addProperty("country_code",strCountryCode);
        paramObject.addProperty("username",edtUsername.getText().toString());

        Log.e("receiver_number :" ,strMobileNumber);
        Log.e("campaign_id :" ,""+Long.valueOf(strCampaignId));
        Log.e("referral_id :" ,""+Long.valueOf(strRefferalId));
        Log.e("country_code :" ,strCountryCode);
        Log.e("username :" ,edtUsername.getText().toString());


        Call<SubmitProfileModel> call = patchService1.submitProfile(paramObject);

        call.enqueue(new Callback<SubmitProfileModel>() {
            @Override
            public void onResponse(Call<SubmitProfileModel> call, Response<SubmitProfileModel> response) {

                CommonMethods.printLogE("Response @ apiSubmitProfile: ", "" + response.code());
                CommonMethods.printLogE("Response @ apiSubmitProfile: ", "" + gson.toJson(response.body()));


                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();


                    updateUI(response.body());



                }else if(response.code() == Constants.API_CODE_NOTFOUND){
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }
            }

            @Override
            public void onFailure(Call<SubmitProfileModel> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });


    }

    private void updateUI(SubmitProfileModel body) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();


        mSessionManager.updatePreferenceString(Constants.KEY_USER_DATA, ""+gson.toJson(body));
        mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN ,body.getauth_token());
        mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE ,"Bearer");
        mSessionManager.updatePreferenceString(Constants.PROTON_ACCOUNT_NAME ,body.getProton().getAccountName());
        mSessionManager.updatePreferenceString(Constants.KEY_PRON_WALLET_DETAILS ,gson.toJson(body.getProton()));
        mSessionManager.updatePreferenceString(Constants.KEY_REVEALIT_PRIVATE_KEY ,body.getrevealit_private_key());
       // mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,body.isActivated());

        //UPDATE FLAG IF USER IS ADMIN OR NOT
        if(body.getAudience().equals("Admin")){
            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,true);
        }else{
            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,false);
        }


        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,true);

        //UPDATE FLAG FOR APPLICATION MODE
        mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);


        Intent mIntent = new Intent(NewAuthEnterUserNameActivity.this,InviteAndEarnActivity.class);
        mIntent.putExtra(Constants.KEY_NEW_AUTH_USERNAME ,body.getProton().getAccountName());
        startActivity(mIntent);
    }

    private void apiCheckIfUsernameExist(){

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

        Call<CheckUserNameStatusModel> call = patchService1.checkIfUserExist("api/usernotexists?username="+edtUsername.getText().toString());

        call.enqueue(new Callback<CheckUserNameStatusModel>() {
            @Override
            public void onResponse(Call<CheckUserNameStatusModel> call, Response<CheckUserNameStatusModel> response) {

                CommonMethods.printLogE("Response @ apiCheckIfUsernameExist: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiCheckIfUsernameExist: ", "" + response.code());

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ apiCheckIfUsernameExist: ", "" + gson.toJson(response.body()));

                    //UPDATE INVITE UI
                    updateInviteUI(response.body().isStatus());

                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }
            }

            @Override
            public void onFailure(Call<CheckUserNameStatusModel> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });


    }

    private void updateInviteUI(boolean isNotExist) {

        if(isNotExist){
            linearUsernameWarnings.setVisibility(View.INVISIBLE);
            linearUsernameTrue.setVisibility(View.VISIBLE);
            imgUsernameStutusFalse.setVisibility(View.INVISIBLE);
            imgUsernameStutusTrue.setVisibility(View.VISIBLE);
            txtContinueDisable.setVisibility(View.INVISIBLE);
            txtContinueEnabled.setVisibility(View.VISIBLE);
            txtUsernameInUse.setVisibility(View.GONE);

        }else{
            linearUsernameWarnings.setVisibility(View.INVISIBLE);
            linearUsernameTrue.setVisibility(View.INVISIBLE);
            imgUsernameStutusFalse.setVisibility(View.INVISIBLE);
            imgUsernameStutusTrue.setVisibility(View.INVISIBLE);
            txtContinueEnabled.setVisibility(View.INVISIBLE);
            txtUsernameInUse.setVisibility(View.VISIBLE);
            txtContinueDisable.setVisibility(View.VISIBLE);
        }
    }

}