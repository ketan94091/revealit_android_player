package com.Revealit.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.BuildConfig;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class GettingStartedActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "GettingStartedActivity";


    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private TextView txtSwappingSilo,txtTestNewAuth,txtLogin,txtGetStarted;

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
        txtTestNewAuth = (TextView)findViewById(R.id.txtTestNewAuth);
        txtSwappingSilo = (TextView)findViewById(R.id.txtSwappingSilo);

        //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
        txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);


    }


    private void setOnclick() {


        txtGetStarted.setOnClickListener(this);
        txtLogin.setOnClickListener(this);
        txtSwappingSilo.setOnClickListener(this);
        txtTestNewAuth.setOnClickListener(this);
    }


    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.txtLogin:

                Intent mLoginIntent = new Intent(this, LoginActivityActivity.class);
                startActivity(mLoginIntent);
                finish();

                break;

            case R.id.txtGetStarted:

                Intent mRegistrationIntent = new Intent(this, RegistrationActivity.class);
                startActivity(mRegistrationIntent);
                finish();

                break;


            case R.id.txtSwappingSilo:

                //OPEN END POINT SELECTION DIALOG
                openEndPointSelectionDialog();

                break;
                case R.id.txtTestNewAuth:

                    Intent mTestNewAuthIntent = new Intent(this, NewAuthActivity.class);
                    startActivity(mTestNewAuthIntent);
                    finish();


                break;

        }
    }

    private void openEndPointSelectionDialog() {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_endpoint_selection, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog mAlertDialog = dialogBuilder.create();
        mAlertDialog.setCancelable(true);

        //SET CURRENT PROGRESSBAR
        ImageView imgCloseDailoge = (ImageView) dialogView.findViewById(R.id.imgCloseDailoge);

        LinearLayout linearBetacurator = (LinearLayout) dialogView.findViewById(R.id.linearBetacurator);
        LinearLayout linearStaging = (LinearLayout) dialogView.findViewById(R.id.linearStaging);
        LinearLayout linearTesting1 = (LinearLayout) dialogView.findViewById(R.id.linearTesting1);
        LinearLayout linearTesting2 = (LinearLayout) dialogView.findViewById(R.id.linearTesting2);
        LinearLayout linearIntegration = (LinearLayout) dialogView.findViewById(R.id.linearIntegration);

        TextView txtBetaCuratorMobile = (TextView) dialogView.findViewById(R.id.txtBetaCuratorMobile);
        TextView txtBetaCuratorRegistration = (TextView) dialogView.findViewById(R.id.txtBetaCuratorRegistration);

        TextView txtStagingMobile = (TextView) dialogView.findViewById(R.id.txtStagingMobile);
        TextView txtStagingRegistration = (TextView) dialogView.findViewById(R.id.txtStagingRegistration);

        TextView txtTesting1Mobile = (TextView) dialogView.findViewById(R.id.txtTesting1Mobile);
        TextView txtTesting1Registration = (TextView) dialogView.findViewById(R.id.txtTesting1Registration);

        TextView txtTesting2Mobile = (TextView) dialogView.findViewById(R.id.txtTesting2Mobile);
        TextView txtTesting2Registration = (TextView) dialogView.findViewById(R.id.txtTesting2Registration);

        TextView txtIntegrationMobile = (TextView) dialogView.findViewById(R.id.txtIntegrationMobile);
        TextView txtIntegrationRegistration = (TextView) dialogView.findViewById(R.id.txtIntegrationRegistration);



        switch (mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID)) {
            case 1:

                linearBetacurator.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                txtBetaCuratorMobile.setTextColor(mContext.getResources().getColor(R.color.colorCurrency));
                txtBetaCuratorRegistration.setTextColor(mContext.getResources().getColor(R.color.colorCurrencyNameSelected));


                break;
            case 2:

                linearStaging.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                txtStagingMobile.setTextColor(mContext.getResources().getColor(R.color.colorCurrency));
                txtStagingRegistration.setTextColor(mContext.getResources().getColor(R.color.colorCurrencyNameSelected));


                break;

            case 3:

                linearTesting1.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                txtTesting1Mobile.setTextColor(mContext.getResources().getColor(R.color.colorCurrency));
                txtTesting1Registration.setTextColor(mContext.getResources().getColor(R.color.colorCurrencyNameSelected));

                break;

            case 4:

                linearTesting2.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                txtTesting2Mobile.setTextColor(mContext.getResources().getColor(R.color.colorCurrency));
                txtTesting2Registration.setTextColor(mContext.getResources().getColor(R.color.colorCurrencyNameSelected));


                break;

            case 5:

                linearIntegration.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                txtIntegrationMobile.setTextColor(mContext.getResources().getColor(R.color.colorCurrency));
                txtIntegrationRegistration.setTextColor(mContext.getResources().getColor(R.color.colorCurrencyNameSelected));


                break;
        }

        imgCloseDailoge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();
            }
        });


        linearBetacurator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CHANGE API END POINT TO BETA
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, Constants.API_END_POINTS_MOBILE_B_CURATOR);
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, Constants.API_END_POINTS_REGISTRATION_B_CURATOR);
                mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 1);

                //UPDATE END POINT NAME
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, mActivity.getResources().getString(R.string.strBeta));


                //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
                txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);


                mAlertDialog.dismiss();
            }
        });
        linearStaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //STAGING CURATOR
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, Constants.API_END_POINTS_MOBILE_S_CURATOR);
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, Constants.API_END_POINTS_REGISTRATION_S_CURATOR);
                mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 2);

                //UPDATE END POINT NAME
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, mActivity.getResources().getString(R.string.strStaging));


                //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
                txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);


                mAlertDialog.dismiss();
            }
        });
        linearTesting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TESTING1 CURATOR
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, Constants.API_END_POINTS_MOBILE_T1_CURATOR);
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, Constants.API_END_POINTS_REGISTRATION_T1_CURATOR);
                mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 3);

                //UPDATE END POINT NAME
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, mActivity.getResources().getString(R.string.strTesting1));


                //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
                txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);


                mAlertDialog.dismiss();
            }
        });

        linearTesting2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TESTING2 CURATOR
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, Constants.API_END_POINTS_MOBILE_T2_CURATOR);
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, Constants.API_END_POINTS_REGISTRATION_T2_CURATOR);
                mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 4);


                //UPDATE END POINT NAME
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, mActivity.getResources().getString(R.string.strTesting2));


                //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
                txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);

                mAlertDialog.dismiss();
            }
        });

        linearIntegration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //INTEGRATION CURATOR
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, Constants.API_END_POINTS_MOBILE_INTEGRATION_CURATOR);
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, Constants.API_END_POINTS_REGISTRATION_INTEGRATION_CURATOR);
                mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, 5);

                //UPDATE END POINT NAME
                mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, mActivity.getResources().getString(R.string.strIntegration));


                //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
                txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);


                mAlertDialog.dismiss();
            }
        });


        mAlertDialog.show();


    }


}

