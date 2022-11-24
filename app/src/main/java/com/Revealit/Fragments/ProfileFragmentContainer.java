package com.Revealit.Fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class ProfileFragmentContainer extends Fragment {

    private static final String TAG = "ProfileFragmentContainer";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private View mView, viewBottomDefault, viewHelp, viewSettings, viewSavedItems, viewAccount;
    private HomeScreenTabLayout mHomeScreenTabLayout;
    private OnFragmentInteractionListener mListener;

    public ProfileFragmentContainer(HomeScreenTabLayout homeScreenTabLayout) {
        this.mHomeScreenTabLayout = homeScreenTabLayout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setTitle(getString(R.string.app_name));
        mView = inflater.inflate(R.layout.profile_container_fragment, container, false);

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


        //LOAD USER PROFILE FRAGMENT
        Fragment childFragment = new UserProfileFragment(mHomeScreenTabLayout);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_fragment_container, childFragment).commit();


    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void messageFromParentFragment(Uri uri);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        if (menuVisible) {
//setIds();

        }
        super.setMenuVisibility(menuVisible);
    }




}
