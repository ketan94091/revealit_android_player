package com.Revealit.Fragments;

import android.app.Activity;
import android.content.Context;
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
import androidx.fragment.app.FragmentTransaction;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class HelpFragmentContainer extends Fragment implements View.OnClickListener {

    private static final String TAG = "HelpFragmentContainer";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private View mView, viewBottomDefault, viewHelp, viewSettings, viewSavedItems, viewAccount;
    private HomeScreenTabLayout mHomeScreenTabLayout;
    private OnFragmentInteractionListener mListener;
    private RelativeLayout relativeBack;
    private LinearLayout linearAbout,linearTermsCondition;


    public HelpFragmentContainer(HomeScreenTabLayout homeScreenTabLayout) {
        this.mHomeScreenTabLayout = homeScreenTabLayout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.help_fragment, container, false);

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
        linearTermsCondition =(LinearLayout)mView.findViewById(R.id.linearTermsCondition);
        linearAbout =(LinearLayout)mView.findViewById(R.id.linearAbout);

    }
    private void setOnClicks() {
        relativeBack.setOnClickListener(this);
        linearTermsCondition.setOnClickListener(this);
        linearAbout.setOnClickListener(this);
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

            case R.id.linearAbout:

                loadFragments(new AboutFragment(mHomeScreenTabLayout));

                break;

            case R.id.linearTermsCondition:



                break;
        }

    }
    private void loadFragments(Fragment loadingFragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.profile_fragment_container, loadingFragment ).commit();
        transaction.addToBackStack(null);

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
