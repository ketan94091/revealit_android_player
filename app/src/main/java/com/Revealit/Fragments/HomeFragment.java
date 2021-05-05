package com.Revealit.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.Revealit.Activities.AssestsList;
import com.Revealit.Activities.VideoViewActivity;
import com.Revealit.R;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.SqliteDatabase.DatabaseHelper;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "HomeFragment";

    private View mView;

    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private Button btnARMode,btnPlayVideo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setTitle(getString(R.string.app_name));
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        return mView;


    }




    @Override
    public void onResume() {
        super.onResume();
        setIds();
        setOnClicks();
    }

    private void setIds() {

        mActivity = getActivity();
        mContext = getActivity();

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        btnPlayVideo = (Button)mView.findViewById(R.id.btnPlayVideo);
        btnARMode = (Button)mView.findViewById(R.id.btnARMode);

    }


    private void setOnClicks() {


        btnPlayVideo.setOnClickListener(this);
        btnARMode.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.btnPlayVideo:

                Intent mIntent = new Intent(mActivity, VideoViewActivity.class);
                mContext.startActivity(mIntent);

                break;

            case R.id.btnARMode:

                Intent mIntentARmode = new Intent(mActivity, AssestsList.class);
                mContext.startActivity(mIntentARmode);


                break;
        }

    }

}
