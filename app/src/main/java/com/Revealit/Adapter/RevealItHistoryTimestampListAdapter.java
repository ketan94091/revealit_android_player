package com.Revealit.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ExoPlayerActivity;
import com.Revealit.Activities.LoginActivityActivity;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.ItemListFromItemIdModel;
import com.Revealit.ModelClasses.RevealitHistoryModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
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

public class RevealItHistoryTimestampListAdapter extends RecyclerView.Adapter<RevealItHistoryTimestampListAdapter.ViewHolder> {


    private final SessionManager mSessionManager;
    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
    private RevealitHistoryModel.Data revealitHistoryData;
    private ArrayList<String> mListAllTimeStamp = new ArrayList<String>();
    private ArrayList<String> mListAllTimeStampOffset = new ArrayList<String>();
    private AlertDialog mDialogeForItems;
    private ProgressBar progressLoadData;
    private int dialogHight,dialogWidth;
    private TextView txtNoPublishedVideo;
    private LinearLayout linearHeaderView;
    int intMediaId, intMediaOffset, intMatchId;


    public RevealItHistoryTimestampListAdapter(Context mContext, Activity mActivity, RevealitHistoryModel.Data revealitHistoryData) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.revealitHistoryData =revealitHistoryData;

        //REPLACE STRING PARENTHESIS [
        String replace = revealitHistoryData.allTimeStamp.replace("[","");
        //REPLACE STRING PARENTHESIS ]
        String replace1 = replace.replace("]","");
        //SEPRATE STRING BY COMMA
        String[] timestampSplit = replace1.split(",");
        //CONVERT STRING TO ARRAYLIST
        this.mListAllTimeStamp = new ArrayList<String>(Arrays.asList(timestampSplit));
        //REVERSE ARRAY LIST
        //Collections.reverse(this.mListAllTimeStamp.subList(this.mListAllTimeStamp.size(),this.mListAllTimeStamp.size()));

        //REPLACE STRING OFFSET PARENTHESIS [
        String replaceOffset = revealitHistoryData.allTimeStampOffset.replace("[","");
        //REPLACE STRING OFFSET PARENTHESIS ]
        String replace1Offset = replaceOffset.replace("]","");
        //SEPRATE STRING OFFSET BY COMMA
        String[] timestampSplitOffset = replace1Offset.split(",");
        //CONVERT STRING OFFSET TO ARRAYLIST
        this.mListAllTimeStampOffset = new ArrayList<String>(Arrays.asList(timestampSplitOffset));
        //REVERSE ARRAY LIST OFFSET
        //Collections.reverse(this.mListAllTimeStampOffset.subList(this.mListAllTimeStampOffset.size(),this.mListAllTimeStampOffset.size()));


       //OPEN SESSION MANAGER
        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtTimeOffsetDisplay;


