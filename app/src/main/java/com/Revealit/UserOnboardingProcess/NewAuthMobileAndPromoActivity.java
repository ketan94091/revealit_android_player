package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.Activities.WebViewScreen;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.InviteModel;
import com.Revealit.ModelClasses.NewAuthStatusModel;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewAuthMobileAndPromoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NewAuthMobileAndPromoActivity";
    boolean isMobileAlreadyUsed;
    long delay = 2000; // 2 seconds after user stops typing
    long delayForMobile = 2000; // 2 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private TextView txtContinueDisable, txtPromoWarnings,txtPromoAmount,txtInviteQuestion,txtContinueEnabled, txtMobileWarnings;
    private ImageView imgCurrencyIcon,imgPromoStatusFalse,imgPromoStatus, imgMobileStutusTrue, imgMobileStutusFalse,imgCancel, imgLogo;
    private EditText edtPromo, edtMobilenumber, edtCountryCode;
    private LinearLayout linearPrivacyPolicy, linearPromoWarnings, linearMobileWarnings;
    private String strCampaignId ="0", strRefferalId="0",strInviteName="",strDefalutCountrycode ="61";
    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                isCountryCodeValid();
            }
        }
    };


    private Runnable input_finish_checker_mobile = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delayForMobile - 500)) {
                isMobileNumberValid();
            }
        }
    };
    private Runnable input_promo = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    apiSendInvites(edtPromo.getText().toString(),true);

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_auth_mobile_and_promo);
        setId();
        setOnClicks();
    }

    private void setId() {
        mActivity = NewAuthMobileAndPromoActivity.this;
        mContext = NewAuthMobileAndPromoActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        imgCancel = (ImageView) findViewById(R.id.imgCancel);
        imgMobileStutusTrue = (ImageView) findViewById(R.id.imgMobileStutusTrue);
        imgMobileStutusFalse = (ImageView) findViewById(R.id.imgMobileStutusFalse);
        imgPromoStatus = (ImageView) findViewById(R.id.imgPromoStatus);
        imgPromoStatusFalse = (ImageView) findViewById(R.id.imgPromoStatusFalse);
        imgCurrencyIcon = (ImageView) findViewById(R.id.imgCurrencyIcon);

        edtCountryCode = (EditText) findViewById(R.id.edtCountryCode);
        edtMobilenumber = (EditText) findViewById(R.id.edtMobilenumber);
        edtPromo = (EditText) findViewById(R.id.edtPromo);

        linearMobileWarnings = (LinearLayout) findViewById(R.id.linearMobileWarnings);
        linearPromoWarnings = (LinearLayout) findViewById(R.id.linearPromoWarnings);
        linearPrivacyPolicy = (LinearLayout) findViewById(R.id.linearPrivacyPolicy);


        txtMobileWarnings = (TextView) findViewById(R.id.txtMobileWarnings);
        txtContinueDisable = (TextView) findViewById(R.id.txtContinueDisable);
        txtContinueEnabled = (TextView) findViewById(R.id.txtContinueEnabled);
        txtInviteQuestion = (TextView) findViewById(R.id.txtInviteQuestion);
        txtPromoAmount = (TextView) findViewById(R.id.txtPromoAmount);
        txtPromoWarnings = (TextView) findViewById(R.id.txtPromoWarnings);

        //GET CAMPAIGN DATA
        apiSendInvites("",false);

        edtMobilenumber.addTextChangedListener(new TextWatcher() {
                                                   @Override
                                                   public void beforeTextChanged(CharSequence s, int start, int count,
                                                                                 int after) {
                                                   }

                                                   @Override
                                                   public void onTextChanged(final CharSequence s, int start, int before,
                                                                             int count) {
                                                       //You need to remove this to run only once
                                                       handler.removeCallbacks(input_finish_checker_mobile);

                                                   }

                                                   @Override
                                                   public void afterTextChanged(final Editable s) {
                                                       //avoid triggering event when text is empty
                                                       last_text_edit = System.currentTimeMillis();
                                                       handler.postDelayed(input_finish_checker_mobile, delayForMobile);

                                                   }
                                               }

        );

        edtCountryCode.addTextChangedListener(new TextWatcher() {
                                                  @Override
                                                  public void beforeTextChanged(CharSequence s, int start, int count,
                                                                                int after) {
                                                  }

                                                  @Override
                                                  public void onTextChanged(final CharSequence s, int start, int before,
                                                                            int count) {
                                                      //You need to remove this to run only once
                                                      handler.removeCallbacks(input_finish_checker);

                                                  }

                                                  @Override
                                                  public void afterTextChanged(final Editable s) {
                                                      //avoid triggering event when text is empty
                                                      last_text_edit = System.currentTimeMillis();
                                                      handler.postDelayed(input_finish_checker, delay);

                                                  }
                                              }

        );
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
                                                           last_text_edit = System.currentTimeMillis();
                                                           handler.postDelayed(input_promo, delay);
                                                       } else {
                                                           apiSendInvites("",false);
                                                       }
                                                   }
                                               }

        );

    }

    private void setOnClicks() {

        txtContinueEnabled.setOnClickListener(this);
        txtContinueDisable.setOnClickListener(this);
        imgCancel.setOnClickListener(this);
        linearPrivacyPolicy.setOnClickListener(this);

    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {
            case R.id.imgLogo:

                finish();

                break;
            case R.id.imgCancel:
                finish();
                break;
            case R.id.txtContinueEnabled:

                if(isRefferalValid()){
                    apiSendOTPtoMobile();
                }
                break;

            case R.id.linearPrivacyPolicy:

                Intent mIntent = new Intent(mActivity, WebViewScreen.class);
                mIntent.putExtra(Constants.RESEARCH_URL, Constants.PRIVACY_POLICY_URL);
                mIntent.putExtra(Constants.RESEARCH_URL_SPONSER, "Privacy Policy");
                startActivity(mIntent);

                break;

        }

    }

    private boolean isRefferalValid() {
        //CHECK IF  CAMPAIGN ID AND REFERRAL ID IS ZERO BUT STILL USER ENTER INVITE NAME
        if(edtPromo.getText().length() != 0 && strCampaignId.equals("0") && strRefferalId.equals("0")){
            linearPromoWarnings.setVisibility(View.VISIBLE);
            imgPromoStatus.setVisibility(View.INVISIBLE);
            imgPromoStatusFalse.setVisibility(View.VISIBLE);
            txtPromoWarnings.setText(getString(R.string.strEnterRightCampaignId));
            return false;
        }else{
            return true;
        }
    }

    private boolean isCountryCodeValid() {

        if (edtCountryCode.getText().toString().isEmpty()) {
            linearMobileWarnings.setVisibility(View.VISIBLE);
            imgMobileStutusFalse.setVisibility(View.VISIBLE);
            imgMobileStutusTrue.setVisibility(View.INVISIBLE);
            disbaledContinueButton();
            txtMobileWarnings.setText(getString(R.string.strErrorCountryCodeEmpty));
            return false;

        } else if (edtCountryCode.getText().toString().length() < 2) {
            linearMobileWarnings.setVisibility(View.VISIBLE);
            imgMobileStutusFalse.setVisibility(View.VISIBLE);
            imgMobileStutusTrue.setVisibility(View.INVISIBLE);
            disbaledContinueButton();
            txtMobileWarnings.setText(getString(R.string.strErrorCountryCodeValid));
            return false;

        }else{
            edtMobilenumber.requestFocus();
            isMobileNumberValid();
            return true;
        }
    }


    private boolean isMobileNumberValid() {


       if(edtCountryCode.getText().toString().isEmpty()) {
            linearMobileWarnings.setVisibility(View.VISIBLE);
            imgMobileStutusFalse.setVisibility(View.VISIBLE);
            imgMobileStutusTrue.setVisibility(View.INVISIBLE);
           disbaledContinueButton();
            txtMobileWarnings.setText(getString(R.string.strErrorCountryCodeEmpty));
            return false;

        } else if (edtCountryCode.getText().toString().length() < 2) {
            linearMobileWarnings.setVisibility(View.VISIBLE);
            imgMobileStutusFalse.setVisibility(View.VISIBLE);
            imgMobileStutusTrue.setVisibility(View.INVISIBLE);
           disbaledContinueButton();
            txtMobileWarnings.setText(getString(R.string.strErrorCountryCodeValid));
            return false;

        }else if (edtMobilenumber.getText().toString().isEmpty()) {
            linearMobileWarnings.setVisibility(View.VISIBLE);
            imgMobileStutusFalse.setVisibility(View.VISIBLE);
            imgMobileStutusTrue.setVisibility(View.INVISIBLE);
            disbaledContinueButton();
            txtMobileWarnings.setText(getString(R.string.strErrorMobileNumberEmpty));
            return false;

        } else if (edtMobilenumber.getText().toString().length() < 7) {
            linearMobileWarnings.setVisibility(View.VISIBLE);
            imgMobileStutusFalse.setVisibility(View.VISIBLE);
            imgMobileStutusTrue.setVisibility(View.INVISIBLE);
            disbaledContinueButton();
            txtMobileWarnings.setText(getString(R.string.strInvalidMobileFormat));
            return false;
        }else{

             //CHECK IF USER EXIST IN DATABASE
            if(edtPromo.getText().length() < 2){
                checkIfMobileAlreadyRegistered();
            }else{
                isCountryCodeValid();
            }


             return true;
         }

    }

    private void checkIfMobileAlreadyRegistered() {


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
        Call<JsonElement> call = patchService1.checkIfPhoneAlreadyRegistered(Constants.API_NEW_AUTH_CHECK_MOBILENUMBER+"?receiver_number="+edtMobilenumber.getText().toString()+"&country_code="+edtCountryCode.getText().toString());

        call.enqueue(new Callback<JsonElement>() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                Log.e("checkIfMobileAlready: ", "" + response.isSuccessful());
                Log.e("checkIfMobileAlready: ", "" + response.code());

                if (response.isSuccessful() && response.code() == 200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    Log.e("checkIfMobileAlready: ", "" + gson.toJson(response.body()));

                    //CHECK IF MOBILE ALREADY IN USED
                    //FALSE -> ALREADY REGISTERED
                    //TRUE -> GOOD TO GO
                    if(response.body().getAsJsonObject().get("status").getAsBoolean()){
                       updateMobileErrorUI(false);
                    }else{
                        updateMobileErrorUI(true);
                    }

                }
            }


            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                updateMobileErrorUI(false);

            }
        });

        updateMobileErrorUI(false);
    }

    private void updateMobileErrorUI(boolean isMobileAlreadyUsed) {

        if (isMobileAlreadyUsed) {
            linearMobileWarnings.setVisibility(View.VISIBLE);
            imgMobileStutusFalse.setVisibility(View.VISIBLE);
            imgMobileStutusTrue.setVisibility(View.INVISIBLE);
            disbaledContinueButton();
            txtMobileWarnings.setText(getString(R.string.strMobileAlreadyUsed));

        } else {
            linearMobileWarnings.setVisibility(View.INVISIBLE);
            imgMobileStutusFalse.setVisibility(View.INVISIBLE);
            imgMobileStutusTrue.setVisibility(View.VISIBLE);
            enabledContinueButton();

        }

    }
    private void disbaledContinueButton(){
        txtContinueDisable.setVisibility(View.VISIBLE);
        txtContinueEnabled.setVisibility(View.GONE);
    }
    private void enabledContinueButton(){
        txtContinueDisable.setVisibility(View.GONE);
        txtContinueEnabled.setVisibility(View.VISIBLE);
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
        paramObject.addProperty("receiver_number", edtMobilenumber.getText().toString());
        paramObject.addProperty("country_code", edtCountryCode.getText().toString());


        Call<NewAuthStatusModel> call = patchService1.verifyPhone(paramObject);

        call.enqueue(new Callback<NewAuthStatusModel>() {
            @Override
            public void onResponse(Call<NewAuthStatusModel> call, Response<NewAuthStatusModel> response) {

                CommonMethods.printLogE("Response @ apiCallToGetOTP: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiCallToGetOTP: ", "" + response.code());
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ apiCallToGetOTP: ", "" + gson.toJson(response.body()));

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

                    CommonMethods.displayToast(mContext, getResources().getString(R.string.strOTPinboxMsg));

                    //MOVE TO NEXT ACTIVITY
                    Intent mIntent = new Intent(NewAuthMobileAndPromoActivity.this, NewAuthEnterOTPActivity.class);
                    mIntent.putExtra(Constants.KEY_MOBILE_NUMBER ,edtMobilenumber.getText().toString());
                    mIntent.putExtra(Constants.KEY_COUNTRY_CODE ,edtCountryCode.getText().toString());
                    mIntent.putExtra(Constants.KEY_CAMPAIGNID ,strCampaignId);
                    mIntent.putExtra(Constants.KEY_REFFERALID ,strRefferalId);
                    mIntent.putExtra(Constants.KEY_NAMEOFINVITE ,edtPromo.getText().toString());
                    startActivity(mIntent);

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        CommonMethods.buildDialog(mContext,"Error : "+ jObjError.getString("message"));
                    } catch (Exception e) {
                        CommonMethods.buildDialog(mContext,"Error : "+e.getMessage());

                    }
                }

            }

            @Override
            public void onFailure(Call<NewAuthStatusModel> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strApiCallFailure));


            }
        });


    }

    private void apiSendInvites(String strCampaignId, boolean isFromCampaignId){

        //OPEN DIALOGUE
        //CommonMethods.showDialog(mContext);

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

        String url="";
        if(strCampaignId.isEmpty()){
            url="";
        }else{
            url="?referral="+strCampaignId;
        }

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


                //CLOSED DIALOGUE
                //CommonMethods.closeDialog();

                switch (response.code()){
                    case Constants.API_CODE_200:

                        //UPDATE INVITE UI
                        updateInviteUI(response.body(),isFromCampaignId);

                        break;

                    case Constants.API_CODE_404:

                        CommonMethods.buildDialog(mContext, getResources().getString(R.string.strStatusCode404Error));

                        break;
                }

            }

            @Override
            public void onFailure(Call<InviteModel> call, Throwable t) {

                //CLOSED DIALOGUE
                //CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strApiCallFailure));


            }
        });


    }

    private void updateInviteUI(InviteModel mInviteModel, boolean isFromCampaignId) {

        //LOAD COVER IMAGE WITH GLIDE
        Glide.with(mActivity)
                .load((mInviteModel.getCurrency_icon_url()))
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
        txtInviteQuestion.setText(mInviteModel.getQuestion());
        edtPromo.setHint(mInviteModel.getPlace_holder());
        txtPromoAmount.setText("Earn "+mInviteModel.getCurrency()+" "+mInviteModel.getCurrency_amount()+" In "+mInviteModel.getCrypto_currency());

        strCampaignId = ""+mInviteModel.getCampaign_id();
        strRefferalId = ""+mInviteModel.getReferral_id();

        //UPDATE PREFERENCE
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_MSG ,""+mInviteModel.getInvitation_message());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_COPY_CLIPBOARD ,""+mInviteModel.getInvitation_message_clipboard());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_BIOMETRIC_PERMISSION ,""+mInviteModel.getBiometrics_permission_message());


        if(!isFromCampaignId){
            linearPromoWarnings.setVisibility(View.INVISIBLE);
            imgPromoStatus.setVisibility(View.INVISIBLE);
            imgPromoStatusFalse.setVisibility(View.INVISIBLE);
            txtPromoWarnings.setText("");

        }else if(isFromCampaignId && strCampaignId.equals("0") && strRefferalId.equals("0")){

            linearPromoWarnings.setVisibility(View.VISIBLE);
            imgPromoStatus.setVisibility(View.INVISIBLE);
            imgPromoStatusFalse.setVisibility(View.VISIBLE);
            txtPromoWarnings.setText(""+mInviteModel.getError());


        }else {

            linearPromoWarnings.setVisibility(View.INVISIBLE);
            imgPromoStatus.setVisibility(View.VISIBLE);
            imgPromoStatusFalse.setVisibility(View.INVISIBLE);
            txtPromoWarnings.setText("");

        }

    }


}