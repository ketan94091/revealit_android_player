package com.Revealit.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.Revealit.Activities.DisplayPrivateKeyActivity;
import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.UserOnboardingProcess.NewAuthSplashScreen;

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
                //GO BACK TO PREVIOUS PAGE
                getFragmentManager().popBackStackImmediate();
                break;

            case R.id.linearLogout:

                openLogoutDialogue();

                break;

            case R.id.linearMultifactorAuth:



                break;

            case R.id.linearPrivateKey:

               Intent mIntent = new Intent(mActivity, DisplayPrivateKeyActivity.class);
               mActivity.startActivity(mIntent);

                break;

        }

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


}
