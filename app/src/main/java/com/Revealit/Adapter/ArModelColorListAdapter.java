package com.Revealit.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ARviewActivity;
import com.Revealit.Activities.ArModelViewerWeb;
import com.Revealit.Activities.ExoPlayerActivity;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.Interfaces.ColorSelectionForARClicks;
import com.Revealit.ModelClasses.DotsLocationsModel;
import com.Revealit.R;

public class ArModelColorListAdapter extends RecyclerView.Adapter<ArModelColorListAdapter.ViewHolder> {


    private View view;
    private Context mContext;
    private ArModelViewerWeb mActivity;
    private ViewHolder viewHolder;
    String[] strings= new String[5000];
    String[] stringsURL= new String[5000];
    private String url;
    private SessionManager mSessionManager;
    private ColorSelectionForARClicks mColorSelectionForARClicks;



    public ArModelColorListAdapter(SessionManager mSessionManager, Context mContext, ArModelViewerWeb mActivity, String[] strings, String url, String[] stringsURL) {

        this.mContext = mContext;
        this.mActivity = mActivity;
        this.strings = strings;
        this.stringsURL = stringsURL;
        this.url = url;
        this.mSessionManager = mSessionManager;
        mSessionManager.openSettings();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgTest;


        public ViewHolder(View mView) {

            super(mView);

            imgTest = (ImageView) mView.findViewById(R.id.imgTest);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_colors_list, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

       // holder.imgTest.setBackgroundColor(Color.parseColor(strings[position]));
        holder.imgTest.setBackground(CommonMethods.drawCircle(Color.parseColor(strings[position]), Color.parseColor("#000000")));


        holder.imgTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( CommonMethods.isDeviceSupportAR(mActivity)) {

                 /*   String strLoadingURL;

                    if(position == 0){
                        strLoadingURL = "https://modelviewer.dev/shared-assets/models/shishkebab.glb";
                    }else if(position  == 1) {
                        strLoadingURL ="https://modelviewer.dev/shared-assets/models/Astronaut.glb";
                    }else{
                        strLoadingURL = "https://storage.googleapis.com/ar-answers-in-search-models/static/Tiger/model.glb";
                    }*/

                    //AR MODEL URL
                    mSessionManager.updatePreferenceString(Constants.AR_MODEL_URL, stringsURL[position]);

                    mActivity.selectedColorURL(stringsURL[position]);

                }

            }
        });


    }



    @Override
    public int getItemCount() {

        return strings.length;
    }
}