package com.Revealit.Fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class AboutFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AboutFragment";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private View mView, viewBottomDefault, viewHelp, viewSettings, viewSavedItems, viewAccount;
    private HomeScreenTabLayout mHomeScreenTabLayout;
    private OnFragmentInteractionListener mListener;
    private RelativeLayout relativeBack;
    private TextView txtBackUpUpdateReminder,txtProfileUpdateReminder,txtMinimumAcceptableApiVersion,txtMinimumAcceptableVersion,txtApiVersionName,txtVersionName;
    private LinearLayout linearAdminData;


    public AboutFragment(HomeScreenTabLayout homeScreenTabLayout) {
        this.mHomeScreenTabLayout = homeScreenTabLayout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.about_fragment, container, false);

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
        linearAdminData =(LinearLayout)mView.findViewById(R.id.linearAdminData);

        txtVersionName=(TextView)mView.findViewById(R.id.txtVersionName);
        txtApiVersionName=(TextView)mView.findViewById(R.id.txtApiVersionName);
        txtMinimumAcceptableVersion=(TextView)mView.findViewById(R.id.txtMinimumAcceptableVersion);
        txtMinimumAcceptableApiVersion=(TextView)mView.findViewById(R.id.txtMinimumAcceptableApiVersion);
        txtProfileUpdateReminder=(TextView)mView.findViewById(R.id.txtProfileUpdateReminder);
        txtBackUpUpdateReminder=(TextView)mView.findViewById(R.id.txtBackUpUpdateReminder);

        //SET APP VERSION
        txtVersionName.setText(getActivity().getString(R.string.strCurrentAppVersion)+" : "+ CommonMethods.installedAppVersion(mContext));
        txtApiVersionName.setText(getActivity().getString(R.string.strApiVersion)+" : "+ mSessionManager.getPreference(Constants.KEY_PUBLIC_SETTING_API_VERSION));

       //CHECK IF USER IS ADMIN
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN)){

        //DISPLAY ONLY FOR ADMIN
        linearAdminData.setVisibility(View.VISIBLE);

        //SET DATA FROM CALLBACK OR LOGIN API
            txtMinimumAcceptableVersion.setText(getActivity().getString(R.string.strMinimumAcceptableVersion)+" : "+ mSessionManager.getPreference(Constants.KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_VERSION));
            txtMinimumAcceptableApiVersion.setText(getActivity().getString(R.string.strMinimumAcceptableApiVersion)+" : "+ mSessionManager.getPreference(Constants.KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_API_VERSION));
            txtProfileUpdateReminder.setText(getActivity().getString(R.string.strProfileUpdateReminder)+" : "+ mSessionManager.getPreferenceInt(Constants.KEY_PUBLIC_SETTING_MINIMUM_PROFILE_REMINDER));
            txtBackUpUpdateReminder.setText(getActivity().getString(R.string.strBackUpUpdateReminder)+" : "+ mSessionManager.getPreferenceInt(Constants.KEY_PUBLIC_SETTING_BACKUP_REMINDER));

     }
    }
    private void setOnClicks() {
        relativeBack.setOnClickListener(this);

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
                //GO BACK TO PREVIOUS PAGE
                getFragmentManager().popBackStackImmediate();
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


        }
        super.setMenuVisibility(menuVisible);
    }


}
