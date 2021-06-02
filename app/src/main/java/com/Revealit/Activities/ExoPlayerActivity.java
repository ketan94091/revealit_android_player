package com.Revealit.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.OnSwipeTouchListener;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.DotsLocationsModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Modifier;
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


public class ExoPlayerActivity extends AppCompatActivity implements View.OnClickListener {


    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private SimpleExoPlayerView exoPlayer;
    private SimpleExoPlayer player;
    private ProgressBar progress;
    private ImageView imgVoulume,imgBackArrow,imgShare;
    private LinearLayout linearMainBottomController;
    private SeekBar ckVolumeBar;
    private AudioManager audioManager;
    private FrameLayout frameOverlay;
    private String strMediaURL = "", strMediaID = "", strMediaTitle = "";
    private ImageView imgDynamicCoordinateView;
    private List<DotsLocationsModel.Datum> locationData;
    private TextView txtVendorName;
    private int heightVideo, widthVideo;
    private int SurfaceViewheightVideo, SurfaceViewwidthVideo;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_exoplayer);

        setIds();
        setOnClicks();

    }


    private void setIds() {

        mActivity = ExoPlayerActivity.this;
        mContext = ExoPlayerActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        //GET MEDIA ID
        strMediaID = getIntent().getStringExtra(Constants.MEDIA_ID);
        strMediaURL = getIntent().getStringExtra(Constants.MEDIA_URL);
        strMediaTitle = getIntent().getStringExtra(Constants.VIDEO_NAME);

        exoPlayer = (SimpleExoPlayerView) findViewById(R.id.exoPlayer);


        progress = (ProgressBar) findViewById(R.id.progress);

        imgShare=(ImageView)findViewById(R.id.imgShare);
        imgBackArrow=(ImageView)findViewById(R.id.imgBackArrow);
        imgVoulume=(ImageView)findViewById(R.id.imgVoulume);

        ckVolumeBar =(SeekBar)findViewById(R.id.ckVolumeBar);

        linearMainBottomController= (LinearLayout)findViewById(R.id.linearMainBottomController);

        frameOverlay = (FrameLayout)findViewById(R.id.frameOverlay);


        //SET AUDIO MANAGER WITH SEEK BAR
        audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        ckVolumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        ckVolumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
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

        frameOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                return true;
            }
        });

        //INITIALIZE EXO PLAYER
        initializePlayer();

    }

    private void setOnClicks() {

        imgShare.setOnClickListener(this);
        imgBackArrow.setOnClickListener(this);
        imgVoulume.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgShare:

                TextureView textureView = (TextureView) exoPlayer.getVideoSurfaceView();
                //imgTest.setImageBitmap(textureView.getBitmap());

                SharePhoto photo = new SharePhoto.Builder().setBitmap(textureView.getBitmap()).build();
                SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
                ShareDialog dialog = new ShareDialog(this);
                if (dialog.canShow(SharePhotoContent.class)) {
                    dialog.show(content);
                } else {
                    CommonMethods.displayToast(mContext, getResources().getString(R.string.strSomethingWentWrong));
                }


                break;
            case R.id.imgBackArrow:

                player.stop();
                player.release();

                finish();

                break;

            case R.id.imgVoulume:

                //VOLUME CONTROL BASED ON CLICK OF VOLUME ICON
                if (linearMainBottomController.getVisibility() == View.VISIBLE) {

                    linearMainBottomController.setVisibility(View.GONE);
                    ckVolumeBar.setVisibility(View.VISIBLE);

                } else {

                    linearMainBottomController.setVisibility(View.VISIBLE);
                    ckVolumeBar.setVisibility(View.GONE);
                }
                
                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.e("onPause", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("onStop", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("onRestart", "onRestart");
    }

    private void initializePlayer() {

        // Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        //Initialize the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        //Initialize simpleExoPlayerView
        exoPlayer.setPlayer(player);
        player.setPlayWhenReady(true);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "CloudinaryExoplayer"));

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        //Uri videoUri = Uri.parse("https://apac.sgp1.digitaloceanspaces.com/video_media/_fcd2713af44521b94c45bc36cb67fcd0.mp4");
        Uri videoUri = Uri.parse(strMediaURL);
        MediaSource videoSource = new ExtractorMediaSource(videoUri,
                dataSourceFactory, extractorsFactory, null, null);


        // Prepare the player with the source.
        player.prepare(videoSource);

        //RESCALE VIDEO
        exoPlayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);

        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playWhenReady && playbackState == Player.STATE_READY) {

                    //HIDE WHEN PLAYER IS READY
                    progress.setVisibility(View.GONE);
                    imgShare.setVisibility(View.GONE);

                    //HIDE OVERLAY
                    frameOverlay.setVisibility(View.GONE);


                } else if (playWhenReady) {

                    //VISIBLE WHEN PLAYER IS BUFFERING
                    progress.setVisibility(View.VISIBLE);
                    imgShare.setVisibility(View.GONE);
                } else {

                    //HIDE AND VISIBLE WHEN PLAYER IS PAUSE
                    progress.setVisibility(View.GONE);
                    imgShare.setVisibility(View.VISIBLE);

                    //API CALL GET DOTS LOCATION
                    //callLocationApi((int)player.getCurrentPosition()/1000);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        player.addVideoListener(new SimpleExoPlayer.VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

                TextureView textureView = (TextureView) exoPlayer.getVideoSurfaceView();

                Log.e("widthK", " : " +textureView.getWidth());
                Log.e("heightK", " : " + textureView.getHeight());

                Log.e("widthVIDEO", " : " +width);
                Log.e("heightVIDEO", " : " + height);

                heightVideo = height;
                widthVideo = width;

                SurfaceViewheightVideo = textureView.getHeight();
                SurfaceViewwidthVideo = textureView.getWidth();

                //SET OVERLAY LAYOUT SAME AS VIDEO VIEW
                frameOverlay.getLayoutParams().height = textureView.getHeight();
                frameOverlay.getLayoutParams().width = textureView.getWidth();
                frameOverlay.requestLayout();

                //VISIBLE FRAMLAYOUT FOR DOTS
                frameOverlay.setVisibility(View.GONE);
            }

            @Override
            public void onRenderedFirstFrame() {

            }
        });

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
                    displayCoordinates(response.body().getData(), 1, 0);

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
    private void displayCoordinates(List<DotsLocationsModel.Datum> data, int intCase, int longPressItemID) {

        //SWITCH CASE
        //CASE : 1 = NORMAL GREEN DOTS
        //CASE : 2 = LONG PRESS
        //CASE : 3 = AMBER DOTS
        //CASE : 4 = BLUE DOTS

        //REMOVE ALL VIEWS
        frameOverlay.removeAllViews();

        switch (intCase) {

            case 1:
                for (int i = 0; i < data.size(); i++) {

                    //ADD DYNAMIC IMAGE VIEW
                    imgDynamicCoordinateView = new ImageView(this);
                    imgDynamicCoordinateView.setImageResource(R.mipmap.icon_product);
                    imgDynamicCoordinateView.setTag(i);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(45, 45);
                    layoutParams.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))));
                    layoutParams.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));

                    //SET DARK COLOR FOR FIRST 3 ITEMS
                    if (i < 3) {
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor("#84C14A")));
                    } else {
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor("#5084C14A")));
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

                    txtVendorName.setTextSize(7);
                    txtVendorName.setTag(i);
                    txtVendorName.setBackgroundResource(R.drawable.bc_video_item_text);
                    FrameLayout.LayoutParams layoutParamsVendor = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParamsVendor.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))) + 50);
                    layoutParamsVendor.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                    frameOverlay.addView(txtVendorName, layoutParamsVendor);


                    //ON LONG PRESS VISIBLE ONLY LONG PRESS ITEMS ELSE DISPLAY IN LIGHT COLOR
                    imgDynamicCoordinateView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View mView) {

                            displayCoordinates(locationData, 2, ((int) mView.getTag()));

                            return true;
                        }
                    });

                }
                break;
            case 2:

                for (int i = 0; i < data.size(); i++) {

                    //ADD DYNAMIC IMAGE VIEW
                    imgDynamicCoordinateView = new ImageView(this);
                    imgDynamicCoordinateView.setImageResource(R.mipmap.icon_product);
                    imgDynamicCoordinateView.setTag(i);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(45, 45);
                    layoutParams.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))));
                    layoutParams.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));

                    //SET DARK COLOR FOR FIRST 3 ITEMS
                    if (i == longPressItemID) {
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor("#84C14A")));
                    } else {
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor("#5084C14A")));
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

                    txtVendorName.setTextSize(7);
                    txtVendorName.setTag(i);
                    txtVendorName.setBackgroundResource(R.drawable.bc_video_item_text);
                    FrameLayout.LayoutParams layoutParamsVendor = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParamsVendor.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis())))+ 50);
                    layoutParamsVendor.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                    frameOverlay.addView(txtVendorName, layoutParamsVendor);


                    //ON LONG PRESS VISIBLE ONLY LONG PRESS ITEMS ELSE DISPLAY IN LIGHT COLOR
                    imgDynamicCoordinateView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View mView) {

                            displayCoordinates(locationData, 2, ((int) mView.getTag()));

                            return true;
                        }
                    });


                }


                break;
            case 3:

                for (int i = 0; i < data.size(); i++) {

                    if (data.get(i).getArmodel() != null) {

                        //ADD DYNAMIC IMAGE VIEW
                        imgDynamicCoordinateView = new ImageView(this);
                        imgDynamicCoordinateView.setImageResource(R.mipmap.icon_product);
                        imgDynamicCoordinateView.setTag(i);
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(45, 45);
                        layoutParams.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))));
                        layoutParams.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(data.get(i).getArDotColor())));
                        frameOverlay.addView(imgDynamicCoordinateView, layoutParams);

                        //ADD DYNAMIC TEXT VIEW FOR VENDOR AND ITEM NAME
                        txtVendorName = new TextView(this);

                        txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getArmodelSponsor());
                        txtVendorName.setTextColor(Color.parseColor("#ffffff"));
                        txtVendorName.setTextSize(7);
                        txtVendorName.setTag(i);
                        txtVendorName.setBackgroundResource(R.drawable.bc_video_item_text);
                        FrameLayout.LayoutParams layoutParamsVendor = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParamsVendor.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis())))+ 50);
                        layoutParamsVendor.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                        frameOverlay.addView(txtVendorName, layoutParamsVendor);

                        //ON LONG PRESS VISIBLE ONLY LONG PRESS ITEMS ELSE DISPLAY IN LIGHT COLOR
                        imgDynamicCoordinateView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View mView) {

                                //displayCoordinates(locationData, 2, ((int) mView.getTag()));

                                return true;
                            }
                        });

                    }

                }
                break;

            case 4:

                for (int i = 0; i < data.size(); i++) {

                    if (data.get(i).getBlueDotMeta() != null) {

                        //ADD DYNAMIC IMAGE VIEW
                        imgDynamicCoordinateView = new ImageView(this);
                        imgDynamicCoordinateView.setImageResource(R.mipmap.icon_product);
                        imgDynamicCoordinateView.setTag(i);
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(45, 45);
                        layoutParams.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))));
                        layoutParams.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(data.get(i).getBlueDotColor())));
                        frameOverlay.addView(imgDynamicCoordinateView, layoutParams);

                        //ADD DYNAMIC TEXT VIEW FOR VENDOR AND ITEM NAME
                        txtVendorName = new TextView(this);

                        txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getArmodelSponsor());
                        txtVendorName.setTextColor(Color.parseColor("#ffffff"));
                        txtVendorName.setTextSize(7);
                        txtVendorName.setTag(i);
                        txtVendorName.setBackgroundResource(R.drawable.bc_video_item_text);
                        FrameLayout.LayoutParams layoutParamsVendor = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParamsVendor.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis())))+ 50);
                        layoutParamsVendor.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                        frameOverlay.addView(txtVendorName, layoutParamsVendor);

                        int finalI = i;
                        imgDynamicCoordinateView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View mView) {
                                View result = frameOverlay.findViewWithTag(mView.getTag());
                                //displayBlueDotsInfo(result,data.get(finalI));

                            }
                        });

                    }

                }


                break;
        }

        frameOverlay.invalidate();


        frameOverlay.setVisibility(View.VISIBLE);

        setOverLayTouch();

        //CLOSE DIALOG
        CommonMethods.closeDialog();

    }
    private void setOverLayTouch() {

        frameOverlay.setOnTouchListener(new OnSwipeTouchListener(ExoPlayerActivity.this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                displayCoordinates(locationData, 4, ((int) 0));
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                displayCoordinates(locationData, 3, ((int) 0));
            }

            @Override
            public void onClick() {
                super.onClick();

            }
        });
    }


    public float pxToDp(float px) {
        float density = mContext.getResources().getDisplayMetrics().density;
        float dp = px / density;
        return dp;
    }
    public float getScreenResolutionX(Context context, float x) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        /*video image device
        720(video height) = 1250(vido width)
        720(device height) =  ?

        1250 * 1050 / 720 = 1875;*/


        // height = device screen height
        //heightVideo = height of provided video
        //x = x Axis coordinate in terms of video height
        //xAxis = new X Axis in terms of device resolution matrix height

        //float xAxis = (x * SurfaceViewheightVideo) / (heightVideo);
        float xAxis = (x * SurfaceViewwidthVideo) / (widthVideo);


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

        // width = device screen with
        //widthVideo = width of provided video
        //y = y Axis coordinate in terms of video with
        //yAxis = new Y Axis in terms of device resolution matrix width

       // float yAxis = (y * SurfaceViewwidthVideo) / widthVideo;
        float yAxis = (y * SurfaceViewheightVideo) / heightVideo;
        /*Log.e("OLD Y : ",""+y);
        Log.e("NEW Y : ",""+yAxis);*/

       /* E/widthK:  : 2047
        2021-06-02 14:41:06.416 12733-12733/com.Revealit E/heightK:  : 1080
        2021-06-02 14:41:06.417 12733-12733/com.Revealit E/widthVIDEO:  : 1280
        2021-06-02 14:41:06.417 12733-12733/com.Revealit E/heightVIDEO:  : 720*/


        return yAxis;
    }

}
