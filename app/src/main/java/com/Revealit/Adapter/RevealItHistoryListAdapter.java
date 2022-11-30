package com.Revealit.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.Interfaces.RemoveListenHistory;
import com.Revealit.ModelClasses.RevealitHistoryModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    private ArrayList<String> mSelectedVideoIds = new ArrayList<>();



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
        RevealItHistoryTimestampListAdapter mRevealItHistoryTimestampListAdapter = new RevealItHistoryTimestampListAdapter(mContext, mActivity, revealitHistoryData.get(position));
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

        holder.imgCoverArt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //showBottomSheetDialog(revealitHistoryData.get(position).getMedia_id());
                return true;
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

        holder.chcekBoxSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean mChecked) {

                if(!mChecked){
                    if(mSelectedVideoIds.contains(String.valueOf(revealitHistoryData.get(position).getMedia_id()))){
                        mSelectedVideoIds.remove(String.valueOf(revealitHistoryData.get(position).getMedia_id()));
                    }
                }else{
                    mSelectedVideoIds.add(String.valueOf(revealitHistoryData.get(position).getMedia_id()));
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
    public  ArrayList<String> getSelectedIds(){
        return  mSelectedVideoIds;
    }


    @Override
    public int getItemCount() {

        return revealitHistoryData.size();
    }

    private void showBottomSheetDialog(int media_id) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_for_clear_listen_history);

        TextView txtCancel = bottomSheetDialog.findViewById(R.id.txtCancel);
        TextView txtClear = bottomSheetDialog.findViewById(R.id.txtClear);
        TextView txtClearAll = bottomSheetDialog.findViewById(R.id.txtClearAll);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();

            }
        });
        txtClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //FIRST CLOSE DIALOGUE AND THEN CALL API
                bottomSheetDialog.cancel();



                //CONDITION
                //IF TRUE -> APP IS IN LIVE MODE - MEANS CALL API'S
                //IF ELSE -> APP IS IN SIMULATION MODE - MEANS SAVE AND DELETE DATA FROM LOCAL
                if(mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)){

                    //CALL API REMOVE SINGLE VIDEO
                    callRemoveVideo(false, media_id);
                }else{

                  //IN ELSE CONDITION
                  //REMOVE SINGLE VIDEO DATA FROM DATABASE
                  mDatabaseHelper.clearSimulationHistoryItem(media_id);

                  //UPDATE LIST THROUGH INTERFACE
                  mRemoveListenHistory.removeListenHistory(false);

                }


            }
        });
        txtClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FIRST CLOSE DIALOGUE AND THEN CALL API
                bottomSheetDialog.cancel();

                //CALL API REMOVE ALL VIDEOS
                if(mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)){
                    callRemoveVideo(true, media_id);
                }else{

                    //CLEAR ALL SIMULATION DATA FROM DATABASE
                    mDatabaseHelper.clearSimulationHistoryDataTable();

                    //UPDATE LIST
                    mRemoveListenHistory.removeListenHistory(false);

                }

            }
        });

        bottomSheetDialog.show();
    }

    private void callRemoveVideo(boolean isClearHistory, int media_id) {

        //SHOW PROGRESS DIALOG
        ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage(mContext.getResources().getString(R.string.strPleaseWait));
        pDialog.setCancelable(false);
        pDialog.show();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request original = chain.request();

                okhttp3.Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + " " + mSessionManager.getPreference(Constants.AUTH_TOKEN))
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();


        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        Call<JsonElement> call;

        List<Integer> mediaIds = new ArrayList<>();
        mediaIds.add(media_id);

        if (!isClearHistory)

            call = patchService1.removeHistory(mediaIds);
        else {
            call = patchService1.removeWholeHistory(isClearHistory);
        }

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ callRemoveVideo: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callRemoveVideo: ", "" + response.code());


                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

                    CommonMethods.printLogE("Response @ callRemoveVideo: ", "Video removed");
                    mRemoveListenHistory.removeListenHistory(true);


                }

                //HIDE PROGRESSBAR
                pDialog.cancel();

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                CommonMethods.printLogE("Response @ callRemoveVideo Error", "" + t.getMessage());

                //BUILD ERROR DIALOG DIALOG
                CommonMethods.buildDialog(mContext, Constants.SOMETHING_WENT_WRONG);


                //HIDE PROGRESSBAR
                pDialog.cancel();


            }
        });


    }
}