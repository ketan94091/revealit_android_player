package com.Revealit.UserOnboardingProcess;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.NewAuthLogin;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class  BiomatricAuthenticationDeleteAccontActivity extends AppCompatActivity {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "NewAuthBiomatricAuthenticationActivity";


    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private RelativeLayout relativeMain;
    private boolean isFromLoginScreen;
    private AlertDialog mAlertDialog;
    private String strUsername, privateKey, revealitPrivateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_biomatric_authentication);


        setIds();


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setIds() {

        mActivity = BiomatricAuthenticationDeleteAccontActivity.this;
        mContext = BiomatricAuthenticationDeleteAccontActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        relativeMain= (RelativeLayout)findViewById(R.id.relativeMain);

        isFromLoginScreen = getIntent().getBooleanExtra(Constants.KEY_ISFROM_LOGIN, false);

        strUsername = getIntent().getStringExtra(Constants.KEY_PROTON_ACCOUNTNAME);
        privateKey = getIntent().getStringExtra(Constants.KEY_PRIVATE_KEY);
        revealitPrivateKey = getIntent().getStringExtra(Constants.KEY_REVEALIT_PRIVATE_KEY);

        //CHECK IF BIOMETRIC HARDWARE AVAILABLE OR NOT
        //ALSO USER ALLOW TO USE BIOMETRIC WHILE REGISTRAION OR FIRST LOGIN
        BiometricManager biometricManager = BiometricManager.from(mContext);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BIOMETRIC_STRONG | DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {

            //OPEN BIOMETRIC PROMPT
            loadBiomatricPrompt();

        } else {

            //OPEN NO AUTHENTICATION DIALOGUE
            CommonMethods.openBiometricActivatioDailogue(mActivity);

        }


    }


    private void loadBiomatricPrompt() {

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(BiomatricAuthenticationDeleteAccontActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);


                finish();


            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);


                    //CALL AUTHENTICATION API
                    //TO GET AUTH TOKEN
                    callAuthenticationAPI(revealitPrivateKey, strUsername, privateKey);

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login to Reveailit")
                .setSubtitle("Log in using your biometric credential")
               // .setNegativeButtonText("Use account password or Pin or Pattern")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .setConfirmationRequired(false)
                .build();

        biometricPrompt.authenticate(promptInfo);

    }
    private void callAuthenticationAPI(String revealItPrivateKey, String username, String strPrivateKey) {

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
        paramObject.addProperty("revealit_private_key",revealItPrivateKey);
        paramObject.addProperty("name", username);


        Call<NewAuthLogin> call = patchService1.newAuthLogin(paramObject);

        call.enqueue(new Callback<NewAuthLogin>() {
            @Override
            public void onResponse(Call<NewAuthLogin> call, Response<NewAuthLogin> response) {

                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + response.code());

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + gson.toJson(response.body()));

                //CLOSE DIALOG
                CommonMethods.closeDialog();


                if (response.isSuccessful() && response.code() == Constants.API_CODE_200 && response.body().getToken() != null) {

                    //IF - USER FOUND THAN CALL REMOVE USER API -> THAN DELETE USER FROM LOCAL ANDROID KEYSTORE
                    //ELSE - DELETE DIRECTLY FROM LOCAL
                    callDeleteUserApi(response.body().getToken());

                }else {

                    try {
                        deleteAccountFromAndroidKeyStore();
                    } catch (ExecutionException |InterruptedException  e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NewAuthLogin> call, Throwable t) {


                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                CommonMethods.closeDialog();

            }
        });

    }
    private void callDeleteUserApi(String token) {

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
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + " " +token)
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


        Call<JsonElement> call = patchService1.removeUser();

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ callDeleteUserApi: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callDeleteUserApi: ", "" + response.code());

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ callDeleteUserApi: ", "" + gson.toJson(response.body()));


                //CLOSE DIALOG
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {
                    try {
                        deleteAccountFromAndroidKeyStore();
                    } catch (ExecutionException |InterruptedException  e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {


                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                CommonMethods.closeDialog();

                finish();

            }
        });
    }

    private void deleteAccountFromAndroidKeyStore() throws ExecutionException, InterruptedException {

       if(new UpdateUserDataInAndroidKeyStoreTask(privateKey,strUsername).execute(mSessionManager).get()){

           //UPDATE GOOGLE DRIVE BACKUP FLAG
           mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_GOOGLE_DRIVE_BACKUP_DONE, false);

          //RETURN TO LIST OF ACTIVE ACCOUNTS
           Intent mIntent = new Intent(this, ListOfActiveAccountsActivity.class);
           startActivity(mIntent);
           finishAffinity();
       }

    }


}
class UpdateUserDataInAndroidKeyStoreTask extends AsyncTask<SessionManager, Integer, Boolean> {

    String strPrivateKey ="";
    String strUsername = "";

    public UpdateUserDataInAndroidKeyStoreTask(String privateKey, String strUsername) {

        this.strPrivateKey =privateKey;
        this.strUsername = strUsername;
    }
    @Override
    protected Boolean doInBackground(SessionManager... mSessionManager) {
        return CommonMethods.updateUserAccountActivationFlag(mSessionManager[0],strPrivateKey,strUsername);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Boolean searchResults) {
        super.onPostExecute(searchResults);

    }
}

