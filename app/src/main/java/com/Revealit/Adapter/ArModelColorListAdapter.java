package com.Revealit.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ArModelViewerWeb;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.GetMultiColorGLB;
import com.Revealit.R;

import java.util.ArrayList;
import java.util.List;

public class ArModelColorListAdapter extends RecyclerView.Adapter<ArModelColorListAdapter.ViewHolder> {


    private View view;
    private Context mContext;
    private ArModelViewerWeb mActivity;
    private ViewHolder viewHolder;
   private List<GetMultiColorGLB.Data> dataList = new ArrayList<>();



    public ArModelColorListAdapter(SessionManager mSessionManager, ArModelViewerWeb mContext, ArModelViewerWeb arModelViewerWeb, List<GetMultiColorGLB.Data> dataList) {

        this.mContext = mContext;
        this.mActivity = arModelViewerWeb;
        this.dataList = dataList;

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
        holder.imgTest.setBackground(CommonMethods.drawCircle(Color.parseColor(dataList.get(position).getGlb_color_hex()), Color.parseColor("#000000")));


        holder.imgTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( CommonMethods.isDeviceSupportAR(mActivity)) {

                    mActivity.selectedColorURL(dataList.get(position).getGlb_filename());

                }

            }
        });


    }



    @Override
    public int getItemCount() {

        return dataList.size();
    }
}