package com.Revealit.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class AppUpgradeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AppUpgradeActivity";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private TextView txtUpgradeApp;
    private boolean isFromCallBackApi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appupgrade);

        setIds();
        setOnClicks();
    }
    private void setIds() {
        mActivity = AppUpgradeActivity.this;
        mContext = AppUpgradeActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        txtUpgradeApp=(TextView)findViewById(R.id.txtUpgradeApp);


    }

    private void setOnClicks() {

        txtUpgradeApp.setOnClickListener(this);

    }


    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.txtUpgradeApp:



                break;
        }

    }



}