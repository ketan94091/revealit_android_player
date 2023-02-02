package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
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
    private TextView txtContinueDisable,txtContinueEnabled,txtPromoWarnings,txtPromoAmount,txtInviteQuestion;
    private EditText edtPromo;
    private ImageView imgPromoStatusFalse,imgPromoStatus;
    private LinearLayout linearPromoWarnings;
    long delay = 2000; // 2 seconds after user stops typing
    long delayForMobile = 2000; // 2 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    private Runnable input_promo = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                apiSendInvites(edtPromo.getText().toString());

            }
        }
    };

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
        edtPromo = (EditText)findViewById(R.id.edtPromo);
        imgPromoStatus =(ImageView)findViewById(R.id.imgPromoStatus);
        imgPromoStatusFalse=(ImageView)findViewById(R.id.imgPromoStatusFalse);
        linearPromoWarnings=(LinearLayout)findViewById(R.id.linearPromoWarnings);


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
                                                    updateUI(null,0);
                                                }
                                            }
                                        }

        );
    }


    private void setOnClicks() {
        txtContinueEnabled.setOnClickListener(this);

    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){
            case R.id.txtContinueEnabled:

                Intent mIntent = new Intent(AddRefferalAndEarnActivity.this, HomeScreenTabLayout.class);
                startActivity(mIntent);

                break;

        }

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

                if (!response.body().getAsJsonObject().get("status").toString().replaceAll("^\"|\"$", "").replaceAll("u0027", "'").replaceAll("\\\\", "").equals("error") ) {

                    //UPDATE INVITE UI
                    updateUI(response.body(),1);

                }else{
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        CommonMethods.buildDialog(mContext,"Invalid Referral");
                    } catch (Exception e) {
                        CommonMethods.buildDialog(mContext,"Invalid Referral");

                    }

                    //DISPLAY ERRORS
                    updateUI(response.body(),2);
                }


            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strApiCallFailure));


            }
        });


    }

    private void updateUI(JsonElement body, int responseType) {

        switch (responseType){
            case 0:
                txtContinueDisable.setVisibility(View.GONE);
                txtContinueEnabled.setVisibility(View.VISIBLE);
                imgPromoStatus.setVisibility(View.GONE);
                imgPromoStatusFalse.setVisibility(View.GONE);
                break;
            case 1:
                txtContinueDisable.setVisibility(View.GONE);
                txtContinueEnabled.setVisibility(View.VISIBLE);
                imgPromoStatus.setVisibility(View.VISIBLE);
                imgPromoStatusFalse.setVisibility(View.GONE);

                //UPDATE ACTIVE FLAG
                if(body.getAsJsonObject().get("is_activated").equals("1")){
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,true);
                }else{
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,true);
                }

                //UPDATE SESSION VALUES
                mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);
                mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN ,true);
                mSessionManager.updatePreferenceBoolean(Constants.IS_FIRST_LOGIN ,true);


                break;
            case 2:
                txtContinueDisable.setVisibility(View.VISIBLE);
                txtContinueEnabled.setVisibility(View.GONE);
                imgPromoStatus.setVisibility(View.GONE);
                imgPromoStatusFalse.setVisibility(View.VISIBLE);
                break;
        }




    }
}