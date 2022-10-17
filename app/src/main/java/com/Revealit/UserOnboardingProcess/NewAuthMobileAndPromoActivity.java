package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

public class NewAuthMobileAndPromoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NewAuthMobileAndPromoActivity";
    boolean isMobileAlreadyUsed = false;
    long delay = 3000; // 1 seconds after user stops typing
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
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
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
                                                       if (s.length() > 0) {
                                                           last_text_edit = System.currentTimeMillis();
                                                           handler.postDelayed(input_finish_checker_mobile, delay);
                                                       } else {

                                                       }
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
                                                      if (s.length() > 0) {
                                                          last_text_edit = System.currentTimeMillis();
                                                          handler.postDelayed(input_finish_checker, delay);
                                                      } else {

                                                      }
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
            txtMobileWarnings.setText(getString(R.string.strErrorCountryCodeEmpty));
            return false;

        } else if (edtCountryCode.getText().toString().length() < 2) {
            linearMobileWarnings.setVisibility(View.VISIBLE);
            imgMobileStutusFalse.setVisibility(View.VISIBLE);
            imgMobileStutusTrue.setVisibility(View.INVISIBLE);
            txtMobileWarnings.setText(getString(R.string.strErrorCountryCodeValid));
            return false;

        }else{
            linearMobileWarnings.setVisibility(View.INVISIBLE);
            imgMobileStutusFalse.setVisibility(View.INVISIBLE);
            imgMobileStutusTrue.setVisibility(View.INVISIBLE);
            txtMobileWarnings.setText("");
            return true;
        }
    }


    private boolean isMobileNumberValid() {



         if (edtMobilenumber.getText().toString().isEmpty()) {
            linearMobileWarnings.setVisibility(View.VISIBLE);
            imgMobileStutusFalse.setVisibility(View.VISIBLE);
            imgMobileStutusTrue.setVisibility(View.INVISIBLE);
            txtMobileWarnings.setText(getString(R.string.strErrorMobileNumberEmpty));
            return false;

        } else if (edtMobilenumber.getText().toString().length() < 7) {
            linearMobileWarnings.setVisibility(View.VISIBLE);
            imgMobileStutusFalse.setVisibility(View.VISIBLE);
            imgMobileStutusTrue.setVisibility(View.INVISIBLE);
            txtMobileWarnings.setText(getString(R.string.strInvalidMobileFormat));
            return false;
        } else if (isMobileAlreadyUsed) {
            linearMobileWarnings.setVisibility(View.VISIBLE);
            imgMobileStutusFalse.setVisibility(View.VISIBLE);
            imgMobileStutusTrue.setVisibility(View.INVISIBLE);
            txtMobileWarnings.setText(getString(R.string.strMobileAlreadyUsed));
            return false;
        } else {
            linearMobileWarnings.setVisibility(View.INVISIBLE);
            imgMobileStutusFalse.setVisibility(View.INVISIBLE);
            imgMobileStutusTrue.setVisibility(View.VISIBLE);
            txtContinueDisable.setVisibility(View.GONE);
            txtContinueEnabled.setVisibility(View.VISIBLE);
            return true;
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
        paramObject.addProperty("receiver_number", edtMobilenumber.getText().toString());
        if(!edtCountryCode.getText().toString().isEmpty()){
            paramObject.addProperty("country_code", edtCountryCode.getText().toString());
        }else{
            //SEND 61 AS A DEFAULT COUNTRY CODE
            paramObject.addProperty("country_code", strDefalutCountrycode);
            //SET COUNTRY CODE
            edtCountryCode.setText(strDefalutCountrycode);

        }

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



                    //MOVE TO NEXT ACTIVITY
                    Intent mIntent = new Intent(NewAuthMobileAndPromoActivity.this, NewAuthEnterOTPActivity.class);
                    mIntent.putExtra(Constants.KEY_MOBILE_NUMBER ,edtMobilenumber.getText().toString());
                    mIntent.putExtra(Constants.KEY_COUNTRY_CODE ,edtCountryCode.getText().toString());
                    mIntent.putExtra(Constants.KEY_CAMPAIGNID ,strCampaignId);
                    mIntent.putExtra(Constants.KEY_REFFERALID ,strRefferalId);
                    mIntent.putExtra(Constants.KEY_NAMEOFINVITE ,edtPromo.getText().toString());
                    startActivity(mIntent);


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

    private void apiSendInvites(String strCampaignId, boolean isFromCampaignId){

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

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ apiSendInvites: ", "" + gson.toJson(response.body()));

                 //UPDATE INVITE UI
                    updateInviteUI(response.body(),isFromCampaignId);

                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }
            }

            @Override
            public void onFailure(Call<InviteModel> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });


    }

    private void updateInviteUI(InviteModel mInviteModel, boolean isFromCampaignId) {

        //LOAD COVER IMAGE WITH GLIDE
        Glide.with(mActivity)
                .load((mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY)+""+mInviteModel.getCurrency_icon_url().substring(1)))
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