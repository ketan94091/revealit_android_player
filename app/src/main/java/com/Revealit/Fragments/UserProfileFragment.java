package com.Revealit.Fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class UserProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "LogoutFragment";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private View mView, viewBottomDefault, viewAdmin,viewHelp, viewSettings, viewSavedItems, viewAccount;
    private HomeScreenTabLayout mHomeScreenTabLayout;
    private TextView txtUsername, txtAdmin,txtHelp, txtSettings, txtMySavedItems, txtAccount, txtStatusMsg, txtStatus, txtCopyToClibBoard, txtMsgCopy;
    private String strUsername, strCopymsg;
    private boolean isUserIsActive =false;
    private LinearLayout linearAccount,linearSettings,linearSavedItems,linearAdmin,linearHelp;

    public UserProfileFragment(HomeScreenTabLayout homeScreenTabLayout) {
        this.mHomeScreenTabLayout = homeScreenTabLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setTitle(getString(R.string.app_name));
        mView = inflater.inflate(R.layout.userprofile_fragment, container, false);

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

        txtMsgCopy = (TextView) mView.findViewById(R.id.txtMsgCopy);
        txtCopyToClibBoard = (TextView) mView.findViewById(R.id.txtCopyToClibBoard);
        txtUsername = (TextView) mView.findViewById(R.id.txtUsername);
        txtStatus = (TextView) mView.findViewById(R.id.txtStatus);
        txtStatusMsg = (TextView) mView.findViewById(R.id.txtStatusMsg);
        txtAccount = (TextView) mView.findViewById(R.id.txtAccount);
        txtMySavedItems = (TextView) mView.findViewById(R.id.txtMySavedItems);
        txtSettings = (TextView) mView.findViewById(R.id.txtSettings);
        txtHelp = (TextView) mView.findViewById(R.id.txtHelp);
        txtAdmin = (TextView) mView.findViewById(R.id.txtAdmin);

        viewAccount = (View) mView.findViewById(R.id.viewAccount);
        viewSavedItems = (View) mView.findViewById(R.id.viewSavedItems);
        viewSettings = (View) mView.findViewById(R.id.viewSettings);
        viewHelp = (View) mView.findViewById(R.id.viewHelp);
        viewAdmin = (View) mView.findViewById(R.id.viewAdmin);
        viewBottomDefault = (View) mView.findViewById(R.id.viewBottomDefault);


        linearAccount=(LinearLayout)mView.findViewById(R.id.linearAccount);
        linearSavedItems=(LinearLayout)mView.findViewById(R.id.linearSavedItems);
        linearSettings=(LinearLayout)mView.findViewById(R.id.linearSettings);
        linearHelp=(LinearLayout)mView.findViewById(R.id.linearHelp);
        linearAdmin=(LinearLayout)mView.findViewById(R.id.linearAdmin);

        //GET USER STATUS
        //TRUE = USER IS ACTIVE AND VERIFIED
        //FLASE= USER IS NOT ACTIVE AND NOT YET VERIFIED
        isUserIsActive = mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_ACTIVE);

        //GET INTENT DATA
        strUsername = mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME);
        strCopymsg = mSessionManager.getPreference(Constants.KEY_INVITE_MSG);

        //SET INVITE MSG WHICH CAME FROM INVITE SETTING API
        txtMsgCopy.setText(strCopymsg.replace("xxxx",strUsername));

        //UPDATE UI BASED ON USER STATUS
        updateUI(isUserIsActive);


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setIds();
    }

    private void setOnClicks() {

        txtCopyToClibBoard.setOnClickListener(this);

        //IF USER IS ACTIVE ONLY
        if(isUserIsActive){
            linearAccount.setOnClickListener(this);
            linearSavedItems.setOnClickListener(this);
            linearSettings.setOnClickListener(this);
            linearHelp.setOnClickListener(this);
            linearAdmin.setOnClickListener(this);
        }

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        if (menuVisible) {
            setIds();
            setOnClicks();

        }
        super.setMenuVisibility(menuVisible);
    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {

            case R.id.txtCopyToClibBoard:

                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.strUsername), strUsername);
                clipboard.setPrimaryClip(clip);

                //TOAST MSG
                CommonMethods.displayToast(mContext, getString(R.string.strUsernameCopied));

                break;
            case R.id.linearAccount:

                loadFragments(new AccountFragmentContainer(mHomeScreenTabLayout));

                break;
            case R.id.linearHelp:

                loadFragments(new HelpFragmentContainer(mHomeScreenTabLayout));

                break;

            case R.id.linearSettings:

                loadFragments(new SettingsFragmentContainer(mHomeScreenTabLayout));

                break;
            case R.id.linearAdmin:

                loadFragments(new AdminFragment(mHomeScreenTabLayout));

                break;
        }

    }

    private void loadFragments(Fragment loadingFragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_fragment_container, loadingFragment ).commit();
        transaction.addToBackStack(null);

    }

    private void updateUI(boolean isUserIsActive) {

        if (!isUserIsActive) {
            //SET VERIFIED MSG IF NOT ACTIVE USER
            txtStatus.setText(getResources().getString(R.string.strStatusPendingVerification));
            txtStatusMsg.setText(getResources().getString(R.string.strPendingVerificationMsg));
            txtStatus.setTextColor(getResources().getColor(R.color.colorCuratorAmber));


            txtAccount.setTextColor(getResources().getColor(R.color.colorInActiveGrey));
            txtMySavedItems.setTextColor(getResources().getColor(R.color.colorInActiveGrey));
            txtSettings.setTextColor(getResources().getColor(R.color.colorInActiveGrey));
            txtHelp.setTextColor(getResources().getColor(R.color.colorInActiveGrey));
            txtAdmin.setTextColor(getResources().getColor(R.color.colorInActiveGrey));

            viewAccount.setBackgroundColor(getResources().getColor(R.color.colorInActiveGrey));
            viewSavedItems.setBackgroundColor(getResources().getColor(R.color.colorInActiveGrey));
            viewSettings.setBackgroundColor(getResources().getColor(R.color.colorInActiveGrey));
            viewHelp.setBackgroundColor(getResources().getColor(R.color.colorInActiveGrey));
            viewAdmin.setBackgroundColor(getResources().getColor(R.color.colorInActiveGrey));
            viewBottomDefault.setBackgroundColor(getResources().getColor(R.color.colorInActiveGrey));

            mHomeScreenTabLayout.tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorInActiveGrey), PorterDuff.Mode.SRC_IN);
            mHomeScreenTabLayout.tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorInActiveGrey), PorterDuff.Mode.SRC_IN);
            mHomeScreenTabLayout.tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorInActiveGrey), PorterDuff.Mode.SRC_IN);
            mHomeScreenTabLayout.tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
            mHomeScreenTabLayout.tabLayout.setTabTextColors(getResources().getColor(R.color.colorInActiveGrey) , getResources().getColor(R.color.colorNewAppGreen));


        } else if (isUserIsActive) {
            //SET VERIFIED MSG IF ACTIVE USER
            txtStatus.setText(getResources().getString(R.string.strVerified));
            txtStatusMsg.setText(getResources().getString(R.string.strThankYouForEnrolingPlatform));
            txtStatus.setTextColor(getResources().getColor(R.color.colorNewAppGreen));


            //SET USERNAME IF USER IS ACTIVE
            txtUsername.setText(getResources().getString(R.string.strUser) + " : "+strUsername);


            txtAccount.setTextColor(getResources().getColor(R.color.colorBlack));
            txtMySavedItems.setTextColor(getResources().getColor(R.color.colorBlack));
            txtSettings.setTextColor(getResources().getColor(R.color.colorBlack));
            txtHelp.setTextColor(getResources().getColor(R.color.colorBlack));
            txtAdmin.setTextColor(getResources().getColor(R.color.colorBlack));

            viewAccount.setBackgroundColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
            viewSavedItems.setBackgroundColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
            viewSettings.setBackgroundColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
            viewHelp.setBackgroundColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
            viewAdmin.setBackgroundColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
            viewBottomDefault.setBackgroundColor(getResources().getColor(R.color.colorBottomBarActiveGrey));


        }

        //HIDE SHOW ADMIN DETAILS
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN)){
            linearAdmin.setVisibility(View.VISIBLE);
        }else{
            linearAdmin.setVisibility(View.INVISIBLE);
        }


    }


}