        public ViewHolder(View mView) {

            super(mView);

            txtTimeOffsetDisplay = (TextView) mView.findViewById(R.id.txtTimeOffsetDisplay);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_revealit_history_timestamp_list, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.txtTimeOffsetDisplay.setText(mListAllTimeStamp.get(position).replace(" ",""));

        holder.txtTimeOffsetDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(mActivity, ExoPlayerActivity.class);
                mIntent.putExtra(Constants.MEDIA_URL, "" + revealitHistoryData.media_url);
                mIntent.putExtra(Constants.MEDIA_ID, "" + revealitHistoryData.media_id);
                mIntent.putExtra(Constants.VIDEO_NAME, "" + revealitHistoryData.media_title);
                mIntent.putExtra(Constants.VIDEO_SEEK_TO, mListAllTimeStampOffset.get(position).replace(" ",""));
                mIntent.putExtra(Constants.IS_VIDEO_SEEK, true);
                mActivity.startActivity(mIntent);
            }
        });

        holder.txtTimeOffsetDisplay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //SET DATA
                intMediaId =revealitHistoryData.media_id;
                intMediaOffset = Integer.valueOf(mListAllTimeStampOffset.get(position).replace(" ",""));
                intMatchId = revealitHistoryData.match_id;

                openItemList(revealitHistoryData.media_id , mListAllTimeStampOffset.get(position).replace(" ",""),mListAllTimeStamp.get(position).replace(" ",""));

                return true;
            }
        });


    }


    @Override
    public int getItemCount() {

        return mListAllTimeStamp.size() > 6 ? 6 : mListAllTimeStamp.size();
        //return  mListAllTimeStamp.size();
    }

    private void openItemList(int intMediaId, String strItemId, String strTimeStamp) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View mView = inflater.inflate(R.layout.alert_dialog_item_list_listenscreen, null);
        dialogBuilder.setView(mView);

        mDialogeForItems = dialogBuilder.create();
        mDialogeForItems.setCancelable(true);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 40);
        mDialogeForItems.getWindow().setBackgroundDrawable(inset);


        //SET CURRENT PROGRESSBAR
        progressLoadData = (ProgressBar) mView.findViewById(R.id.progressLoadData);
        txtNoPublishedVideo=(TextView)mView.findViewById(R.id.txtNoPublishedVideo);
        linearHeaderView=(LinearLayout)mView.findViewById(R.id.linearHeaderView);

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
        callGetItesmsList(mView, intMediaId, strItemId, mDialogeForItems,strTimeStamp);


        mDialogeForItems.show();

    }

    private void callGetItesmsList(View dialogView, int intMediaId, String strItemId, AlertDialog mDialogeForItems, String strTimeStamp) {

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
                        updateItemsDialogue(dialogView, response.body().getData(), mDialogeForItems , revealitHistoryData.media_title , strTimeStamp);

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

    private void updateItemsDialogue(View dialogView, List<ItemListFromItemIdModel.Data> itemListData, AlertDialog mDialogeForItems, String strMediaTitle, String strTimeStamp) {


        //DISPLAY TITLE
        TextView txtVideTitle =(TextView)dialogView.findViewById(R.id.txtVideoTitle);
        txtVideTitle.setText(strMediaTitle);

        //DISPLAY TITLE
        TextView txtTimeStamp =(TextView)dialogView.findViewById(R.id.txtTimeStamp);
        txtTimeStamp.setText(strTimeStamp);

        //CLOSE DIALOGE
        ImageView imgCloseDailoge = (ImageView) dialogView.findViewById(R.id.imgCloseDailoge);
        imgCloseDailoge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialogeForItems.dismiss();
            }
        });

        //LINEAR DELETE VIEW
        LinearLayout mlinearDeleteTimeStamp = (LinearLayout)dialogView.findViewById(R.id.linearDeleteTimeStamp);
        mlinearDeleteTimeStamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        //CREATE DIALOGUE
                       createDeleteDialogue(mDialogeForItems);
                    }
                });
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

        //VISIBLE HEADER VIEW
        linearHeaderView.setVisibility(View.VISIBLE);

        //DISPLAY TEXT
        if(itemListData.size() == 0)
        txtNoPublishedVideo.setVisibility(View.VISIBLE);


    }

    private void createDeleteDialogue(AlertDialog mDialogeForItems) {

            android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mActivity);
            dialogBuilder.setCancelable(false);
            LayoutInflater inflater = mActivity.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.delete_timestamp_confirmation_dailoague, null);
            mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogBuilder.setView(dialogView);


            final AlertDialog mAlertDialog = dialogBuilder.create();
            TextView txtYes = (TextView) dialogView.findViewById(R.id.txtYes);
            TextView txtNo = (TextView) dialogView.findViewById(R.id.txtNo);


        txtNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mAlertDialog.dismiss();

                }
            });


        txtYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mAlertDialog.dismiss();

                    callRemoveTimeStamp(mDialogeForItems);


                }
            });

            mAlertDialog.show();

        }

    private void callRemoveTimeStamp(AlertDialog mDialogeForItems) {

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
        paramObject.addProperty("match_id", intMatchId);
        paramObject.addProperty("media_id", intMediaId);
        paramObject.addProperty("playback_offset", intMediaOffset);

        Call<JsonElement> call = patchService1.removeSingleTimeStamp(paramObject);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ callRemoveTimeStamp: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callRemoveTimeStamp: ", "" + response.code());


                if (response.code() == Constants.API_CODE_200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    mDialogeForItems.dismiss();

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
            public void onFailure(Call<JsonElement> call, Throwable t) {



            }
        });

    }

}