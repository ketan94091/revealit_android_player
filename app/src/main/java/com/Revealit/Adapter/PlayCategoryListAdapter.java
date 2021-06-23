package com.Revealit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Fragments.HomeFragment;
import com.Revealit.ModelClasses.CategoryNamesListModel;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

import java.util.ArrayList;
import java.util.Random;

public class PlayCategoryListAdapter extends RecyclerView.Adapter<PlayCategoryListAdapter.ViewHolder> {



    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
    ArrayList<CategoryNamesListModel.DataBean> strCategoryList = new ArrayList<>();
    DatabaseHelper mDatabaseHelper ;


    public PlayCategoryListAdapter(Context mContext, Activity mActivity, ArrayList<CategoryNamesListModel.DataBean> strCategoryList, DatabaseHelper mDatabaseHelper) {
        this.mContext= mContext;
        this.mActivity= mActivity;
        this.strCategoryList= strCategoryList;
        this.mDatabaseHelper =mDatabaseHelper;

        //OPEN DATABSE
        mDatabaseHelper = new DatabaseHelper(this.mContext);
        mDatabaseHelper.open();

        /*//REMOVE FIRST OBJECT
        //AS FIRST OBJECT WE CONSIDER AS FEATURED AND WE ARE NOT SHOWING IT IN LIST
        if(strCategoryList.get(0).getCategoryName().equalsIgnoreCase("New Mums")) {
            this.strCategoryList.remove("New Mums");
        }*/

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtCategoryTitle ;
        private final RecyclerView recycleIndividualCategoryList;

        public ViewHolder(View mView) {

            super(mView);

            txtCategoryTitle = (TextView) mView.findViewById(R.id.txtCategoryTitle);
            recycleIndividualCategoryList = (RecyclerView)mView.findViewById(R.id.recycleIndividualCategoryList);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_category_play_list, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {



        //SET GROUP NAME
        holder.txtCategoryTitle.setText(""+strCategoryList.get(position).getCategoryName());



        //SET DUMMY LIST VIEW
        PlayIndividualCategoryListAdapter mPlayCategoryListAdapter = new PlayIndividualCategoryListAdapter(mContext,mActivity, mDatabaseHelper.getCategoryWisePlayListByName(strCategoryList.get(position).getCategoryName()));
        holder.recycleIndividualCategoryList.setAdapter(mPlayCategoryListAdapter);

    }


    @Override
    public int getItemCount() {

        return strCategoryList.size();
    }


}