package com.Revealit.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.UserOnboardingProcess.NewAuthSplashScreen;

public class InternetBrokenActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "InternetBrokenActivity";
    private DatabaseHelper mDatabaseHelper;
    private SessionManager mSessionManager;
    private TextView txtRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_broken);

        setId();
        setOnClicks();
    }

    private void setId() {

        mActivity = InternetBrokenActivity.this;
        mContext = InternetBrokenActivity.this;

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        txtRefresh =(TextView)findViewById(R.id.txtRefresh);


    }
    private void setOnClicks() {

        txtRefresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.txtRefresh:
                if(CommonMethods.isInternetAvailable(mContext)){
                    Intent mIntent = new Intent(this, NewAuthSplashScreen.class);
                    startActivity(mIntent);
                    finish();
                }

                break;
        }

    }
}