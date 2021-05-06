package com.Revealit.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.Executor;

public class BiomatricAuthenticationActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "RevealitNameActivity";


    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private RelativeLayout relativeMain;

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

        mActivity = BiomatricAuthenticationActivity.this;
        mContext = BiomatricAuthenticationActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        relativeMain= (RelativeLayout)findViewById(R.id.relativeMain);


        //CHECK IF BIOMETRIC HARDWARE AVAILABLE OR NOT
        //ALSO USER ALLOW TO USE BIOMETRIC WHILE REGISTRAION OR FIRST LOGIN
        BiometricManager biometricManager = BiometricManager.from(mContext);
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS && mSessionManager.getPreferenceBoolean(Constants.IS_ALLOW_BIOMETRIC)) {

            relativeMain.setVisibility(View.VISIBLE);

            //OPEN BIOMETRIC PROMPT
            loadBiomatricPrompt();

        } else {

            Intent mIntent = new Intent(BiomatricAuthenticationActivity.this, LoginActivityActivity.class);
            startActivity(mIntent);
            finish();
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
        biometricPrompt = new BiometricPrompt(BiomatricAuthenticationActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                Intent mIntent = new Intent(BiomatricAuthenticationActivity.this, LoginActivityActivity.class);
                startActivity(mIntent);
                finish();

            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Intent mIntent = new Intent(BiomatricAuthenticationActivity.this, HomeScreenTabLayout.class);
                startActivity(mIntent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login to Reveailit")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use Credentials")
                .setConfirmationRequired(false)
                .build();

        biometricPrompt.authenticate(promptInfo);

    }


}

