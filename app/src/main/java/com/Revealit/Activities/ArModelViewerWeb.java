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
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Adapter.ArModelColorListAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.Interfaces.ColorSelectionForARClicks;
import com.Revealit.ModelClasses.GetMultiColorGLB;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArModelViewerWeb extends AppCompatActivity implements View.OnClickListener, ColorSelectionForARClicks {

    private ArModelViewerWeb mActivity;
    private ArModelViewerWeb mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private WebView webView;
    private ProgressBar progressbar;
    private String strLoadingURL = "";
    private ImageView imgARview, imgCancel;
    private RecyclerView recycleChooseColors;
    private RelativeLayout relatvieMainLayout;

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


        imgCancel = (ImageView) findViewById(R.id.imgCancel);
        imgARview = (ImageView) findViewById(R.id.imgARview);

        recycleChooseColors = (RecyclerView) findViewById(R.id.recycleChooseColors);

        relatvieMainLayout= (RelativeLayout)findViewById(R.id.relatvieMainLayout);

        WebSettings webSettings = webView.getSettings();
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //webView.pauseTimers();

        //API CALL TO GET MULTI COLOR GLBS
        callGetMultiColorGLBs(getIntent().getIntExtra(Constants.AR_MODEL_ID, 0));

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
                mARviewIntent.putExtra(Constants.AR_VIEW_URL,strLoadingURL);
                mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_NAME, getIntent().getStringExtra(Constants.AR_VIEW_MODEL_NAME));
                mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_URL, getIntent().getStringExtra(Constants.AR_VIEW_MODEL_URL));
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

        webView.loadDataWithBaseURL(null, CommonMethods.getHtMLdataForARmodelViewer(strUrls), "text/html", "utf-8", null);


        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {

                progressbar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void selectedColorURL(String strURL) {

        strLoadingURL =  strURL;

        loadWebview(strURL);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void callGetMultiColorGLBs(int intARmodelID) {

        //WAITING DIALOG
        //DISPLAY DIALOG
        CommonMethods.showDialog(mContext);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request original = chain.request();

                okhttp3.Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + " " + mSessionManager.getPreference(Constants.AUTH_TOKEN))
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_END_POINTS_MOBILE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();


        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

        Call<GetMultiColorGLB> call = patchService1.getMulticolorGlbs(Constants.API_GET_MULTICOLOR_GLBS + "" + intARmodelID);

        call.enqueue(new Callback<GetMultiColorGLB>() {
            @Override
            public void onResponse(Call<GetMultiColorGLB> call, Response<GetMultiColorGLB> response) {

                CommonMethods.printLogE("Response @ callGetMultiColorGLBs: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callGetMultiColorGLBs: ", "" + response.code());



                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    if(response.body().getData().size() != 0){

                        //ASSIGN VALUE TO LOADING URL VERIABLE
                        strLoadingURL = response.body().getData().get(0).getGlb_filename();

                        //LOAD FIRST GLB
                        loadWebview(response.body().getData().get(0).getGlb_filename());

                        //LOAD COLOR LIST
                        ArModelColorListAdapter mArModelColorListAdapter = new ArModelColorListAdapter(mSessionManager, mContext, ArModelViewerWeb.this , response.body().getData());
                        recycleChooseColors.setAdapter(mArModelColorListAdapter);

                        //VISIBLE UI
                        relatvieMainLayout.setVisibility(View.VISIBLE);

                        //DISPLAY DIALOG
                        CommonMethods.closeDialog();

                    }else {

                        //DISPLAY DIALOG
                        CommonMethods.closeDialog();

                        //IF NO DATA FINISHED ACTIVITY
                        finish();
                    }

                }
            }

            @Override
            public void onFailure(Call<GetMultiColorGLB> call, Throwable t) {

                //DISPLAY DIALOG
                CommonMethods.closeDialog();

                //IF NO DATA FINISHED ACTIVITY
                finish();

            }
        });


    }

}