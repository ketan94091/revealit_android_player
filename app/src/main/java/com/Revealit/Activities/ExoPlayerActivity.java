package com.Revealit.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Adapter.BlueDotsMetaListAdapter;
import com.Revealit.Adapter.InfluencersListAdapter;
import com.Revealit.Adapter.RecipesListAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.OnSwipeTouchListener;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.DotsLocationsModel;
import com.Revealit.ModelClasses.GetRecipesDetails;
import com.Revealit.ModelClasses.InfluencersModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.exoplayer2.ExoPlaybackException;
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
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ExoPlayerActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "ExoPlayerActivity";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private SimpleExoPlayerView exoPlayer;
    private SimpleExoPlayer player;
    private ProgressBar progress;
    private ImageView imgShareImage, imgVoulume, imgBackArrow, imgRecipe, imgInfluencer, imgShare;
    private LinearLayout linearRecipeShareInfluencer, linearMainBottomController;
    private SeekBar seekFontSize, ckVolumeBar;
    private AudioManager audioManager;
    private FrameLayout frameOverlay;
    public String strMediaURL, strMediaID = "", strMediaTitle = "", strColorWhite = "#ffffff", strGreenDarkColor = "#84C14A", strGreenLightColor = "#5084C14A", strAmberDark="#FF9500", strAmberLight ="#4DFF9500", strBlueDark="#3498db", strBlueLight ="#4D3498DB";
    public boolean isVideoSeek = false;
    private  int videoSeekTo= 0;
    private ImageView imgDynamicCoordinateView;
    private List<DotsLocationsModel.Datum> locationData;
    public int heightVideo, widthVideo;
    private RelativeLayout relativeCaptureImageWithText, relativeShareView;
    private EditText edtTextOnCaptureImage;
    private TextView txtVendorName, txtCancel, txtTitle,txtShare;
    private Bitmap savedBitMap;
    private int REQUEST_PERMISSION = 100;
    private int dialogHight = 0, dialogWidth = 0;
    boolean isMiddleSwipe = false;



    String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private ProgressBar progressLoadData;
    private AlertDialog mAlertDialogRecipe;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        isVideoSeek = getIntent().getBooleanExtra(Constants.IS_VIDEO_SEEK, false);
        videoSeekTo =Integer.parseInt(getIntent().getStringExtra(Constants.VIDEO_SEEK_TO));

        exoPlayer = (SimpleExoPlayerView) findViewById(R.id.exoPlayer);


        progress = (ProgressBar) findViewById(R.id.progress);

        imgShare = (ImageView) findViewById(R.id.imgShare);
        imgRecipe = (ImageView) findViewById(R.id.imgRecipe);
        imgInfluencer = (ImageView) findViewById(R.id.imgInfluencer);
        imgBackArrow = (ImageView) findViewById(R.id.imgBackArrow);
        imgVoulume = (ImageView) findViewById(R.id.imgVoulume);
        imgShareImage = (ImageView) findViewById(R.id.imgShareImage);


        ckVolumeBar = (SeekBar) findViewById(R.id.ckVolumeBar);
        seekFontSize = (SeekBar) findViewById(R.id.seekFontSize);

        linearMainBottomController = (LinearLayout) findViewById(R.id.linearMainBottomController);
        linearRecipeShareInfluencer = (LinearLayout) findViewById(R.id.linearRecipeShareInfluencer);

        frameOverlay = (FrameLayout) findViewById(R.id.frameOverlay);

        relativeShareView = (RelativeLayout) findViewById(R.id.relativeShareView);
        relativeCaptureImageWithText = (RelativeLayout) findViewById(R.id.relativeCaptureImageWithText);

        edtTextOnCaptureImage = (EditText) findViewById(R.id.edtTextOnCaptureImage);

        txtCancel = (TextView) findViewById(R.id.txtCancel);
        txtShare = (TextView) findViewById(R.id.txtShare);
        txtTitle = (TextView) findViewById(R.id.txtTitle);


        //SET AUDIO MANAGER WITH SEEK BAR
        audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolumePercentage = (100 * currentVolume) / maxVolume;
        ckVolumeBar.setProgress(currentVolumePercentage);
        ckVolumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

        ckVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                ckVolumeBar.setProgress(progress);

            }
        });

        //INCREASED FONT SIZE WITH PROGRESSING SEEKBAR
        seekFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {

                edtTextOnCaptureImage.setTextSize(progress);

            }
        });


        imgShareImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //VISIBLE EDIT TEXT ON TOUCH OF SNAP SHOT IMAGE
                edtTextOnCaptureImage.setFocusable(true);
                edtTextOnCaptureImage.setVisibility(View.VISIBLE);


                return true;


            }
        });

        //SET VIDEO TITLE
        txtTitle.setText(strMediaTitle);


        //CALL CHECK IF RECIPE AVAILABLE
        callCheckIfRecipeAvailable(strMediaID);

        //CALL CHECK IF INFLUENCERS AVAILABLE
        callCheckIfInfluencersAvailable(strMediaID);

        //INITIALIZE EXO PLAYER
        initializePlayer();


    }


    private void setOnClicks() {

        imgShare.setOnClickListener(this);
        imgInfluencer.setOnClickListener(this);
        imgRecipe.setOnClickListener(this);
        imgBackArrow.setOnClickListener(this);
        imgVoulume.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        txtShare.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.imgInfluencer:

                //OPEN INFLUENCERS DIALOG
                openInfluencerDialoge(strMediaID);

                break;

            case R.id.imgRecipe:

                //OPEN RECIPES DIALOG
                openRecipesDialoge(strMediaID);

                break;
            case R.id.imgShare:

                //SET DEFAULT SIZE
                edtTextOnCaptureImage.setTextSize(15);

                //CLEAR EDIT TEXT ON EACH SHARE
                edtTextOnCaptureImage.setText("");

                //RESET SEEKBAR
                seekFontSize.setProgress(0);

                //GET CAPTURE SCREEN HEIGHT AND WIDTH
                TextureView textureView = (TextureView) exoPlayer.getVideoSurfaceView();

                //DISPLAY CAPTURED BIT MAP
                imgShareImage.setImageBitmap(textureView.getBitmap());

                //VISIBLE VIEW
                relativeShareView.setVisibility(View.VISIBLE);

                break;
            case R.id.imgBackArrow:

                //STOP AND RELEASE PLAYER
                player.stop();
                player.release();

                finish();

                break;

            case R.id.imgVoulume:

                //VOLUME CONTROLLER BASED ON CLICK OF VOLUME ICON
                if (linearMainBottomController.getVisibility() == View.VISIBLE) {

                    linearMainBottomController.setVisibility(View.GONE);
                    ckVolumeBar.setVisibility(View.VISIBLE);

                } else {

                    linearMainBottomController.setVisibility(View.VISIBLE);
                    ckVolumeBar.setVisibility(View.GONE);
                }

                break;

            case R.id.txtCancel:

                //DISMISS SHARE VIEW
                relativeShareView.setVisibility(View.GONE);

                //CLEAR EDIT TEXT
                //SO EVERY SHARE VIEW IT'S SHOULD START FROM ZERO
                edtTextOnCaptureImage.setText("");

                break;
            case R.id.txtShare:

                //CHECK IF EXTERNAL PERMISSION IS GRANTED OR NOT
                if (mSessionManager.getPreferenceBoolean(Constants.READ_WRITE_PERMISSION)) {

                    //EDIT TEXT VISIBILITY GONE IN CASE OF THERE IS NO TEXT
                    if (edtTextOnCaptureImage.getText().toString().equals("")) {
                        edtTextOnCaptureImage.setVisibility(View.GONE);
                    }

                    //CREATE BIT MAP FROM RELATIVE LAYOUT FOR SHARING WITH OVERLAY TEXT
                    //SAVE IT IN LOCAL STORAGE AND THEN SHARE
                    savedBitMap = Bitmap.createBitmap(relativeCaptureImageWithText.getWidth(), relativeCaptureImageWithText.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(savedBitMap);
                    relativeCaptureImageWithText.draw(canvas);

                    //SAVE IMAGE
                    storeImage(savedBitMap);

                    //OPEN ANCHOR VIEW
                    displayPopupWindow(txtShare);

                } else {

                    //OPNE PERMISSION DIALOG
                    readWriteExternalStoragePermission();
                }

                break;


        }

    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        //IF RECIPE DIALOG IS VISIBLE THEN CHANGE ORIENTATION OF THAT DIALOGE SAME AS DISPLAY IN INITIAL MODE
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mAlertDialogRecipe != null && mAlertDialogRecipe.isShowing()) {
                mAlertDialogRecipe.getWindow().setLayout(dialogWidth, dialogHight);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //RELEASE PLAYER IN CASE BACK BUTTON PRESS
        player.stop();
        player.release();

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    private void initializePlayer() {

        // Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //Initialize the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        //Initialize simpleExoPlayerView
        exoPlayer.setPlayer(player);

        //PLAYER SEEK TO
        //MULTIPLAY BY 100 TO CONVERT MS TO SECONDS
        player.seekTo(videoSeekTo * 1000);

        //CHECK IF PLAYER IS READY TO PLAY AND CAME FROM LISTEN SCREEN
        //IF VIDEO IS FROM LISTEN SCREEN PAUSE VIDEO
        //ELSE CONTINUE PLAYING
        if (isVideoSeek){
            player.setPlayWhenReady(false);
        }else{
            player.setPlayWhenReady(true);
        }


        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Exo2"));

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        //strMediaURL = "https://revtesting.sgp1.digitaloceanspaces.com/video_media/_02d78c5fadffc4a277f27a1002bca8ea.mp4";

        Uri videoUri = Uri.parse(strMediaURL);
        MediaSource videoSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);

        // Prepare the player with the source.
        player.prepare(videoSource);


        //ADD LISTENER FOR FURTHER USE
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

                if (isLoading) {
                    progress.setVisibility(View.VISIBLE);
                } else {
                    progress.setVisibility(View.GONE);
                }

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playWhenReady && playbackState == Player.STATE_READY) {

                    //HIDE WHEN PLAYER IS READY
                    progress.setVisibility(View.GONE);
                    linearRecipeShareInfluencer.setVisibility(View.GONE);
                    relativeShareView.setVisibility(View.GONE);

                    //HIDE OVERLAY
                    frameOverlay.setVisibility(View.GONE);



                } else if (playbackState == Player.STATE_ENDED) {

                    //HIDE WHEN PLAYER IS ENDED VIDEO
                    progress.setVisibility(View.GONE);


                } else if (playWhenReady) {

                    //VISIBLE WHEN PLAYER IS BUFFERING
                    progress.setVisibility(View.VISIBLE);
                    linearRecipeShareInfluencer.setVisibility(View.GONE);
                    relativeShareView.setVisibility(View.GONE);

                    //HIDE OVERLAY
                    frameOverlay.setVisibility(View.GONE);

                } else {

                    //HIDE AND VISIBLE WHEN PLAYER IS PAUSE
                    progress.setVisibility(View.GONE);
                    linearRecipeShareInfluencer.setVisibility(View.VISIBLE);

                    //UPDATE SWIPE FLAG
                    isMiddleSwipe = false;

                    //API CALL TO GET DOTS LOCATION
                    // PASS ARGUMENT WITH CURRENT VIDEO IN SECONDS
                    callLocationApi((int) player.getCurrentPosition() / 1000);

                    //CALL REWARD API
                    //CASE -1 ---> PAUSE THE VIDEO
                    callUploadRewardData(1 , Integer.parseInt(strMediaID));
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


                //GET VIDEO HEIGHT AND WIDTH
                heightVideo = height;
                widthVideo = width;


                //GET TEXTURE VIEW WHERE VIDEO WILL DISPLAY
                //SET DYNAMIC HEIGHT WIDTH BASED ON LOADED VIDEO
                TextureView textureView = (TextureView) exoPlayer.getVideoSurfaceView();
                textureView.getLayoutParams().height = height;
                textureView.getLayoutParams().width = width;
                textureView.requestLayout();

                //SET DYNAMIC GRAVITY CENTRE FOR TEXTURE VIEW
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
                params.gravity = Gravity.CENTER;
                textureView.setLayoutParams(params);


                //SET OVERLAY LAYOUT SAME AS VIDEO VIEW
                //SET DYNAMIC HEIGHT WIDTH BASED ON LOADED VIDEO
                frameOverlay.getLayoutParams().height = height;
                frameOverlay.getLayoutParams().width = width;
                frameOverlay.requestLayout();

                //SET DYNAMIC GRAVITY CENTRE FOR FRAME LAYOUT ABOVE EXACTLY VIDEO SURFACE VIEW
                RelativeLayout.LayoutParams ovrLayparam = new RelativeLayout.LayoutParams(width, height);
                ovrLayparam.addRule(RelativeLayout.CENTER_HORIZONTAL);
                ovrLayparam.addRule(RelativeLayout.CENTER_VERTICAL);
                frameOverlay.setLayoutParams(ovrLayparam);


                //SET SHARE VIEW LAYOUT SAME AS VIDEO VIEW
                //SET DYNAMIC HEIGHT WIDTH BASED ON LOADED VIDEO
                relativeShareView.getLayoutParams().height = height;
                relativeShareView.getLayoutParams().width = width;
                relativeShareView.requestLayout();

                //SET DYNAMIC GRAVITY CENTRE
                //LAYOUT MARGIN SO SHARE VIEW LOOKS SMALLER THAN ACTUAL VIDEO VIEW
                RelativeLayout.LayoutParams shareView = new RelativeLayout.LayoutParams((width - 50), (height - 50));
                shareView.addRule(RelativeLayout.CENTER_HORIZONTAL);
                shareView.addRule(RelativeLayout.CENTER_VERTICAL);
                relativeShareView.setLayoutParams(shareView);


                //INITIAL VISIBLITY OF FRAM LAYOUT
                frameOverlay.setVisibility(View.GONE);
            }

            @Override
            public void onRenderedFirstFrame() {

            }
        });
