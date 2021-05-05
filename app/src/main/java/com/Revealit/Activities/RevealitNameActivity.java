package com.Revealit.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Trace;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.android.youtube.player.YouTubeBaseActivity;

public class RevealitNameActivity extends YouTubeBaseActivity implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "RegistrationActivity";


    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView imgGoBack;
    private EditText edtUsername;
    private TextView txtGetNextDisabled,txtNextEnabled;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_revealit);


        setIds();
        setOnclick();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setIds() {

        mActivity = RevealitNameActivity.this;
        mContext = RevealitNameActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgGoBack =(ImageView)findViewById(R.id.imgGoBack);

        edtUsername = (EditText)findViewById(R.id.edtUsername);

        txtGetNextDisabled =(TextView)findViewById(R.id.txtGetNextDisabled);
        txtNextEnabled =(TextView)findViewById(R.id.txtNextEnabled);

        //SET USERNAME FROM EMAIL
        String strUserEMail = mSessionManager.getPreference(Constants.PROTON_EMAIL);
        String[] parts = strUserEMail.split("@");
        edtUsername.setText(""+parts[0]);



        edtUsername.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if (s.toString().length() == 0){
                    edtUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
                    txtGetNextDisabled.setVisibility(View.VISIBLE);
                    txtNextEnabled.setVisibility(View.GONE);
                }else {
                    edtUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
                    txtGetNextDisabled.setVisibility(View.GONE);
                    txtNextEnabled.setVisibility(View.VISIBLE);
                }

            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });


    }


    private void setOnclick() {

        imgGoBack.setOnClickListener(this);
        txtNextEnabled.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.imgGoBack:

                finish();

                break;
            case R.id.txtNextEnabled:



                break;
        }
    }


}

