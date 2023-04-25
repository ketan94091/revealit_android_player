package com.Revealit.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.SavedItemListActivity;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.SavedProductListModel;
import com.Revealit.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class SavedItemsListsAdapter extends RecyclerView.Adapter<SavedItemsListsAdapter.ViewHolder> {


    private View view;
    private Context mContext;
    private SavedItemListActivity mActivity;
    private ViewHolder viewHolder;
    public ArrayList<SavedProductListModel.Item> data;
    private int selectedPosition = -1;
    private RecyclerView recycleAccountList;
    private String strItemId;
    private SessionManager mSessionManager;
    private BottomSheetDialog bottomSheetDialog;


    public SavedItemsListsAdapter(Context mContext, SavedItemListActivity mActivity, ArrayList<SavedProductListModel.Item> data) {

        this.mContext = mContext;
        this.mActivity = mActivity;
        this.data = data;
        this.recycleAccountList =recycleAccountList;
        this.strItemId =strItemId;
        this.mSessionManager = mSessionManager;
        this.bottomSheetDialog= bottomSheetDialog;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtListName,txtTotalItemsInList;
        private final CheckBox checkListName;


        public ViewHolder(View mView) {

            super(mView);

            txtListName = (TextView) mView.findViewById(R.id.txtListName);
            txtTotalItemsInList = (TextView) mView.findViewById(R.id.txtTotalItemsInList);
            checkListName = (CheckBox) mView.findViewById(R.id.checkListName);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_saved_list_names, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.txtListName.setText(data.get(position).getProduct_name());
        holder.txtTotalItemsInList.setText(data.get(position).getImage());


    }



    @Override
    public int getItemCount() {

        return data.size();
    }


}