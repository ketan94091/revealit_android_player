package com.Revealit.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WebViewScreen extends AppCompatActivity implements View.OnClickListener {

    private Handler handler;
    private WebViewScreen mActivity;
    private WebViewScreen mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private WebView webView;
    private ImageView imgBackArrow;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);


        setIds();
        onClicks();

    }

    private void setIds() {


        mActivity = WebViewScreen.this;
        mContext = WebViewScreen.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        webView = (WebView)findViewById(R.id.webView);
        imgBackArrow = (ImageView)findViewById(R.id.imgBackArrow);
        txtTitle = (TextView)findViewById(R.id.txtTitle);

        WebSettings webSettings = webView.getSettings();
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView .loadUrl(getIntent().getStringExtra(Constants.RESEARCH_URL));

        //SET TITLE
        txtTitle.setText(getIntent().getStringExtra(Constants.RESEARCH_URL_SPONSER));

    }

    private void onClicks() {

        imgBackArrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {


        switch (mView.getId()){


            case R.id.imgBackArrow:
                finish();
                break;
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}