/*

        TrackSelection.Factory adaptiveTrackSelection = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(mContext),
                new DefaultTrackSelector(adaptiveTrackSelection),
                new DefaultLoadControl());

        exoPlayer.setPlayer(player);
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();

        Uri uri = Uri.parse(strMediaURL);
        Handler mainHandler = new Handler();
        MediaSource mediaSource = new HlsMediaSource(uri,
                dataSourceFactory, mainHandler, null);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
*/
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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
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

                if (response.code() == Constants.API_CODE_200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ callLocationApi : ", "" + gson.toJson(response.body()));


                    //SET LOCATION DATA INTO STATIC ARRAY
                    locationData = response.body().getData();

                    //DISPLAY COORDINATES FOR DOTS
                    displayCoordinates(response.body().getData(), 1, 0);


                } else if (response.code() == Constants.API_CODE_401) {

                    Intent mLoginIntent = new Intent(mActivity, LoginActivityActivity.class);
                    mActivity.startActivity(mLoginIntent);
                    mActivity.finish();

                }

            }

            @Override
            public void onFailure(Call<DotsLocationsModel> call, Throwable t) {

               // CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });

    }

    private void displayCoordinates(List<DotsLocationsModel.Datum> data, int intCase, int longPressItemID) {

        //SWITCH CASE
        //CASE : 1 = NORMAL GREEN DOTS
        //CASE : 2 = LONG PRESS // DISPLAY ONLY PRESSED DOTS DATA
        //CASE : 3 = AMBER DOTS
        //CASE : 4 = BLUE DOTS
        //CASE : 5 = LONG PRESS// DISPLAY ONLY ONE AMBER DOTS DATA
        //CASE : 6 = LONG PRESS// DISPLAY ONLY ONE BLUE DOTS DATA

        //REMOVE ALL VIEWS
        frameOverlay.removeAllViews();

        switch (intCase) {

            case 1:
                for (int i = 0; i < data.size(); i++) {

                    //ADD DYNAMIC IMAGE VIEW
                    imgDynamicCoordinateView = new ImageView(this);
                    imgDynamicCoordinateView.setImageResource(R.mipmap.icon_product);
                    imgDynamicCoordinateView.setTag(i);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(50, 50);
                    layoutParams.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))));
                    layoutParams.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));

                    //SET DARK COLOR FOR FIRST 3 ITEMS
                    if (i < 3) {
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(strGreenDarkColor)));
                    } else {
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(strGreenLightColor)));
                    }

                    frameOverlay.addView(imgDynamicCoordinateView, layoutParams);

                    //ADD DYNAMIC TEXT VIEW FOR VENDOR AND ITEM NAME
                    txtVendorName = new TextView(this);

                    //VISIBLE ONLY FIRST THREE TEXT
                    if (i < 3) {
                        txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                        txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                    } else {
                        txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                        txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                        txtVendorName.setVisibility(View.GONE);
                    }

                    txtVendorName.setTextSize(8);
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

                    //ON CLICK PURCHASE SCREEN SHOULD SEE
                    int finalI = i;
                    imgDynamicCoordinateView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //CALL REWARD API
                            //CASE - 2 ---> CLICK ON GREEN DOTS
                            callUploadRewardData(2 ,Integer.parseInt(data.get(finalI).getItemId()));

                            Intent mIntent = new Intent(ExoPlayerActivity.this, ProductBuyingScreenActivity.class);
                            mIntent.putExtra("ITEM_ID", data.get(finalI).getItemId());
                            startActivity(mIntent);

                        }
                    });

                    txtVendorName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent mIntent = new Intent(mActivity, WebViewScreen.class);
                            mIntent.putExtra(Constants.RESEARCH_URL, "" + data.get(finalI).getVendorUrl());
                            mIntent.putExtra(Constants.RESEARCH_URL_SPONSER, "" + data.get(finalI).getVendor());
                            startActivity(mIntent);


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
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(50, 50);
                    layoutParams.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))));
                    layoutParams.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));

                    //SET DARK COLOR FOR FIRST 3 ITEMS
                    if (i == longPressItemID) {
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(strGreenDarkColor)));
                    } else {
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(strGreenLightColor)));
                    }

                    frameOverlay.addView(imgDynamicCoordinateView, layoutParams);

                    //ADD DYNAMIC TEXT VIEW FOR VENDOR AND ITEM NAME
                    txtVendorName = new TextView(this);

                    //VISIBLE ONLY FIRST THREE TEXT
                    if (i == longPressItemID) {
                        txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                        txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                    } else {
                        txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                        txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                        txtVendorName.setVisibility(View.GONE);
                    }

                    txtVendorName.setTextSize(8);
                    txtVendorName.setTag(i);
                    txtVendorName.setBackgroundResource(R.drawable.bc_video_item_text);
                    FrameLayout.LayoutParams layoutParamsVendor = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParamsVendor.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))) + 50);
                    layoutParamsVendor.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                    frameOverlay.addView(txtVendorName, layoutParamsVendor);

                    int finalI = i;

                    //ON LONG PRESS VISIBLE ONLY LONG PRESS ITEMS ELSE DISPLAY IN LIGHT COLOR
                    imgDynamicCoordinateView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View mView) {

                            displayCoordinates(locationData, 2, ((int) mView.getTag()));

                            return true;
                        }
                    });

                    //ON CLICK PURCHASE SCREEN SHOULD SEE
                    imgDynamicCoordinateView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //CALL REWARD API
                            //CASE - 2 ---> CLICK ON GREEN DOTS
                            callUploadRewardData(2 ,Integer.parseInt(data.get(finalI).getItemId()));

                            Intent mIntent = new Intent(ExoPlayerActivity.this, ProductBuyingScreenActivity.class);
                            mIntent.putExtra("ITEM_ID", data.get(finalI).getItemId());
                            startActivity(mIntent);

                        }
                    });

                    txtVendorName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent mIntent = new Intent(mActivity, WebViewScreen.class);
                            mIntent.putExtra(Constants.RESEARCH_URL, "" + data.get(finalI).getVendorUrl());
                            mIntent.putExtra(Constants.RESEARCH_URL_SPONSER, "" + data.get(finalI).getVendor());
                            startActivity(mIntent);


                        }
                    });


                }


                break;
            case 3:

                for (int i = 0; i < data.size(); i++) {

                    if (data.get(i).getGlb_model_url() != null) {

                        //ADD DYNAMIC IMAGE VIEW
                        imgDynamicCoordinateView = new ImageView(this);
                        imgDynamicCoordinateView.setImageResource(R.mipmap.icon_product);
                        imgDynamicCoordinateView.setTag(i);
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(50, 50);
                        layoutParams.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))));
                        layoutParams.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(data.get(i).getArDotColor())));
                        frameOverlay.addView(imgDynamicCoordinateView, layoutParams);

                        //SET DARK COLOR FOR FIRST 3 ITEMS
                        if (i < 3) {
                            ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(strAmberDark)));
                        } else {
                            ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(strAmberLight)));
                        }

                        //ADD DYNAMIC TEXT VIEW FOR VENDOR AND ITEM NAME
                        txtVendorName = new TextView(this);

                        //VISIBLE ONLY FIRST THREE TEXT
                        if (i < 3) {
                            txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                            txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                        } else {
                            txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                            txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                            txtVendorName.setVisibility(View.GONE);
                        }

                        txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getArmodelSponsor());
                        txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                        txtVendorName.setTextSize(8);
                        txtVendorName.setTag(i);
                        txtVendorName.setBackgroundResource(R.drawable.bc_video_item_text);
                        FrameLayout.LayoutParams layoutParamsVendor = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParamsVendor.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))) + 50);
                        layoutParamsVendor.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                        frameOverlay.addView(txtVendorName, layoutParamsVendor);

                        int finalI = i;

                        //ON LONG PRESS VISIBLE ONLY LONG PRESS ITEMS ELSE DISPLAY IN LIGHT COLOR
                        imgDynamicCoordinateView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View mView) {

                                //CALL REWARD API
                                //CASE - 2 ---> CLICK ON AMBER DOTS
                                callUploadRewardData(2 ,Integer.parseInt(data.get(finalI).getItemId()));

                                //IF DEVICE SUPPORT AR VIEW
                                //IF APP ENVIRONMENT IS T-CURATOR(WHICH SUPPORT MULTIPLE GLB)
                                if (CommonMethods.isDeviceSupportAR(mActivity) && mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID) == 2) {
                                    //OPEN AR VIEW
                                    Intent mARviewIntent = new Intent(ExoPlayerActivity.this, ArModelViewerWeb.class);
                                    mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_NAME, data.get(finalI).getVendor());
                                    mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_URL, data.get(finalI).getVendorUrl());
                                    mARviewIntent.putExtra(Constants.AR_MODEL_ID, data.get(finalI).getItemId());
                                    startActivity(mARviewIntent);
                                }else{
                                    //IF OTHER ENVIRONMENT OTHER THAN T CURATOR SHOULD OPEN DIRECTLY IN TO AR VIEW
                                    if(CommonMethods.isDeviceSupportAR(mActivity)) {
                                        Intent mARviewIntent = new Intent(mActivity, ARviewActivity.class);
                                        mARviewIntent.putExtra(Constants.AR_VIEW_URL, data.get(finalI).getGlb_model_url());
                                        mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_NAME, data.get(finalI).getVendor());
                                        mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_URL,  data.get(finalI).getVendorUrl());
                                        startActivity(mARviewIntent);
                                    }else{
                                        CommonMethods.displayToast(mContext ,"Device not support AR camera");
                                    }
                                }

                            }
                        });


                        txtVendorName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent mIntent = new Intent(mActivity, WebViewScreen.class);
                                mIntent.putExtra(Constants.RESEARCH_URL, "" + data.get(finalI).getVendorUrl());
                                mIntent.putExtra(Constants.RESEARCH_URL_SPONSER, "" + data.get(finalI).getVendor());
                                startActivity(mIntent);

                            }
                        });

                        //ON LONG PRESS VISIBLE ONLY LONG PRESS ITEMS ELSE DISPLAY IN LIGHT COLOR
                        imgDynamicCoordinateView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View mView) {

                                displayCoordinates(locationData, 5, ((int) mView.getTag()));

                                return true;
                            }
                        });

                    }

                }
                break;

            case 4:

                for (int i = 0; i < data.size(); i++) {

                    if (data.get(i).getBlueDotMeta().size() != 0) {

                        //ADD DYNAMIC IMAGE VIEW
                        imgDynamicCoordinateView = new ImageView(this);
                        imgDynamicCoordinateView.setImageResource(R.mipmap.icon_product);
                        imgDynamicCoordinateView.setTag(i);
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(50, 50);
                        layoutParams.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))));
                        layoutParams.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(data.get(i).getBlueDotColor())));
                        frameOverlay.addView(imgDynamicCoordinateView, layoutParams);

                        //SET DARK COLOR FOR FIRST 3 ITEMS
                        if (i < 3) {
                            ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(strBlueDark)));
                        } else {
                            ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(strBlueLight)));
                        }

                        //ADD DYNAMIC TEXT VIEW FOR VENDOR AND ITEM NAME
                        txtVendorName = new TextView(this);

                        //VISIBLE ONLY FIRST THREE TEXT
                        if (i < 3) {
                            txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                            txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                        } else {
                            txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                            txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                            txtVendorName.setVisibility(View.GONE);
                        }

                        txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getArmodelSponsor());
                        txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                        txtVendorName.setTextSize(8);
                        txtVendorName.setTag(i);
                        txtVendorName.setBackgroundResource(R.drawable.bc_video_item_text);
                        FrameLayout.LayoutParams layoutParamsVendor = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParamsVendor.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))) + 50);
                        layoutParamsVendor.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                        frameOverlay.addView(txtVendorName, layoutParamsVendor);

                        int finalI = i;

                        imgDynamicCoordinateView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View mView) {

                                //CALL REWARD API
                                //CASE - 2 ---> CLICK ON BLUE DOTS
                                callUploadRewardData(2 ,Integer.parseInt(data.get(finalI).getItemId()));

                                View result = frameOverlay.findViewWithTag(mView.getTag());
                                displayBlueDotsInfo(result, data.get(finalI));

                            }
                        });

                        txtVendorName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent mIntent = new Intent(mActivity, WebViewScreen.class);
                                mIntent.putExtra(Constants.RESEARCH_URL, "" + data.get(finalI).getVendorUrl());
                                mIntent.putExtra(Constants.RESEARCH_URL_SPONSER, "" + data.get(finalI).getVendor());
                                startActivity(mIntent);

                            }
                        });

                        //ON LONG PRESS VISIBLE ONLY LONG PRESS ITEMS ELSE DISPLAY IN LIGHT COLOR
                        imgDynamicCoordinateView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View mView) {

                                displayCoordinates(locationData, 6, ((int) mView.getTag()));

                                return true;
                            }
                        });

                    }

                }
                break;
            case 5:

                for (int i = 0; i < data.size(); i++) {

                    //ADD DYNAMIC IMAGE VIEW
                    imgDynamicCoordinateView = new ImageView(this);
                    imgDynamicCoordinateView.setImageResource(R.mipmap.icon_product);
                    imgDynamicCoordinateView.setTag(i);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(50, 50);
                    layoutParams.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))));
                    layoutParams.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));

                    //SET DARK COLOR FOR FIRST 3 ITEMS
                    if (i == longPressItemID) {
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(strAmberDark)));
                    } else {
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(strAmberLight)));
                    }

                    frameOverlay.addView(imgDynamicCoordinateView, layoutParams);

                    //ADD DYNAMIC TEXT VIEW FOR VENDOR AND ITEM NAME
                    txtVendorName = new TextView(this);

                    //VISIBLE ONLY FIRST THREE TEXT
                    if (i == longPressItemID) {
                        txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                        txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                    } else {
                        txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                        txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                        txtVendorName.setVisibility(View.GONE);
                    }

                    txtVendorName.setTextSize(8);
                    txtVendorName.setTag(i);
                    txtVendorName.setBackgroundResource(R.drawable.bc_video_item_text);
                    FrameLayout.LayoutParams layoutParamsVendor = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParamsVendor.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))) + 50);
                    layoutParamsVendor.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                    frameOverlay.addView(txtVendorName, layoutParamsVendor);

                    int finalI = i;

                    //ON LONG PRESS VISIBLE ONLY LONG PRESS ITEMS ELSE DISPLAY IN LIGHT COLOR
                    imgDynamicCoordinateView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View mView) {

                            //CALL REWARD API
                            //CASE - 2 ---> CLICK ON AMBER DOTS
                            callUploadRewardData(2 ,Integer.parseInt(data.get(finalI).getItemId()));

                            //IF DEVICE SUPPORT AR VIEW
                            //IF APP ENVIRONMENT IS T-CURATOR(WHICH SUPPORT MULTIPLE GLB)
                            if (CommonMethods.isDeviceSupportAR(mActivity) && mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID) == 2) {
                                //OPEN AR VIEW
                                Intent mARviewIntent = new Intent(ExoPlayerActivity.this, ArModelViewerWeb.class);
                                mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_NAME, data.get(finalI).getVendor());
                                mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_URL, data.get(finalI).getVendorUrl());
                                mARviewIntent.putExtra(Constants.AR_MODEL_ID, data.get(finalI).getItemId());
                                startActivity(mARviewIntent);
                            }else{
                                //IF OTHER ENVIRONMENT OTHER THAN T CURATOR SHOULD OPEN DIRECTLY IN TO AR VIEW
                                if(CommonMethods.isDeviceSupportAR(mActivity)) {
                                    Intent mARviewIntent = new Intent(mActivity, ARviewActivity.class);
                                    mARviewIntent.putExtra(Constants.AR_VIEW_URL, data.get(finalI).getGlb_model_url());
                                    mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_NAME, data.get(finalI).getVendor());
                                    mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_URL,  data.get(finalI).getVendorUrl());
                                    startActivity(mARviewIntent);
                                }else{
                                    CommonMethods.displayToast(mContext ,"Device not support AR camera");
                                }
                            }

                        }
                    });


                    txtVendorName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent mIntent = new Intent(mActivity, WebViewScreen.class);
                            mIntent.putExtra(Constants.RESEARCH_URL, "" + data.get(finalI).getVendorUrl());
                            mIntent.putExtra(Constants.RESEARCH_URL_SPONSER, "" + data.get(finalI).getVendor());
                            startActivity(mIntent);

                        }
                    });

                    //ON LONG PRESS VISIBLE ONLY LONG PRESS ITEMS ELSE DISPLAY IN LIGHT COLOR
                    imgDynamicCoordinateView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View mView) {

                            displayCoordinates(locationData, 5, ((int) mView.getTag()));

                            return true;
                        }
                    });



                }
                break;

            case 6:

                for (int i = 0; i < data.size(); i++) {

                    //ADD DYNAMIC IMAGE VIEW
                    imgDynamicCoordinateView = new ImageView(this);
                    imgDynamicCoordinateView.setImageResource(R.mipmap.icon_product);
                    imgDynamicCoordinateView.setTag(i);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(50, 50);
                    layoutParams.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))));
                    layoutParams.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));

                    //SET DARK COLOR FOR FIRST 3 ITEMS
                    if (i == longPressItemID) {
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(strBlueDark)));
                    } else {
                        ImageViewCompat.setImageTintList(imgDynamicCoordinateView, ColorStateList.valueOf(Color.parseColor(strBlueLight)));
                    }

                    frameOverlay.addView(imgDynamicCoordinateView, layoutParams);

                    //ADD DYNAMIC TEXT VIEW FOR VENDOR AND ITEM NAME
                    txtVendorName = new TextView(this);

                    //VISIBLE ONLY FIRST THREE TEXT
                    if (i == longPressItemID) {
                        txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                        txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                    } else {
                        txtVendorName.setText(" " + data.get(i).getItemName() + " \n " + data.get(i).getVendor());
                        txtVendorName.setTextColor(Color.parseColor(strColorWhite));
                        txtVendorName.setVisibility(View.GONE);
                    }

                    txtVendorName.setTextSize(8);
                    txtVendorName.setTag(i);
                    txtVendorName.setBackgroundResource(R.drawable.bc_video_item_text);
                    FrameLayout.LayoutParams layoutParamsVendor = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParamsVendor.leftMargin = Math.round(pxToDp(getScreenResolutionX(mContext, (data.get(i).getxAxis()))) + 50);
                    layoutParamsVendor.topMargin = Math.round(pxToDp(getScreenResolutionY(mContext, (data.get(i).getyAxis()))));
                    frameOverlay.addView(txtVendorName, layoutParamsVendor);

                    int finalI = i;


                    //ON LONG PRESS VISIBLE ONLY LONG PRESS ITEMS ELSE DISPLAY IN LIGHT COLOR
                    imgDynamicCoordinateView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View mView) {

                            displayCoordinates(locationData, 6, ((int) mView.getTag()));

                            return true;
                        }
                    });

                    imgDynamicCoordinateView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View mView) {

                            //CALL REWARD API
                            //CASE - 2 ---> CLICK ON BLUE DOTS
                            callUploadRewardData(2 ,Integer.parseInt(data.get(finalI).getItemId()));

                            View result = frameOverlay.findViewWithTag(mView.getTag());
                            displayBlueDotsInfo(result, data.get(finalI));

                        }
                    });

                    txtVendorName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent mIntent = new Intent(mActivity, WebViewScreen.class);
                            mIntent.putExtra(Constants.RESEARCH_URL, "" + data.get(finalI).getVendorUrl());
                            mIntent.putExtra(Constants.RESEARCH_URL_SPONSER, "" + data.get(finalI).getVendor());
                            startActivity(mIntent);

                        }
                    });



                }


                break;
        }

        frameOverlay.invalidate();


        //FRAM OVEY VISIBLE AFTER DYNAMICALLY ADDED VIEW
        frameOverlay.setVisibility(View.VISIBLE);

        //SET FRAM OVER LAY TOUCH FOR FURTHER FUNCTIONALITY
        setOverLayTouch();

        //CLOSE DIALOG
        CommonMethods.closeDialog();

    }

    private void setOverLayTouch() {



        frameOverlay.setOnTouchListener(new OnSwipeTouchListener(ExoPlayerActivity.this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();

                // CASE 1 & 4 ---> SWIPE DOTS
                if(isMiddleSwipe){
                    displayCoordinates(locationData, 1, ((int) 0));
                    //CALL REWARD API
                    callUploadRewardData(1 , Integer.parseInt(strMediaID));

                    Log.e("GREEN DOTS"," : GREEN DOTS");

                    //UPDATE FLAG
                    isMiddleSwipe = false;

                }else if(!isMiddleSwipe){
                    displayCoordinates(locationData, 4, ((int) 0));
                    //CALL REWARD API
                    callUploadRewardData(4 , Integer.parseInt(strMediaID));

                    Log.e("BLUE DOTS"," : BLUE DOTS");

                    //UPDATE FLAG
                    isMiddleSwipe = true;
                }

            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();

                // CASE 1 & 3 ---> SWIPE DOTS
                if(isMiddleSwipe){
                    displayCoordinates(locationData, 1, ((int) 0));
                    //CALL REWARD API
                    callUploadRewardData(1 , Integer.parseInt(strMediaID));

                    Log.e("GREEN DOTS"," : GREEN DOTS");

                    //UPDATE FLAG
                    isMiddleSwipe = false;

                }else if(!isMiddleSwipe){
                    displayCoordinates(locationData, 3, ((int) 0));
                    //CALL REWARD API
                    callUploadRewardData(3 , Integer.parseInt(strMediaID));

                    Log.e("AMBER DOTS"," : AMBER DOTS");

                    //UPDATE FLAG
                    isMiddleSwipe = true;
                }

            }

            @Override
            public void onClick() {
                super.onClick();

            }



        });
    }

    private void storeImage(Bitmap savedBitMap) {

        //DELETE OLD FILES
       //String root = Environment.getExternalStorageDirectory().toString();
        String root = mActivity.getApplicationContext().getFilesDir().getPath();
        File deletOldFiles = new File(root, mSessionManager.getPreference(Constants.SAVED_IMAGE_FILE_NAME));
        if (deletOldFiles.exists()) deletOldFiles.delete();

        //SHARE IMAGE FILE NAME IN SESSION MANAGER SO WE CAN USE IT FURTHER FOR SOCIAL MEDIA SHARING
        mSessionManager.updatePreferenceString(Constants.SAVED_IMAGE_FILE_NAME, "" + System.currentTimeMillis() + ".png");


        File file = new File(root, mSessionManager.getPreference(Constants.SAVED_IMAGE_FILE_NAME));
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            savedBitMap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayPopupWindow(View anchorView) {

        PopupWindow popupShareSocialMedia = new PopupWindow(ExoPlayerActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.share_anchor_view_popup_content, null);
        layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, 450);
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        layout.measure(spec, spec);
        popupShareSocialMedia.setContentView(layout);
        popupShareSocialMedia.setHeight(450);
        popupShareSocialMedia.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupShareSocialMedia.setOutsideTouchable(true);
        popupShareSocialMedia.setFocusable(true);
        popupShareSocialMedia.setBackgroundDrawable(new BitmapDrawable());
        popupShareSocialMedia.showAsDropDown(anchorView);

        TextView txtFacebook = (TextView) layout.findViewById(R.id.txtFacebook);
        TextView txtTwitter = (TextView) layout.findViewById(R.id.txtTwitter);
        TextView txtInstagram = (TextView) layout.findViewById(R.id.txtInstagram);
        txtFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CHECK IF FACEBOOK INSTALLED OR NOT
                if (CommonMethods.isAppInstalled(getApplicationContext(), "com.facebook.katana")) {

                    SharePhoto photo = new SharePhoto.Builder().setBitmap(savedBitMap).build();
                    SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
                    ShareDialog dialog = new ShareDialog(ExoPlayerActivity.this);
                    if (dialog.canShow(SharePhotoContent.class)) {
                        dialog.show(content);
                    } else {
                        CommonMethods.displayToast(mContext, getResources().getString(R.string.strSomethingWentWrong));
                    }
                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strFbNotInstalled));
                }

                //CLOSE POPUP WINDOW
                popupShareSocialMedia.dismiss();

            }
        });
        txtTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonMethods.isAppInstalled(mContext, "com.twitter.android")) {

                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        //File media = new File(mActivity.getApplicationContext().getFilesDir().getPath() + "/" + mSessionManager.getPreference(Constants.SAVED_IMAGE_FILE_NAME));
                        intent.putExtra(Intent.EXTRA_STREAM, getImageUri(getApplicationContext(),savedBitMap));
                        intent.setPackage("com.twitter.android");
                        startActivity(intent);
                    }catch (Exception e) {
                       CommonMethods.printLogE(TAG ,"exeception : "+ e);
                    }


                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strTwitterNotInstalled));
                }
                //CLOSE POPUP WINDOW
                popupShareSocialMedia.dismiss();

            }
        });
        txtInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonMethods.isAppInstalled(mContext, "com.instagram.android")) {

                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        //File media = new File(mActivity.getApplicationContext().getFilesDir().getPath() + "/" + mSessionManager.getPreference(Constants.SAVED_IMAGE_FILE_NAME));
                        intent.putExtra(Intent.EXTRA_STREAM, getImageUri(getApplicationContext(),savedBitMap));
                        intent.setPackage("com.instagram.android");
                        startActivity(intent);
                    }catch (Exception e) {
                        CommonMethods.printLogE(TAG ,"exeception : "+ e);
                    }

                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strInstagramNotInstalled));
                }

                //CLOSE POPUP WINDOW
                popupShareSocialMedia.dismiss();
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, UUID.randomUUID().toString() + ".png", "drawing");
        return Uri.parse(path);
    }

    private void displayBlueDotsInfo(View anchorView, DotsLocationsModel.Datum blueDotMeta) {

        PopupWindow popupBlueDots = new PopupWindow(ExoPlayerActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.anchor_view_blue_dots_content, null);
        layout.measure(400, 500);
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        layout.measure(spec, spec);
        popupBlueDots.setContentView(layout);
        popupBlueDots.setHeight(500);
        popupBlueDots.setWidth(400);
        popupBlueDots.setOutsideTouchable(true);
        popupBlueDots.setFocusable(true);
        popupBlueDots.setBackgroundDrawable(new BitmapDrawable());
        popupBlueDots.showAsDropDown(anchorView);

        RecyclerView recycleBlueDotsMeta = (RecyclerView) layout.findViewById(R.id.recycleBlueDotsMeta);
        ImageView imgSponsorLogo = (ImageView) layout.findViewById(R.id.imgSponsorLogo);

        //LAYOUT MANAGER FOR BLUE DOTS META
        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(mActivity);
        recycleBlueDotsMeta.setLayoutManager(recylerViewLayoutManager);

        BlueDotsMetaListAdapter mBlueDotsMetaListAdapter = new BlueDotsMetaListAdapter(mContext, ExoPlayerActivity.this, blueDotMeta.getBlueDotMeta(), blueDotMeta.getItemWiki().getSponsorName());
        recycleBlueDotsMeta.setAdapter(mBlueDotsMetaListAdapter);

        //DISPLAY MAIN TITLE
        //LOAD COVER IMAGE WITH GLIDE
        Glide.with(mActivity)
                .load(blueDotMeta.getItemWiki().getSponsorImageUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(imgSponsorLogo);

    }

    public float pxToDp(float px) {
        float density = mContext.getResources().getDisplayMetrics().density;
        float dp = px / density;
        return dp;
    }

    public float getScreenResolutionX(Context context, float xCoordinate) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;


        /*video image device
        720(video height) = 1250(vido width)
        720(device height) =  ?

        1250 * 1050 / 720 = 1875;*/


        // height = device screen height
        //heightVideo = height of provided video
        //x = x Axis coordinate in terms of video height
        //xAxis = new X Axis in terms of device resolution matrix height

        float xAxis = (xCoordinate * width) / widthVideo;


        return xAxis;
    }

    public float getScreenResolutionY(Context context, float yCoordinate) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int height = metrics.heightPixels;

        // width = device screen with
        //widthVideo = width of provided video
        //y = y Axis coordinate in terms of video with
        //yAxis = new Y Axis in terms of device resolution matrix width

        float yAxis = (yCoordinate * height) / heightVideo;


        return yAxis;
    }

    private void openInfluencerDialoge(String itemId) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ExoPlayerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_influencers, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog mAlertDialog = dialogBuilder.create();
        mAlertDialog.setCancelable(true);

        //SET CURRENT PROGRESSBAR
        progressLoadData = (ProgressBar) dialogView.findViewById(R.id.progressLoadData);

        //GET RECIPES DETAILS DETAILS
        callGetInfluencerData(dialogView, itemId, mAlertDialog);

        mAlertDialog.show();

    }

    private void openRecipesDialoge(String itemId) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ExoPlayerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_recipes, null);
        dialogBuilder.setView(dialogView);

        mAlertDialogRecipe = dialogBuilder.create();
        mAlertDialogRecipe.setCancelable(true);

        //SET CURRENT PROGRESSBAR
        progressLoadData = (ProgressBar) dialogView.findViewById(R.id.progressLoadData);

        final View decorView = mAlertDialogRecipe.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        decorView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        //GET HEIGHT & WIDTH OF THE DIALOG WHICH WE WILL USE FOR RESIZING THE VIEW
                        dialogWidth = decorView.getMeasuredWidth();
                        dialogHight = decorView.getMeasuredHeight();

                    }

                });

        //GET RECIPES DETAILS DETAILS
        callGetRecipeData(dialogView, itemId, mAlertDialogRecipe);


        mAlertDialogRecipe.show();

    }

    private void callGetInfluencerData(View dialogView, String itemId, AlertDialog mAlertDialog) {

        CommonMethods.printLogE("Response @ callGetInfluencerData ITEM ID : ", "" + itemId);

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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .client(httpClient1.newBuilder().connectTimeout(10, TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).build())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient1)
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

        Call<InfluencersModel> call = patchService1.getInfluencers(Constants.API_GET_INFLUENCERS_DETAILS + itemId + "/influencers");

        call.enqueue(new Callback<InfluencersModel>() {
            @Override
            public void onResponse(Call<InfluencersModel> call, retrofit2.Response<InfluencersModel> response) {


                CommonMethods.printLogE("Response @ callGetInfluencerData : ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callGetInfluencerData : ", "" + response.code());


                if (response.code() == Constants.API_CODE_200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ callGetInfluencerData : ", "" + gson.toJson(response.body()));

                    //UPDATE UI
                    updateInfluencerUI(dialogView, response.body(), mAlertDialog);


                } else if (response.code() == Constants.API_CODE_401) {

                    progressLoadData.setVisibility(View.GONE);
                    mAlertDialog.dismiss();

                    Intent mLoginIntent = new Intent(mActivity, LoginActivityActivity.class);
                    mActivity.startActivity(mLoginIntent);
                    mActivity.finish();

                } else {
                    progressLoadData.setVisibility(View.GONE);
                    mAlertDialog.dismiss();

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strNoDataFound));

                }

            }

            @Override
            public void onFailure(Call<InfluencersModel> call, Throwable t) {

                progressLoadData.setVisibility(View.GONE);
                mAlertDialog.dismiss();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strNoDataFound));

            }
        });


    }

    private void callGetRecipeData(View dialogView, String itemId, AlertDialog mAlertDialog) {

        CommonMethods.printLogE("Response @ callGetRecipeData ITEM ID : ", "" + itemId);

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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .client(httpClient1.newBuilder().connectTimeout(10, TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).build())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient1)
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

        Call<GetRecipesDetails> call = patchService1.getRecipesDetails(Constants.API_GET_RECIPES_DETAILS + itemId + "/recipe");

        call.enqueue(new Callback<GetRecipesDetails>() {
            @Override
            public void onResponse(Call<GetRecipesDetails> call, retrofit2.Response<GetRecipesDetails> response) {


                CommonMethods.printLogE("Response @ callGetRecipeData : ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callGetRecipeData : ", "" + response.code());

                if (response.code() == Constants.API_CODE_200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ callGetRecipeData : ", "" + gson.toJson(response.body()));

                    //UPDATE UI
                    updateRecipeDialoge(dialogView, response.body().getData(), mAlertDialog);


                } else if (response.code() == Constants.API_CODE_401) {

                    progressLoadData.setVisibility(View.GONE);
                    mAlertDialog.dismiss();

                    Intent mLoginIntent = new Intent(mActivity, LoginActivityActivity.class);
                    mActivity.startActivity(mLoginIntent);
                    mActivity.finish();

                } else {
                    progressLoadData.setVisibility(View.GONE);
                    mAlertDialog.dismiss();

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strNoDataFound));

                }

            }

            @Override
            public void onFailure(Call<GetRecipesDetails> call, Throwable t) {

                progressLoadData.setVisibility(View.GONE);
                mAlertDialog.dismiss();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strNoDataFound));

            }
        });


    }

    private void callCheckIfInfluencersAvailable(String itemId) {

        CommonMethods.printLogE("Response @ callCheckIfInfluencersAvailable ITEM ID : ", "" + itemId);

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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .client(httpClient1.newBuilder().connectTimeout(10, TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).build())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient1)
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

        Call<InfluencersModel> call = patchService1.getInfluencers(Constants.API_GET_INFLUENCERS_DETAILS + itemId + "/influencers");

        call.enqueue(new Callback<InfluencersModel>() {
            @Override
            public void onResponse(Call<InfluencersModel> call, retrofit2.Response<InfluencersModel> response) {


                CommonMethods.printLogE("Response @ callCheckIfInfluencersAvailable : ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callCheckIfInfluencersAvailable : ", "" + response.code());

                if (response.code() == Constants.API_CODE_200 && response.body().getData() != null) {

                    //SET RECIPE ICON IF RECIPE IS AVAILABLE THEN DISPLAY ELSE GONE
                    imgInfluencer.setVisibility(View.VISIBLE);
                } else {
                    //SET RECIPE ICON IF RECIPE IS AVAILABLE THEN DISPLAY ELSE GONE
                    imgInfluencer.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(Call<InfluencersModel> call, Throwable t) {

                //SET RECIPE ICON IF RECIPE IS AVAILABLE THEN DISPLAY ELSE GONE
                imgInfluencer.setVisibility(View.VISIBLE);

            }
        });


    }


    private void callCheckIfRecipeAvailable(String itemId) {

        CommonMethods.printLogE("Response @ callCheckIfRecipeAvailable ITEM ID : ", "" + itemId);

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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .client(httpClient1.newBuilder().connectTimeout(10, TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).build())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient1)
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

        Call<GetRecipesDetails> call = patchService1.getRecipesDetails(Constants.API_GET_RECIPES_DETAILS + itemId + "/recipe");

        call.enqueue(new Callback<GetRecipesDetails>() {
            @Override
            public void onResponse(Call<GetRecipesDetails> call, retrofit2.Response<GetRecipesDetails> response) {


                CommonMethods.printLogE("Response @ callCheckIfRecipeAvailable : ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callCheckIfRecipeAvailable : ", "" + response.code());

                if (response.code() == Constants.API_CODE_200 && response.body().getData() != null) {

                    //SET RECIPE ICON IF RECIPE IS AVAILABLE THEN DISPLAY ELSE GONE
                    imgRecipe.setVisibility(View.VISIBLE);
                } else {
                    //SET RECIPE ICON IF RECIPE IS AVAILABLE THEN DISPLAY ELSE GONE
                    imgRecipe.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<GetRecipesDetails> call, Throwable t) {


                //SET RECIPE ICON IF RECIPE IS AVAILABLE THEN DISPLAY ELSE GONE
                imgRecipe.setVisibility(View.GONE);

            }
        });


    }

    private void updateRecipeDialoge(View dialogView, List<GetRecipesDetails.Data> recipesListData, AlertDialog mAlertDialog) {

        //CLOSE DIALOGE
        ImageView imgCloseDailoge = (ImageView) dialogView.findViewById(R.id.imgCloseDailoge);
        imgCloseDailoge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();
            }
        });

        //SET RECIPES LIST
        RecyclerView recyclerViewRecipesList = (RecyclerView) dialogView.findViewById(R.id.recycleRecipesList);
        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(mActivity);
        recyclerViewRecipesList.setLayoutManager(recylerViewLayoutManager);

        RecipesListAdapter mRecipesListAdapter = new RecipesListAdapter(mContext, mActivity, recipesListData);
        recyclerViewRecipesList.setAdapter(mRecipesListAdapter);


        RelativeLayout relativeContentDialogView = (RelativeLayout) dialogView.findViewById(R.id.relativeContent);
        relativeContentDialogView.setVisibility(View.VISIBLE);

        //HIDE PROGRESS AFTER SETTING ALL DATA
        progressLoadData.setVisibility(View.GONE);


    }

    private void updateInfluencerUI(View dialogView, InfluencersModel influencersData, AlertDialog mAlertDialog) {

        //CLOSE DIALOGE
        ImageView imgCloseDailoge = (ImageView) dialogView.findViewById(R.id.imgCloseDailoge);
        imgCloseDailoge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();
            }
        });

        //SET INFLUENCERS LIST
        RecyclerView recycleviewInfluencerList = (RecyclerView) dialogView.findViewById(R.id.recycleInfluencerList);
        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(mActivity);
        recycleviewInfluencerList.setLayoutManager(recylerViewLayoutManager);

        //CHECK IF SPONSER IMAGE OBJECT IS NULL
        String sponserImage ="";
        if(influencersData.getSponsor() !=  null){
            sponserImage = influencersData.getSponsor().getInfluencerAdvertImgUrl();
        }else {
            sponserImage = "";
        }

        InfluencersListAdapter mInfluencersListAdapter = new InfluencersListAdapter(mContext, mActivity, influencersData.getData(),sponserImage );
        recycleviewInfluencerList.setAdapter(mInfluencersListAdapter);


        RelativeLayout relativeContentDialogView = (RelativeLayout) dialogView.findViewById(R.id.relativeContent);
        relativeContentDialogView.setVisibility(View.VISIBLE);

        //HIDE PROGRESS AFTER SETTING ALL DATA
        progressLoadData.setVisibility(View.GONE);


    }


    private void readWriteExternalStoragePermission() {

        if (ContextCompat.checkSelfPermission(ExoPlayerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ExoPlayerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        } else {

            requestStoragePermission();

        }
    }

    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.strPermissionNeeded))
                    .setMessage(getResources().getString(R.string.strPermissionNeededMsg))
                    .setPositiveButton(getResources().getString(R.string.strOk), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ExoPlayerActivity.this, PERMISSIONS, REQUEST_PERMISSION);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.strCancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mSessionManager.updatePreferenceBoolean(Constants.READ_WRITE_PERMISSION, true);
            }
        }

    }

    private void callUploadRewardData(int type, int videoORitemID) {

        //CASE 1- USER PAUSE THE VIDEO
        //CASE-2 - USER CLICK ANY DOTS SUCH AS GREEN, BLUE OR AMBER
        //CASE-3 - USER SWIP DOTS ANY OF THE DOTS.

        String strType=" " ;

        switch (type) {
            case 1:
                strType = Constants.REWARD_TYPE_PAUSE;
                break;
            case 2:
                strType = Constants.REWARD_TYPE_INTERECTION_OR_CLICK;
                break;
            case 3:
                strType = Constants.REWARD_TYPE_SWIPE;
                break;
        }


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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();



        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty(Constants.REWARD_TYPE, strType);
        paramObject.addProperty(Constants.REWARD_TYPE_ID, String.valueOf(videoORitemID));

        Call<JsonElement> call = patchService1.rewardData(strType, String.valueOf(videoORitemID));

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ callUploadRewardData: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callUploadRewardData: ", "" + response.code());


                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {



            }
        });

    }


}
