package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.content.Context;
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

public class AddRefferalAndEarnActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "InviteAndEarnActivity";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private TextView txtContinueEnabled,txtPromoWarnings,txtPromoAmount,txtInviteQuestion;
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
                                                    updateUI(null);
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
            case R.id.imgLogo:

                break;

        }

    }
    private void apiSendInvites(String strCampaignId){


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request original = chain.request();

                okhttp3.Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoxMTc5NDEyMDA3OTQ4MTg5MTMsImlzcyI6Imh0dHA6Ly9yZXZlYWxpdC5pbyIsImF1ZCI6Imh0dHA6Ly9leGFtcGxlLmNvbSIsImlhdCI6IjE2NzQyMTIxNzMiLCJleHAiOiIxNjg5NzY0MTczIiwibm9uY2UiOiJ2YWx1ZSBwYXNzIGV4cGVjdCByZXR1cm4iLCJuYW1lIjoiYmV0YWRvbmUucnR2In0.NB3xvdXgIJt5jTU4kZ02FQVeOFP0r8BWDvVKuj3Uj3A")
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


                switch (response.code()){
                    case Constants.API_CODE_200:

                        //UPDATE INVITE UI
                        updateUI(response.body());

                        break;

                    case Constants.API_CODE_404:

                        CommonMethods.buildDialog(mContext, getResources().getString(R.string.strStatusCode404Error));

                        break;
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strApiCallFailure));


            }
        });


    }

    private void updateUI(JsonElement body) {



    }
}