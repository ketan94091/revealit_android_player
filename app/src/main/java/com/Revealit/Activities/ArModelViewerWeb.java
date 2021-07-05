package com.Revealit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Adapter.ArModelColorListAdapter;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.Interfaces.ColorSelectionForARClicks;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class ArModelViewerWeb extends AppCompatActivity implements View.OnClickListener, ColorSelectionForARClicks {

    private Handler handler;
    private ArModelViewerWeb mActivity;
    private ArModelViewerWeb mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private WebView webView;
    private ImageView imgBackArrow;
    private TextView txtChange,txtLoadAR;
    private ProgressBar progressbar;
    private boolean isLoad =false;
    private String strLoadingURL ="";
    private int displayHeight =0, displayWidth = 0 ;
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
    private void onClicks() {
        imgCancel.setOnClickListener(this);
        imgARview.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {


        switch (mView.getId()) {


           /* case R.id.txtChange:

                if(isLoad){

                    strLoadingURL = "https://modelviewer.dev/shared-assets/models/shishkebab.glb";
                    loadWebview(strLoadingURL);
                    isLoad =false;
                }else {
                    strLoadingURL ="https://modelviewer.dev/shared-assets/models/Astronaut.glb";
                    loadWebview(strLoadingURL);
                    isLoad = true;
                }



                break;*/
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

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        displayHeight = (metrics.heightPixels - 200);
        displayWidth = metrics.widthPixels;



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


    private void loadWebview(String strUrls) {

        //PROGRESSBAR VISIBLE
        progressbar.setVisibility(View.VISIBLE);

        String string = "<html>\n" +
                "    <head>\n" +
                "        <title>&lt;model-viewer&gt; Augmented Reality</title>\n" +
                "        <meta charset=\"utf-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, user-scalable=no,\n" +
                "        minimum-scale=1.0, maximum-scale=1.0\">\n" +
                "        \n" +
                "      \n" +
                "        <!-- \uD83D\uDC81 Include both scripts below to support all browsers! -->\n" +
                "      \n" +
                "        <!-- Loads <model-viewer> for modern browsers: -->\n" +
                "        <script type=\"module\" src=\"https://unpkg.com/@google/model-viewer/dist/model-viewer.js\">\n" +
                "        </script>\n" +
                "      \n" +
                "        <!-- Loads <model-viewer> for old browsers like IE11: -->\n" +
                "        <script nomodule=\"\" src=\"https://unpkg.com/@google/model-viewer/dist/model-viewer-legacy.js\">\n" +
                "        </script>\n" +
                "      \n" +
                "        <!-- The following libraries and polyfills are recommended to maximize browser support -->\n" +
                "        <!-- NOTE: you must adjust the paths as appropriate for your project -->\n" +
                "            \n" +
                "        <!-- \uD83D\uDEA8 REQUIRED: Web Components polyfill to support Edge and Firefox < 63 -->\n" +
                "        <script src=\"https://unpkg.com/@webcomponents/webcomponentsjs@2.1.3/webcomponents-loader.js\"></script>\n" +
                "      \n" +
                "        <!-- \uD83D\uDC81 OPTIONAL: Intersection Observer polyfill for better performance in Safari and IE11 -->\n" +
                "        <script src=\"https://unpkg.com/intersection-observer@0.5.1/intersection-observer.js\"></script>\n" +
                "      \n" +
                "        <!-- \uD83D\uDC81 OPTIONAL: Resize Observer polyfill improves resize behavior in non-Chrome browsers -->\n" +
                "        <script src=\"https://unpkg.com/resize-observer-polyfill@1.5.1/dist/ResizeObserver.js\"></script>\n" +
                "      \n" +
                "        <!-- \uD83D\uDC81 OPTIONAL: Fullscreen polyfill is required for experimental AR features in Canary -->\n" +
                "        <!--<script src=\"https://unpkg.com/fullscreen-polyfill@1.0.2/dist/fullscreen.polyfill.js\"></script>-->\n" +
                "      \n" +
                "        <!-- \uD83D\uDC81 OPTIONAL: Include prismatic.js for Magic Leap support -->\n" +
                "        <!--<script src=\"https://unpkg.com/@magicleap/prismatic@0.18.2/prismatic.min.js\"></script>-->\n" +
                "      <style type=\"text/css\">\n" +
                "        body {\n" +
                "  margin: 1em;\n" +
                "  padding: 0;\n" +
                "  font-family: Google Sans, Noto, Roboto, Helvetica Neue, sans-serif;\n" +
                "  color: #244376;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "#card {\n" +
                "  margin: 3em auto;\n" +
                "  display: flex;\n" +
                "  flex-direction: column;\n" +
                "  max-width: 600px;\n" +
                "  border-radius: 6px;\n" +
                "  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.25);\n" +
                "  overflow: hidden;\n" +
                "}\n" +
                "\n" +
                "model-viewer {\n" +
                "  width: 100%;\n" +
                "  height: 450px;\n" +
                "}\n" +
                "\n" +
                ".attribution {\n" +
                "  display: flex;\n" +
                "  flex-direction: row;\n" +
                "  margin: 1em;\n" +
                "}\n" +
                "\n" +
                ".attribution h1 {\n" +
                "  margin: 0 0 0.25em;\n" +
                "}\n" +
                "\n" +
                ".attribution img {\n" +
                "  opacity: 0.5;\n" +
                "  height: 2em;\n" +
                "}\n" +
                "\n" +
                ".attribution .cc {\n" +
                "  flex-shrink: 0;\n" +
                "  text-decoration: none;\n" +
                "}\n" +
                "\n" +
                "footer {\n" +
                "  display: flex;\n" +
                "  flex-direction: column;\n" +
                "  max-width: 600px;\n" +
                "  margin: auto;\n" +
                "  text-align: center;\n" +
                "  font-style: italic;\n" +
                "  line-height: 1.5em;\n" +
                "}\n" +
                "      </style>\n" +
                "      </head>\n" +
                "    <body>\n" +
                "        <model-viewer src=\""+strUrls+"\"+  auto-rotate=\"\" camera-controls=\"\" shadow-intensity=\"1\" alt=\"A 3D model of a rocket\" background-color=\"#70BCD1\" ar-status=\"not-presenting\">\n" +
                "                        \n" +
                "        </model-viewer>\n" +
                "      \n" +
                "      \n" +
                "    </body>\n" +
                "</html>\n";
        webView.loadDataWithBaseURL(null,string,"text/html", "utf-8", null);


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