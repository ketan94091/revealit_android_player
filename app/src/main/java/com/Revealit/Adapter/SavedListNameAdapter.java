package com.Revealit.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ProductBuyingScreenActivity;
import com.Revealit.ModelClasses.SavedProductListModel;
import com.Revealit.R;

import java.util.List;

public class SavedListNameAdapter extends RecyclerView.Adapter<SavedListNameAdapter.ViewHolder> {


    private View view;
    private Context mContext;
    private ProductBuyingScreenActivity mActivity;
    private ViewHolder viewHolder;
    public List<SavedProductListModel.Data> data;


    public SavedListNameAdapter(Context mContext, ProductBuyingScreenActivity mActivity, List<SavedProductListModel.Data> data) {

        this.mContext = mContext;
        this.mActivity = mActivity;
        this.data = data;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtListName,txtTotalItemsInList;



        public ViewHolder(View mView) {

            super(mView);

            txtListName = (TextView) mView.findViewById(R.id.txtListName);
            txtTotalItemsInList = (TextView) mView.findViewById(R.id.txtTotalItemsInList);

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





    }



    @Override
    public int getItemCount() {

        return data.size();
    }


}