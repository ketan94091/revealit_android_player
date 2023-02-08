package com.Revealit.Fragments;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.Revealit.Activities.DisplayPrivateKeyActivity;
import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.UserOnboardingProcess.NewAuthSplashScreen;

import java.util.concurrent.Executor;

public class PrivacyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "PrivacyFragment";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private View mView, viewBottomDefault, viewHelp, viewSettings, viewSavedItems, viewAccount;
    private HomeScreenTabLayout mHomeScreenTabLayout;
    private OnFragmentInteractionListener mListener;
    private RelativeLayout relativeBack;
    private LinearLayout linearPrivateKey,linearLogout,linearMultifactorAuth;
    private ImageView imgBackArrow;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;


    public PrivacyFragment(HomeScreenTabLayout homeScreenTabLayout) {
        this.mHomeScreenTabLayout = homeScreenTabLayout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.privacy_fragment, container, false);

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
        linearMultifactorAuth =(LinearLayout)mView.findViewById(R.id.linearMultifactorAuth);
        linearLogout =(LinearLayout)mView.findViewById(R.id.linearLogout);
        linearPrivateKey =(LinearLayout)mView.findViewById(R.id.linearPrivateKey);

        imgBackArrow =(ImageView)mView.findViewById(R.id.imgBackArrow);

        //HIDE PRIVATE KEY IF LOGGED IN USER IS NOT ADMIN
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN)){
            linearPrivateKey.setVisibility(View.VISIBLE);
        }


    }
    private void setOnClicks() {
        imgBackArrow.setOnClickListener(this);
        linearMultifactorAuth.setOnClickListener(this);
        linearLogout.setOnClickListener(this);
        linearPrivateKey.setOnClickListener(this);
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
            case R.id.imgBackArrow:
                backToPreviousFragment();
                break;

            case R.id.linearLogout:

                openLogoutDialogue();

                break;

            case R.id.linearMultifactorAuth:



                break;

            case R.id.linearPrivateKey:

                //CHECK IF BIOMETRIC HARDWARE AVAILABLE OR NOT
                //ALSO USER ALLOW TO USE BIOMETRIC WHILE REGISTRAION OR FIRST LOGIN
                BiometricManager biometricManager = BiometricManager.from(mContext);
                if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BIOMETRIC_STRONG | DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {

                    //OPEN BIOMETRIC PROMPT
                    loadBiomatricPrompt();

                } else {

                    //OPEN NO AUTHENTICATION DIALOGUE
                    CommonMethods.openBiometricActivatioDailogue(mActivity);

                }


                break;

        }

    }
    private void loadBiomatricPrompt() {

        executor = ContextCompat.getMainExecutor(mContext);
        biometricPrompt = new BiometricPrompt((FragmentActivity) mContext,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                //ALLOW USER TO VIEW PRIVATE KEY
                Intent mIntent = new Intent(mActivity, DisplayPrivateKeyActivity.class);
                mActivity.startActivity(mIntent);

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login to Reveailit")
                .setSubtitle("Log in using your biometric credential")
                // .setNegativeButtonText("Use account password or Pin or Pattern")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .setConfirmationRequired(false)
                .build();

        biometricPrompt.authenticate(promptInfo);

    }
    private void openLogoutDialogue() {

        new AlertDialog.Builder(mHomeScreenTabLayout).setCancelable(false)

                .setTitle(Constants.APPLICATION_NAME)

                .setMessage(Constants.LOGOUT_FROM_APP)

                .setPositiveButton(Constants.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //CLEAR PUSHER NOTIFICATION INTEREST
                        //PushNotifications.clearAllState();

                        //UPDATE LOGIN FLAG
                        mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN,false);
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_ISFROM_LOGOUT,true);
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN, false);
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_CANCEL_REFERRAL, false);

                        // SEND USER TO LANDING SCREEN
                        Intent mIntent = new Intent(mActivity, NewAuthSplashScreen.class);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        mActivity.finish();

                    }
                })

                .setNegativeButton(Constants.NO, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                })
                .show();
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

            //OPEN LOGOUT DIALOGUE
            //openLogoutDialogue();


        }
        super.setMenuVisibility(menuVisible);
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


}
