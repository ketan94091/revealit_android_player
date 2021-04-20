package com.Revealit.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.CustomViews.CustomViewDraw;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;


public class VideoViewActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "VideoViewActivity";

    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private VideoView mVideoView;
    private ImageView imgBackArrow, imgVoulume, imgPlayPause;
    private ProgressBar mProgress;
    private TextView txtCurrentTime, txtTotalTime;
    private SeekBar ckVolumeBar, ckVideoProgress;
    private boolean isPlayButtonClicked = false;
    private int intTotalDuration, intCurrentPosition;
    private LinearLayout linearControls;
    private AudioManager audioManager;
    private RelativeLayout relativeControllerMain,relativeHeaderView, relativeOverLay, relativeHeaderFooter;

    float[] x= new float[]{156, 601, 532, 101};
    float[] y= new float[]{834, 689, 783, 662};
    String[] items= new String[]{"Cake","Glass Jar","White Ceramic Article","Red Ceramic Cup"};
    private ImageView imgDynamicCoordinateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        mVideoView = (VideoView) findViewById(R.id.mVideoView);

        imgPlayPause = (ImageView) findViewById(R.id.imgPlayPause);
        imgVoulume = (ImageView) findViewById(R.id.imgVoulume);
        imgBackArrow = (ImageView) findViewById(R.id.imgBackArrow);


        mProgress = (ProgressBar) findViewById(R.id.mProgress);

        txtCurrentTime = (TextView) findViewById(R.id.txtCurrentTime);
        txtTotalTime = (TextView) findViewById(R.id.txtTotalTime);

        ckVideoProgress = (SeekBar) findViewById(R.id.ckVideoProgress);
        ckVolumeBar = (SeekBar) findViewById(R.id.ckVolumeBar);

        linearControls = (LinearLayout) findViewById(R.id.linearControls);

        relativeHeaderFooter = (RelativeLayout) findViewById(R.id.relativeHeaderFooter);
        relativeOverLay = (RelativeLayout) findViewById(R.id.relativeOverLay);
        relativeHeaderView = (RelativeLayout) findViewById(R.id.relativeHeaderView);
        relativeControllerMain = (RelativeLayout) findViewById(R.id.relativeControllerMain);


        mVideoView.setVideoPath("https://apac.sgp1.digitaloceanspaces.com/video_media/_8c2512f08315871cb526e57d8307431e.mp4");

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
             x = new float[]{788, 242, 1344, 1032};
             y= new float[]{413, 91, 297, 334};
            displayCoordinates();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            x= new float[]{156, 601, 532, 101};
            y= new float[]{834, 689, 783, 662};
            displayCoordinates();
        }
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

                    displayCoordinates();

                    mVideoView.pause();
                    relativeOverLay.setVisibility(View.VISIBLE);
                    imgPlayPause.setImageResource(R.drawable.ic_media_play);
                    setOverLayTouch();

                } else {
                    mVideoView.start();
                    relativeOverLay.setVisibility(View.GONE);
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

    private void displayCoordinates() {

        relativeOverLay.removeAllViews();

        for(int i= 0 ; i < 4 ;i++){
            imgDynamicCoordinateView = new ImageView(this);
            imgDynamicCoordinateView.setImageResource(R.drawable.overlay_light_green);
            imgDynamicCoordinateView.setTag(i);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(45,45);
            layoutParams.leftMargin = Math.round(x[i]);
            layoutParams.topMargin = Math.round(y[i]);
            relativeOverLay.addView(imgDynamicCoordinateView, layoutParams);

            imgDynamicCoordinateView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View mView) {

                 // Toast.makeText(mContext ,""+ items[(int) mView.getTag()],Toast.LENGTH_LONG).show();

                    AlertDialog alertDialog = new AlertDialog.Builder(VideoViewActivity.this).create(); //Read Update
                    alertDialog.setTitle("Revealit");
                    //alertDialog.setMessage("You Clicked On Dot : "+items[(int) mView.getTag()]);
                    alertDialog.setMessage("You Clicked On Dot : "+((int) mView.getTag()+1));

                    alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();  //<-- See This!

                }
            });

        }



    }

    private void setOverLayTouch() {
        relativeOverLay.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_DOWN)) {

                    Display display = getWindowManager().getDefaultDisplay();
                    int width=display.getWidth();
                    int height=display.getHeight();

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


}