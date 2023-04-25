package com.Revealit.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.SavedItemListActivity;
import com.Revealit.Activities.UserSavedListActivity;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.SavedProductListModel;
import com.Revealit.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.util.List;

public class UserSavedListsAdapter extends RecyclerView.Adapter<UserSavedListsAdapter.ViewHolder> {


    private View view;
    private Context mContext;
    private UserSavedListActivity mActivity;
    private ViewHolder viewHolder;
    public List<SavedProductListModel.Data> data;
    private int selectedPosition = -1;
    private RecyclerView recycleAccountList;
    private String strItemId;
    private SessionManager mSessionManager;
    private BottomSheetDialog bottomSheetDialog;


    public UserSavedListsAdapter(Context mContext, UserSavedListActivity mActivity, List<SavedProductListModel.Data> data) {

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
        private final RelativeLayout relatativeMain;


        public ViewHolder(View mView) {

            super(mView);

            txtListName = (TextView) mView.findViewById(R.id.txtListName);
            txtTotalItemsInList = (TextView) mView.findViewById(R.id.txtTotalItemsInList);
            checkListName = (CheckBox) mView.findViewById(R.id.checkListName);
            relatativeMain = (RelativeLayout) mView.findViewById(R.id.relatativeMain);

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

        holder.txtListName.setText(data.get(position).getName());
        holder.txtTotalItemsInList.setText(data.get(position).getItems().size() + ": Items");

        holder.checkListName.setVisibility(View.GONE);

        holder.relatativeMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Gson mGson = new Gson();
                Intent intent = new Intent(mActivity, SavedItemListActivity.class);
                intent.putExtra("data", mGson.toJson( data.get(position).getItems()));
                mActivity.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {

        return data.size();
    }


}