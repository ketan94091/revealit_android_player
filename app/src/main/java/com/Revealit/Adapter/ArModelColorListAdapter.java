package com.Revealit.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.R;

public class ArModelColorListAdapter extends RecyclerView.Adapter<ArModelColorListAdapter.ViewHolder> {


    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
    String[] strings= new String[5000];



    public ArModelColorListAdapter(Context mContext, Activity mActivity, String[] strings) {

        this.mContext = mContext;
        this.mActivity = mActivity;
        this.strings = strings;

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


        holder.imgTest.setBackgroundColor(Color.parseColor(strings[position]));


        holder.imgTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });


    }


    @Override
    public int getItemCount() {

        return strings.length;
    }
}