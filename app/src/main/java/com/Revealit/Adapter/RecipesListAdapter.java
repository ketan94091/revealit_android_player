package com.Revealit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ARviewActivity;
import com.Revealit.Activities.ExoPlayerActivity;
import com.Revealit.Activities.WebViewScreen;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.ModelClasses.GetProductDetailsModel;
import com.Revealit.ModelClasses.GetRecipesDetails;
import com.Revealit.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

public class RecipesListAdapter extends RecyclerView.Adapter<RecipesListAdapter.ViewHolder> {



    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
    private List<GetRecipesDetails.Data> recipesListData = new ArrayList<>();


    public RecipesListAdapter(Context mContext, Activity mActivity, List<GetRecipesDetails.Data> recipesListData) {

        this.mContext= mContext;
        this.mActivity= mActivity;
        this.recipesListData =recipesListData;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtRecipeName,txtRecipeShorDescription,txtIngridience,txtHtmlIngidience,txtHtmlInstructionsSteps,txtInstructions,txtChefName;
        private final RelativeLayout relativePurchase;
        private final ImageView imgRecipeLogo,imgVendorLogo ,imgChef,imgARview;


        public ViewHolder(View mView) {

            super(mView);

            txtRecipeName = (TextView) mView.findViewById(R.id.txtRecipeName);
            txtRecipeShorDescription = (TextView) mView.findViewById(R.id.txtRecipeShorDescription);
            txtIngridience = (TextView) mView.findViewById(R.id.txtIngridience);
            txtHtmlIngidience = (TextView) mView.findViewById(R.id.txtHtmlIngidience);
            txtChefName = (TextView) mView.findViewById(R.id.txtChefName);
            txtInstructions = (TextView) mView.findViewById(R.id.txtInstructions);
            txtHtmlInstructionsSteps = (TextView) mView.findViewById(R.id.txtHtmlInstructionsSteps);

            relativePurchase = (RelativeLayout) mView.findViewById(R.id.relativePurchase);

            imgRecipeLogo = (ImageView)mView.findViewById(R.id.imgRecipeLogo);
            imgVendorLogo = (ImageView)mView.findViewById(R.id.imgVendorLogo);
            imgARview = (ImageView)mView.findViewById(R.id.imgARview);
            imgChef = (ImageView)mView.findViewById(R.id.imgChef);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_recipes_list, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.txtRecipeName.setText(recipesListData.get(position).getName());
        holder.txtRecipeShorDescription.setText(recipesListData.get(position).getDescription());
        holder.txtChefName.setText(recipesListData.get(position).getChef().getName());




        //MAKE TEXTVIEW SCROLLABLE
        holder.txtHtmlIngidience.setMaxLines(1000);
        holder.txtHtmlIngidience.setVerticalScrollBarEnabled(true);
        holder.txtHtmlIngidience.setMovementMethod(new ScrollingMovementMethod());
        holder.txtHtmlIngidience.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isLarger;
                isLarger = ((TextView) v).getLineCount()
                        * ((TextView) v).getLineHeight() > v.getHeight();
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        && isLarger) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);

                } else {
                    v.getParent().requestDisallowInterceptTouchEvent(false);

                }
                return false;
            }
        });


        //MAKE TEXTVIEW SCROLLABLE
        holder.txtHtmlInstructionsSteps.setMaxLines(1000);
        holder.txtHtmlInstructionsSteps.setVerticalScrollBarEnabled(true);
        holder.txtHtmlInstructionsSteps.setMovementMethod(new ScrollingMovementMethod());
        holder.txtHtmlInstructionsSteps.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isLarger;
                isLarger = ((TextView) v).getLineCount()
                        * ((TextView) v).getLineHeight() > v.getHeight();
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        && isLarger) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);

                } else {
                    v.getParent().requestDisallowInterceptTouchEvent(false);

                }
                return false;
            }
        });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.txtHtmlInstructionsSteps.setText(Html.fromHtml(recipesListData.get(position).getFlat_directions(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.txtHtmlInstructionsSteps.setText(Html.fromHtml(recipesListData.get(position).getFlat_directions()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.txtHtmlIngidience.setText(Html.fromHtml(recipesListData.get(position).getFlat_ingredients(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.txtHtmlIngidience.setText(Html.fromHtml(recipesListData.get(position).getFlat_ingredients()));
        }


        Glide.with(mContext)
                .load(""+recipesListData.get(position).getRecipe_advert_img_url())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(holder.imgVendorLogo);


        Glide.with(mContext)
                .load(""+recipesListData.get(position).getRecipe_image())
                .placeholder(R.drawable.placeholder)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(holder.imgRecipeLogo);

        Glide.with(mContext)
                .load(""+recipesListData.get(position).getChef().getImg_url())
                .placeholder(R.drawable.placeholder)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(holder.imgChef);


        //HIDE SHOW AR VIEW ICON
        if(recipesListData.get(position).getGlb_model_url() != null){
            holder.imgARview.setVisibility(View.VISIBLE);
        }else {
            holder.imgARview.setVisibility(View.INVISIBLE);
        }

        //HIDE VENDOR LOGO VIEW ICON
        if(recipesListData.get(position).getRecipe_advert_img_url() != null){
            holder.imgVendorLogo.setVisibility(View.VISIBLE);
        }else {
            holder.imgVendorLogo.setVisibility(View.GONE);
        }


        //HIDE PURCHASE BUTTON VIEW ICON
        if(recipesListData.get(position).getShopping_url() != null){
            holder.relativePurchase.setVisibility(View.VISIBLE);
        }else {
            holder.relativePurchase.setVisibility(View.GONE);
        }


        //HIDE SHORT DESCRIPTION VIEW ICON
        if(recipesListData.get(position).getDescription() != null){
            holder.txtRecipeShorDescription.setVisibility(View.VISIBLE);
        }else {
            holder.txtRecipeShorDescription.setVisibility(View.GONE);
        }



        holder.relativePurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(mActivity, WebViewScreen.class);
                mIntent.putExtra(Constants.RESEARCH_URL ,""+recipesListData.get(position).getShopping_url());
                mIntent.putExtra(Constants.RESEARCH_URL_SPONSER ,"");
                mActivity.startActivity(mIntent);

            }
        });

        holder.imgARview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //OPEN AR VIEW
                if( CommonMethods.isDeviceSupportAR(mActivity)) {
                    Intent mARviewIntent = new Intent(mActivity, ARviewActivity.class);
                    mARviewIntent.putExtra(Constants.AR_VIEW_URL, recipesListData.get(position).getGlb_model_url());
                    mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_NAME, recipesListData.get(position).getArmodel_name());
                    mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_URL, recipesListData.get(position).getArmodel_sponsor());
                    mActivity.startActivity(mARviewIntent);
                }

            }
        });


    }


    @Override
    public int getItemCount() {

        return recipesListData.size();
    }
}