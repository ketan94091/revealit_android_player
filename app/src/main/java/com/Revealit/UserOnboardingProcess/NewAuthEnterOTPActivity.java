package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.KeyStoreServerInstancesModel;
import com.Revealit.ModelClasses.NewAuthStatusModel;
import com.Revealit.ModelClasses.SubmitProfileModel;
import com.Revealit.ModelClasses.UserDetailsFromPublicKeyModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
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
    private boolean isOtpVarified, isFromImportKey;
    private String strCountryCode,strMobileNumber,strCampaignId,strRefferalId,strInvitename;
    private Gson mGson;

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
         isFromImportKey =getIntent().getBooleanExtra(Constants.KEY_IS_FROM_IMPORT_KEY,false);

        //SET MOBILE NUMBER
        txtMobileNumber.setText(getString(R.string.strWehaveRecieved) + " (+" + strCountryCode+" "+ strMobileNumber + ") " + getString(R.string.strEnterTheCodeBelow));


        edtOne.addTextChangedListener(new GenericTextWatcher(edtOne, edtTwo));
        edtTwo.addTextChangedListener(new GenericTextWatcher(edtTwo, edtThree));
        edtThree.addTextChangedListener(new GenericTextWatcher(edtThree, edtFour));
        edtFour.addTextChangedListener(new GenericTextWatcher(edtFour, edtFive));
        edtFive.addTextChangedListener(new GenericTextWatcher(edtFive, edtSix));
        edtSix.addTextChangedListener(new GenericTextWatcher(edtSix, null));

        edtOne.setOnKeyListener(new GenericKey(edtOne, null));
        edtTwo.setOnKeyListener(new GenericKey(edtTwo, edtOne));
        edtThree.setOnKeyListener(new GenericKey(edtThree, edtTwo));
        edtFour.setOnKeyListener(new GenericKey(edtFour,edtThree));
        edtFive.setOnKeyListener(new GenericKey(edtFive,edtFour));
        edtSix.setOnKeyListener(new GenericKey(edtSix,edtFive));

        mGson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();

    }


    public void checkIfAllDigitEntered() {

        if (!edtOne.getText().toString().isEmpty() && !edtTwo.getText().toString().isEmpty() && !edtThree.getText().toString().isEmpty() && !edtFour.getText().toString().isEmpty() && !edtFive.getText().toString().isEmpty() && !edtSix.getText().toString().isEmpty()) {
            apiCallVerifyOTPMobile();
        }else{
            txtContinueEnabled.setVisibility(View.INVISIBLE);
            txtContinueDisable.setVisibility(View.VISIBLE);
            txtInvalidOTP.setVisibility(View.GONE);
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
                   openEnterUsernameScreen();
                }else{
                    CommonMethods.displayToast(mContext,getString(R.string.strInvalidCode));
                }

                break;
        }

    }

    private void openEnterUsernameScreen() {


        if(isFromImportKey){
            apiCallResubmitProfile();
        }else {

            Intent mIntent = new Intent(NewAuthEnterOTPActivity.this, NewAuthEnterUserNameActivity.class);
            mIntent.putExtra(Constants.KEY_MOBILE_NUMBER ,strMobileNumber);
            mIntent.putExtra(Constants.KEY_COUNTRY_CODE ,strCountryCode);
            mIntent.putExtra(Constants.KEY_CAMPAIGNID ,strCampaignId);
            mIntent.putExtra(Constants.KEY_REFFERALID ,strRefferalId);
            mIntent.putExtra(Constants.KEY_NAMEOFINVITE ,strInvitename);
            startActivity(mIntent);
        }

    }

    private void apiCallResubmitProfile() {

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
        paramObject.addProperty("username",mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_USERNAME));
        paramObject.addProperty("public_key",mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PUBLICKEY));


        Call<UserDetailsFromPublicKeyModel> call = patchService1.reSubmitProfile(paramObject);

        call.enqueue(new Callback<UserDetailsFromPublicKeyModel>() {
            @Override
            public void onResponse(Call<UserDetailsFromPublicKeyModel> call, Response<UserDetailsFromPublicKeyModel> response) {

                CommonMethods.printLogE("Response @ apiSubmitProfile: ", "" + response.code());
                CommonMethods.printLogE("Response @ apiSubmitProfile: ", "" + gson.toJson(response.body()));

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

                    //IF USER AVAILABLE IN TO KEYSTORE BUT DELETED FLAG =1
                    //UPDATE USER DELETED FLAG = 0
                    if(mSessionManager.getPreferenceBoolean(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_IS_USER_DELETED)){
                        if(CommonMethods.updateUserAccountActivationFlag(mSessionManager,mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY),mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_USERNAME))){
                            //AFTER UPDATING FLAG
                            //GET USER DETAILS AND LOGGED IN BY SAVING REQUIRE PARAMETERS TO SESSION MANAGER
                            getEnteredPrivateKeyDetails(response.body());
                        }
                    }else{

                        if(CommonMethods.checkEnterPrivateKeyIsFromOtherSilos(mSessionManager,mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY))) {
                            //UPDATE EXISTING DETAILS TO KEYSTORE
                            try {
                                if(new UpdateUserDetailsInAndroidKeyStoreTask(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY),mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_USERNAME),response.body().getRole(), response.body().getRevealit_private_key(),response.body().getAuth_token(), Integer.parseInt(response.body().getIs_activated()), response.body().getRemove(),response.body().getAudience()).execute(mSessionManager).get()){
                                    //GO TO NEXT ACTIVITY
                                    Intent mIntent = new Intent(NewAuthEnterOTPActivity.this, HomeScreenTabLayout.class);
                                    startActivity(mIntent);
                                    finishAffinity();
                                }
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else{
                            //SAVE DATA TO THE KEYSTORE IF USER IS DELETED FROM LOCAL/FROM SERVER
                            saveDataToTheAndroidKeyStore(response.body(), mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_USERNAME));

                        }



                    }

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
            public void onFailure(Call<UserDetailsFromPublicKeyModel> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });

    }

    private void getEnteredPrivateKeyDetails(UserDetailsFromPublicKeyModel body) {

        mSessionManager.updatePreferenceString(Constants.KEY_USER_DATA, mSessionManager.getPreference("" + mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID)));
        mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN, body.getAuth_token());
        mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE, mContext.getResources().getString(R.string.strTokenBearer));
        mSessionManager.updatePreferenceString(Constants.PROTON_ACCOUNT_NAME, mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_USERNAME));
        mSessionManager.updatePreferenceString(Constants.KEY_REVEALIT_PRIVATE_KEY, body.getRevealit_private_key());
        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_REGISTRATION_DONE, true);
        mSessionManager.updatePreferenceString(Constants.KEY_MOBILE_NUMBER,strMobileNumber);
        mSessionManager.updatePreferenceBoolean(Constants.IS_FIRST_TIME_ACCOUNT_SYNC, false);


        //STORE DATA IN TO KEYSTORE
        CommonMethods.encryptKey(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY), Constants.KEY_PRIVATE_KEY,body.getRevealit_private_key(), mSessionManager);
        CommonMethods.encryptKey(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PUBLICKEY),Constants.KEY_PUBLIC_KEY, body.getRevealit_private_key(), mSessionManager);
        CommonMethods.encryptKey("xyz",Constants.KEY_MNEMONICS, body.getRevealit_private_key(), mSessionManager);
        CommonMethods.encryptKey(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY_PEM),Constants.KEY_PRIVATE_KEY_PEM, body.getRevealit_private_key(), mSessionManager);
        CommonMethods.encryptKey(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PUBLICKEY_PEM), Constants.KEY_PUBLIC_KEY_PEM,body.getRevealit_private_key(), mSessionManager);


        //UPDATE FLAG IF USER IS ADMIN OR NOT
        if (body.getRole().equals(mContext.getResources().getString(R.string.strAdmin))) {
            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN, true);
        } else {
            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN, false);
        }

        //UPDATE ACTIVE FLAG
        if (body.getIs_activated().equals("1")) {
            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE, true);
        } else {
            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE, false);
        }

        //UPDATE FLAG FOR APPLICATION MODE
        mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);

        //UPDATE EXISTING DETAILS TO KEYSTORE
        try {
            if(new UpdateUserDetailsInAndroidKeyStoreTask(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY),mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_USERNAME),body.getRole(), body.getRevealit_private_key(),body.getAuth_token(), Integer.parseInt(body.getIs_activated()), body.getRemove(),body.getAudience()).execute(mSessionManager).get()){}
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //GO TO BIOMETRIC CONFIRMATION ACTIVITY
        Intent mIntent = new Intent(mActivity, NewAuthBiomatricAuthenticationActivity.class);
        mIntent.putExtra(Constants.KEY_NEW_AUTH_USERNAME, mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_USERNAME));
        mActivity.startActivity(mIntent);
        mActivity.finishAffinity();
    }

    private void saveDataToTheAndroidKeyStore(UserDetailsFromPublicKeyModel body, String username) {


        if(body != null ){

            //MAKE SubmitProfileModel CLASS FROM ALL DATA
            SubmitProfileModel mSubmitModel = new SubmitProfileModel();
            mSubmitModel.setStatus(body.getStatus());
            mSubmitModel.setrevealit_private_key(body.getRevealit_private_key());
            mSubmitModel.setRole(body.getRole());
            mSubmitModel.setauth_token(body.getAuth_token());
            mSubmitModel.setAudience(body.getAudience());

            //SET PROTON DATA
            SubmitProfileModel.Proton mProton = new SubmitProfileModel.Proton();
            mProton.setAccountName(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_USERNAME));
            mProton.setPublicKey(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PUBLICKEY));
            mProton.setPrivateKey(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY));
            mProton.setMnemonic("");
            mProton.setPublic_pem(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PUBLICKEY_PEM));
            mProton.setPrivate_pem(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY_PEM));
            mSubmitModel.setProton(mProton);

            mSubmitModel.setIs_activated(""+body.getIs_activated());
            mSubmitModel.setMessage("");
            mSubmitModel.setError_code(0);
            mSubmitModel.setServerInstance("");

            mSessionManager.updatePreferenceString(Constants.KEY_USER_DATA, ""+mGson.toJson(mSubmitModel));
            mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN ,body.getAuth_token());
            mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE ,getResources().getString(R.string.strTokenBearer));
            mSessionManager.updatePreferenceString(Constants.PROTON_ACCOUNT_NAME ,username);
            //mSessionManager.updatePreferenceString(Constants.KEY_PROTON_WALLET_DETAILS,gson.toJson(body.getProton()));
            mSessionManager.updatePreferenceString(Constants.KEY_REVEALIT_PRIVATE_KEY ,body.getRevealit_private_key());
            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_REGISTRATION_DONE ,true);
            mSessionManager.updatePreferenceString(Constants.KEY_MOBILE_NUMBER,mSessionManager.getPreference(Constants.KEY_MOBILE_NUMBER));


            //STORE DATA IN TO KEYSTORE
            //FOR DISPLAY PURPOSE
            CommonMethods.encryptKey(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY), Constants.KEY_PRIVATE_KEY ,body.getRevealit_private_key(), mSessionManager);
            CommonMethods.encryptKey(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PUBLICKEY),Constants.KEY_PUBLIC_KEY,body.getRevealit_private_key(), mSessionManager);
            CommonMethods.encryptKey("xyz",Constants.KEY_MNEMONICS,body.getRevealit_private_key(), mSessionManager);
            CommonMethods.encryptKey(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY_PEM),Constants.KEY_PRIVATE_KEY_PEM,body.getRevealit_private_key(), mSessionManager);
            CommonMethods.encryptKey(mSessionManager.getPreference(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PUBLICKEY_PEM),Constants.KEY_PUBLIC_KEY_PEM,body.getRevealit_private_key(), mSessionManager);


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
            storeKeyStoreInstances(mSubmitModel);

        }


    }

    private void storeKeyStoreInstances(SubmitProfileModel body) {

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
                    if(jsonArray.getJSONObject(i).getInt("isAccountRemoved") == 1){
                        mModel.setIsAccountRemoved(1);
                    }

                    SubmitProfileModel mSubmitProfileModel = new SubmitProfileModel();
                    mSubmitProfileModel.setAudience(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("audience"));
                    mSubmitProfileModel.setauth_token(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("token"));
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

                    //SET USER DETAILS
                    KeyStoreServerInstancesModel.UserProfile mUserProfile = new KeyStoreServerInstancesModel.UserProfile();
                    if(jsonArray.getJSONObject(i).get("userProfile").toString() != "null"){
                        mUserProfile.setId(jsonArray.getJSONObject(i).getJSONObject("userProfile").getInt("id"));
                        mUserProfile.setUser_id(jsonArray.getJSONObject(i).getJSONObject("userProfile").getString("user_id"));
                        mUserProfile.setName(jsonArray.getJSONObject(i).getJSONObject("userProfile").getString("name"));
                        mUserProfile.setFirst_name(jsonArray.getJSONObject(i).getJSONObject("userProfile").getString("first_name"));
                        mUserProfile.setLast_name(jsonArray.getJSONObject(i).getJSONObject("userProfile").getString("last_name"));
                        mUserProfile.setEmail(jsonArray.getJSONObject(i).getJSONObject("userProfile").getString("email"));
                        mUserProfile.setDate_of_birth(jsonArray.getJSONObject(i).getJSONObject("userProfile").getString("date_of_birth"));
                        mUserProfile.setGender(jsonArray.getJSONObject(i).getJSONObject("userProfile").getString("gender"));
                        mUserProfile.setProfile_image(jsonArray.getJSONObject(i).getJSONObject("userProfile").getString("profile_image"));
                        mUserProfile.setAccount_type(jsonArray.getJSONObject(i).getJSONObject("userProfile").getString("account_type"));
                        mUserProfile.setClassification(jsonArray.getJSONObject(i).getJSONObject("userProfile").getString("classification"));
                        mUserProfile.setAudience(jsonArray.getJSONObject(i).getJSONObject("userProfile").getString("audience"));
                        mUserProfile.setRevealit_private_key(jsonArray.getJSONObject(i).getJSONObject("userProfile").getString("revealit_private_key"));
                        mModel.setUserProfile(mUserProfile);
                    }else{
                        mModel.setUserProfile(null);
                    }

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

        //GO TO NEXT ACTIVITY
        Intent mIntent = new Intent(NewAuthEnterOTPActivity.this, HomeScreenTabLayout.class);
        startActivity(mIntent);
        finishAffinity();

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

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

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
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ apiCallVerifyOTPMobile: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiCallVerifyOTPMobile: ", "" + response.code());
                CommonMethods.printLogE("Response @ apiCallVerifyOTPMobile: ", "" + gson.toJson(response.body()));

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();


                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

                    if(response.body().getStatus().toString().contains(getResources().getString(R.string.strApproved))){

                        //TOAST SEND OTP MSG
                        CommonMethods.displayToast(mContext, response.body().getStatus());

                        txtContinueEnabled.setVisibility(View.VISIBLE);
                        txtContinueDisable.setVisibility(View.INVISIBLE);
                        txtInvalidOTP.setVisibility(View.GONE);
                        txtVerifiedSuccessully.setVisibility(View.VISIBLE);
                        linearResendCode.setVisibility(View.GONE);

                        edtOne.setTextColor(getColor(R.color.colorBlack));
                        edtTwo.setTextColor(getColor(R.color.colorBlack));
                        edtThree.setTextColor(getColor(R.color.colorBlack));
                        edtFour.setTextColor(getColor(R.color.colorBlack));
                        edtFive.setTextColor(getColor(R.color.colorBlack));
                        edtSix.setTextColor(getColor(R.color.colorBlack));

                        //UPDATE FLAG
                        isOtpVarified = true;

                        //HIDE KEY BOARD
                        CommonMethods.hideKeyboard(mActivity);


                        //OPEN ENTER USERNAME SCREEN
                        openEnterUsernameScreen();

                    }else{

                        CommonMethods.buildRevealitCustomDialog(mActivity,mContext, getResources().getString(R.string.strInvalidVerificationCode), getResources().getString(R.string.strInvalidVerificationCodeMessage));

                    }

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        CommonMethods.buildRevealitCustomDialog(mActivity,mContext, getResources().getString(R.string.strInvalidVerificationCode), jObjError.getString("message"));

                    } catch (Exception e) {
                        CommonMethods.buildRevealitCustomDialog(mActivity,mContext, getResources().getString(R.string.strInvalidVerificationCode), ""+e.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<NewAuthStatusModel> call, Throwable t) {

                isOtpVarified = false;

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildRevealitCustomDialog(mActivity,mContext, getResources().getString(R.string.strInvalidVerificationCode), getResources().getString(R.string.strApiCallFailed));


            }
        });

    }



    class GenericTextWatcher implements TextWatcher {
        private EditText edtCurrent;
        private EditText nextView;

        public GenericTextWatcher(EditText edtCurrent, EditText nextView) {
            this.edtCurrent = edtCurrent;
            this.nextView = nextView;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();

            if (text.length() == 1 && nextView != null){
                nextView.requestFocus();
            }
            checkIfAllDigitEntered();

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    }
    class GenericKey implements View.OnKeyListener {

        private EditText edtCurrent;
        private EditText edtPrevious;

        public GenericKey(EditText edtCurrent, EditText edtPrevious) {
            this.edtCurrent = edtCurrent;
            this.edtPrevious = edtPrevious;
        }

        @Override
        public boolean onKey(View currentView, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.getId() != R.id.edtOne && edtCurrent.getText().toString().isEmpty()
            ) {
                edtPrevious.setText("");
                edtPrevious.requestFocus();
                return true;
            }
            return false;
        }
    }




}
class UpdateUserDetailsInAndroidKeyStoreTask extends AsyncTask<SessionManager, Integer, Boolean> {

    String strPrivateKey ="";
    String strUsername = "";
    String strUserRole = "";
    String revealitPrivateKey = "";
    String token = "";
    int isUserActivate ;
    int isUserRemoved ;
    String audience = "";

    public UpdateUserDetailsInAndroidKeyStoreTask(String privateKey, String strUsername,String role,String revealitPrivateKey,
                                                  String token,
                                                  int isUserActivate,
                                                  int isUserRemoved,
                                                  String audience) {

        this.strPrivateKey =privateKey;
        this.strUsername = strUsername;
        this.strUserRole = role;
        this.revealitPrivateKey = revealitPrivateKey;
        this.token = token;
        this.isUserActivate = isUserActivate;
        this.isUserRemoved = isUserRemoved;
        this.audience = audience;
    }
    @Override
    protected Boolean doInBackground(SessionManager... mSessionManager) {
        return CommonMethods.updateUserDetailsFromShortOnboardinToKeyChain(mSessionManager[0],strPrivateKey,strUsername,strUserRole,
                revealitPrivateKey,token,isUserActivate, isUserRemoved,audience);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Boolean searchResults) {
        super.onPostExecute(searchResults);

    }
}
