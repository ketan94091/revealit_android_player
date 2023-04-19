package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.InviteModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

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

public class AddRefferalAndEarnActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "InviteAndEarnActivity";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private TextView txtUsername,txtContinueDisable,txtContinueEnabled,txtPromoWarnings,txtPromoAmount,txtInviteQuestion;
    private EditText edtPromo;
    private ImageView imgTelegram,imgInstagram,imgCurrencyIcon,imgClearId;
    private LinearLayout linearImgCancel,linearPromoWarnings;
    long delay = 2000; // 2 seconds after user stops typing
    long delayForMobile = 2000; // 2 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    private Runnable input_promo = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                //API SETTINGS DATA CALL
                apiInviteSettings(edtPromo.getText().toString());

            }
        }
    };
    private boolean isReferralValid= false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_refferal_and_earn);
        setIds();
        setOnClicks();
    }



    private void setIds() {

        mActivity = AddRefferalAndEarnActivity.this;
        mContext = AddRefferalAndEarnActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        txtInviteQuestion = (TextView) findViewById(R.id.txtInviteQuestion);
        txtPromoAmount = (TextView) findViewById(R.id.txtPromoAmount);
        txtPromoWarnings = (TextView) findViewById(R.id.txtPromoWarnings);
        txtContinueEnabled = (TextView) findViewById(R.id.txtContinueEnabled);
        txtContinueDisable = (TextView) findViewById(R.id.txtContinueDisable);
        txtUsername = (TextView) findViewById(R.id.txtUsername);

        edtPromo = (EditText)findViewById(R.id.edtPromo);

        imgClearId =(ImageView)findViewById(R.id.imgClearId);
        imgCurrencyIcon=(ImageView)findViewById(R.id.imgCurrencyIcon);
        imgTelegram=(ImageView)findViewById(R.id.imgTelegram);
        imgInstagram=(ImageView)findViewById(R.id.imgInstagram);

        linearPromoWarnings=(LinearLayout)findViewById(R.id.linearPromoWarnings);
        linearImgCancel=(LinearLayout)findViewById(R.id.linearImgCancel);

        //DISPLAY USER NAME
        txtUsername.setText(mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));

        //DISPLAY DYNAMIC PLACE HOLDER FROM SETTING API
        edtPromo.setHint(mSessionManager.getPreference(Constants.KEY_INVITE_PLACEHOLDER));


        edtPromo.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count,
                                                                          int after) {
                                            }

                                            @Override
                                            public void onTextChanged(final CharSequence s, int start, int before,
                                                                      int count) {
                                                //You need to remove this to run only once
                                                handler.removeCallbacks(input_promo);

                                            }

                                            @Override
                                            public void afterTextChanged(final Editable s) {
                                                //avoid triggering event when text is empty

                                                if (s.length() > 0) {
                                                    txtContinueEnabled.setVisibility(View.GONE);
                                                    txtContinueDisable.setVisibility(View.VISIBLE);
                                                    last_text_edit = System.currentTimeMillis();
                                                    handler.postDelayed(input_promo, delay);

                                                } else {
                                                    updateScreenUI(null,0);
                                                }
                                            }
                                        }

        );
    }


    private void setOnClicks() {
        txtContinueEnabled.setOnClickListener(this);
        imgTelegram.setOnClickListener(this);
        imgInstagram.setOnClickListener(this);
        linearImgCancel.setOnClickListener(this);
        imgClearId.setOnClickListener(this);

    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){
            case R.id.txtContinueEnabled:


                if(edtPromo.getText().toString().isEmpty()){
                    //REDIRECT TO HOME SCREEN
                    Intent mIntent = new Intent(AddRefferalAndEarnActivity.this, HomeScreenTabLayout.class);
                    startActivity(mIntent);

                }else if(isReferralValid){

                    apiSendInvites(edtPromo.getText().toString());

                }

                else{
                    CommonMethods.buildDialog(mContext,"Please add valid referral!");
                }

                break;
            case R.id.imgTelegram:

                Intent mTelegramBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.strTelegramURL)));
                startActivity(mTelegramBrowser);

                break;
            case R.id.imgInstagram:

                Intent mInstagramBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.strInstagramURL)));
                startActivity(mInstagramBrowser);

                break;
            case R.id.linearImgCancel:

                    //GO TO NEXT ACTIVITY
                    Intent mIntentCancel = new Intent(AddRefferalAndEarnActivity.this, HomeScreenTabLayout.class);
                    startActivity(mIntentCancel);
                    finishAffinity();

                break;
            case R.id.imgClearId:

                //CLEAR EDIT TEXT
                edtPromo.setText("");

        }

    }
    private void apiInviteSettings(String strInviteID){


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
        String url="?referral="+strInviteID;


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


                switch (response.code()){
                    case Constants.API_CODE_200:
                        if(response.body().getError().equals("")){
                            updateScreenUI(response.body(),1);
                        }else {
                            updateScreenUI(response.body(),2);
                        }


                        break;

                    case Constants.API_CODE_404:

                        CommonMethods.buildDialog(mContext, getResources().getString(R.string.strStatusCode404Error));

                        break;
                }

            }

            @Override
            public void onFailure(Call<InviteModel> call, Throwable t) {

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strApiCallFailure));


            }
        });


    }
    private void apiSendInvites(String strCampaignId){


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
        paramObject.addProperty("referral", strCampaignId);


        Call<JsonElement> call = patchService1.addReferral(paramObject);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ apiSendInvites: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiSendInvites: ", "" + response.code());
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ apiSendInvites: ", "" + gson.toJson(response.body()));



                if (response.code() == 200 && !response.body().getAsJsonObject().get("status").toString().replaceAll("^\"|\"$", "").replaceAll("u0027", "'").replaceAll("\\\\", "").equals("error") ) {

                    //UPDATE INVITE UI
                    updateUI(response.body(),1,"");

                }else{

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        CommonMethods.buildDialog(mContext,""+jObjError.getString("message"));

                        //DISPLAY ERRORS
                        updateUI(response.body(),2,""+jObjError.getString("message"));
                    } catch (Exception e) {


                    }


                }


            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strApiCallFailure));


            }
        });


    }


    private void updateScreenUI(InviteModel body, int responseType) {

        switch (responseType){
            case 0:
                txtContinueDisable.setVisibility(View.GONE);
                txtContinueEnabled.setVisibility(View.VISIBLE);
                imgClearId.setVisibility(View.GONE);
                linearPromoWarnings.setVisibility(View.GONE);
                txtPromoAmount.setText("");
                imgCurrencyIcon.setVisibility(View.GONE);

                isReferralValid = false;

                updateUI(null,0,"");

                break;
            case 1:
                txtContinueDisable.setVisibility(View.GONE);
                txtContinueEnabled.setVisibility(View.VISIBLE);

                imgClearId.setVisibility(View.VISIBLE);
                linearPromoWarnings.setVisibility(View.GONE);

                //DISPLAY EARNING IN CURRENCY
                txtPromoAmount.setText("Earn "+body.getCurrency()+" "+body.getCurrency_amount()+" In "+body.getCrypto_currency());

                //LOAD CURRENCY ICON
                Glide.with(mActivity)
                        .load((mSessionManager.getPreference(Constants.KEY_INVITE_CURRENCY_ICON)))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                return false;
                            }
                        })
                        .into(imgCurrencyIcon);

                isReferralValid = true;

                break;
            case 2:
                txtContinueDisable.setVisibility(View.VISIBLE);
                txtContinueEnabled.setVisibility(View.GONE);
                imgClearId.setVisibility(View.VISIBLE);
                linearPromoWarnings.setVisibility(View.VISIBLE);
                txtPromoAmount.setText("");
                txtPromoWarnings.setText(getString(R.string.strEnterRightCampaignId));

                isReferralValid = false;
                break;
        }

    }



    private void updateUI(JsonElement body, int responseType, String error) {

        switch (responseType){
            case 0:
                txtContinueDisable.setVisibility(View.GONE);
                txtContinueEnabled.setVisibility(View.VISIBLE);
                imgClearId.setVisibility(View.GONE);
                linearPromoWarnings.setVisibility(View.GONE);
                imgCurrencyIcon.setVisibility(View.GONE);

                break;
            case 1:
                txtContinueDisable.setVisibility(View.GONE);
                txtContinueEnabled.setVisibility(View.VISIBLE);
                imgClearId.setVisibility(View.VISIBLE);


                //UPDATE ACTIVE FLAG
                if(body.getAsJsonObject().get("is_activated").toString().replaceAll("^\"|\"$", "").replaceAll("u0027", "'").replaceAll("\\\\", "").equals("1")){
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,true);
                }else{
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,false);
                }

                //UPDATE SESSION VALUES
                mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);
                mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN ,true);
                mSessionManager.updatePreferenceBoolean(Constants.IS_FIRST_LOGIN ,true);

                //REDIRECT TO HOME SCREEN
                Intent mIntent = new Intent(AddRefferalAndEarnActivity.this, HomeScreenTabLayout.class);
                startActivity(mIntent);

                break;
            case 2:
                txtContinueDisable.setVisibility(View.VISIBLE);
                txtContinueEnabled.setVisibility(View.GONE);
                imgClearId.setVisibility(View.VISIBLE);
                linearPromoWarnings.setVisibility(View.VISIBLE);
                txtPromoWarnings.setText(error);

                break;
        }




    }
}