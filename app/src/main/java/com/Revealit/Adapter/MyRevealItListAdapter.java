package com.Revealit.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ExoPlayerActivity;
import com.Revealit.Activities.LoginActivityActivity;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.CategoryWisePlayListModel;
import com.Revealit.ModelClasses.ItemListFromItemIdModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
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

public class MyRevealItListAdapter extends RecyclerView.Adapter<MyRevealItListAdapter.ViewHolder> {


    private final SessionManager mSessionManager;
    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
    // private  ArrayList<CategoryWisePlayListModel.DataBean> testList   = new ArrayList<>();
    private List<CategoryWisePlayListModel.DataBean> testList = new ArrayList<>();
    private ArrayList<Long> mLongRevealTime = new ArrayList<>();
    private int timeStampDummy = 5;
    private AlertDialog mDialogeForItems;
    private ProgressBar progressLoadData;
    private int dialogHight,dialogWidth;
    private TextView txtNoPublishedVideo;

    public MyRevealItListAdapter(Context mContext, Activity mActivity, List<CategoryWisePlayListModel.DataBean> testList, DatabaseHelper mDatabaseHelper, ArrayList<Long> mLongRevealTime) {

        this.mContext = mContext;
        this.mActivity = mActivity;
        this.testList = testList;
        this.mLongRevealTime = mLongRevealTime;

        //OPEN SESSION MANAGER
        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtVideoName, txtVideoNameSubTitle, txtTimeOffsetDisplay,txtTime;
        private final ImageView imgCoverArt;
        private final ProgressBar progressImgLoad;
        private final RelativeLayout relatativeMain;


        public ViewHolder(View mView) {

            super(mView);

            imgCoverArt = (ImageView) mView.findViewById(R.id.imgCoverArt);

            txtVideoName = (TextView) mView.findViewById(R.id.txtVideoName);
            txtVideoNameSubTitle = (TextView) mView.findViewById(R.id.txtVideoNameSubTitle);
            txtTime = (TextView) mView.findViewById(R.id.txtTime);
            txtTimeOffsetDisplay = (TextView) mView.findViewById(R.id.txtTimeOffsetDisplay);
            progressImgLoad = (ProgressBar) mView.findViewById(R.id.progressImgLoad);
            relatativeMain = (RelativeLayout) mView.findViewById(R.id.relatativeMain);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_my_revealit_list, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        //LOAD COVER IMAGE WITH GLIDE
        Glide.with(mActivity)
                .load("" + testList.get(position).getMediaCoverArt())
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(10)))
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

        holder.txtVideoName.setText(testList.get(position).getMediaTitle());
        //holder.txtVideoNameSubTitle.setText(testList.get(position).getMediaShowTitle());
        //holder.txtTime.setText("" + CommonMethods.timeDifference(mLongRevealTime.get(position)));

        //SET DUMMY TIME STAMPS
        holder.txtTimeOffsetDisplay.setText("0:00:"+(timeStampDummy + position));
        holder.txtTimeOffsetDisplay.setPaintFlags(holder.txtTimeOffsetDisplay.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);



