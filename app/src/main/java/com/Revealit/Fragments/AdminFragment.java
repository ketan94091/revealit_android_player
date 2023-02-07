package com.Revealit.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.UserOnboardingProcess.NewAuthGetStartedActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

import java.lang.reflect.Modifier;

public class AdminFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AdminFragment";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private View mView, viewBottomDefault, viewHelp, viewSettings, viewSavedItems, viewAccount;
    private HomeScreenTabLayout mHomeScreenTabLayout;
    private OnFragmentInteractionListener mListener;
    private RelativeLayout relativeBack;
    private String strServerName, strMobileKey,strRegistrationKey;
    private int intEnvironmetID;
    private AlertDialog mAlertDialog = null;
    private Gson gsonBuilder;
    private String strSelectedServerName;
    private LinearLayout linearMobileDevTwo,linearDemo,linearIntegration,linearTesting3,linearTesting2,linearTesting1,linearStaging,linearBetacurator;
    private TextView txtLive,txtSimulation;


    public AdminFragment(HomeScreenTabLayout homeScreenTabLayout) {
        this.mHomeScreenTabLayout = homeScreenTabLayout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.admin_fragment, container, false);

        setIds();
        setOnClicks();


        return mView;

    }


    private void setIds() {


        mActivity = getActivity();
        mContext = getActivity();

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        relativeBack =(RelativeLayout)mView.findViewById(R.id.relativeBack);

        SwitchCompat switchModeOfApp =(SwitchCompat) mView.findViewById(R.id.switchModeOfApp);

         linearBetacurator = (LinearLayout) mView.findViewById(R.id.linearBetacurator);
         linearStaging = (LinearLayout) mView.findViewById(R.id.linearStaging);
         linearTesting1 = (LinearLayout) mView.findViewById(R.id.linearTesting1);
         linearTesting2 = (LinearLayout) mView.findViewById(R.id.linearTesting2);
         linearTesting3 = (LinearLayout) mView.findViewById(R.id.linearTesting3);
         linearIntegration = (LinearLayout) mView.findViewById(R.id.linearIntegration);
         linearDemo = (LinearLayout) mView.findViewById(R.id.linearDemo);
        linearMobileDevTwo = (LinearLayout) mView.findViewById(R.id.linearMobileDevTwo);


         txtSimulation = (TextView) mView.findViewById(R.id.txtSimulation);
         txtLive = (TextView) mView.findViewById(R.id.txtLive);


        //SET SELECTED ENVIRONMENT
        strMobileKey = mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY);
        strRegistrationKey = mSessionManager.getPreference(Constants.API_END_POINTS_REGISTRATION_KEY);
        strServerName = mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME);
        intEnvironmetID = mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID);


        //CHECK SWITCH MODE TRUE IF APP MODE IS LIVE
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
            switchModeOfApp.setChecked(true);
            txtSimulation.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
            txtLive.setTextColor(getResources().getColor(R.color.colorBlack));
        }else {
            switchModeOfApp.setChecked(false);
            txtSimulation.setTextColor(getResources().getColor(R.color.colorBlack));
            txtLive.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
        }

        //GSON CONVERTER FOR CONVERTING STRING OBJECT TO JSON OBJECT
        gsonBuilder = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();


        switchModeOfApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    //UPDATE FLAG FOR APPLICATION MODE
                    //IF TRUE APP IS IN LIVE MODE
                    //ELSE APP IS IN SIMULATION MODE
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);

                    txtSimulation.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
                    txtLive.setTextColor(getResources().getColor(R.color.colorBlack));

                    //CALL INTERFACE HOMESCREEN TAB LAYOUT FOR CHANGING TABS
                    mHomeScreenTabLayout.isSimulationModeActive(isChecked);

                }else{
                    //CLEAR DUMMY DATA
                    mDatabaseHelper.clearSimulationHistoryDataTable();

                    //UPDATE FLAG FOR APPLICATION MODE
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, false);

                    txtSimulation.setTextColor(getResources().getColor(R.color.colorBlack));
                    txtLive.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));

                    //CALL INTERFACE HOMESCREEN TAB LAYOUT FOR CHANGING TABS
                    mHomeScreenTabLayout.isSimulationModeActive(isChecked);
                }

            }

        });

        //CHANGE THE BACKGROUND FOR THE SELECTED SILOS
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

                linearTesting3.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;

            case 6:

                linearIntegration.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;
            case 7:

                linearDemo.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;
            case 8:

                linearMobileDevTwo.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;
        }

        linearBetacurator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                        //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_B_CURATOR,Constants.API_END_POINTS_REGISTRATION_B_CURATOR, mActivity.getResources().getString(R.string.strBeta),1);


                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        linearStaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_S_CURATOR,Constants.API_END_POINTS_REGISTRATION_S_CURATOR, mActivity.getResources().getString(R.string.strStaging),2);


                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        linearTesting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_T1_CURATOR,Constants.API_END_POINTS_REGISTRATION_T1_CURATOR, mActivity.getResources().getString(R.string.strTesting1),3);


                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        linearTesting2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_T2_CURATOR,Constants.API_END_POINTS_REGISTRATION_T2_CURATOR, mActivity.getResources().getString(R.string.strTesting2),4);


                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        linearTesting3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_T3_CURATOR,Constants.API_END_POINTS_REGISTRATION_T3_CURATOR, mActivity.getResources().getString(R.string.strTesting3),5);


                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        linearIntegration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_INTEGRATION_CURATOR,Constants.API_END_POINTS_REGISTRATION_INTEGRATION_CURATOR, mActivity.getResources().getString(R.string.strIntegration),6);

                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        linearDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_DEMO_CURATOR,Constants.API_END_POINTS_REGISTRATION_DEMO_CURATOR, mActivity.getResources().getString(R.string.strDemo),7);

                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        linearMobileDevTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_ANDROID_M1_CURATOR,Constants.API_END_POINTS_REGISTRATION__ANDROID_M1_CURATOR, mActivity.getResources().getString(R.string.strAndroidMobile1),8);

                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                try {
                    checkUserAvailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
      

    }

    private void checkUserAvailable() throws JSONException {

            //UPDATE LOGIN FLAG
            mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN,false);
            mSessionManager.updatePreferenceBoolean(Constants.KEY_ISFROM_LOGOUT,true);
            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN, false);

            //GO TO GET STARTED SCREEN FOR LOGIN OR SIGN UP
            // SEND USER TO LANDING SCREEN
            Intent mIntent = new Intent(mActivity, NewAuthGetStartedActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
            mActivity.finishAffinity();

    }







    private void setOnClicks() {
        relativeBack.setOnClickListener(this);
    }

    private void updateEnvironment(String strMobileKey, String strRegistrationKey, String strServerName, int intEnvironmetID){

        //SELECTED SERVER NAME
         strSelectedServerName = strServerName;


        //CHANGE API END POINT TO BETA
        mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, strMobileKey);
        mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, strRegistrationKey);
        mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, intEnvironmetID);

        //UPDATE END POINT NAME
        mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, strServerName);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){
            case R.id.relativeBack:
                backToPreviousFragment();
                break;
                
        }

    }

    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    backToPreviousFragment();

                    return true;
                }
                return false;
            }
        });
    }
    private void backToPreviousFragment() {
        getFragmentManager().popBackStackImmediate();
    }
   

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void messageFromParentFragment(Uri uri);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setIds();
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        if (menuVisible) {
            
        }
        super.setMenuVisibility(menuVisible);
    }


}
