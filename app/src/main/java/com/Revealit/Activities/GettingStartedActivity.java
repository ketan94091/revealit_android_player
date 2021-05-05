package com.Revealit.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.android.youtube.player.YouTubeBaseActivity;

public class GettingStartedActivity extends YouTubeBaseActivity implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "GettingStartedActivity";


    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private TextView txtLogin,txtGetStarted;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_getting_started);


        setIds();
        setOnclick();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setIds() {

        mActivity = GettingStartedActivity.this;
        mContext = GettingStartedActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        txtGetStarted = (TextView)findViewById(R.id.txtGetStarted);
        txtLogin = (TextView)findViewById(R.id.txtLogin);


    }


    private void setOnclick() {


        txtGetStarted.setOnClickListener(this);
        txtLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.txtLogin:

                Intent mLoginIntent = new Intent(this, LoginActivityActivity.class);
                startActivity(mLoginIntent);

                break;

            case R.id.txtGetStarted:

                Intent mRegistrationIntent = new Intent(this, RegistrationActivity.class);
                startActivity(mRegistrationIntent);

                break;
        }
    }
    

}

