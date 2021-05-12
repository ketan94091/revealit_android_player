package com.Revealit.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.Activities.LoginActivityActivity;
import com.Revealit.Adapter.RewardSummeryListAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.GetAccountDetailsModel;
import com.Revealit.ModelClasses.RewardHistoryDatabaseModel;
import com.Revealit.ModelClasses.RewardHistoryModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WalletFragment extends Fragment implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "WalletFragment";

    private View mView;

    private GridLayoutManager mGridLayoutManager;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private RecyclerView recycleRewardHistory;
    private LinearLayoutManager recylerViewLayoutManager;
    private RewardSummeryListAdapter2 mRewardSummeryListAdapter2;
    private ImageView imgRefresh;
    private TextView txtAmount,txtAccountName;
    private RelativeLayout relativeAccountDetails;
    private LinearLayout linearRewardHistory;
    private int intPageCount = 0;
    private int intTotalPageCount = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setTitle(getString(R.string.app_name));
        mView = inflater.inflate(R.layout.fragment_wallet, container, false);

        return mView;

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setIds() {

        mActivity = getActivity();
        mContext = getActivity();

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        //REFRESH INITIAL COUNT
        intPageCount = 0;
        intTotalPageCount=0;

        imgRefresh = (ImageView)mView.findViewById(R.id.imgRefresh);

        txtAccountName =(TextView)mView.findViewById(R.id.txtAccountName);
        txtAmount =(TextView)mView.findViewById(R.id.txtAmount);

        relativeAccountDetails = (RelativeLayout)mView.findViewById(R.id.relativeAccountDetails);

        linearRewardHistory =(LinearLayout)mView.findViewById(R.id.linearRewardHistory);

        recycleRewardHistory = (RecyclerView)mView.findViewById(R.id.recycleRewardHistory);
        recylerViewLayoutManager = new LinearLayoutManager(mActivity);
        recycleRewardHistory.setLayoutManager(recylerViewLayoutManager);


        //SET VISIBILITY
        relativeAccountDetails.setVisibility(View.GONE);
        linearRewardHistory.setVisibility(View.GONE);

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            //SET IDS
            setIds();
            setOnClicks();

            //CALL WALLET(ACCOUNTS) DETAILS
            callWalletDetails();
        }
    }

    private void setOnClicks() {


        imgRefresh.setOnClickListener(this);
    }


    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.imgRefresh:

                //REFRESH INITIAL COUNT
                intPageCount = 0;
                intTotalPageCount=0;

                //CALL WALLET(ACCOUNTS) DETAILS
                callWalletDetails();

                break;

        }

    }

    private void callWalletDetails() {
        //DISPLAY DIALOG
        CommonMethods.showDialog(mContext);

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
                .baseUrl(Constants.API_END_POINTS)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

        Call<GetAccountDetailsModel> call = patchService1.getUserAccountDetails("garry");

        call.enqueue(new Callback<GetAccountDetailsModel>() {
            @Override
            public void onResponse(Call<GetAccountDetailsModel> call, Response<GetAccountDetailsModel> response) {

                CommonMethods.printLogE("Response @ callWalletDetails : ","" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callWalletDetails : " ,""+ response.code());

                //CLOSE DIALOG
                CommonMethods.closeDialog();


                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ callWalletDetails : " ,""+ gson.toJson(response.body()));

                    //UPDATE UI
                    relativeAccountDetails.setVisibility(View.VISIBLE);
                    txtAccountName.setText(""+response.body().getData().getAccountName());
                    txtAmount.setText(""+response.body().getData().getTokens().get(0).getInUsd());


                    //CALL REWARD HISTORY
                    callRewardHistory(0);

                }else {

                    CommonMethods.buildDialog(mContext,getResources().getString(R.string.strSomethingWentWrong));
                }
            }

            @Override
            public void onFailure(Call<GetAccountDetailsModel> call, Throwable t) {

                CommonMethods.printLogE("Response @ callWalletDetails : " ,""+t);


                CommonMethods.buildDialog(mContext,getResources().getString(R.string.strSomethingWentWrong));


                CommonMethods.closeDialog();

            }
        });

    }

    private void callRewardHistory(int i) {

        //DISPLAY DIALOG
        CommonMethods.showDialog(mContext);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                okhttp3.Request requestOriginal = chain.request();

                okhttp3.Request request = requestOriginal.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + " " + mSessionManager.getPreference(Constants.AUTH_TOKEN))
                        .method(requestOriginal.method(), requestOriginal.body())
                        .build();


                return chain.proceed(request);
            }
        });
        final OkHttpClient httpClient1 = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_END_POINTS_STAGING)
                .client(httpClient1.newBuilder().connectTimeout(10, TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).build())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient1)
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);


        Call<RewardHistoryModel> call = patchService1.getRewardHistory();

        call.enqueue(new Callback<RewardHistoryModel>() {
            @Override
            public void onResponse(Call<RewardHistoryModel> call, retrofit2.Response<RewardHistoryModel> response) {


                //CLOSE DIALOG
                CommonMethods.closeDialog();

                CommonMethods.printLogE("Response @ callRewardHistory : ","" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callRewardHistory : " ,""+ response.code());


                if (response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ callRewardHistory : " ,""+ gson.toJson(response.body()));

                    //CLEAR TABLE
                    mDatabaseHelper.clearRewardTable();

                    //SET TOTAL PAGE COUNT
                    intTotalPageCount = response.body().getMeta().getTotal();

                    //SAVE DATA IN TO DATABASE
                    if (response.body().getData().size() != 0){

                        for (int i =0 ; i < response.body().getData().size() ; i++) {
                            mDatabaseHelper.insertRewardHistoryData(""+response.body().getData().get(i).getAmount(),
                                    ""+response.body().getData().get(i).getAction(),
                                    ""+response.body().getData().get(i).getDisplayDate());
                        }
                    }

                    //UPDATE UI
                    updateRewardHistoryUI();


                } else if (response.code() == Constants.API_USER_UNAUTHORIZED) {

                    Intent mLoginIntent = new Intent(mActivity, LoginActivityActivity.class);
                    mActivity.startActivity(mLoginIntent);
                    mActivity.finish();

                } else {

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                }

            }

            @Override
            public void onFailure(Call<RewardHistoryModel> call, Throwable t) {

                //CLOSE DIALOG
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });

    }
    private void apiGetMoreRewardData(int intPageCount) {

        //DISPLAY DIALOG
        CommonMethods.showDialog(mContext);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                okhttp3.Request requestOriginal = chain.request();

                okhttp3.Request request = requestOriginal.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + " " + mSessionManager.getPreference(Constants.AUTH_TOKEN))
                        .method(requestOriginal.method(), requestOriginal.body())
                        .build();


                return chain.proceed(request);
            }
        });
        final OkHttpClient httpClient1 = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_END_POINTS_STAGING)
                .client(httpClient1.newBuilder().connectTimeout(10, TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).build())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient1)
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);


        Call<RewardHistoryModel> call = patchService1.getMoreRewardData(intPageCount);

        call.enqueue(new Callback<RewardHistoryModel>() {
            @Override
            public void onResponse(Call<RewardHistoryModel> call, retrofit2.Response<RewardHistoryModel> response) {


                //CLOSE DIALOG
                CommonMethods.closeDialog();

                CommonMethods.printLogE("Response @ apiGetMoreRewardData : ","" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiGetMoreRewardData : " ,""+ response.code());


                if (response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ apiGetMoreRewardData : " ,""+ gson.toJson(response.body()));


                    //SAVE DATA IN TO DATABASE
                    if (response.body().getData().size() != 0){

                        for (int i =0 ; i < response.body().getData().size() ; i++) {
                            mDatabaseHelper.insertRewardHistoryData(""+response.body().getData().get(i).getAmount(),
                                    ""+response.body().getData().get(i).getAction(),
                                    ""+response.body().getData().get(i).getDisplayDate());
                        }

                        //UPDATE UI
                        updateRewardHistoryUI();
                    }




                } else if (response.code() == Constants.API_USER_UNAUTHORIZED) {

                    Intent mLoginIntent = new Intent(mActivity, LoginActivityActivity.class);
                    mActivity.startActivity(mLoginIntent);
                    mActivity.finish();

                } else {

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                }

            }

            @Override
            public void onFailure(Call<RewardHistoryModel> call, Throwable t) {

                //CLOSE DIALOG
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });

    }

    private void updateRewardHistoryUI() {


        //VISIBLE UI
        linearRewardHistory.setVisibility(View.VISIBLE);

        //SET ADAPTER
        mRewardSummeryListAdapter2 = new RewardSummeryListAdapter2(mContext,mActivity,mDatabaseHelper.gettRewardHistoryData());
        recycleRewardHistory.setAdapter(mRewardSummeryListAdapter2);


    }


    public class RewardSummeryListAdapter2 extends RecyclerView.Adapter<RewardSummeryListAdapter2.ViewHolder> {



        private View view;
        private Context mContext;
        private Activity mActivity;
        private RewardSummeryListAdapter2.ViewHolder viewHolder;
        private ArrayList<RewardHistoryDatabaseModel.Datum> rewardHistoryDataList = new ArrayList<>();



        public RewardSummeryListAdapter2(Context mContext, Activity mActivity, ArrayList<RewardHistoryDatabaseModel.Datum> rewardHistoryDataList) {
            this.mContext= mContext;
            this.mActivity= mActivity;
            this.rewardHistoryDataList =rewardHistoryDataList;

        }

        public  class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView txtType,txtWhen ,txtAmount,txtLoadMore;


            public ViewHolder(View mView) {

                super(mView);

                txtType = (TextView) mView.findViewById(R.id.txtType);
                txtAmount = (TextView) mView.findViewById(R.id.txtAmount);
                txtWhen = (TextView) mView.findViewById(R.id.txtWhen);
                txtLoadMore = (TextView) mView.findViewById(R.id.txtLoadMore);


            }
        }

        @Override
        public RewardSummeryListAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            view = LayoutInflater.from(mActivity).inflate(R.layout.raw_reward_summery_list, parent, false);

            view.setTag(viewHolder);
            viewHolder = new RewardSummeryListAdapter2.ViewHolder(view);


            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RewardSummeryListAdapter2.ViewHolder holder, final int position) {

            holder.txtType.setText(""+rewardHistoryDataList.get(position).getAction());
            holder.txtAmount.setText(""+rewardHistoryDataList.get(position).getAmount());
            holder.txtWhen.setText(""+rewardHistoryDataList.get(position).getDisplayDate());

            if (position == (rewardHistoryDataList.size() -1) && intPageCount != intTotalPageCount){
                holder.txtLoadMore.setVisibility(View.VISIBLE);
            }else {
                holder.txtLoadMore.setVisibility(View.GONE);
            }

            holder.txtLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //INCREASE PAGE COUNT
                    intPageCount ++;

                    apiGetMoreRewardData(intPageCount);
                }
            });

        }


        @Override
        public int getItemCount() {

            return rewardHistoryDataList.size();
        }
    }




}
