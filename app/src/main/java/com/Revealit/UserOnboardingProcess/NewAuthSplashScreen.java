package com.Revealit.UserOnboardingProcess;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.InviteModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

public class NewAuthSplashScreen extends AppCompatActivity {

    private Handler handler;
    private NewAuthSplashScreen mActivity;
    private NewAuthSplashScreen mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private Gson mGson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_new_auth_splash_screen);

        mActivity = NewAuthSplashScreen.this;
        mContext = NewAuthSplashScreen.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();


        //GET DATA FROM THE CAMERA QR CODE
        //IT MUST BE IN THE FORM OF REVEALIT OR PROTON
        Intent intent = getIntent();
        Uri data = intent.getData();
        if(data != null && data.toString().contains("revealit") || data != null && data.toString().contains("proton")){
            mSessionManager.updatePreferenceBoolean(Constants.KEY_QR_CODE_FROM_CAMERA, true);
            mSessionManager.updatePreferenceString(Constants.KEY_QR_CODE_FROM_CAMERA_VALUE,data.toString() );
        }else{
            mSessionManager.updatePreferenceBoolean(Constants.KEY_QR_CODE_FROM_CAMERA, false);
            mSessionManager.updatePreferenceString(Constants.KEY_QR_CODE_FROM_CAMERA_VALUE,"");
        }

        mGson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                    .serializeNulls()
                    .create();


        //MERGER OLD KEYSTORE ARCHITECTURE IN TO NEW ARCHITECTURE
        checkBackwardCompatibilityForAccounts();


    }

    private void checkBackwardCompatibilityForAccounts() {

//        ArrayList<KeyStoreServerInstancesModel.Data> dataArrayList = new ArrayList<>();
//
//        //LOOP FOR 7 TIMES AS WE HAVE 7 SERVES AND PROBABLY ADMIN USERS CREATED USERS IN ALL SILOS
//        for (int j =1 ; j < 9 ;j++){
//
//            mGson = new GsonBuilder()
//                    .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
//                    .serializeNulls()
//                    .create();
//
//            try{
//                //CHECK IF THE SELECTED SILOS IS AVAILABLE TO THE SESSION MANAGER IN ENCRYPTED FORMAT
//                //IF TRUE -> FETCH EXISTING DATA FROM KEYSTORE AND CALL AUTH API
//                //ELSE -> DISPLAY USER NOT AVAILABLE TO SELECTED SILOS SILOS
//                Cryptography mCryptography = new Cryptography(Constants.KEY_SILOS_ALIAS);
//
//                if(!mSessionManager.getPreference(Constants.KEY_SILOS_DATA+""+j).isEmpty()){
//                    //FETCH USER DATA FROM KEYSTORE
//                    String  userData = mCryptography.decrypt(mSessionManager.getPreference(Constants.KEY_SILOS_DATA+""+j));
//                    //Log.e("USER",""+userData);
//                    //CHECK IF USER DATA IS NOT NULL
//                    //IF TRUE -> CONTINUE FETCHING DATA
//                    //ELSE -> DISPLAY NOT FOUND MSG
//                    if(!userData.isEmpty()){
//
//                        //CONVERT DATA TO JSON OBJECT
//                        JSONObject mJson= new JSONObject(userData);
//
//
//                        //SUBMIT PROFILE MODEL FROM OLD SILOS
//                        SubmitProfileModel mOldSubmitProfileModel = new SubmitProfileModel();
//                        mOldSubmitProfileModel.setStatus(mJson.getString("status"));
//                        mOldSubmitProfileModel.setrevealit_private_key(mJson.getString("revealit_private_key"));
//                        mOldSubmitProfileModel.setRole(mJson.getString("role"));
//                        mOldSubmitProfileModel.setauth_token(mJson.getString("token"));
//                        mOldSubmitProfileModel.setAudience(mJson.getString("audience"));
//                        mOldSubmitProfileModel.setIs_activated(mJson.getString("is_activated"));
//                        mOldSubmitProfileModel.setMessage(mJson.getString("message"));
//                        mOldSubmitProfileModel.setError_code(mJson.getInt("error_code"));
//                        mOldSubmitProfileModel.setServerInstance("");
//
//                        SubmitProfileModel.Proton mProtonOld = new SubmitProfileModel.Proton();
//                        mProtonOld.setAccountName(mJson.getJSONObject("proton").getString("account_name"));
//                        mProtonOld.setPublicKey(mJson.getJSONObject("proton").getString("public_key"));
//                        mProtonOld.setPrivateKey(mJson.getJSONObject("proton").getString("private_key"));
//                        mProtonOld.setMnemonic(mJson.getJSONObject("proton").getString("mnemonic"));
//                        mProtonOld.setPublic_pem(mJson.getJSONObject("proton").getString("public_pem"));
//                        mProtonOld.setPrivate_pem(mJson.getJSONObject("proton").getString("private_pem"));
//                        mOldSubmitProfileModel.setProton(mProtonOld);
//
//                        //STORE SERVER INSTANCE NAME
//                        ArrayList<KeyStoreServerInstancesModel.Data> mInstancesModel = new ArrayList<>();
//                        KeyStoreServerInstancesModel.Data mModelData = new KeyStoreServerInstancesModel.Data();
//                        switch (j) {
//
//                            case 1:
//                                mModelData.setServerInstanceName(getResources().getString(R.string.strBeta));
//                                break;
//                            case 2:
//                                mModelData.setServerInstanceName(getResources().getString(R.string.strStaging));
//                                break;
//                            case 3:
//                                mModelData.setServerInstanceName(getResources().getString(R.string.strTesting1));
//                                break;
//                            case 4:
//                                mModelData.setServerInstanceName(getResources().getString(R.string.strTesting2));
//                                break;
//                            case 5:
//                                mModelData.setServerInstanceName(getResources().getString(R.string.strTesting3));
//                                break;
//                            case 6:
//                                mModelData.setServerInstanceName(getResources().getString(R.string.strIntegration));
//                                break;
//                            case 7:
//                                mModelData.setServerInstanceName(getResources().getString(R.string.strDemo));
//                                break;
//                            case 8:
//                                mModelData.setServerInstanceName(getResources().getString(R.string.strAndroidMobile1));
//                                break;
//                        }
//                        mModelData.setMobileNumber("");
//                        mModelData.setServerInstanceId(j);
//                        mModelData.setSubmitProfileModel(mOldSubmitProfileModel);
//                        mInstancesModel.add(mModelData);
//
//                        //CHECK IF DATA IS ALREADY STORED IN TO KEYSTORE
//                        //IF STORED -> FETCH FROM KEYSTORE, CONVERT TO LIST, CREATE NEW OBJECT AND ADD THAT OBJECT TO LIST.
//                        //ELSE -> TREAT AS FIRST USER
//                        if(!CommonMethods.checkIfInstanceKeyStoreData(mSessionManager).isEmpty()){
//
//                            //CONVERT DATA TO JSON ARRAY
//                            //CREATE NEW ARRAY FROM THE STRING ARRAY
//                            //AFTER ADDING ALL SAVED DATA ADD NEWLY CREATED USER DATA
//                            try {
//                                JSONArray jsonArray =new JSONArray(CommonMethods.checkIfInstanceKeyStoreData(mSessionManager));
//                                 dataArrayList = new ArrayList<>();
//
//                                for (int i=0 ;i < jsonArray.length();i++){
//
//                                    KeyStoreServerInstancesModel.Data mModel = new KeyStoreServerInstancesModel.Data();
//                                    mModel.setServerInstanceName(jsonArray.getJSONObject(i).getString("serverInstanceName"));
//                                    mModel.setMobileNumber(jsonArray.getJSONObject(i).getString("mobileNumber"));
//                                    mModel.setServerInstanceId(jsonArray.getJSONObject(i).getInt("serverInstanceId"));
//
//                                    SubmitProfileModel mSubmitProfileModel = new SubmitProfileModel();
//                                    mSubmitProfileModel.setAudience(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("audience"));
//                                    mSubmitProfileModel.setauth_token(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("token"));
//                                    mSubmitProfileModel.setError_code(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getInt("error_code"));
//                                    mSubmitProfileModel.setIs_activated(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("is_activated"));
//                                    mSubmitProfileModel.setMessage(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("message"));
//                                    mSubmitProfileModel.setrevealit_private_key(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("revealit_private_key"));
//                                    mSubmitProfileModel.setRole(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("role"));
//                                    mSubmitProfileModel.setServerInstance(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("serverInstance"));
//                                    mSubmitProfileModel.setStatus(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("status"));
//
//                                    SubmitProfileModel.Proton mProton = new SubmitProfileModel.Proton();
//                                    mProton.setAccountName(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("account_name"));
//                                    mProton.setMnemonic(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("mnemonic"));
//                                    mProton.setPrivateKey(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("private_key"));
//                                    mProton.setPrivate_pem(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("private_pem"));
//                                    mProton.setPublicKey(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("public_key"));
//                                    mProton.setPublic_pem(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("public_pem"));
//                                    mSubmitProfileModel.setProton(mProton);
//
//                                    mModel.setSubmitProfileModel(mSubmitProfileModel);
//
//                                    dataArrayList.add(mModel);
//
//                                }
//
//                                //ADD NEWLY CREATED INSTANCE OBJECT TO THE ARRAYLIST
//                                dataArrayList.add(mModelData);
//
//                                //EMPTY DATA
//                                mSessionManager.updatePreferenceString(Constants.KEY_SILOS_DATA+""+j ,"");
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//
//                        }else{
//                            //ADD NEWLY CREATED INSTANCE OBJECT TO THE ARRAYLIST
//                            dataArrayList.add(mModelData);
//
//
//                            //EMPTY DATA
//                            mSessionManager.updatePreferenceString(Constants.KEY_SILOS_DATA+""+j ,"");
//
//                        }
//
//
//                        //STORE WHOLE ARRAY IN TO STRING FORMAT IN KEYSTORE
//                        CommonMethods.encryptKey(""+mGson.toJson(dataArrayList),  Constants.KEY_SILOS_DATA,Constants.KEY_SILOS_ALIAS, mSessionManager);
//
//
//
//                    }
//
//
//
//                } } catch (InvalidAlgorithmParameterException e) {
//                e.printStackTrace();
//            } catch (NoSuchPaddingException e) {
//                e.printStackTrace();
//            } catch (IllegalBlockSizeException e) {
//                e.printStackTrace();
//            } catch (CertificateException e) {
//                e.printStackTrace();
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (KeyStoreException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (BadPaddingException e) {
//                e.printStackTrace();
//            } catch (NoSuchProviderException e) {
//                e.printStackTrace();
//            } catch (InvalidKeyException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }



        //FALSE == APP OPEN FIRST TIME
        //TRUE  == APP NOT OPEN FIRST TIME
        if (!mSessionManager.getPreferenceBoolean(Constants.IS_APP_OPEN_FIRST_TIME)) {

            //UPDATE DEFAULT BLOCK PRODUCERS
            //CREATE ARRAY LIST FOR LOAD PRODUCERS
            ArrayList<String> mBlockProducerArray = new ArrayList<>();
            mBlockProducerArray.add(Constants.GET_PROTON_ACCOUNT_NAME_BASE_URL);

            //SAVE THIS TO SESSION MANAGER
            mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_BLOCK_PRODUCERS, mGson.toJson(mBlockProducerArray));



            //SAVE TESTING END POINTS
            //CHANGE API END POINT TO ALPHA T CURATOR
            mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, Constants.API_END_POINTS_MOBILE_B_CURATOR);
            mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, Constants.API_END_POINTS_REGISTRATION_B_CURATOR);
            mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, mActivity.getResources().getString(R.string.strBeta));


            switch (Constants.API_END_POINTS_MOBILE_B_CURATOR) {

                case Constants.API_END_POINTS_MOBILE_B_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 1);
                    break;
                case Constants.API_END_POINTS_MOBILE_S_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 2);
                    break;
                case Constants.API_END_POINTS_MOBILE_T1_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 3);
                    break;
                case Constants.API_END_POINTS_MOBILE_T2_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 4);
                    break;
                case Constants.API_END_POINTS_MOBILE_T3_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 5);
                    break;
                case Constants.API_END_POINTS_MOBILE_INTEGRATION_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 6);
                    break;
                case Constants.API_END_POINTS_MOBILE_DEMO_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 7);
                    break;
                case Constants.API_END_POINTS_MOBILE_ANDROID_M1_CURATOR:
                    mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 8);
                    break;
            }

            //CHANGE THE VALUE OF APP OPEN
            mSessionManager.updatePreferenceBoolean(Constants.IS_APP_OPEN_FIRST_TIME, true);
        }



        //API SETTINGS DATA CALL
        apiInviteSettings();

        }

    private void openNextActivity(InviteModel mInviteModel) {


        //UPDATE PREFERENCE FOR APP SETTINGS
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_MSG ,""+mInviteModel.getInvitation_message());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_COPY_CLIPBOARD ,""+mInviteModel.getInvitation_message_clipboard());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_BIOMETRIC_PERMISSION ,""+getResources().getString(R.string.strBiomatricPermissionTwo));
        mSessionManager.updatePreferenceString(Constants.KEY_CALL_FOR_INVITE_MSG ,""+mInviteModel.getInvitation_message());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_CYPTO_CURRNCY ,""+mInviteModel.getCrypto_currency());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_CURRNCY ,""+mInviteModel.getCurrency());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_CURRNCY_AMOUNT ,""+mInviteModel.getCurrency_amount());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_PLACEHOLDER ,""+mInviteModel.getPlace_holder());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_CURRENCY_ICON ,""+mInviteModel.getCurrency_icon_url());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_QUESTION ,""+mInviteModel.getQuestion());

        //INTENT
        //CHECK IF USER IS ALREADY LOGGED IN OR NOT
        if (!mSessionManager.getPreferenceBoolean(Constants.USER_LOGGED_IN)) {
            Intent mIntent = new Intent(NewAuthSplashScreen.this, NewAuthGetStartedActivity.class);
            startActivity(mIntent);
            finishAffinity();
        }else {
            //CALL CALLBACK API
            Intent mIntent = new Intent(NewAuthSplashScreen.this, HomeScreenTabLayout.class);
            mIntent.putExtra(Constants.KEY_ISFROM_LOGIN, false);
            startActivity(mIntent);
            finishAffinity();

        }


    }

    private void apiInviteSettings(){


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

                        //UPDATE INVITE UI
                        openNextActivity(response.body());

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


}