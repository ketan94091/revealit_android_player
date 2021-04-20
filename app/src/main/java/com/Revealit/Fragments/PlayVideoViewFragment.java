package com.Revealit.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.CoursesModel;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

import java.util.ArrayList;

public class PlayVideoViewFragment extends Fragment implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "PlayVideoViewFragment";

    private View mView;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private VideoView mVideoView;
    private ImageView imgVoulume,imgPlayPause;
    private ProgressBar mProgress;
    private TextView txtCurrentTime,txtTotalTime;
    private SeekBar ckVolumeBar,ckVideoProgress;
    private boolean isPlayButtonClicked = false;
    private int intTotalDuration,intCurrentPosition;
    private LinearLayout linearControls;
    private AudioManager audioManager;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setTitle(getArguments().getString(Constants.VIDEO_VIEW));
        mView = inflater.inflate(R.layout.fragment_video_view, container, false);

        setIds();
        setOnClicks();


        return mView;


    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private void setIds() {

        mActivity = getActivity();
        mContext = getActivity();

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        mVideoView = (VideoView) mView.findViewById(R.id.mVideoView);

        imgPlayPause = (ImageView) mView.findViewById(R.id.imgPlayPause);
        imgVoulume = (ImageView) mView.findViewById(R.id.imgVoulume);


        mProgress = (ProgressBar)mView.findViewById(R.id.mProgress);

        txtCurrentTime = (TextView) mView.findViewById(R.id.txtCurrentTime);
        txtTotalTime = (TextView) mView.findViewById(R.id.txtTotalTime);

        ckVideoProgress=(SeekBar)mView.findViewById(R.id.ckVideoProgress);
        ckVolumeBar=(SeekBar)mView.findViewById(R.id.ckVolumeBar);

        linearControls=(LinearLayout)mView.findViewById(R.id.linearControls);



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
            }
        });

        audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        ckVolumeBar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        ckVolumeBar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));


        ckVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar arg0)
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0)
            {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
            {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }
        });

    }
    private void setOnClicks() {

        imgPlayPause.setOnClickListener(this);
        imgVoulume.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.imgPlayPause:

                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    imgPlayPause.setImageResource(R.drawable.ic_media_play);
                } else {
                    mVideoView.start();
                    imgPlayPause.setImageResource(R.drawable.ic_media_pause);
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
        }

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
                } catch (IllegalStateException ed){
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
