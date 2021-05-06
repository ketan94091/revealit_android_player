package com.Revealit.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Dimension;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.DotsLocationsModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class VideoViewActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "VideoViewActivity";

    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private VideoView mVideoView;
    private ImageView imgBackArrow, imgVoulume, imgPlayPause;
    private ProgressBar mProgress;
    private TextView txtCurrentTime, txtTitle,txtTotalTime;
    private SeekBar ckVolumeBar, ckVideoProgress;
    private boolean isPlayButtonClicked = false;
    private int intTotalDuration, intCurrentPosition;
    private LinearLayout linearControls;
    private AudioManager audioManager;
    private RelativeLayout relativeControllerMain, relativeHeaderView, relativeHeaderFooter;
    private FrameLayout frameOverlay;

    private ImageView imgDynamicCoordinateView;
    private int minusHight, minusWidth;
    private int heightVideo, widthVideo;
    private int videoPauseTime = 0;
    private String strMediaURL = "", strMediaID = "", strMediaTitle="";
    private TextView txtVendorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_view_new);

        setIds();
        setOnClicks();

    }

    private void setIds() {

        mActivity = VideoViewActivity.this;
        mContext = VideoViewActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        //GET MEDIA ID
        strMediaID = getIntent().getStringExtra(Constants.MEDIA_ID);
        strMediaURL = getIntent().getStringExtra(Constants.MEDIA_URL);
        strMediaTitle = getIntent().getStringExtra(Constants.VIDEO_NAME);

        mVideoView = (VideoView) findViewById(R.id.mVideoView);

        imgPlayPause = (ImageView) findViewById(R.id.imgPlayPause);
        imgVoulume = (ImageView) findViewById(R.id.imgVoulume);
        imgBackArrow = (ImageView) findViewById(R.id.imgBackArrow);

        mProgress = (ProgressBar) findViewById(R.id.mProgress);

        txtCurrentTime = (TextView) findViewById(R.id.txtCurrentTime);
        txtTotalTime = (TextView) findViewById(R.id.txtTotalTime);
        txtTitle = (TextView) findViewById(R.id.txtTitle);

        ckVideoProgress = (SeekBar) findViewById(R.id.ckVideoProgress);
        ckVolumeBar = (SeekBar) findViewById(R.id.ckVolumeBar);

        linearControls = (LinearLayout) findViewById(R.id.linearControls);

        relativeHeaderFooter = (RelativeLayout) findViewById(R.id.relativeHeaderFooter);
        relativeHeaderView = (RelativeLayout) findViewById(R.id.relativeHeaderView);
        relativeControllerMain = (RelativeLayout) findViewById(R.id.relativeControllerMain);

        frameOverlay = (FrameLayout) findViewById(R.id.frameOverlay);

        txtTitle.setText(strMediaTitle.replace("\u0027", "'"));

        //SHOW MEDIA TITLE
       /* if(strMediaTitle.contains("\u0027s")) {
            txtTitle.setText(strMediaTitle.replace("\u0027s", "'s"));
        }else{
            txtTitle.setText(strMediaTitle);
        }*/

        mVideoView.setVideoPath(strMediaURL);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                //GET VIDEO HEIGHT AND WIDTH
                widthVideo = mp.getVideoWidth();
                heightVideo = mp.getVideoHeight();

                //SET OVERLAY LAYOUT SAME AS VIDEO VIEW
                frameOverlay.getLayoutParams().height = heightVideo;
                frameOverlay.getLayoutParams().width = widthVideo;
                frameOverlay.requestLayout();


                //HIDE PROGRESSBAR
                mProgress.setVisibility(View.GONE);

                //SET VIDEO SEEKBAR
                setVideoProgress();

                //START VISEO WHEN IT PREPARE
                mVideoView.start();

                //SET VIDEO OVERLAY TOUCH
                setVideoViewOnTOuch();

            }
        });

        audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        ckVolumeBar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        ckVolumeBar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));


        ckVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }
        });


    }

    private void setOnClicks() {

        imgPlayPause.setOnClickListener(this);
        imgVoulume.setOnClickListener(this);
        imgBackArrow.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {

            case R.id.imgPlayPause:

                if (mVideoView.isPlaying()) {

                    //REMOVE ALL VIEWS
                    frameOverlay.removeAllViews();

                    //PAUSE VIDOE
                    mVideoView.pause();

                    //CHANGE PLAY ICON
                    imgPlayPause.setImageResource(R.drawable.ic_media_play);


                    //GET TIME DURATION IN SECONDS
                    String[] units = txtCurrentTime.getText().toString().split(":"); //will break the string up into an array
                    int minutes = Integer.parseInt(units[0]); //first element
                    int seconds = Integer.parseInt(units[1]); //second element
                    int duration = 60 * minutes + seconds;


                    //API CALL GET DOTS LOCATION
                    callLocationApi(duration);


                } else {
                    mVideoView.start();
                    frameOverlay.setVisibility(View.GONE);
                    relativeHeaderView.setVisibility(View.VISIBLE);
                    imgPlayPause.setImageResource(R.drawable.ic_media_pause);

                    setVideoViewOnTOuch();
                }

                break;

            case R.id.imgVoulume:

                if (linearControls.getVisibility() == View.VISIBLE) {
                    linearControls.setVisibility(View.GONE);
                    ckVolumeBar.setVisibility(View.VISIBLE);
                } else {
                    linearControls.setVisibility(View.VISIBLE);
                    ckVolumeBar.setVisibility(View.GONE);
                }

                break;
            case R.id.imgBackArrow:

                mVideoView.stopPlayback();
                finish();


                break;
        }

    }

    private void callLocationApi(int duration) {

        CommonMethods.printLogE("Response @ callLocationApi TIME IN SECOND : ", "" + duration);

        //DISPLAY DIALOG
        CommonMethods.showDialog(mContext);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                okhttp3.Request requestOriginal = chain.request();

                okhttp3.Request request = requestOriginal.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + " " + mSessionManager.getPreference(Constants.AUTH_TOKEN))
                        .method(requestOriginal.method(), requestOriginal.body())
                        .build();


                return chain.proceed(request);
            }
        });
        final OkHttpClient httpClient1 = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_END_POINTS)
                .client(httpClient1.newBuilder().connectTimeout(10, TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).build())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient1)
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

        Call<DotsLocationsModel> call = patchService1.getVideoDotsLocation(strMediaID, String.valueOf(duration));

        call.enqueue(new Callback<DotsLocationsModel>() {
            @Override
            public void onResponse(Call<DotsLocationsModel> call, retrofit2.Response<DotsLocationsModel> response) {


                CommonMethods.printLogE("Response @ callLocationApi : ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callLocationApi : ", "" + response.code());

                if (response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ callLocationApi : ", "" + gson.toJson(response.body()));


                    //DISPLAY COORDINATES FOR DOTS
                    displayCoordinates(response.body().getData());


                } else if (response.code() == Constants.API_USER_UNAUTHORIZED) {

                    //CLOSE DIALOG
                    CommonMethods.closeDialog();

                    Intent mLoginIntent = new Intent(mActivity, LoginActivityActivity.class);
                    mActivity.startActivity(mLoginIntent);
                    mActivity.finish();

                } else {

                    //CLOSE DIALOG
                    CommonMethods.closeDialog();

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                    mActivity.finish();
                }

            }

            @Override
            public void onFailure(Call<DotsLocationsModel> call, Throwable t) {

                //CLOSE DIALOG
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                mActivity.finish();

            }
        });


    }

    private void displayCoordinates(List<DotsLocationsModel.Datum> data) {


        for (int i = 0; i < data.size(); i++) {

            //ADD DYNAMIC IMAGE VIEW
            imgDynamicCoordinateView = new ImageView(this);
            imgDynamicCoordinateView.setImageResource(R.drawable.dots);
            imgDynamicCoordinateView.setTag(i);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(45, 45);
            layoutParams.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))) + 30);
            layoutParams.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))) + 10);
            ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(""+data.get(i).getItemDotColor())));
            frameOverlay.addView(imgDynamicCoordinateView, layoutParams);

            //ADD DYNAMIC TEXT VIEW FOR VENDOR AND ITEM NAME
            /*txtVendorName = new TextView(this);
            txtVendorName.setText(" "+data.get(i).getItemName() +" \n "+data.get(i).getVendor());
           // txtVendorName.setTextColor(Color.parseColor(""+data.get(i).getItemLabelColor()));
            txtVendorName.setTextColor(Color.parseColor("#ffffff"));
            txtVendorName.setTextSize(9);
            txtVendorName.setTag(i);
            FrameLayout.LayoutParams layoutParamsVendor = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParamsVendor.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))) + 80);
            layoutParamsVendor.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))) + 10);
            frameOverlay.addView(txtVendorName, layoutParamsVendor);*/


            imgDynamicCoordinateView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View mView) {

                    // Toast.makeText(mContext ,""+ items[(int) mView.getTag()],Toast.LENGTH_LONG).show();

                    AlertDialog alertDialog = new AlertDialog.Builder(VideoViewActivity.this).create(); //Read Update
                    alertDialog.setTitle("Revealit");
                    //alertDialog.setMessage("You Clicked On Dot : "+items[(int) mView.getTag()]);
                    alertDialog.setMessage("You Clicked On Dot : " + ((int) mView.getTag() + 1));

                    alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();

                }
            });

           /* txtVendorName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View mView) {

                    // Toast.makeText(mContext ,""+ items[(int) mView.getTag()],Toast.LENGTH_LONG).show();

                    AlertDialog alertDialog = new AlertDialog.Builder(VideoViewActivity.this).create(); //Read Update
                    alertDialog.setTitle("Revealit");
                    //alertDialog.setMessage("You Clicked On Dot : "+items[(int) mView.getTag()]);
                    alertDialog.setMessage("You Clicked On Dot : " + ((int) mView.getTag() + 1));

                    alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();

                }
            });*/

        }


        frameOverlay.invalidate();


        frameOverlay.setVisibility(View.VISIBLE);

        setOverLayTouch();

        //CLOSE DIALOG
        CommonMethods.closeDialog();


    }

    private void setOverLayTouch() {
        frameOverlay.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {

                if ((event.getAction() == MotionEvent.ACTION_DOWN)) {

                    if (relativeHeaderFooter.getVisibility() == View.VISIBLE) {
                        relativeHeaderFooter.setVisibility(View.GONE);


                    } else {
                        relativeHeaderFooter.setVisibility(View.VISIBLE);

                    }

                }
                return true;
            }
        });
    }

    private void setVideoViewOnTOuch() {
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_DOWN)) {

                    if (relativeHeaderFooter.getVisibility() == View.VISIBLE) {
                        relativeHeaderFooter.setVisibility(View.GONE);

                    } else {
                        relativeHeaderFooter.setVisibility(View.VISIBLE);

                    }


                }
                return true;
            }
        });
    }

    public void setVideoProgress() {
        //get the video duration
        intCurrentPosition = mVideoView.getCurrentPosition();
        intTotalDuration = mVideoView.getDuration();

        //display video duration
        txtCurrentTime.setText(timeConversion((long) intCurrentPosition));
        txtTotalTime.setText(timeConversion((long) intTotalDuration));
        ckVideoProgress.setMax((int) intTotalDuration);
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    intCurrentPosition = mVideoView.getCurrentPosition();
                    txtCurrentTime.setText(timeConversion((long) intCurrentPosition));
                    ckVideoProgress.setProgress((int) intCurrentPosition);
                    handler.postDelayed(this, 1000);
                } catch (IllegalStateException ed) {
                    ed.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1000);


        ckVideoProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                intCurrentPosition = seekBar.getProgress();
                mVideoView.seekTo((int) intCurrentPosition);
            }
        });

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            relativeHeaderFooter.setVisibility(View.VISIBLE);
        } else {
            relativeHeaderFooter.setVisibility(View.GONE);

        }
    }

    public String timeConversion(long value) {
        String videoTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;


        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            videoTime = String.format("%02d:%02d", mns, scs);
        }
        return videoTime;
    }

    public float pxToDp(float px) {
        float density = mContext.getResources().getDisplayMetrics().density;
        float dp = px / density;
        return dp;
    }

    public static float dpToPx(float dp) {
        return (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public float getScreenResolutionX(Context context, float x) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        /*video image device
        720(video height) = 1250(vido width)
        720(device height) =  ?

        1250 * 1080 / 720 = 1875;*/


        // height = device screen height
        //heightVideo = height of provided video
        //x = x Axis coordinate in terms of video height
        //xAxis = new X Axis in terms of device resolution matrix height

        float xAxis = (x * height) / (heightVideo);


        /*Log.e("VIDEO HEIGHT : ",""+heightVideo);
        Log.e("VIDEO WIDTH : ",""+widthVideo);
        Log.e("DEVICE HEIGHT : ",""+height);
        Log.e("DEVICE WIDTH : ",""+width);
        Log.e("OLD X : ",""+x);
        Log.e("NEW X : ",""+xAxis);*/


        return xAxis;
    }

    public float getScreenResolutionY(Context context, float y) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        // width = device screen with
        //widthVideo = width of provided video
        //y = y Axis coordinate in terms of video with
        //yAxis = new Y Axis in terms of device resolution matrix width

        float yAxis = (y * width) / widthVideo;
        /*Log.e("OLD Y : ",""+y);
        Log.e("NEW Y : ",""+yAxis);*/

        return yAxis;
    }

}