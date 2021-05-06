package com.Revealit.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.GenericTextWatcher;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.CheckEmailModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
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

public class VerificationCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "RegistrationActivity";


    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private EditText edtOTPSix,edtOTPFive,edtOTPFour,edtOTPThree,edtOTPTwo,edtOTPone;
    private ImageView imgGoBack;
    private TextView txtNextDisabled,txtNextEnabled;
    private String strCode = "";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_code_verification);


        setIds();
        setOnclick();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setIds() {

        mActivity = VerificationCodeActivity.this;
        mContext = VerificationCodeActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgGoBack=(ImageView)findViewById(R.id.imgGoBack);


        edtOTPone = (EditText) findViewById(R.id.edtOTPone);
        edtOTPTwo = (EditText) findViewById(R.id.edtOTPTwo);
        edtOTPThree = (EditText) findViewById(R.id.edtOTPThree);
        edtOTPFour = (EditText) findViewById(R.id.edtOTPFour);
        edtOTPFive = (EditText) findViewById(R.id.edtOTPFive);
        edtOTPSix = (EditText) findViewById(R.id.edtOTPSix);

        txtNextDisabled = (TextView)findViewById(R.id.txtNextDisabled);
        txtNextEnabled = (TextView)findViewById(R.id.txtNextEnabled);

        edtOTPone.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                updateUI();
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        edtOTPTwo.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                updateUI();
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        edtOTPThree.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                updateUI();
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
        edtOTPFour.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                updateUI();
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        edtOTPFive.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                updateUI();
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
        edtOTPSix.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                updateUI();
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });


        edtOTPone.addTextChangedListener(new GenericTextWatcher(edtOTPTwo, edtOTPone));
        edtOTPTwo.addTextChangedListener(new GenericTextWatcher(edtOTPThree, edtOTPone));
        edtOTPThree.addTextChangedListener(new GenericTextWatcher(edtOTPFour, edtOTPTwo));
        edtOTPFour.addTextChangedListener(new GenericTextWatcher(edtOTPFive, edtOTPThree));
        edtOTPFive.addTextChangedListener(new GenericTextWatcher(edtOTPSix, edtOTPFour));
        edtOTPSix.addTextChangedListener(new GenericTextWatcher(edtOTPSix, edtOTPFive));


    }

    private void updateUI() {

        if(!edtOTPone.getText().toString().isEmpty() &&
                !edtOTPTwo.getText().toString().isEmpty() &&
                !edtOTPThree.getText().toString().isEmpty() &&
                !edtOTPFour.getText().toString().isEmpty() &&
                !edtOTPFive.getText().toString().isEmpty() &&
                !edtOTPSix.getText().toString().isEmpty()) {

            txtNextEnabled.setVisibility(View.VISIBLE);
            txtNextDisabled.setVisibility(View.GONE);

            strCode = edtOTPone.getText().toString()+edtOTPTwo.getText().toString()+edtOTPThree.getText().toString()+edtOTPFour.getText().toString()+edtOTPFive.getText().toString()+edtOTPSix.getText().toString();


        }else {

            txtNextEnabled.setVisibility(View.GONE);
            txtNextDisabled.setVisibility(View.VISIBLE);

        }


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


                apiCallForVerifyCode();

                break;
        }
    }

    private void apiCallForVerifyCode() {

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
                    .baseUrl(Constants.API_END_POINTS_IVA_TEST)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                    .build();

            UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
            JsonObject paramObject = new JsonObject();
            paramObject.addProperty(Constants.PROTON_EMAIL, mSessionManager.getPreference(Constants.PROTON_EMAIL));
            paramObject.addProperty(Constants.PROTON_VERIFICATION_CODE, strCode);

            Call<CheckEmailModel> call = patchService1.verifyCode(paramObject);

            call.enqueue(new Callback<CheckEmailModel>() {
                @Override
                public void onResponse(Call<CheckEmailModel> call, Response<CheckEmailModel> response) {

                    CommonMethods.printLogE("Response @ apiCallForVerifyCode: ", "" + response.isSuccessful());
                    CommonMethods.printLogE("Response @ apiCallForVerifyCode: ", "" + response.code());

                    //CLOSED DIALOGUE
                    CommonMethods.closeDialog();

                    if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                        Gson gson = new GsonBuilder()
                                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                                .serializeNulls()
                                .create();

                        CommonMethods.printLogE("Response @ apiCallForVerifyCode: ", "" + gson.toJson(response.body()));


                        if (response.body().getStatus().equals("ok")) {

                            Intent mIntent = new Intent(VerificationCodeActivity.this, RevealitNameActivity.class);
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

