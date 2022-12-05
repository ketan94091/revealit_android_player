package com.Revealit.UserOnboardingProcess;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.KeyStoreServerInstancesModel;
import com.Revealit.ModelClasses.SubmitProfileModel;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.Utils.Cryptography;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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


        //MERGER OLD KEYSTORE ARCHITECTURE IN TO NEW ARCHITECTURE
        checkBackwardCompatibilityForAccounts();




    }

    private void checkBackwardCompatibilityForAccounts() {

        ArrayList<KeyStoreServerInstancesModel.Data> dataArrayList = new ArrayList<>();

        //LOOP FOR 7 TIMES AS WE HAVE 7 SERVES AND PROBABLY ADMIN USERS CREATED USERS IN ALL SILOS
        for (int j =1 ; j < 8 ;j++){
            Log.e("JJ",""+j);

            mGson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                    .serializeNulls()
                    .create();

            try{
                //CHECK IF THE SELECTED SILOS IS AVAILABLE TO THE SESSION MANAGER IN ENCRYPTED FORMAT
                //IF TRUE -> FETCH EXISTING DATA FROM KEYSTORE AND CALL AUTH API
                //ELSE -> DISPLAY USER NOT AVAILABLE TO SELECTED SILOS SILOS
                Cryptography mCryptography = new Cryptography(Constants.KEY_SILOS_ALIAS);

                if(!mSessionManager.getPreference(Constants.KEY_SILOS_DATA+""+j).isEmpty()){
                    //FETCH USER DATA FROM KEYSTORE
                    String  userData = mCryptography.decrypt(mSessionManager.getPreference(Constants.KEY_SILOS_DATA+""+j));
                    Log.e("USER",""+userData);
                    //CHECK IF USER DATA IS NOT NULL
                    //IF TRUE -> CONTINUE FETCHING DATA
                    //ELSE -> DISPLAY NOT FOUND MSG
                    if(!userData.isEmpty()){

                        //CONVERT DATA TO JSON OBJECT
                        JSONObject mJson= new JSONObject(userData);


                        //SUBMIT PROFILE MODEL FROM OLD SILOS
                        SubmitProfileModel mOldSubmitProfileModel = new SubmitProfileModel();
                        mOldSubmitProfileModel.setStatus(mJson.getString("status"));
                        mOldSubmitProfileModel.setrevealit_private_key(mJson.getString("revealit_private_key"));
                        mOldSubmitProfileModel.setRole(mJson.getString("role"));
                        mOldSubmitProfileModel.setauth_token(mJson.getString("auth_token"));
                        mOldSubmitProfileModel.setAudience(mJson.getString("audience"));
                        mOldSubmitProfileModel.setIs_activated(mJson.getString("is_activated"));
                        mOldSubmitProfileModel.setMessage(mJson.getString("message"));
                        mOldSubmitProfileModel.setError_code(mJson.getInt("error_code"));
                        mOldSubmitProfileModel.setServerInstance("");

                        SubmitProfileModel.Proton mProtonOld = new SubmitProfileModel.Proton();
                        mProtonOld.setAccountName(mJson.getJSONObject("proton").getString("account_name"));
                        mProtonOld.setPublicKey(mJson.getJSONObject("proton").getString("public_key"));
                        mProtonOld.setPrivateKey(mJson.getJSONObject("proton").getString("private_key"));
                        mProtonOld.setMnemonic(mJson.getJSONObject("proton").getString("mnemonic"));
                        mProtonOld.setPublic_pem(mJson.getJSONObject("proton").getString("public_pem"));
                        mProtonOld.setPrivate_pem(mJson.getJSONObject("proton").getString("private_pem"));
                        mOldSubmitProfileModel.setProton(mProtonOld);

                        //STORE SERVER INSTANCE NAME
                        ArrayList<KeyStoreServerInstancesModel.Data> mInstancesModel = new ArrayList<>();
                        KeyStoreServerInstancesModel.Data mModelData = new KeyStoreServerInstancesModel.Data();
                        switch (j) {

                            case 1:
                                mModelData.setServerInstanceName(getResources().getString(R.string.strBeta));
                                break;
                            case 2:
                                mModelData.setServerInstanceName(getResources().getString(R.string.strStaging));
                                break;
                            case 3:
                                mModelData.setServerInstanceName(getResources().getString(R.string.strTesting1));
                                break;
                            case 4:
                                mModelData.setServerInstanceName(getResources().getString(R.string.strTesting2));
                                break;
                            case 5:
                                mModelData.setServerInstanceName(getResources().getString(R.string.strTesting3));
                                break;
                            case 6:
                                mModelData.setServerInstanceName(getResources().getString(R.string.strIntegration));
                                break;
                            case 7:
                                mModelData.setServerInstanceName(getResources().getString(R.string.strDemo));
                                break;
                        }
                        mModelData.setMobileNumber("");
                        mModelData.setServerInstanceId(j);
                        mModelData.setSubmitProfileModel(mOldSubmitProfileModel);
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
                                 dataArrayList = new ArrayList<>();

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

                                //EMPTY DATA
                                mSessionManager.updatePreferenceString(Constants.KEY_SILOS_DATA+""+j ,"");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }else{
                            //ADD NEWLY CREATED INSTANCE OBJECT TO THE ARRAYLIST
                            dataArrayList.add(mModelData);


                            //EMPTY DATA
                            mSessionManager.updatePreferenceString(Constants.KEY_SILOS_DATA+""+j ,"");

                        }


                        //STORE WHOLE ARRAY IN TO STRING FORMAT IN KEYSTORE
                        CommonMethods.encryptKey(""+mGson.toJson(dataArrayList),  Constants.KEY_SILOS_DATA,Constants.KEY_SILOS_ALIAS, mSessionManager);



                    }



                } } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        openNextActivity();

        }

    private void openNextActivity() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                //FALSE == APP OPEN FIRST TIME
                //TRUE  == APP NOT OPEN FIRST TIME
                if (!mSessionManager.getPreferenceBoolean(Constants.IS_APP_OPEN_FIRST_TIME)) {
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
                    }

                    //CHANGE THE VALUE OF APP OPEN
                    mSessionManager.updatePreferenceBoolean(Constants.IS_APP_OPEN_FIRST_TIME, true);
                }


                //INTENT
                //CHECK IF USER IS ALREADY LOGGED IN OR NOT
                if (!mSessionManager.getPreferenceBoolean(Constants.USER_LOGGED_IN)) {
                    Intent mIntent = new Intent(NewAuthSplashScreen.this, NewAuthGetStartedActivity.class);
                    startActivity(mIntent);
                    finish();
                }
                else if(mSessionManager.getPreferenceBoolean(Constants.KEY_ISFROM_LOGOUT)){

                    //CLEAR FLAG - IF USER CAME FROM LOGOUT AND THAN UPDATE FLAG
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_ISFROM_LOGOUT, false);

                    Intent mIntent = new Intent(NewAuthSplashScreen.this, NewAuthBiomatricAuthenticationActivity.class);
                    mIntent.putExtra(Constants.KEY_ISFROM_LOGIN, true);
                    startActivity(mIntent);
                    finish();
                } else {
                    Intent mIntent = new Intent(NewAuthSplashScreen.this, NewAuthBiomatricAuthenticationActivity.class);
                    mIntent.putExtra(Constants.KEY_ISFROM_LOGIN, false);
                    startActivity(mIntent);
                    finish();

                }


            }
        }, 1000);

    }


}