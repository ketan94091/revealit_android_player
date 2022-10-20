package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class NewAuthGetStartedActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NewAuthGetStartedActivi";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private TextView txtSignup,txtLogin;


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

    }

    private void setOnClicks() {

        if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_REGISTRATION_DONE)){
            txtLogin.setOnClickListener(this);
        }

        txtSignup.setOnClickListener(this);

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

        }

    }

}