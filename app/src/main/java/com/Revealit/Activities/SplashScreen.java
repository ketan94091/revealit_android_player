package com.Revealit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

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


        /*relativeDots.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_DOWN)) {

                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    Log.e("X : ", ""+x);
                    Log.e("Y : ", ""+y);

                }
                return true;
            }
        });
*/

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //INTENT
                //CHECK IF USER IS ALRADY LOGGED IN OR NOT
               /* if (!mSessionManager.getPreferenceBoolean(Constants.USER_LOGGED_IN)) {
                    Intent mIntent = new Intent(SplashScreen.this, GettingStartedActivity.class);
                    startActivity(mIntent);
                    finish();
                } else {
                    Intent mIntent = new Intent(SplashScreen.this, BiomatricAuthenticationActivity.class);
                    startActivity(mIntent);
                    finish();
                }*/
            }
        }, 3000);
    }

}