        holder.txtTimeOffsetDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(mActivity, ExoPlayerActivity.class);
                mIntent.putExtra(Constants.MEDIA_URL, "" + testList.get(position).getMediaUrl());
                mIntent.putExtra(Constants.MEDIA_ID, "" + testList.get(position).getMediaID());
                mIntent.putExtra(Constants.VIDEO_NAME, "" + testList.get(position).getMediaShowTitle());
                mIntent.putExtra(Constants.VIDEO_SEEK_TO, ""+(timeStampDummy + position));
                mIntent.putExtra(Constants.IS_VIDEO_SEEK, true);
                mActivity.startActivity(mIntent);

            }
        });

        holder.txtTimeOffsetDisplay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                openItemList(testList.get(position).getMediaID() , ""+(timeStampDummy + position));

                return true;
            }
        });



    }

    private void openItemList(int intMediaId, String strItemId) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_item_list_listenscreen, null);
        dialogBuilder.setView(dialogView);

        mDialogeForItems = dialogBuilder.create();
        mDialogeForItems.setCancelable(true);

        //SET CURRENT PROGRESSBAR
        progressLoadData = (ProgressBar) dialogView.findViewById(R.id.progressLoadData);
        txtNoPublishedVideo=(TextView)dialogView.findViewById(R.id.txtNoPublishedVideo);

        final View decorView = mDialogeForItems.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        decorView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        //GET HEIGHT & WIDTH OF THE DIALOG WHICH WE WILL USE FOR RESIZING THE VIEW
                        dialogWidth = decorView.getMeasuredWidth();
                        dialogHight = decorView.getMeasuredHeight();

                    }

                });

        //GET LIST OF PRODUCTS
        callGetItesmsList(dialogView, intMediaId, strItemId, mDialogeForItems);


        mDialogeForItems.show();

    }

    private void callGetItesmsList(View dialogView, int intMediaId, String strItemId, AlertDialog mDialogeForItems) {

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
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty(Constants.MEDIA_ID_FOR_ITEMS, intMediaId);
        paramObject.addProperty(Constants.PLAYBACK_OFFSET_FOR_ITEMS, Integer.valueOf(strItemId));

        Call<ItemListFromItemIdModel> call = patchService1.getItemListFromItemID(intMediaId,  Integer.valueOf(strItemId));

        call.enqueue(new Callback<ItemListFromItemIdModel>() {
            @Override
            public void onResponse(Call<ItemListFromItemIdModel> call, Response<ItemListFromItemIdModel> response) {

                CommonMethods.printLogE("Response @ callGetItesmsList: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callGetItesmsList: ", "" + response.code());

                if (response.code() == Constants.API_CODE_200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ callGetItesmsList : ", "" + gson.toJson(response.body()));

                    //UPDATE UI
                    updateItemsDialogue(dialogView, response.body().getData(), mDialogeForItems);


                } else if (response.code() == Constants.API_CODE_401) {

                    progressLoadData.setVisibility(View.GONE);
                    mDialogeForItems.dismiss();

                    Intent mLoginIntent = new Intent(mActivity, LoginActivityActivity.class);
                    mActivity.startActivity(mLoginIntent);
                    mActivity.finish();

                } else {
                    progressLoadData.setVisibility(View.GONE);
                    mDialogeForItems.dismiss();

                    CommonMethods.buildDialog(mContext, mActivity.getResources().getString(R.string.strNoDataFound));

                }
            }

            @Override
            public void onFailure(Call<ItemListFromItemIdModel> call, Throwable t) {


            }
        });

    }

    private void updateItemsDialogue(View dialogView, List<ItemListFromItemIdModel.Data> itemListData, AlertDialog mDialogeForItems) {

        //CLOSE DIALOGE
        ImageView imgCloseDailoge = (ImageView) dialogView.findViewById(R.id.imgCloseDailoge);
        imgCloseDailoge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialogeForItems.dismiss();
            }
        });

        //SET ITEMS LIST
        RecyclerView recyclerViewRecipesList = (RecyclerView) dialogView.findViewById(R.id.recycleRecipesList);
        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(mActivity);
        recyclerViewRecipesList.setLayoutManager(recylerViewLayoutManager);

        ItemsListForListenScreenAdapter mItemsListForListenScreenAdapter = new ItemsListForListenScreenAdapter(mContext, mActivity, itemListData);
        recyclerViewRecipesList.setAdapter(mItemsListForListenScreenAdapter);


        RelativeLayout relativeContentDialogView = (RelativeLayout) dialogView.findViewById(R.id.relativeContent);
        relativeContentDialogView.setVisibility(View.VISIBLE);

        //HIDE PROGRESS AFTER SETTING ALL DATA
        progressLoadData.setVisibility(View.GONE);

        //DISPLAY TEXT
        if(itemListData.size() == 0)
            txtNoPublishedVideo.setVisibility(View.VISIBLE);


    }

    @Override
    public int getItemCount() {

        return testList.size();
    }
}