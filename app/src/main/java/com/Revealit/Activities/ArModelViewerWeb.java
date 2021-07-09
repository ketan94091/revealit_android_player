package com.Revealit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Adapter.ArModelColorListAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.Interfaces.ColorSelectionForARClicks;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class ArModelViewerWeb extends AppCompatActivity implements View.OnClickListener, ColorSelectionForARClicks {

    private ArModelViewerWeb mActivity;
    private ArModelViewerWeb mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private WebView webView;
    private ProgressBar progressbar;
    private String strLoadingURL ="";
    private ImageView imgARview,imgCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ar_model_viewer_view);


        setIds();
        onClicks();

    }
    private void setIds() {


        mActivity = ArModelViewerWeb.this;
        mContext = ArModelViewerWeb.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        webView = (WebView) findViewById(R.id.webView);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);


        imgCancel =(ImageView) findViewById(R.id.imgCancel);
        imgARview =(ImageView) findViewById(R.id.imgARview);

        WebSettings webSettings = webView.getSettings();
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //webView.pauseTimers();


        RecyclerView recycleChooseColors = (RecyclerView) findViewById(R.id.recycleChooseColors);

        String[] strings = new String[5555555];
        String[] stringURL = new String[5555555];

        if(getIntent().getIntExtra("ID" ,0) == 1) {
            strings = new String[]{"#579229", "#000000", "#f3f2ed"};
            stringURL = new String[]{"https://mturk.sgp1.cdn.digitaloceanspaces.com/0/1c.BoosButcherBlock_Basil_Final.glb",
                    "https://mturk.sgp1.cdn.digitaloceanspaces.com/0/1a.BoosButchersBlock_Black_Final.glb",
                    "https://mturk.sgp1.cdn.digitaloceanspaces.com/0/1b.BoosButcherBlock_White_Final.glb"};
        }else if(getIntent().getIntExtra("ID" ,0) == 2){
            strings = new String[]{"#f3f2ed", "#BB371A"};
            stringURL = new String[]{"https://mturk.sgp1.cdn.digitaloceanspaces.com/0/2_KitchenAid_Mixer_Pearl.glb",
                    "https://mturk.sgp1.cdn.digitaloceanspaces.com/0/2a.KitchenAid_StandMixer_CARED_680569095664.glb"};
        }else {
            strings = new String[]{"#579229", "#000000", "#f3f2ed"};
            stringURL = new String[]{"https://mturk.sgp1.cdn.digitaloceanspaces.com/0/1c.BoosButcherBlock_Basil_Final.glb",
                    "https://mturk.sgp1.cdn.digitaloceanspaces.com/0/1a.BoosButchersBlock_Black_Final.glb",
                    "https://mturk.sgp1.cdn.digitaloceanspaces.com/0/1b.BoosButcherBlock_White_Final.glb"};
        }

        //AR MODEL URL
        mSessionManager.updatePreferenceString(Constants.AR_MODEL_URL, stringURL[0]);
        loadWebview(mSessionManager.getPreference(Constants.AR_MODEL_URL));


        ArModelColorListAdapter mArModelColorListAdapter = new ArModelColorListAdapter(mSessionManager ,mContext, ArModelViewerWeb.this, strings, strLoadingURL , stringURL);
        recycleChooseColors.setAdapter(mArModelColorListAdapter);


    }
    private void onClicks() {
        imgCancel.setOnClickListener(this);
        imgARview.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {


        switch (mView.getId()) {

            case R.id.imgARview:

                Intent mARviewIntent = new Intent(mActivity, ARviewActivity.class);
                mARviewIntent.putExtra(Constants.AR_VIEW_URL, mSessionManager.getPreference(Constants.AR_MODEL_URL));
                mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_NAME, "");
                mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_URL, "");
                startActivity(mARviewIntent);

                break;

            case R.id.imgCancel:

               finish();

                break;
        }
    }




    public void loadWebview(String strUrls) {

        //PROGRESSBAR VISIBLE
        progressbar.setVisibility(View.VISIBLE);

        webView.loadDataWithBaseURL(null, CommonMethods.getHtMLdataForARmodelViewer(strUrls),"text/html", "utf-8", null);


        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {

                progressbar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void selectedColorURL(String strURL) {

        loadWebview(strURL);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}