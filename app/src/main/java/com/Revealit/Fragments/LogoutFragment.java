package com.Revealit.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.Revealit.Activities.GettingStartedActivity;
import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class LogoutFragment extends Fragment{

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "LogoutFragment";
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private View mView;
    private Activity homeScreenTabLayout;

    public LogoutFragment(HomeScreenTabLayout homeScreenTabLayout) {
        this.homeScreenTabLayout = homeScreenTabLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setTitle(getString(R.string.app_name));
        mView = inflater.inflate(R.layout.logout_fragment, container, false);

        setIds();

        return mView;

    }

    private void setIds() {


        mActivity = getActivity();
        mContext = getActivity();

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setIds();
    }

    private void openLogoutDialogue() {

      new AlertDialog.Builder(homeScreenTabLayout).setCancelable(false)

                .setTitle(Constants.APPLICATION_NAME)

                .setMessage(Constants.LOGOUT_FROM_APP)

                .setPositiveButton(Constants.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // SEND USER TO LANDING SCREEN
                        Intent mIntent = new Intent(mActivity, GettingStartedActivity.class);
                        startActivity(mIntent);
                        mActivity.finish();

                    }
                })

                .setNegativeButton(Constants.NO, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent mIntent = new Intent(mActivity, HomeScreenTabLayout.class);
                        startActivity(mIntent);
                        mActivity.finish();

                    }
                })
                .show();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        if (menuVisible) {

            //OPEN LOGOUT DIALOGUE
            openLogoutDialogue();


        }
        super.setMenuVisibility(menuVisible);
    }

}
