package com.Revealit.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashScreen extends AppCompatActivity {

    private Handler handler;
    private SplashScreen mActivity;
    private SplashScreen mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private RelativeLayout relativeDots;

    private float[] X = {207, 354, 152, 392, 563};
    private float[] Y = {424, 217, 1158, 1491, 1496};
    private ImageView imgDynamicCoordinateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        mActivity = SplashScreen.this;
        mContext = SplashScreen.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        relativeDots = (RelativeLayout) findViewById(R.id.relativeDots);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        for (int i = 0; i < Y.length; i++) {
            imgDynamicCoordinateView = new ImageView(this);
            imgDynamicCoordinateView.setImageResource(R.drawable.dots);
            imgDynamicCoordinateView.setTag(i);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(35, 35);
            layoutParams.leftMargin = Math.round(X[i]);
            layoutParams.topMargin = Math.round(Y[i]);
            relativeDots.addView(imgDynamicCoordinateView, layoutParams);
        }


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                //FALSE == APP OPEN FIRST TIME
                //TRUE  == APP NOT OPEN FIRST TIME

                if (!mSessionManager.getPreferenceBoolean(Constants.IS_APP_OPEN_FIRST_TIME)) {
                    //SAVE TESTING END POINTS
                    //CHANGE API END POINT TO ALPHA T CURATOR
                    mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, Constants.API_END_POINTS_MOBILE);
                    mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, Constants.API_END_POINTS_REGISTRATION);
                    mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, mActivity.getResources().getString(R.string.strBeta));

                    switch (Constants.API_END_POINTS_MOBILE) {

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
                        case Constants.API_END_POINTS_MOBILE_INTEGRATION_CURATOR:
                            mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 5);
                            break;
                    }

                    //CHANGE THE VALUE OF APP OPEN
                    mSessionManager.updatePreferenceBoolean(Constants.IS_APP_OPEN_FIRST_TIME, true);
                }


                //INTENT
                //CHECK IF USER IS ALRADY LOGGED IN OR NOT
                if (!mSessionManager.getPreferenceBoolean(Constants.USER_LOGGED_IN)) {
                    Intent mIntent = new Intent(SplashScreen.this, GettingStartedActivity.class);
                    startActivity(mIntent);
                    finish();
                } else {
                    Intent mIntent = new Intent(SplashScreen.this, BiomatricAuthenticationActivity.class);
                    startActivity(mIntent);
                    finish();
                }
            }
        }, 3000);


    }

}