package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class InviteAndEarnActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "InviteAndEarnActivity";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView  imgCancel, imgLogo;
    private TextView txtEarnWhileInstallingApp,txtCallForActionIvites,txtCopyToClibBoard,txtMsgCopy,txtContinueEnabled;
    private String strCallForActionMsg,strCopymsg,strUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_and_earn);
        setIds();
        setOnClicks();
    }



    private void setIds() {

        mActivity = InviteAndEarnActivity.this;
        mContext = InviteAndEarnActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        imgCancel = (ImageView) findViewById(R.id.imgCancel);

        txtContinueEnabled = (TextView) findViewById(R.id.txtContinueEnabled);
        txtMsgCopy = (TextView) findViewById(R.id.txtMsgCopy);
        txtCopyToClibBoard = (TextView) findViewById(R.id.txtCopyToClibBoard);
        txtCallForActionIvites = (TextView) findViewById(R.id.txtCallForActionIvites);
        txtEarnWhileInstallingApp = (TextView) findViewById(R.id.txtEarnWhileInstallingApp);

        //GET INTENT DATA
         strUsername = getIntent().getStringExtra(Constants.KEY_NEW_AUTH_USERNAME);
         strCopymsg = mSessionManager.getPreference(Constants.KEY_INVITE_MSG);
         strCallForActionMsg = mSessionManager.getPreference(Constants.KEY_CALL_FOR_INVITE_MSG);

         //SET CALL FOR ACTION MSG
         //txtCallForActionIvites.setText(strCallForActionMsg);

         //SET INVITE MSG WHICH CAME FROM INVITE SETTING API
        if(strCopymsg.contains("xxxx")){
            txtMsgCopy.setText(strCopymsg.replace("xxxx",strUsername));
        }else{
            txtMsgCopy.setText(strCopymsg.replace("XXXX",strUsername));
        }
        //SET INVITE MSG WHICH CAME FROM INVITE SETTING API
        if(strCopymsg.contains("xxxx")){
            txtCallForActionIvites.setText(strCallForActionMsg.replace("xxxx",strUsername));
        }else{
            txtCallForActionIvites.setText(strCallForActionMsg.replace("XXXX",strUsername));
        }

         //SET INVITE CURRENCY MSG
        txtEarnWhileInstallingApp.setText("Earn "+mSessionManager.getPreference(Constants.KEY_INVITE_CURRNCY)+" "+mSessionManager.getPreference(Constants.KEY_INVITE_CURRNCY_AMOUNT) +" in "+mSessionManager.getPreference(Constants.KEY_INVITE_CYPTO_CURRNCY)+ " every time a friend installs the revealit TV app");

    }
    private void setOnClicks() {
        imgCancel.setOnClickListener(this);
        imgLogo.setOnClickListener(this);
        txtContinueEnabled.setOnClickListener(this);
        txtCopyToClibBoard.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){
            case R.id.imgLogo:

                break;
            case R.id.txtCopyToClibBoard:

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.strUsername),mSessionManager.getPreference(Constants.KEY_INVITE_COPY_CLIPBOARD).replace("xxxx",strUsername));
                clipboard.setPrimaryClip(clip);

                //TOAST MSG
                CommonMethods.displayToast(mContext, getString(R.string.strUsernameCopied));

                break;
            case R.id.imgCancel:
                finish();
                break;
            case R.id.txtContinueEnabled:


                Intent mIntent = new Intent(InviteAndEarnActivity.this, AddRefferalAndEarnActivity.class);
                startActivity(mIntent);
                finish();

                break;
        }

    }
}