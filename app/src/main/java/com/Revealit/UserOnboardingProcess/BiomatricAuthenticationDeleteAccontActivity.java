package com.Revealit.UserOnboardingProcess;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class BiomatricAuthenticationDeleteAccontActivity extends AppCompatActivity implements View.OnClickListener {

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
    private String strUsername, privateKey;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_biomatric_authentication);


        setIds();
        setOnclick();

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

        //CHECK IF BIOMETRIC HARDWARE AVAILABLE OR NOT
        //ALSO USER ALLOW TO USE BIOMETRIC WHILE REGISTRAION OR FIRST LOGIN
        BiometricManager biometricManager = BiometricManager.from(mContext);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BIOMETRIC_STRONG | DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS && mSessionManager.getPreferenceBoolean(Constants.IS_ALLOW_BIOMETRIC )) {

            //OPEN BIOMETRIC PROMPT
            loadBiomatricPrompt();

        } else {

            //OPEN NO AUTHENTICATION DIALOGUE
            CommonMethods.openBiometricActivatioDailogue(mActivity);

        }


    }


    private void setOnclick() {

    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {

            case R.id.imgGoBack:

                finish();

                break;
            case R.id.txtNextEnabled:


                break;
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


                finishAffinity();


            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);


                try {
                    deleteAccountFromAndroidKeyStore();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


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

    private void deleteAccountFromAndroidKeyStore() throws ExecutionException, InterruptedException {

       if(new UpdateUserDataInAndroidKeyStoreTask(privateKey,strUsername).execute(mSessionManager).get()){
           finish();
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

