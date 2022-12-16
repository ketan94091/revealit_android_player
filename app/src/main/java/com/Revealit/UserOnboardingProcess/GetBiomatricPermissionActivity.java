package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class GetBiomatricPermissionActivity extends AppCompatActivity {
    private static final String TAG = "BiomatricConfirmationActivity";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biomatric_confirmation);

        setIds();
    }


    private void setIds() {

        mActivity = GetBiomatricPermissionActivity.this;
        mContext = GetBiomatricPermissionActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();
        
        //OPEN PERMISSION DIALOGUE
        openBiomatricPermissionDialog();

    }
    private void openBiomatricPermissionDialog() {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_biomatric_permission_dailoague, null);
        dialogBuilder.setView(dialogView);


        final AlertDialog mAlertDialog = dialogBuilder.create();
        TextView txtDontAllow = (TextView) dialogView.findViewById(R.id.txtDontAllow);
        TextView txtOk = (TextView) dialogView.findViewById(R.id.txtOk);
        TextView txtDisplayMsg = (TextView) dialogView.findViewById(R.id.txtDisplayMsg);

        //BIOMETRIC PERMISSION MSG
        txtDisplayMsg.setText(mSessionManager.getPreference(Constants.KEY_INVITE_BIOMETRIC_PERMISSION));

        txtDontAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE FLAG IF USER ALLOW BIOMETRIC AUTHENTICATION
                mSessionManager.updatePreferenceBoolean(Constants.IS_ALLOW_BIOMETRIC , false);

                mAlertDialog.dismiss();

                openHomeScreen();

            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE FLAG IF USER ALLOW BIOMETRIC AUTHENTICATION
                mSessionManager.updatePreferenceBoolean(Constants.IS_ALLOW_BIOMETRIC , true);

                mAlertDialog.dismiss();

                openHomeScreen();

            }
        });
        mAlertDialog.show();

    }

    private void openHomeScreen() {

        //UPDATE SESSION VALUES
        mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);
        mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN ,true);
        mSessionManager.updatePreferenceBoolean(Constants.IS_FIRST_LOGIN ,true);

        Intent mIntent = new Intent(GetBiomatricPermissionActivity.this, HomeScreenTabLayout.class);
        startActivity(mIntent);
        finish();
    }
}