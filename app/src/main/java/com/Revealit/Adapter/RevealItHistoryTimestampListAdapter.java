package com.Revealit.Adapter;

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
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.txtTimeOffsetDisplay.setText(mListAllTimeStamp.get(position).replace(" ",""));
        //holder.txtTimeOffsetDisplay.setPaintFlags(holder.txtTimeOffsetDisplay.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);



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

                openItemList(revealitHistoryData.media_id , mListAllTimeStampOffset.get(position).replace(" ",""),mListAllTimeStamp.get(position).replace(" ",""));

                return true;
            }
        });


    }


    @Override
    public int getItemCount() {

        //return mListAllTimeStamp.size() > 5 ? 5 : mListAllTimeStamp.size();
        return  mListAllTimeStamp.size();
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
                        if(response.body().getData().size() != 0){
                            updateItemsDialogue(dialogView, response.body().getData(), mDialogeForItems , revealitHistoryData.media_title , strTimeStamp);
                        }else {

                            //DISMISS DIALOGUE
                            mDialogeForItems.dismiss();

                            Intent mIntent = new Intent(mActivity, ExoPlayerActivity.class);
                            mIntent.putExtra(Constants.MEDIA_URL, "" + revealitHistoryData.media_url);
                            mIntent.putExtra(Constants.MEDIA_ID, "" + revealitHistoryData.media_id);
                            mIntent.putExtra(Constants.VIDEO_NAME, "" + revealitHistoryData.media_title);
                            mIntent.putExtra(Constants.VIDEO_SEEK_TO, strItemId);
                            mIntent.putExtra(Constants.IS_VIDEO_SEEK, true);
                            mActivity.startActivity(mIntent);


                        }



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
}