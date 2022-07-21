package com.Revealit.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Adapter.MyRevealItListAdapter;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.CustomViews.RippleBackground;
import com.Revealit.ModelClasses.CategoryWisePlayListModel;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

import java.util.ArrayList;


public class ListenFragmentNonSimulationMode extends Fragment implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "ListenFragmentNonSimulationMode";

    private View mView;

    private GridLayoutManager mGridLayoutManager;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView imgListen;
    private TextView txtRevealCount;
    private RecyclerView recycleRevealList;
    private LinearLayoutManager recylerViewLayoutManager;
    private MyRevealItListAdapter mMyRevealItListAdapter;
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private RippleBackground rippleBackground;
    private int tapCount = 1;// IGONORE FIRST COUNT AND THEN START GETTING DATA FROM ITEMS TO END ITEMS.
    private ArrayList<CategoryWisePlayListModel.DataBean> mCategoryWisePlayListModel = new ArrayList<>();
    private ArrayList<Long> mLongRevealTime = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setTitle(getString(R.string.app_name));
        mView = inflater.inflate(R.layout.fragment_listen, container, false);


        return mView;

    }

    private void setIds() {

        mActivity = getActivity();
        mContext = getActivity();

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgListen = (ImageView) mView.findViewById(R.id.imgListen);

        txtRevealCount = (TextView) mView.findViewById(R.id.txtRevealCount);

        rippleBackground = (RippleBackground) mView.findViewById(R.id.content);


        recycleRevealList = (RecyclerView) mView.findViewById(R.id.recycleRevealList);
        recylerViewLayoutManager = new LinearLayoutManager(mActivity);
        recycleRevealList.setLayoutManager(recylerViewLayoutManager);


        //GET WATCH DATA FROM DATABASE
        mCategoryWisePlayListModel = mDatabaseHelper.getCategoryWisePlayList();

        mLongRevealTime = new ArrayList<>();
        mLongRevealTime.clear();

        tapCount = 1;

        updateUI(0, 0);



    }
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        if (menuVisible) {
            //SET IDS
            setIds();
            setOnClicks();
        }
        super.setMenuVisibility(menuVisible);
    }


    @Override
    public void onResume() {
        super.onResume();

        //SET IDS
        setIds();
        setOnClicks();


    }

    private void setOnClicks() {

        imgListen.setOnClickListener(this);
    }


    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {

            case R.id.imgListen:

                //SET IMAGE CLICKBLE FALSE
                imgListen.setClickable(false);

                if(tapCount < (mDatabaseHelper.getCategoryWisePlayList().size()+1)) {

                    //START ANIMATION
                    rippleBackground.startRippleAnimation();


                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            //ADD CURRENT TIME IN ARRAYLIST
                            mLongRevealTime.add(System.currentTimeMillis());

                            updateUI(0, tapCount);

                            //INCREASE TAP COUNT
                            tapCount++;

                            //START ANIMATION
                            rippleBackground.stopRippleAnimation();


                        }
                    }, 3000);

                }

                break;


        }

    }

    private void updateUI(int from, int to) {


        //SET CATEGORY LIST
        mMyRevealItListAdapter = new MyRevealItListAdapter(mContext, mActivity, mCategoryWisePlayListModel.subList(from, to), mDatabaseHelper,mLongRevealTime);
        recycleRevealList.setAdapter(mMyRevealItListAdapter);

        //SET SIZE TO REVEAL IT
        txtRevealCount.setText(""+mCategoryWisePlayListModel.subList(from, to).size()+" reveals");

        //SET IMAGE CLICKBLE TRUE
        imgListen.setClickable(true);
    }


}
