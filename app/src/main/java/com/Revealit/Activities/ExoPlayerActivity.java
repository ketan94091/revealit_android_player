package com.Revealit.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
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
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.HashMap;


public class ExoPlayerActivity extends AppCompatActivity implements View.OnClickListener {


    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private SimpleExoPlayerView exoPlayer;
    private SimpleExoPlayer player;
    private TextView txtTest;
    private RelativeLayout relativeVIew;
    private MediaMetadataRetriever mmr;
    private ProgressBar progress;
    private ImageView imgTest;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        exoPlayer = (SimpleExoPlayerView) findViewById(R.id.exoPlayer);
        txtTest = (TextView)findViewById(R.id.txtTest);
        progress =(ProgressBar)findViewById(R.id.progress);
        imgTest = (ImageView)findViewById(R.id.imgTest);

        initializePlayer();

    }

    private void initializePlayer() {

        mmr = new MediaMetadataRetriever();
        mmr.setDataSource("https://apac.sgp1.digitaloceanspaces.com/video_media/_fcd2713af44521b94c45bc36cb67fcd0.mp4");

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

// Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, Util.getUserAgent(this, "CloudinaryExoplayer"));

// Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

// This is the MediaSource representing the media to be played.
        Uri videoUri = Uri.parse("https://apac.sgp1.digitaloceanspaces.com/video_media/_fcd2713af44521b94c45bc36cb67fcd0.mp4");
        MediaSource videoSource = new ExtractorMediaSource(videoUri,
                dataSourceFactory, extractorsFactory, null, null);



// Prepare the player with the source.
        player.prepare(videoSource);
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

                if (playbackState == Player.STATE_BUFFERING){
                    progress.setVisibility(View.VISIBLE);
                } else {
                    progress.setVisibility(View.INVISIBLE);

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

                Log.e("width"," : "+width);
                Log.e("height"," : "+height);
            }

            @Override
            public void onRenderedFirstFrame() {

            }
        });


    }

    private void setOnClicks() {

        txtTest.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.txtTest:

                TextureView textureView = (TextureView) exoPlayer.getVideoSurfaceView();
                imgTest.setImageBitmap(textureView.getBitmap());

                SharePhoto photo = new SharePhoto.Builder().setBitmap(textureView.getBitmap()).build();
                SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
                ShareDialog dialog = new ShareDialog(this);
                if (dialog.canShow(SharePhotoContent.class)) {
                    dialog.show(content);
                } else {
                    CommonMethods.displayToast(mContext, getResources().getString(R.string.strSomethingWentWrong));
                }


                break;

        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        Log.e("onPause","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("onStop","onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("onRestart","onRestart");
    }
}
