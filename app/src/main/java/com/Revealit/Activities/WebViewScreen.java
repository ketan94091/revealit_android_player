package com.Revealit.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class WebViewScreen extends AppCompatActivity implements View.OnClickListener {

    private Handler handler;
    private WebViewScreen mActivity;
    private WebViewScreen mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private WebView webView;
    private ImageView imgBackArrow;
    private TextView txtTitle;
    private ProgressBar progressbar;

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

        webView = (WebView) findViewById(R.id.webView);

        imgBackArrow = (ImageView) findViewById(R.id.imgBackArrow);

        txtTitle = (TextView) findViewById(R.id.txtTitle);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        WebSettings webSettings = webView.getSettings();
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView .loadUrl(getIntent().getStringExtra(Constants.RESEARCH_URL));
        //webView .loadUrl("http://primehomedirect.com");

        //SET TITLE
        txtTitle.setText(getIntent().getStringExtra(Constants.RESEARCH_URL_SPONSER));
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {

                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

    }

    private void onClicks() {

        imgBackArrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {


        switch (mView.getId()) {


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