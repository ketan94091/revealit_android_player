package com.Revealit.Fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.Revealit.Activities.ExoPlayerActivity;
import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.Activities.QrCodeScannerActivity;
import com.Revealit.BuildConfig;
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
    private View mView,  viewAdmin,viewHelp, viewSettings, viewSavedItems, viewAccount;
    private HomeScreenTabLayout mHomeScreenTabLayout;
    private TextView txtUsername, txtAdmin,txtHelp, txtSettings, txtMySavedItems, txtAccount, txtStatusMsg, txtStatus, txtCopyToClibBoard, txtMsgCopy;
    private String strUsername, strCopymsg;
    private boolean isUserIsActive =false;
    private LinearLayout linearAccount,linearSettings,linearSavedItems,linearUserStatus,linearAdmin,linearHelp;
    private ImageView imgLogo,imgScanQRcode;

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


        linearAccount=(LinearLayout)mView.findViewById(R.id.linearAccount);
        linearSavedItems=(LinearLayout)mView.findViewById(R.id.linearSavedItems);
        linearSettings=(LinearLayout)mView.findViewById(R.id.linearSettings);
        linearHelp=(LinearLayout)mView.findViewById(R.id.linearHelp);
        linearAdmin=(LinearLayout)mView.findViewById(R.id.linearAdmin);
        linearUserStatus=(LinearLayout)mView.findViewById(R.id.linearUserStatus);

        imgScanQRcode =(ImageView)mView.findViewById(R.id.imgScanQRcode);
        imgLogo =(ImageView)mView.findViewById(R.id.imgLogo);

        //GET USER STATUS
        //TRUE = USER IS ACTIVE AND VERIFIED
        //FLASE= USER IS NOT ACTIVE AND NOT YET VERIFIED
        isUserIsActive = mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_ACTIVE);

        //GET INTENT DATA
        strUsername = mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME);
        strCopymsg = mSessionManager.getPreference(Constants.KEY_INVITE_MSG);

        //SET INVITE MSG WHICH CAME FROM INVITE SETTING API

        if(strCopymsg.contains("xxxx")){
            txtMsgCopy.setText(strCopymsg.replace("xxxx",strUsername));
        }else{
            txtMsgCopy.setText(strCopymsg.replace("XXXX",strUsername));
        }


        //UPDATE UI BASED ON USER STATUS
        updateUI(isUserIsActive);

        //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
        txtAdmin.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);


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
            imgScanQRcode.setOnClickListener(this);
            imgLogo.setOnClickListener(this);
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
                ClipData clip = ClipData.newPlainText(getString(R.string.strUsername), mSessionManager.getPreference(Constants.KEY_INVITE_COPY_CLIPBOARD).replace("xxxx",strUsername));
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
            case R.id.imgScanQRcode:

                Intent mIntentQRCodeActivity = new Intent(mActivity, QrCodeScannerActivity.class);
                mActivity.startActivity(mIntentQRCodeActivity);


                break;
            case R.id.imgLogo:

                Intent mIntent = new Intent(mActivity, ExoPlayerActivity.class);
                mIntent.putExtra(Constants.MEDIA_URL, Constants.EDUCATION_VIDEO_URL);
                mIntent.putExtra(Constants.MEDIA_ID, "0");
                mIntent.putExtra(Constants.VIDEO_NAME,Constants.EDUCATION_VIDEO_TITLE);
                mIntent.putExtra(Constants.VIDEO_SEEK_TO,"0");
                mIntent.putExtra(Constants.IS_VIDEO_SEEK, false);
                mActivity.startActivity(mIntent);

                break;
        }

    }

    private void loadFragments(Fragment loadingFragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_fragment_container, loadingFragment ).commit();
        transaction.addToBackStack(loadingFragment.getTag());

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


        } else if (isUserIsActive) {
            //SET VERIFIED MSG IF ACTIVE USER
            txtStatus.setText(getResources().getString(R.string.strVerified));
            txtStatusMsg.setText(getResources().getString(R.string.strThankYouForEnrolingPlatform));
            txtStatus.setTextColor(getResources().getColor(R.color.colorNewAppGreen));

            //HIDE USER STATUS AS STATUS IS ALREADY VERIFIED
            linearUserStatus.setVisibility(View.GONE);

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


        }

        //HIDE SHOW ADMIN DETAILS
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN)){
            linearAdmin.setVisibility(View.VISIBLE);
        }else{
            linearAdmin.setVisibility(View.INVISIBLE);
        }




    }


}
