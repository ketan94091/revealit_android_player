package com.Revealit.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Dimension;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.ImageViewCompat;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.Screenshot;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.DotsLocationsModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private ImageView imgShareImage, imgShare, imgBackArrow, imgVoulume, imgPlayPause;
    private ProgressBar mProgress;
    private TextView txtCurrentTime, txtShare, txtCancel, txtTitle, txtTotalTime;
    private SeekBar ckVolumeBar, ckVideoProgress;
    private boolean isPlayButtonClicked = false;
    private int intTotalDuration, intCurrentPosition;
    private LinearLayout linearControls;
    private AudioManager audioManager;
    private RelativeLayout relativeCaptureImageWithText, relativeShareView, relativeControllerMain, relativeHeaderView, relativeHeaderFooter;
    private FrameLayout frameOverlay;

    private ImageView imgDynamicCoordinateView;
    private int minusHight, minusWidth;
    private int heightVideo, widthVideo;
    private int videoPauseTime = 0;
    private String strMediaURL = "", strMediaID = "", strMediaTitle = "";
    private TextView txtVendorName;
    private List<DotsLocationsModel.Datum> locationData = new ArrayList<>();
    private MediaMetadataRetriever mediaMetadataRetriever;
    private EditText edtTextOnCaptureImage;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };
    private Bitmap savedBitMap;
    private PopupWindow popup;

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
        imgShare = (ImageView) findViewById(R.id.imgShare);
        imgShareImage = (ImageView) findViewById(R.id.imgShareImage);

        mProgress = (ProgressBar) findViewById(R.id.mProgress);

        txtCurrentTime = (TextView) findViewById(R.id.txtCurrentTime);
        txtTotalTime = (TextView) findViewById(R.id.txtTotalTime);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtCancel = (TextView) findViewById(R.id.txtCancel);
        txtShare = (TextView) findViewById(R.id.txtShare);

        edtTextOnCaptureImage = (EditText) findViewById(R.id.edtTextOnCaptureImage);

        ckVideoProgress = (SeekBar) findViewById(R.id.ckVideoProgress);
        ckVolumeBar = (SeekBar) findViewById(R.id.ckVolumeBar);

        linearControls = (LinearLayout) findViewById(R.id.linearControls);

        relativeHeaderFooter = (RelativeLayout) findViewById(R.id.relativeHeaderFooter);
        relativeHeaderView = (RelativeLayout) findViewById(R.id.relativeHeaderView);
        relativeControllerMain = (RelativeLayout) findViewById(R.id.relativeControllerMain);
        relativeShareView = (RelativeLayout) findViewById(R.id.relativeShareView);
        relativeCaptureImageWithText = (RelativeLayout) findViewById(R.id.relativeCaptureImageWithText);

        frameOverlay = (FrameLayout) findViewById(R.id.frameOverlay);

        mVideoView.setVideoPath(strMediaURL);


        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mediaMetadataRetriever = new MediaMetadataRetriever();
                //mediaMetadataRetriever.setDataSource(strMediaURL);
                mediaMetadataRetriever.setDataSource(strMediaURL, new HashMap<String, String>());


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


        imgShareImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                edtTextOnCaptureImage.setFocusable(true);
                edtTextOnCaptureImage.setVisibility(View.VISIBLE);

                return true;


            }
        });


    }

    private void setOnClicks() {

        imgPlayPause.setOnClickListener(this);
        imgVoulume.setOnClickListener(this);
        imgBackArrow.setOnClickListener(this);
        imgShare.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        txtShare.setOnClickListener(this);
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

                    //SET SHARE BUTTON VISIBILITY ON PLAY PAUSE
                    imgShare.setVisibility(View.VISIBLE);

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

                    //ON VIDEO PLAYING SHARE VIEW SHOULD BE HIDE
                    relativeShareView.setVisibility(View.GONE);

                    //SET SHARE BUTTON VISIBILITY ON PLAY PAUSE
                    imgShare.setVisibility(View.GONE);

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
            case R.id.imgShare:

                //GET CAPTURE SCREEN HEIGHT AND WIDTH
                Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(mVideoView.getCurrentPosition() * 1000); //unit in microsecond
                int widthBitmap = bmFrame.getWidth();
                int heightBitmap = bmFrame.getHeight();

                //SET CAPTURE HEIGHT WITH TO IMAGEVIEW
                relativeCaptureImageWithText.getLayoutParams().height = heightBitmap;
                relativeCaptureImageWithText.getLayoutParams().width = widthBitmap;
                relativeCaptureImageWithText.requestLayout();

                //SET CAPTURE IMAGE
                imgShareImage.setImageBitmap(bmFrame);

                //VISIBLE VIEW
                relativeShareView.setVisibility(View.VISIBLE);

                break;
            case R.id.txtCancel:

                relativeShareView.setVisibility(View.GONE);

                //CLOSE POPUP WINDOW
                popup.dismiss();


            case R.id.txtShare:

                relativeCaptureImageWithText.setDrawingCacheEnabled(true);

                relativeCaptureImageWithText.buildDrawingCache();

                savedBitMap = relativeCaptureImageWithText.getDrawingCache();

                //OPEN ANCHOR VIEW
                displayPopupWindow(txtShare);

                break;

            case R.id.txtFacebook:

                //CHEK IF FACEBOOK INSTALLED OR NOT
                if (CommonMethods.isFbInstalled(mContext)) {
                    SharePhoto photo = new SharePhoto.Builder().setBitmap(savedBitMap).build();
                    SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
                    ShareDialog dialog = new ShareDialog(this);
                    if (dialog.canShow(SharePhotoContent.class)) {
                        dialog.show(content);
                    } else {
                        CommonMethods.displayToast(mContext, getResources().getString(R.string.strSomethingWentWrong));
                    }
                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strFbNotInstalled));
                }

                //CLOSE POPUP WINDOW
                popup.dismiss();

                break;

            case R.id.txtTwitter:

                CommonMethods.displayToast(mContext, "BUTTON CLICKED: TWITTER");

                //CLOSE POPUP WINDOW
                popup.dismiss();

                break;
            case R.id.txtInstagram:

                CommonMethods.displayToast(mContext, "BUTTON CLICKED: INSTA");

                //CLOSE POPUP WINDOW
                popup.dismiss();

                break;
        }

    }


    private void displayPopupWindow(View anchorView) {

        popup = new PopupWindow(VideoViewActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.share_anchor_view_popup_content, null);
        popup.setContentView(layout);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(anchorView);

        TextView txtFacebook = (TextView) layout.findViewById(R.id.txtFacebook);
        TextView txtTwitter = (TextView) layout.findViewById(R.id.txtTwitter);
        TextView txtInstagram = (TextView) layout.findViewById(R.id.txtInstagram);
        txtFacebook.setOnClickListener(this);
        txtTwitter.setOnClickListener(this);
        txtInstagram.setOnClickListener(this);
    }


    private void callLocationApi(int duration) {

        CommonMethods.printLogE("Response @ callLocationApi TIME IN SECOND : ", "" + duration);

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
                .baseUrl(Constants.API_END_POINTS_STAGING)
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
                    displayCoordinates(response.body().getData(), false, 0);

                    //SET LOCATION DATA INTO STATIC ARRAY
                    locationData = response.body().getData();


                } else if (response.code() == Constants.API_USER_UNAUTHORIZED) {

                    Intent mLoginIntent = new Intent(mActivity, LoginActivityActivity.class);
                    mActivity.startActivity(mLoginIntent);
                    mActivity.finish();

                } else {


                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }

            }

            @Override
            public void onFailure(Call<DotsLocationsModel> call, Throwable t) {

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });


    }

    private void displayCoordinates(List<DotsLocationsModel.Datum> data, boolean isFromLongPress, int longPressItemID) {

        //REMOVE ALL VIEWS
        frameOverlay.removeAllViews();

        if (!isFromLongPress) {

            for (int i = 0; i < data.size(); i++) {

                //ADD DYNAMIC IMAGE VIEW
                imgDynamicCoordinateView = new ImageView(this);
                imgDynamicCoordinateView.setImageResource(R.mipmap.icon_product);
                imgDynamicCoordinateView.setTag(i);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(45, 45);
                layoutParams.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))) + 30);
                layoutParams.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));

                //SET DARK COLOR FOR FIRST 3 ITEMS
                if (i < 3) {
                    ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor("#84C14A")));
                } else {
                    ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor("#8084C14A")));
                }

                frameOverlay.addView(imgDynamicCoordinateView, layoutParams);

                //ADD DYNAMIC TEXT VIEW FOR VENDOR AND ITEM NAME
                txtVendorName = new TextView(this);

                //VISIBLE ONLY FIRST THREE TEXT
                if (i < 3) {
                    txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                    txtVendorName.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                    txtVendorName.setTextColor(Color.parseColor("#ffffff"));
                    txtVendorName.setVisibility(View.GONE);
                }

                txtVendorName.setTextSize(9);
                txtVendorName.setTag(i);
                txtVendorName.setBackgroundResource(R.drawable.bc_video_item_text);
                FrameLayout.LayoutParams layoutParamsVendor = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParamsVendor.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))) + 80);
                layoutParamsVendor.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                frameOverlay.addView(txtVendorName, layoutParamsVendor);


                //ON LONG PRESS VISIBLE ONLY LONG PRESS ITEMS ELSE DISPLAY IN LIGHT COLOR
                imgDynamicCoordinateView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View mView) {

                        displayCoordinates(locationData, true, ((int) mView.getTag()));

                        return true;
                    }
                });

            /*imgDynamicCoordinateView.setOnClickListener(new View.OnClickListener() {
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
*/

            }
        } else {

            for (int i = 0; i < data.size(); i++) {

                //ADD DYNAMIC IMAGE VIEW
                imgDynamicCoordinateView = new ImageView(this);
                imgDynamicCoordinateView.setImageResource(R.mipmap.icon_product);
                imgDynamicCoordinateView.setTag(i);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(45, 45);
                layoutParams.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))) + 30);
                layoutParams.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));

                //SET DARK COLOR FOR FIRST 3 ITEMS
                if (i == longPressItemID) {
                    ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor("#84C14A")));
                } else {
                    ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor("#8084C14A")));
                }

                frameOverlay.addView(imgDynamicCoordinateView, layoutParams);

                //ADD DYNAMIC TEXT VIEW FOR VENDOR AND ITEM NAME
                txtVendorName = new TextView(this);

                //VISIBLE ONLY FIRST THREE TEXT
                if (i == longPressItemID) {
                    txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                    txtVendorName.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                    txtVendorName.setTextColor(Color.parseColor("#ffffff"));
                    txtVendorName.setVisibility(View.GONE);
                }

                txtVendorName.setTextSize(9);
                txtVendorName.setTag(i);
                txtVendorName.setBackgroundResource(R.drawable.bc_video_item_text);
                FrameLayout.LayoutParams layoutParamsVendor = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParamsVendor.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))) + 80);
                layoutParamsVendor.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                frameOverlay.addView(txtVendorName, layoutParamsVendor);


                //ON LONG PRESS VISIBLE ONLY LONG PRESS ITEMS ELSE DISPLAY IN LIGHT COLOR
                imgDynamicCoordinateView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View mView) {

                        displayCoordinates(locationData, true, ((int) mView.getTag()));

                        return true;
                    }
                });


            }

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