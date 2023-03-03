package com.Revealit.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Revealit.Activities.CloudBackupActivity;
import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class SettingsFragmentContainer extends Fragment implements View.OnClickListener {

    private static final String TAG = "ProfileFragmentContainer";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private View mView, viewBottomDefault, viewHelp, viewSettings, viewSavedItems, viewAccount;
    private HomeScreenTabLayout mHomeScreenTabLayout;
    private OnFragmentInteractionListener mListener;
    private RelativeLayout relativeBack;
    private LinearLayout linearCloudBackup;


    public SettingsFragmentContainer(HomeScreenTabLayout homeScreenTabLayout) {
        this.mHomeScreenTabLayout = homeScreenTabLayout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.settings_fragment, container, false);

        setIds();
        setOnClicks();


        return mView;

    }

    private void setOnClicks() {
        relativeBack.setOnClickListener(this);
        linearCloudBackup.setOnClickListener(this);
    }

    private void setIds() {


        mActivity = getActivity();
        mContext = getActivity();

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        relativeBack =(RelativeLayout)mView.findViewById(R.id.relativeBack);
        linearCloudBackup =(LinearLayout)mView.findViewById(R.id.linearCloudBackup);

        //HIDE SHOW ADMIN DETAILS
        //CHECK USER IS ADMIN USER OR SUPER ADMIN USER
        //SUPER ADMIN -> IF ANY BETA USER IS ADMIN THAN CONSIDER USER AS ADMIN
        if(CommonMethods.checkIfUserIsSuperAdmin(mSessionManager,mContext)){
            linearCloudBackup.setVisibility(View.VISIBLE);
        }else{
            linearCloudBackup.setVisibility(View.GONE);
        }




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

                case R.id.linearCloudBackup:

                    Intent mIntent = new Intent(mContext, CloudBackupActivity.class);
                    startActivity(mIntent);

                break;
        }

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
