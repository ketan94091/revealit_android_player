package com.Revealit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import com.Revealit.CommonClasse.Constants;
import com.Revealit.R;

import java.util.ArrayList;
import java.util.Random;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {



    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
    private NavController navController;
    private ArrayList<Integer> intStandards = new ArrayList<>();


    public CourseListAdapter(Context mContext, Activity mActivity, ArrayList<Integer> intStandards) {
        this.mContext= mContext;
        this.mActivity= mActivity;
        this.intStandards= intStandards;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtStandard,txtCourseName ;
        private final RelativeLayout relativeItemClick;

        public ViewHolder(View mView) {

            super(mView);
            txtCourseName = (TextView) mView.findViewById(R.id.txtCourseName);
            txtStandard = (TextView) mView.findViewById(R.id.txtStandard);
            relativeItemClick = (RelativeLayout) mView.findViewById(R.id.relativeItemClick);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_course_list, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        Random rnd = new Random();
        //int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.relativeItemClick.setBackgroundColor(mActivity.getResources().getColor(R.color.colorAppThemeLight));

        //SET GROUP NAME
        holder.txtCourseName.setText(""+intStandards.get(position));

        //SET ONCLICK
        holder.relativeItemClick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                /*NavController navController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.STANDARD , intStandards.get(position));
                navController.navigate(R.id.subjectListFragment, bundle);*/

            }
        });

    }


    @Override
    public int getItemCount() {

        return intStandards.size();
    }

}