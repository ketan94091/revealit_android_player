package com.Revealit.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ExoPlayerActivity;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.Interfaces.RemoveListenHistory;
import com.Revealit.ModelClasses.RevealitHistoryModel;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.Utils.CacheDataSourceFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import java.util.ArrayList;
import java.util.Arrays;

public class RevealItHistoryListAdapter extends RecyclerView.Adapter<RevealItHistoryListAdapter.ViewHolder> {


    private final DatabaseHelper mDatabaseHelper;
    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
    public ArrayList<RevealitHistoryModel.Data> revealitHistoryData;
    private LinearLayoutManager recylerViewLayoutManager;
    private SessionManager mSessionManager;
    private RemoveListenHistory mRemoveListenHistory;
    private boolean isCheckSelected,shouldCheckBoxVisible;
    private ArrayList<Integer> mSelectedVideoIds = new ArrayList<>();
    private ArrayList<String> mListAllTimeStampOffset = new ArrayList<String>();




    public RevealItHistoryListAdapter(Context mContext, Activity mActivity, ArrayList<RevealitHistoryModel.Data> revealitHistoryData, RemoveListenHistory mRemoveListenHistory, boolean isCheckSelected,boolean shouldCheckBoxVisible) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.revealitHistoryData = revealitHistoryData;
        this.mRemoveListenHistory =mRemoveListenHistory;
        this.isCheckSelected =isCheckSelected;
        this.shouldCheckBoxVisible =shouldCheckBoxVisible;


        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();


    }

    public void updateListData(ArrayList<RevealitHistoryModel.Data> mRevealitHistoryList, boolean isCheckBoxSelected,boolean shouldCheckBoxVisible) {

        //CLEAR CURRENT LIST
        revealitHistoryData.clear();

        //ADD FRESH LIST
        revealitHistoryData.addAll(mRevealitHistoryList);

        //UPDATE CHECK BOX DATA
        this.isCheckSelected = isCheckBoxSelected;

        //UPDATE CHECK BOX VISIBILITY
        this.shouldCheckBoxVisible = shouldCheckBoxVisible;

        //CLEAR SELECTED IDS
        mSelectedVideoIds.clear();

        //NOTIFY LISTENER
       notifyDataSetChanged();

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtVideoName;
        private final ImageView imgCoverArt;
        private final ProgressBar progressImgLoad;
        private final RelativeLayout relatativeMain;
        private final RecyclerView recycleRevealHistoryTimestamps;
        private final CheckBox chcekBoxSelection;


        public ViewHolder(View mView) {

            super(mView);

            imgCoverArt = (ImageView) mView.findViewById(R.id.imgCoverArt);

            txtVideoName = (TextView) mView.findViewById(R.id.txtVideoName);
            progressImgLoad = (ProgressBar) mView.findViewById(R.id.progressImgLoad);
            relatativeMain = (RelativeLayout) mView.findViewById(R.id.relatativeMain);
            recycleRevealHistoryTimestamps = (RecyclerView) mView.findViewById(R.id.recycleRevealHistoryTimestamps);
            chcekBoxSelection =(CheckBox)mView.findViewById(R.id.chcekBoxSelection);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_revealit_history_list, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,2);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // set Horizontal Orientation
        holder.recycleRevealHistoryTimestamps.setLayoutManager(gridLayoutManager);


        //SET ADAPTER TO TIME STAMP LIST
        //SET CATEGORY LIST
        RevealItHistoryTimestampListAdapter mRevealItHistoryTimestampListAdapter = new RevealItHistoryTimestampListAdapter(mContext, mActivity, revealitHistoryData.get(position),mRemoveListenHistory);
        holder.recycleRevealHistoryTimestamps.setAdapter(mRevealItHistoryTimestampListAdapter);
       // holder.recycleRevealHistoryTimestamps.setLayoutManager(new GridLayoutManager(mContext,3, LinearLayoutManager.VERTICAL, true));


        //PRE-LOAD VIDEOS
        preloadVideos( revealitHistoryData.get(position).getMedia_url());

        //LOAD COVER IMAGE WITH GLIDE
        Glide.with(mActivity)
                .load("" + revealitHistoryData.get(position).getMedia_cover_art())
                .placeholder(R.drawable.placeholder)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressImgLoad.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressImgLoad.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imgCoverArt);

        holder.txtVideoName.setText(revealitHistoryData.get(position).getMedia_title());

        holder.imgCoverArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //REPLACE STRING OFFSET PARENTHESIS [
                String replaceOffset = revealitHistoryData.get(position).getAllTimeStampOffset().replace("[","");
                //REPLACE STRING OFFSET PARENTHESIS ]
                String replace1Offset = replaceOffset.replace("]","");
                //SEPRATE STRING OFFSET BY COMMA
                String[] timestampSplitOffset = replace1Offset.split(",");
                //CONVERT STRING OFFSET TO ARRAYLIST
                mListAllTimeStampOffset = new ArrayList<String>(Arrays.asList(timestampSplitOffset));


                Intent mIntent = new Intent(mActivity, ExoPlayerActivity.class);
                mIntent.putExtra(Constants.MEDIA_URL, "" + revealitHistoryData.get(position).media_url);
                mIntent.putExtra(Constants.MEDIA_ID, "" + revealitHistoryData.get(position).media_id);
                mIntent.putExtra(Constants.VIDEO_NAME, "" + revealitHistoryData.get(position).media_title);
                mIntent.putExtra(Constants.VIDEO_SEEK_TO, mListAllTimeStampOffset.get(mListAllTimeStampOffset.size() - 1).replaceAll(" ",""));
                mIntent.putExtra(Constants.IS_VIDEO_SEEK, false);
                mActivity.startActivity(mIntent);

            }
        });

        //CHECK CHECK BOX
        if(shouldCheckBoxVisible){
            holder.chcekBoxSelection.setVisibility(View.VISIBLE);

        }else{
            holder.chcekBoxSelection.setVisibility(View.GONE);
        }

        //UPDATE CHECKBOX CHECKED
        holder.chcekBoxSelection.setChecked(isCheckSelected);


        //CLEAR ARRAY IS USER DE-SELECT ALL
        if(!isCheckSelected){
            mSelectedVideoIds.clear();
        }


        holder.chcekBoxSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean mChecked) {

                if(mSelectedVideoIds.contains(revealitHistoryData.get(position).getMedia_id())){

                    for(int i=0;i<mSelectedVideoIds.size();i++){

                       if(mSelectedVideoIds.get(i) == revealitHistoryData.get(position).getMedia_id()){
                           mSelectedVideoIds.remove(i);
                       }
                    }

                }else{
                    mSelectedVideoIds.add(revealitHistoryData.get(position).getMedia_id());

                }


                //UPDATE LIST THROUGH INTERFACE
                mRemoveListenHistory.getSelectedIds(mSelectedVideoIds);


            }
        });

    }

    private void preloadVideos(String media_url) {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        SimpleExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        MediaSource audioSource = new ExtractorMediaSource(Uri.parse(media_url),
                new CacheDataSourceFactory(mContext, 100 * 1024 * 1024, 5 * 1024 * 1024), new DefaultExtractorsFactory(), null, null);
    }
    public  ArrayList<Integer> getSelectedIds(){
        return  mSelectedVideoIds;
    }


    @Override
    public int getItemCount() {

        return revealitHistoryData.size();
    }


}