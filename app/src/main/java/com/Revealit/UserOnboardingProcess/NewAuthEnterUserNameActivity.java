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

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.CheckUserNameStatusModel;
import com.Revealit.ModelClasses.KeyStoreServerInstancesModel;
import com.Revealit.ModelClasses.SubmitProfileModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.Utils.DeCryptor;
import com.Revealit.Utils.EnCryptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
    private LinearLayout linearUsernameHint,linearUsernameTrue, linearUsernameWarnings;
    private EditText edtUsername;
    private String strCountryCode,strMobileNumber,strCampaignId,strRefferalId,strInvitename;


    long delay = 1000; // 2 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    private EnCryptor encryptor;
    private DeCryptor decryptor;
    private Runnable input_username = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                apiCheckIfUsernameExist();
            }
        }
    };
    private Gson mGson;


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
        linearUsernameHint = (LinearLayout) findViewById(R.id.linearUsernameHint);

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
                                                           linearUsernameHint.setVisibility(View.VISIBLE);
                                                           txtUsernameInUse.setVisibility(View.GONE);
                                                           txtContinueDisable.setVisibility(View.VISIBLE);
                                                           txtContinueEnabled.setVisibility(View.GONE);
                                                       }else{
                                                           linearUsernameWarnings.setVisibility(View.VISIBLE);
                                                           linearUsernameTrue.setVisibility(View.INVISIBLE);
                                                           imgUsernameStutusFalse.setVisibility(View.VISIBLE);
                                                           imgUsernameStutusTrue.setVisibility(View.INVISIBLE);
                                                           linearUsernameHint.setVisibility(View.INVISIBLE);
                                                           txtUsernameInUse.setVisibility(View.GONE);
                                                           txtContinueDisable.setVisibility(View.GONE);
                                                           txtContinueEnabled.setVisibility(View.VISIBLE);
                                                       }
                                                   }
                                               }

        );

        mGson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();


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


        Call<SubmitProfileModel> call = patchService1.submitProfile(paramObject);

        call.enqueue(new Callback<SubmitProfileModel>() {
            @Override
            public void onResponse(Call<SubmitProfileModel> call, Response<SubmitProfileModel> response) {

                CommonMethods.printLogE("Response @ apiSubmitProfile: ", "" + response.code());
                CommonMethods.printLogE("Response @ apiSubmitProfile: ", "" + gson.toJson(response.body()));

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

                    updateUI(response.body());

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        CommonMethods.buildDialog(mContext,"Error Code : "+jObjError.getString("error_code") +" "+ jObjError.getString("message"));
                    } catch (Exception e) {
                        CommonMethods.buildDialog(mContext,"Error Code : "+e.getMessage());

                    }
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

        mSessionManager.updatePreferenceString(Constants.KEY_USER_DATA, ""+mGson.toJson(body));
        mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN ,body.getauth_token());
        mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE ,getResources().getString(R.string.strTokenBearer));
        mSessionManager.updatePreferenceString(Constants.PROTON_ACCOUNT_NAME ,body.getProton().getAccountName());
        //mSessionManager.updatePreferenceString(Constants.KEY_PROTON_WALLET_DETAILS,gson.toJson(body.getProton()));
        mSessionManager.updatePreferenceString(Constants.KEY_REVEALIT_PRIVATE_KEY ,body.getrevealit_private_key());
        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_REGISTRATION_DONE ,true);
        mSessionManager.updatePreferenceString(Constants.KEY_MOBILE_NUMBER,strCountryCode+strMobileNumber);

        //STORE DATA IN TO KEYSTORE
        //FOR DISPLAY PURPOSE
        CommonMethods.encryptKey(body.getProton().getPrivateKey(), Constants.KEY_PRIVATE_KEY ,body.getrevealit_private_key(), mSessionManager);
        CommonMethods.encryptKey(body.getProton().getPublicKey(),Constants.KEY_PUBLIC_KEY,body.getrevealit_private_key(), mSessionManager);
        CommonMethods.encryptKey(body.getProton().getMnemonic(),Constants.KEY_MNEMONICS,body.getrevealit_private_key(), mSessionManager);
        CommonMethods.encryptKey(body.getProton().getPrivate_pem(),Constants.KEY_PRIVATE_KEY_PEM,body.getrevealit_private_key(), mSessionManager);
        CommonMethods.encryptKey(body.getProton().getPublic_pem(),Constants.KEY_PUBLIC_KEY_PEM,body.getrevealit_private_key(), mSessionManager);



        //UPDATE FLAG IF USER IS ADMIN OR NOT
        if(body.getRole().equals(getResources().getString(R.string.strAdmin))){
            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,true);
        }else{
            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,false);
        }

        //UPDATE ACTIVE FLAG
        if(body.getIs_activated().equals("1")){
            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,true);
        }else{
            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,false);
        }

        //UPDATE FLAG FOR APPLICATION MODE
        mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);


        //SET KEY STORE INSTANCE DATA
        storeKeyStoreInstances(body);

    }

    private void storeKeyStoreInstances(SubmitProfileModel body) {

        //STORE DATA FOR SWAPPING SILOS
        //THIS IS TEMPORARY FOR ADMIN USERS
        //OVERRIDE EXISTING SILOS IF ADMIN CREATE NEW WITH FOR EXISTING SAVED SILOS
       //encryptKey(""+mGson.toJson(body),  Constants.KEY_SILOS_DATA+""+mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID),Constants.KEY_SILOS_ALIAS);

        //CREATE LIST WHICH COULD ENCRYPT AND THAN STORE IN THE KEY STORE AS A STRING
        ArrayList<KeyStoreServerInstancesModel.Data> mInstancesModel = new ArrayList<>();
        KeyStoreServerInstancesModel.Data mModelData = new KeyStoreServerInstancesModel.Data();
        mModelData.setServerInstanceName(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME));
        mModelData.setMobileNumber(mSessionManager.getPreference(Constants.KEY_MOBILE_NUMBER));
        mModelData.setServerInstanceId(mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID));
        mModelData.setSubmitProfileModel(body);
        mInstancesModel.add(mModelData);

        //CHECK IF DATA IS ALREADY STORED IN TO KEYSTORE
        //IF STORED -> FETCH FROM KEYSTORE, CONVERT TO LIST, CREATE NEW OBJECT AND ADD THAT OBJECT TO LIST.
        //ELSE -> TREAT AS FIRST USER
        if(!CommonMethods.checkIfInstanceKeyStoreData(mSessionManager).isEmpty()){

            //CONVERT DATA TO JSON ARRAY
            //CREATE NEW ARRAY FROM THE STRING ARRAY
            //AFTER ADDING ALL SAVED DATA ADD NEWLY CREATED USER DATA
            try {
                JSONArray jsonArray =new JSONArray(CommonMethods.checkIfInstanceKeyStoreData(mSessionManager));
                ArrayList<KeyStoreServerInstancesModel.Data> dataArrayList = new ArrayList<>();

                for (int i=0 ;i < jsonArray.length();i++){

                    KeyStoreServerInstancesModel.Data mModel = new KeyStoreServerInstancesModel.Data();
                    mModel.setServerInstanceName(jsonArray.getJSONObject(i).getString("serverInstanceName"));
                    mModel.setMobileNumber(jsonArray.getJSONObject(i).getString("mobileNumber"));
                    mModel.setServerInstanceId(jsonArray.getJSONObject(i).getInt("serverInstanceId"));

                    SubmitProfileModel mSubmitProfileModel = new SubmitProfileModel();
                    mSubmitProfileModel.setAudience(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("audience"));
                    mSubmitProfileModel.setauth_token(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("auth_token"));
                    mSubmitProfileModel.setError_code(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getInt("error_code"));
                    mSubmitProfileModel.setIs_activated(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("is_activated"));
                    mSubmitProfileModel.setMessage(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("message"));
                    mSubmitProfileModel.setrevealit_private_key(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("revealit_private_key"));
                    mSubmitProfileModel.setRole(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("role"));
                    mSubmitProfileModel.setServerInstance(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("serverInstance"));
                    mSubmitProfileModel.setStatus(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("status"));

                    SubmitProfileModel.Proton mProton = new SubmitProfileModel.Proton();
                    mProton.setAccountName(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("account_name"));
                    mProton.setMnemonic(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("mnemonic"));
                    mProton.setPrivateKey(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("private_key"));
                    mProton.setPrivate_pem(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("private_pem"));
                    mProton.setPublicKey(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("public_key"));
                    mProton.setPublic_pem(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("public_pem"));
                    mSubmitProfileModel.setProton(mProton);

                    mModel.setSubmitProfileModel(mSubmitProfileModel);

                    dataArrayList.add(mModel);
                }

                //ADD NEWLY CREATED INSTANCE OBJECT TO THE ARRAYLIST
                dataArrayList.add(mModelData);


                //STORE WHOLE ARRAY IN TO STRING FORMAT IN KEYSTORE
                CommonMethods.encryptKey(""+mGson.toJson(dataArrayList),  Constants.KEY_SILOS_DATA,Constants.KEY_SILOS_ALIAS, mSessionManager);



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            //STORE WHOLE JSON IN TO STRING
            CommonMethods.encryptKey(""+mGson.toJson(mInstancesModel),  Constants.KEY_SILOS_DATA,Constants.KEY_SILOS_ALIAS, mSessionManager);
        }

        //UPDATE FIRST OPEN FLAG
        mSessionManager.updatePreferenceBoolean(Constants.IS_USER_OPEN_APP_FIRST_TIME,false);

        //UPDATE EDUCATIONAL VIDEO FLAG
        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_EDUCATION_VIDEO_PLAYED, false);

        //GO TO NEXT ACTIVITY
        Intent mIntent = new Intent(NewAuthEnterUserNameActivity.this,InviteAndEarnActivity.class);
        mIntent.putExtra(Constants.KEY_NEW_AUTH_USERNAME ,body.getProton().getAccountName());
        startActivity(mIntent);

    }




    private void apiCheckIfUsernameExist(){

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

        Call<CheckUserNameStatusModel> call = patchService1.checkIfUserExist("api/usernotexists?username="+edtUsername.getText().toString());

        call.enqueue(new Callback<CheckUserNameStatusModel>() {
            @Override
            public void onResponse(Call<CheckUserNameStatusModel> call, Response<CheckUserNameStatusModel> response) {

                CommonMethods.printLogE("Response @ apiCheckIfUsernameExist: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiCheckIfUsernameExist: ", "" + response.code());

                //CLOSED DIALOGUE
                //CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ apiCheckIfUsernameExist: ", "" + gson.toJson(response.body()));

                    //UPDATE INVITE UI
                    updateInviteUI(response.body().isStatus());

                } else if(response.code() == Constants.API_CODE_404){
                    CommonMethods.buildDialog(mContext, "Username or Mobile number already registered with revealit Tv Platform!");

                }else{
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));
                }
            }

            @Override
            public void onFailure(Call<CheckUserNameStatusModel> call, Throwable t) {

                //CLOSED DIALOGUE
                //CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });


    }

    private void updateInviteUI(boolean isNotExist) {

        if(isNotExist){
            linearUsernameWarnings.setVisibility(View.INVISIBLE);
            linearUsernameTrue.setVisibility(View.VISIBLE);
            linearUsernameHint.setVisibility(View.INVISIBLE);
            imgUsernameStutusFalse.setVisibility(View.INVISIBLE);
            imgUsernameStutusTrue.setVisibility(View.VISIBLE);
            txtContinueDisable.setVisibility(View.INVISIBLE);
            txtContinueEnabled.setVisibility(View.VISIBLE);
            txtUsernameInUse.setVisibility(View.GONE);

        }else{
            linearUsernameWarnings.setVisibility(View.INVISIBLE);
            linearUsernameTrue.setVisibility(View.INVISIBLE);
            linearUsernameHint.setVisibility(View.VISIBLE);
            imgUsernameStutusFalse.setVisibility(View.INVISIBLE);
            imgUsernameStutusTrue.setVisibility(View.INVISIBLE);
            txtContinueEnabled.setVisibility(View.INVISIBLE);
            txtUsernameInUse.setVisibility(View.VISIBLE);
            txtContinueDisable.setVisibility(View.VISIBLE);
        }
    }

}