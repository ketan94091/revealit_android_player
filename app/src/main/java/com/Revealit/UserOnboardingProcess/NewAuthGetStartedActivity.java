package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.Activities.MaintanaceActivity;
import com.Revealit.BuildConfig;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.NewAuthLogin;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.Utils.Cryptography;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

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
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewAuthGetStartedActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NewAuthGetStartedActivi";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private TextView txtSignup,txtLogin,txtSwappingSilo;
    private String strServerName, strMobileKey,strRegistrationKey,strSelectedServerName;
    private int intEnvironmetID;
    AlertDialog mAlertDialog = null;
    private Gson gsonBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_auth_get_started);

        setId();
        setOnClicks();
    }

    private void setId() {
        mActivity = NewAuthGetStartedActivity.this;
        mContext = NewAuthGetStartedActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        txtLogin =(TextView) findViewById(R.id.txtLogin);
        txtSignup =(TextView) findViewById(R.id.txtSignup);
        txtSwappingSilo =(TextView) findViewById(R.id.txtSwappingSilo);

        //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
        txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);

        //HIDE SHOW ADMIN DETAILS
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN)){
            txtSwappingSilo.setVisibility(View.VISIBLE);
        }else{
            txtSwappingSilo.setVisibility(View.INVISIBLE);
        }

        //CHECK IF DEVICE ACTIVATED BIOMETRIC
        if(!CommonMethods.IsDeviceSecured(mContext)){
            CommonMethods.openBiometricActivatioDailogue(mActivity);
        }

        //SET SELECT ENVIRONMENT
        strMobileKey = mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY);
        strRegistrationKey = mSessionManager.getPreference(Constants.API_END_POINTS_REGISTRATION_KEY);
        strServerName = mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME);
        intEnvironmetID = mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID);

        //GSON CONVERTER
        gsonBuilder = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();


    }

    private void setOnClicks() {

        if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_REGISTRATION_DONE)){
            txtLogin.setOnClickListener(this);
        }

        txtSignup.setOnClickListener(this);
        txtSwappingSilo.setOnClickListener(this);

    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){
            case R.id.txtLogin:

                Intent mIntentLogin = new Intent(NewAuthGetStartedActivity.this, NewAuthBiomatricAuthenticationActivity.class);
                mIntentLogin.putExtra(Constants.KEY_ISFROM_LOGIN, true);
                startActivity(mIntentLogin);
                break;

            case R.id.txtSignup:
                Intent mIntent = new Intent(NewAuthGetStartedActivity.this, NewAuthMobileAndPromoActivity.class);
                startActivity(mIntent);
                break;
            case R.id.txtSwappingSilo:

                //OPEN END POINT SELECTION DIALOG
                openEndPointSelectionDialog();

                break;

        }

    }

    private void openEndPointSelectionDialog() {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_endpoint_selection, null);
        dialogBuilder.setView(dialogView);

        mAlertDialog = dialogBuilder.create();
        mAlertDialog.setCancelable(true);

        //SET CURRENT PROGRESSBAR
        ImageView imgCloseDailoge = (ImageView) dialogView.findViewById(R.id.imgCloseDailoge);

        SwitchCompat switchModeOfApp =(SwitchCompat)dialogView.findViewById(R.id.switchModeOfApp);

        LinearLayout linearBetacurator = (LinearLayout)dialogView. findViewById(R.id.linearBetacurator);
        LinearLayout linearStaging = (LinearLayout)dialogView. findViewById(R.id.linearStaging);
        LinearLayout linearTesting1 = (LinearLayout)dialogView. findViewById(R.id.linearTesting1);
        LinearLayout linearTesting2 = (LinearLayout)dialogView. findViewById(R.id.linearTesting2);
        LinearLayout linearTesting3 = (LinearLayout) dialogView.findViewById(R.id.linearTesting3);
        LinearLayout linearIntegration = (LinearLayout) dialogView.findViewById(R.id.linearIntegration);
        LinearLayout linearDemo = (LinearLayout) dialogView.findViewById(R.id.linearDemo);


        TextView txtSimulation = (TextView)dialogView. findViewById(R.id.txtSimulation);
        TextView txtLive = (TextView)dialogView. findViewById(R.id.txtLive);

        TextView txtBetaCuratorMobile = (TextView)dialogView. findViewById(R.id.txtBetaCuratorMobile);
        TextView txtBetaCuratorRegistration = (TextView)dialogView. findViewById(R.id.txtBetaCuratorRegistration);

        TextView txtStagingMobile = (TextView)dialogView. findViewById(R.id.txtStagingMobile);
        TextView txtStagingRegistration = (TextView)dialogView. findViewById(R.id.txtStagingRegistration);

        TextView txtTesting1Mobile = (TextView)dialogView. findViewById(R.id.txtTesting1Mobile);
        TextView txtTesting1Registration = (TextView)dialogView. findViewById(R.id.txtTesting1Registration);

        TextView txtTesting2Mobile = (TextView)dialogView. findViewById(R.id.txtTesting2Mobile);
        TextView txtTesting2Registration = (TextView)dialogView. findViewById(R.id.txtTesting2Registration);

        TextView txtTesting3Mobile = (TextView) dialogView.findViewById(R.id.txtTesting3Mobile);
        TextView txtTesting3Registration = (TextView) dialogView.findViewById(R.id.txtTesting3Registration);

        TextView txtIntegrationMobile = (TextView)dialogView. findViewById(R.id.txtIntegrationMobile);
        TextView txtIntegrationRegistration = (TextView)dialogView. findViewById(R.id.txtIntegrationRegistration);

        TextView txtDemoMobile = (TextView) dialogView.findViewById(R.id.txtDemoMobile);
        TextView txtDemoRegistration = (TextView) dialogView.findViewById(R.id.txtDemoRegistration);


        //SET SELECT ENVIRONMENT
        strMobileKey = mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY);
        strRegistrationKey = mSessionManager.getPreference(Constants.API_END_POINTS_REGISTRATION_KEY);
        strServerName = mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME);
        intEnvironmetID = mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID);


        //CHECKBOX TRUE IF APP MODE IS LIVE
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
            switchModeOfApp.setChecked(true);
            txtSimulation.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
            txtLive.setTextColor(getResources().getColor(R.color.colorBlack));
        }else {
            switchModeOfApp.setChecked(false);
            txtSimulation.setTextColor(getResources().getColor(R.color.colorBlack));
            txtLive.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
        }

        switchModeOfApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    //UPDATE FLAG FOR APPLICATION MODE
                    //IF TRUE APP IS IN LIVE MODE
                    //ELSE APP IS IN SIMULATION MODE
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);

                    txtSimulation.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
                    txtLive.setTextColor(getResources().getColor(R.color.colorBlack));

                }else{
                    //CLEAR DUMMY DATA
                    mDatabaseHelper.clearSimulationHistoryDataTable();

                    //UPDATE FLAG FOR APPLICATION MODE
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, false);

                    txtSimulation.setTextColor(getResources().getColor(R.color.colorBlack));
                    txtLive.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));


                }

            }


        });

        switch (mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID)) {
            case 1:

                linearBetacurator.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;
            case 2:

                linearStaging.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;

            case 3:

                linearTesting1.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;

            case 4:

                linearTesting2.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;
            case 5:

                linearTesting3.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;

            case 6:

                linearIntegration.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;
            case 7:

                linearDemo.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;
        }

        imgCloseDailoge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               mAlertDialog.dismiss();

            }
        });

        linearBetacurator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_B_CURATOR,Constants.API_END_POINTS_REGISTRATION_B_CURATOR, mActivity.getResources().getString(R.string.strBeta),1);

                 //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        linearStaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_S_CURATOR,Constants.API_END_POINTS_REGISTRATION_S_CURATOR, mActivity.getResources().getString(R.string.strStaging),2);


                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        linearTesting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_T1_CURATOR,Constants.API_END_POINTS_REGISTRATION_T1_CURATOR, mActivity.getResources().getString(R.string.strTesting1),3);

                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        linearTesting2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_T2_CURATOR,Constants.API_END_POINTS_REGISTRATION_T2_CURATOR, mActivity.getResources().getString(R.string.strTesting2),4);

                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        linearTesting3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_T3_CURATOR,Constants.API_END_POINTS_REGISTRATION_T3_CURATOR, mActivity.getResources().getString(R.string.strTesting3),5);

                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        linearIntegration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_INTEGRATION_CURATOR,Constants.API_END_POINTS_REGISTRATION_INTEGRATION_CURATOR, mActivity.getResources().getString(R.string.strIntegration),5);

                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        linearDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_DEMO_CURATOR,Constants.API_END_POINTS_REGISTRATION_DEMO_CURATOR, mActivity.getResources().getString(R.string.strDemo),7);

                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });



        mAlertDialog.show();


    }
    private void updateEnvironment(String strMobileKey, String strRegistrationKey, String strServerName, int intEnvironmetID){

        strSelectedServerName = strServerName;

        //CHANGE API END POINT TO BETA
        mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, strMobileKey);
        mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, strRegistrationKey);
        mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, intEnvironmetID);

        //UPDATE END POINT NAME
        mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, strServerName);

        //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
        txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);


        mAlertDialog.cancel();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkUserAvailable() throws JSONException {

        try{
            //OPEN KEYSTORE
            Cryptography mCryptography = new Cryptography(Constants.KEY_SILOS_ALIAS);

            if(!mSessionManager.getPreference(Constants.KEY_SILOS_DATA+""+mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID)).isEmpty()){
                //GET PRIVATE KEY
                String  userData = mCryptography.decrypt(mSessionManager.getPreference(Constants.KEY_SILOS_DATA+""+mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID)));

                if(!userData.isEmpty()){
                    JSONObject mJson= new JSONObject(userData);

                    //UPDATE SESSION MANAGER
                    mSessionManager.updatePreferenceString(Constants.KEY_USER_DATA, mSessionManager.getPreference(""+mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID)));
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN ,mJson.getString("auth_token"));
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE ,getResources().getString(R.string.strTokenBearer));
                    mSessionManager.updatePreferenceString(Constants.PROTON_ACCOUNT_NAME ,mJson.getJSONObject("proton").getString("account_name"));
                    //mSessionManager.updatePreferenceString(Constants.KEY_PROTON_WALLET_DETAILS,gson.toJson(body.getProton()));
                    mSessionManager.updatePreferenceString(Constants.KEY_REVEALIT_PRIVATE_KEY ,mJson.getString("revealit_private_key"));
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_REGISTRATION_DONE ,true);

                    //STORE DATA IN TO KEYSTORE
                    encryptKey(mJson.getJSONObject("proton").getString("private_key"), Constants.KEY_PRIVATE_KEY ,mJson.getString("revealit_private_key"));
                    encryptKey(mJson.getJSONObject("proton").getString("public_key"),Constants.KEY_PUBLIC_KEY,mJson.getString("revealit_private_key"));
                    encryptKey(mJson.getJSONObject("proton").getString("mnemonic"),Constants.KEY_MNEMONICS,mJson.getString("revealit_private_key"));
                    encryptKey(mJson.getJSONObject("proton").getString("private_pem"),Constants.KEY_PRIVATE_KEY_PEM,mJson.getString("revealit_private_key"));
                    encryptKey(mJson.getJSONObject("proton").getString("public_pem"),Constants.KEY_PUBLIC_KEY_PEM,mJson.getString("revealit_private_key"));


                    //UPDATE FLAG IF USER IS ADMIN OR NOT
                    if(mJson.getString("role").equals(getResources().getString(R.string.strAdmin))){
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,true);
                    }else{
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,false);
                    }

                    //UPDATE ACTIVE FLAG
                    if(mJson.getString("is_activated").equals("1")){
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,true);
                    }else{
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,false);
                    }

                    //UPDATE FLAG FOR APPLICATION MODE
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);

                    callAuthenticationAPI(mJson.getString("revealit_private_key"),mJson.getJSONObject("proton").getString("account_name"));


                }else{
                    displayAlertForUserNotFound();
                }
            }else{
                displayAlertForUserNotFound();
            }


        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException | NoSuchProviderException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException ex) {
            ex.printStackTrace();
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void encryptKey(String keyToStore, String alias, String keyStoreName) {

        try{

            //CREATE CRYPTOGRAPHY
            Cryptography mCryptography = new Cryptography(keyStoreName);

            //STORE AND ENCRYPT DATA IN KEYSTORE// returns base 64 data: 'BASE64_DATA,BASE64_IV'
            String encrypted = mCryptography.encrypt(keyToStore);

            //SAVE ENCRYPTED DATA TO PREFERENCE FOR SMOOTH TRANSITION
            mSessionManager.updatePreferenceString(alias,encrypted);


        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |IOException | NoSuchProviderException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException ex) {
            ex.printStackTrace();
        }

    }
    private void callAuthenticationAPI(String revealitPrivateKey, String userName) {

        //DISPLAY DIALOG
        CommonMethods.showDialog(mContext);


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
        paramObject.addProperty("revealit_private_key",revealitPrivateKey);
        paramObject.addProperty("name", userName);


        Call<NewAuthLogin> call = patchService1.newAuthLogin(paramObject);

        call.enqueue(new Callback<NewAuthLogin>() {
            @Override
            public void onResponse(Call<NewAuthLogin> call, Response<NewAuthLogin> response) {

                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + response.code());
                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + gsonBuilder.toJson(response.body()));

                //CLOSE DIALOG
                CommonMethods.closeDialog();


                if (response.isSuccessful() && response.code() == Constants.API_CODE_200 && response.body().getToken() != null) {


                    //SAVE AUTHENTICATION DATA
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN, response.body().getToken());
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE, response.body().getToken_type());
                    mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN, true);
                    mSessionManager.updatePreferenceBoolean(Constants.IS_FIRST_LOGIN, true);

                    //UPDATE FLAG IF USER IS ADMIN OR NOT
                    if(response.body().getRole().equals(getResources().getString(R.string.strAdmin))){
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,true);
                    }else{
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,false);
                    }

                    //UPDATE ACTIVE FLAG
                    if(response.body().getIs_activated().equals("1")){
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,true);
                    }else{
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,false);
                    }

                    //SAVE PUBLIC SETTINGS
                    if(response.body().getPuplic_settings() != null){
                        mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_API_VERSION, response.body().getPuplic_settings().getApi_version());
                        mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_VERSION, response.body().getPuplic_settings().getMinumum_acceptable_version());
                        mSessionManager.updatePreferenceString(Constants.KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_API_VERSION, response.body().getPuplic_settings().getMinumum_acceptable_api_version());
                        mSessionManager.updatePreferenceInteger(Constants.KEY_PUBLIC_SETTING_MINIMUM_PROFILE_REMINDER, response.body().getPuplic_settings().getProfile_update_reminder_period());
                        mSessionManager.updatePreferenceInteger(Constants.KEY_PUBLIC_SETTING_BACKUP_REMINDER, response.body().getPuplic_settings().getBackup_update_reminder_period());
                    }

                    //CHECK IF APPLICATION IS IN MAINTENANCE
                    if(response.body().getPuplic_settings() != null && response.body().getPuplic_settings().getMaintenance().equals("1")){
                        //MOVE TO MAINTENANCE SCREEN
                        Intent mIntent = new Intent(mActivity, MaintanaceActivity.class);
                        mIntent.putExtra(Constants.KEY_IS_FROM_CALLBACKAPI,false);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        mActivity.finishAffinity();
                    }else {
                        Intent mIntent = new Intent(mActivity, HomeScreenTabLayout.class);
                        mIntent.putExtra(Constants.KEY_IS_FROM_REGISTRATION_SCREEN, false);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        mActivity.finishAffinity();
                    }

                }else {
                    displayAlertForUserNotFound();
                }
            }

            @Override
            public void onFailure(Call<NewAuthLogin> call, Throwable t) {


                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                CommonMethods.closeDialog();

            }
        });

    }


    private void displayAlertForUserNotFound() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_user_not_found_for_selected_silos, null);
        dialogBuilder.setView(dialogView);

        mAlertDialog = dialogBuilder.create();
        mAlertDialog.setCancelable(true);

        //SET CURRENT PROGRESSBAR
        ImageView imgCloseDailoge = (ImageView) dialogView.findViewById(R.id.imgCloseDailoge);


        TextView txtContent = (TextView)dialogView. findViewById(R.id.txtContent);
        TextView txtContinue = (TextView)dialogView. findViewById(R.id.txtContinue);
        TextView txtCancel = (TextView)dialogView. findViewById(R.id.txtCancel);

        //SET DYNAMIC CONTENT
        txtContent.setText("The current user "+mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME)+" does not have an account at "+strSelectedServerName);

        imgCloseDailoge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE DEFAULT ENVIRONMENT IF THERE IS NO USER AVAILABLE TO THE SELECTED ENVIRONMENT
                //IN CASE ERROR SET DEFAULT ENVIRONMENT
                updateEnvironment(strMobileKey,strRegistrationKey, strServerName,intEnvironmetID);

                mAlertDialog.cancel();

            }
        });
        txtContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE LOGIN FLAG
                mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN,false);
                mSessionManager.updatePreferenceBoolean(Constants.KEY_ISFROM_LOGOUT,true);

                // SEND USER TO LANDING SCREEN
                Intent mIntent = new Intent(mActivity, NewAuthMobileAndPromoActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mIntent);
                mActivity.finish();

                mAlertDialog.cancel();

            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE DEFAULT ENVIRONMENT IF THERE IS NO USER AVAILABLE TO THE SELECTED ENVIRONMENT
                //IN CASE ERROR SET DEFAULT ENVIRONMENT
                updateEnvironment(strMobileKey,strRegistrationKey, strServerName,intEnvironmetID);

                mAlertDialog.cancel();

            }
        });

        mAlertDialog.show();

    }


}