package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.Revealit.BuildConfig;
import com.Revealit.CommonClasse.CommonMethods;
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
    private TextView txtSignup,txtLogin,txtSwappingSilo;
    private String strServerName, strMobileKey,strRegistrationKey;
    private int intEnvironmetID;
    AlertDialog mAlertDialog = null;


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
        txtSwappingSilo =(TextView) findViewById(R.id.txtSwappingSilo);

        //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
        txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);

        //HIDE SHOW ADMIN DETAILS
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN)){
            txtSwappingSilo.setVisibility(View.VISIBLE);
        }else{
            txtSwappingSilo.setVisibility(View.INVISIBLE);
        }


        //CHECK IF DEVICE ACTIVATED BIOMETRIC
        if(!CommonMethods.IsDeviceSecured(mContext)){
            CommonMethods.openStartOverDialogue(mActivity);
        }

    }

    private void setOnClicks() {

        if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_REGISTRATION_DONE)){
            txtLogin.setOnClickListener(this);
        }

        txtSignup.setOnClickListener(this);
        txtSwappingSilo.setOnClickListener(this);

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
            case R.id.txtSwappingSilo:

                //OPEN END POINT SELECTION DIALOG
                openEndPointSelectionDialog();

                break;

        }

    }

    private void openEndPointSelectionDialog() {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_endpoint_selection, null);
        dialogBuilder.setView(dialogView);

        mAlertDialog = dialogBuilder.create();
        mAlertDialog.setCancelable(true);

        //SET CURRENT PROGRESSBAR
        ImageView imgCloseDailoge = (ImageView) dialogView.findViewById(R.id.imgCloseDailoge);

        SwitchCompat switchModeOfApp =(SwitchCompat)dialogView.findViewById(R.id.switchModeOfApp);

        LinearLayout linearBetacurator = (LinearLayout)dialogView. findViewById(R.id.linearBetacurator);
        LinearLayout linearStaging = (LinearLayout)dialogView. findViewById(R.id.linearStaging);
        LinearLayout linearTesting1 = (LinearLayout)dialogView. findViewById(R.id.linearTesting1);
        LinearLayout linearTesting2 = (LinearLayout)dialogView. findViewById(R.id.linearTesting2);
        LinearLayout linearIntegration = (LinearLayout) dialogView.findViewById(R.id.linearIntegration);

        TextView txtBetaCuratorMobile = (TextView)dialogView. findViewById(R.id.txtBetaCuratorMobile);
        TextView txtBetaCuratorRegistration = (TextView)dialogView. findViewById(R.id.txtBetaCuratorRegistration);
        TextView txtSimulation = (TextView)dialogView. findViewById(R.id.txtSimulation);
        TextView txtLive = (TextView)dialogView. findViewById(R.id.txtLive);

        TextView txtStagingMobile = (TextView)dialogView. findViewById(R.id.txtStagingMobile);
        TextView txtStagingRegistration = (TextView)dialogView. findViewById(R.id.txtStagingRegistration);

        TextView txtTesting1Mobile = (TextView)dialogView. findViewById(R.id.txtTesting1Mobile);
        TextView txtTesting1Registration = (TextView)dialogView. findViewById(R.id.txtTesting1Registration);

        TextView txtTesting2Mobile = (TextView)dialogView. findViewById(R.id.txtTesting2Mobile);
        TextView txtTesting2Registration = (TextView)dialogView. findViewById(R.id.txtTesting2Registration);

        TextView txtIntegrationMobile = (TextView)dialogView. findViewById(R.id.txtIntegrationMobile);
        TextView txtIntegrationRegistration = (TextView)dialogView. findViewById(R.id.txtIntegrationRegistration);



        //SET SELECT ENVIRONMENT
        strMobileKey = mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY);
        strRegistrationKey = mSessionManager.getPreference(Constants.API_END_POINTS_REGISTRATION_KEY);
        strServerName = mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME);
        intEnvironmetID = mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID);


        //CHECKBOX TRUE IF APP MODE IS LIVE
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
            switchModeOfApp.setChecked(true);
            txtSimulation.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
            txtLive.setTextColor(getResources().getColor(R.color.colorBlack));
        }else {
            switchModeOfApp.setChecked(false);
            txtSimulation.setTextColor(getResources().getColor(R.color.colorBlack));
            txtLive.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
        }

        switchModeOfApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



                if (isChecked){
                    //UPDATE FLAG FOR APPLICATION MODE
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);
                    txtSimulation.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
                    txtLive.setTextColor(getResources().getColor(R.color.colorBlack));

                }else{
                    //CLEAR DUMMY DATA
                    mDatabaseHelper.clearSimulationHistoryDataTable();

                    //UPDATE FLAG FOR APPLICATION MODE
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, false);
                    txtSimulation.setTextColor(getResources().getColor(R.color.colorBlack));
                    txtLive.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
                }

            }


        });

        switch (mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID)) {
            case 1:

                linearBetacurator.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;
            case 2:

                linearStaging.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;

            case 3:

                linearTesting1.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;

            case 4:

                linearTesting2.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;

            case 5:

                linearIntegration.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

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

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_B_CURATOR,Constants.API_END_POINTS_REGISTRATION_B_CURATOR, mActivity.getResources().getString(R.string.strBeta),1);



            }
        });
        linearStaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_S_CURATOR,Constants.API_END_POINTS_REGISTRATION_S_CURATOR, mActivity.getResources().getString(R.string.strStaging),2);


            }
        });
        linearTesting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_T1_CURATOR,Constants.API_END_POINTS_REGISTRATION_T1_CURATOR, mActivity.getResources().getString(R.string.strTesting1),3);


            }
        });

        linearTesting2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_T2_CURATOR,Constants.API_END_POINTS_REGISTRATION_T2_CURATOR, mActivity.getResources().getString(R.string.strTesting2),4);



            }
        });

        linearIntegration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_INTEGRATION_CURATOR,Constants.API_END_POINTS_REGISTRATION_INTEGRATION_CURATOR, mActivity.getResources().getString(R.string.strIntegration),5);
                

            }
        });



        mAlertDialog.show();


    }
    private void updateEnvironment(String strMobileKey, String strRegistrationKey, String strServerName, int intEnvironmetID){

        //CHANGE API END POINT TO BETA
        mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, strMobileKey);
        mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, strRegistrationKey);
        mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, intEnvironmetID);

        //UPDATE END POINT NAME
        mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, strServerName);


        //CLOSE DIALOGUE
        mAlertDialog.cancel();

        //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
        txtSwappingSilo.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);

    }

}