package com.Revealit.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.AssestsList;
import com.Revealit.Activities.TestActivity;
import com.Revealit.Activities.VideoViewActivity;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.ModelClasses.CoursesModel;
import com.Revealit.R;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.SqliteDatabase.DatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "HomeFragment";

    private View mView;

    private GridLayoutManager mGridLayoutManager;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private RecyclerView recycleCourseList;
    //private CourseListAdapter mCourseListAdapter;
    private  ArrayList<CoursesModel.DataBean> CoursesModelList = new ArrayList<>();
    private ArrayList<Integer> intStandar = new ArrayList<>();
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

        recycleCourseList = (RecyclerView)mView.findViewById(R.id.recycleCourseList);
        mGridLayoutManager = new GridLayoutManager(mActivity,2);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(mContext, R.dimen.item_offset);
        recycleCourseList.addItemDecoration(itemDecoration);
        recycleCourseList.setLayoutManager(mGridLayoutManager);


        //CLEAR LISTS
        intStandar.clear();
        CoursesModelList.clear();

        CoursesModelList = mDatabaseHelper.getAllCoursesList();

        if(CoursesModelList.size() != 0) {

            for (int i =0 ; i<CoursesModelList.size() ;i++ ){

                if (!intStandar.contains(CoursesModelList.get(i).getStandard())) {
                    intStandar.add(CoursesModelList.get(i).getStandard());

                }
            }

        }

        //SET GROUP ADAPTER
       /* mCourseListAdapter = new CourseListAdapter(mContext,mActivity, intStandar);
        recycleCourseList.setAdapter(mCourseListAdapter);*/

    }


    private void setOnClicks() {


        btnPlayVideo.setOnClickListener(this);
        btnARMode.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.btnPlayVideo:

               /* NavController navController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.VIDEO_VIEW, "Video View");
                navController.navigate(R.id.playVideoView, bundle);*/

                Intent mIntent = new Intent(mActivity, VideoViewActivity.class);
                mContext.startActivity(mIntent);

                break;

            case R.id.btnARMode:

               /* NavController navController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.VIDEO_VIEW, "Video View");
                navController.navigate(R.id.playVideoView, bundle);*/

                Intent mIntentARmode = new Intent(mActivity, AssestsList.class);
                mContext.startActivity(mIntentARmode);


                break;
        }

    }

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }

}
