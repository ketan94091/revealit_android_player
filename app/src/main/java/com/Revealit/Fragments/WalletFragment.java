package com.Revealit.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ExoPlayerActivity;
import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.Activities.LoginActivityActivity;
import com.Revealit.Activities.QrCodeScannerActivity;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.GetAccountDetailsModel;
import com.Revealit.ModelClasses.RewardHistoryDatabaseModel;
import com.Revealit.ModelClasses.RewardHistoryModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private ImageView imgLoadingBalance,imgLogo,imgScanQRcode,imgSponsor, imgRefresh;
    private TextView txtRefreshingHistory,txtVersionName, txtCurrencyType, txtAmount, txtAccountName;
    private RelativeLayout relativeAccountDetails;
    private LinearLayout linearCurrency, linearRewardHistory;
    private int intPageCount = 1;
    private int intTotalPageCount = 0;
    private String strAccountName = "", strAmount = "";
    private HomeScreenTabLayout mHomeScreenTabLayout;
    private ProgressDialog pDialog;

    public WalletFragment(HomeScreenTabLayout homeScreenTabLayout) {

        this.mHomeScreenTabLayout = homeScreenTabLayout;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setTitle(getString(R.string.app_name));
        mView = inflater.inflate(R.layout.fragment_wallet, container, false);

//        setIds();
//        setOnClicks();

        return mView;

    }



    private void setIds() {

        mActivity = getActivity();
        mContext = getActivity();

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        //REFRESH INITIAL COUNT
        intPageCount = 1;
        intTotalPageCount = 0;

        imgRefresh = (ImageView) mView.findViewById(R.id.imgRefresh);
        imgSponsor = (ImageView) mView.findViewById(R.id.imgSponsor);
        imgScanQRcode =(ImageView)mView.findViewById(R.id.imgScanQRcode);
        imgLogo =(ImageView)mView.findViewById(R.id.imgLogo);
        imgLoadingBalance =(ImageView)mView.findViewById(R.id.imgLoadingBalance);


        txtAccountName = (TextView) mView.findViewById(R.id.txtAccountName);
        txtAmount = (TextView) mView.findViewById(R.id.txtAmount);
        txtCurrencyType = (TextView) mView.findViewById(R.id.txtCurrencyType);
        txtVersionName = (TextView) mView.findViewById(R.id.txtVersionName);
        txtRefreshingHistory = (TextView) mView.findViewById(R.id.txtRefreshingHistory);

        relativeAccountDetails = (RelativeLayout) mView.findViewById(R.id.relativeAccountDetails);

        linearRewardHistory = (LinearLayout) mView.findViewById(R.id.linearRewardHistory);
        linearCurrency = (LinearLayout) mView.findViewById(R.id.linearCurrency);

        recycleRewardHistory = (RecyclerView) mView.findViewById(R.id.recycleRewardHistory);
        recylerViewLayoutManager = new LinearLayoutManager(mActivity);
        recycleRewardHistory.setLayoutManager(recylerViewLayoutManager);


        //SET VISIBILITY
        relativeAccountDetails.setVisibility(View.VISIBLE);
        linearRewardHistory.setVisibility(View.GONE);
        txtRefreshingHistory.setVisibility(View.VISIBLE);


        //SET APPLICATION INSTALLED VERSION NAME
        txtVersionName.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+CommonMethods.installedAppVersion(mContext));

        //SET USERNAME
        txtAccountName.setText(mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imgLoadingBalance.animate().rotationBy(360).withEndAction(this).setDuration(3000).setInterpolator(new LinearInterpolator()).start();
            }
        };

        imgLoadingBalance.animate().rotationBy(360).withEndAction(runnable).setDuration(3000).setInterpolator(new LinearInterpolator()).start();

        //CALL WALLET(ACCOUNTS) DETAILS
        callWalletDetails();


    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        if (menuVisible) {

            //SET IDS
            setIds();
            setOnClicks();



        }
        super.setMenuVisibility(menuVisible);
    }

    private void setOnClicks() {

        imgRefresh.setOnClickListener(this);
        linearCurrency.setOnClickListener(this);
        txtVersionName.setOnClickListener(this);
        imgScanQRcode.setOnClickListener(this);
        imgLogo.setOnClickListener(this);
    }


    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {
            case R.id.imgScanQRcode:

                Intent mIntentQRCodeActivity = new Intent(mActivity, QrCodeScannerActivity.class);
                mActivity.startActivity(mIntentQRCodeActivity);


                break;
            case R.id.imgLogo:

                Intent mIntent = new Intent(mActivity, ExoPlayerActivity.class);
                mIntent.putExtra(Constants.MEDIA_URL, Constants.EDUCATION_VIDEO_URL);
                mIntent.putExtra(Constants.MEDIA_ID, "0");
                mIntent.putExtra(Constants.VIDEO_NAME,Constants.EDUCATION_VIDEO_TITLE);
                mIntent.putExtra(Constants.VIDEO_SEEK_TO,"0");
                mIntent.putExtra(Constants.IS_VIDEO_SEEK, false);
                mActivity.startActivity(mIntent);

                break;

            case R.id.imgRefresh:

                //REFRESH INITIAL COUNT
                intPageCount = 1;
                intTotalPageCount = 0;

                //CALL WALLET(ACCOUNTS) DETAILS
                callWalletDetails();

                break;

            case R.id.linearCurrency:


                //OPEN CURRENCY SELECTOR DIALOG
                if (mDatabaseHelper.getCurrencyList().size() != 0) {
                    openCurrencySelectorDialog();
                }

                break;

            case R.id.txtVersionName:



                break;

        }

    }




    private void openCurrencySelectorDialog() {

        final Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_currency_list);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView recycleCurrencyList = dialog.findViewById(R.id.recycleCurrencyList);

        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        recycleCurrencyList.setLayoutManager(manager);

        //SET ADAPTER
        CurrencyListAdapter mCurrencyListAdapter = new CurrencyListAdapter(mContext, mActivity, mDatabaseHelper.getCurrencyList(), dialog);
        recycleCurrencyList.setAdapter(mCurrencyListAdapter);

        dialog.show();

    }

    private void callWalletDetails() {

//        //DISPLAY DIALOG
//        pDialog = new ProgressDialog(mContext);
//        pDialog.setMessage(mContext.getResources().getString(R.string.strFacingWalletDetails));
//        pDialog.setCancelable(false);
//        pDialog.show();


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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_REGISTRATION_KEY))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

         Call<GetAccountDetailsModel> call = patchService1.getUserAccountDetails(mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));



        call.enqueue(new Callback<GetAccountDetailsModel>() {
            @Override
            public void onResponse(Call<GetAccountDetailsModel> call, Response<GetAccountDetailsModel> response) {

                //CLOSE DIALOGUE
                //pDialog.cancel();

                CommonMethods.printLogE("Response @ callWalletDetails : ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callWalletDetails : ", "" + response.code());

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ callWalletDetails : ", "" + gson.toJson(response.body()));

                    if (response.body().getMessage() != null  || response.body().getData() != null) {

                        //CHECK IF ACCOUNT DATA IS NOT NULL
                        if (response.body().getData().getTokens() != null) {

                            //SET ACCOUNT DETAILS
                            strAccountName = "" + response.body().getData().getAccountName();

                            if (!mSessionManager.getPreferenceBoolean(Constants.IS_FIRST_TIME_ACCOUNT_SYNC)) {

                                //UPDATE FLAG
                                mSessionManager.updatePreferenceBoolean(Constants.IS_FIRST_TIME_ACCOUNT_SYNC, true);

                                //GET INITIAL VALUES IN DOLLAR
                                mSessionManager.updatePreferenceString(Constants.ACCOUNT_BALANCE,String.valueOf(response.body().getData().getTokens().get(0).getAmount()));
                                mSessionManager.updatePreferenceString(Constants.ACCOUNT_CURRENCY_TYPE, "" + response.body().getData().getTokens().get(0).getSymbol());

                            }


                        } else {

                            //GET INITIAL VALUES IN DOLLAR
                            mSessionManager.updatePreferenceString(Constants.ACCOUNT_BALANCE, "0.0");
                            mSessionManager.updatePreferenceString(Constants.ACCOUNT_CURRENCY_TYPE, "USD");


                        }

                        //CLEAR TABLE
                        mDatabaseHelper.clearCurrencyListTable();

                        // ADD CURRENCY LIST IN TO DATABASE
                        if (response.body().getData().getTokens().get(0).getValues().size() != 0) {

                            for (int i = 0; i < response.body().getData().getTokens().get(0).getValues().size(); i++) {

                                mDatabaseHelper.insertCurrencyListData(response.body().getData().getTokens().get(0).getValues().get(i).getSymbol(),
                                        response.body().getData().getTokens().get(0).getValues().get(i).getValue(),
                                        response.body().getData().getTokens().get(0).getValues().get(i).getIcon(),
                                        response.body().getData().getTokens().get(0).getValues().get(i).getName());

                                //REFRESH LATEST ACCOUNT DETAILS
                                if (mSessionManager.getPreference(Constants.ACCOUNT_CURRENCY_TYPE).equals(response.body().getData().getTokens().get(0).getValues().get(i).getSymbol())) {
                                    mSessionManager.updatePreferenceString(Constants.ACCOUNT_BALANCE, response.body().getData().getTokens().get(0).getValues().get(i).getValue());
                                }
                            }
                        }


                    }



                    //CALL REWARD HISTORY
                    callRewardHistory(0);


                } else {

                    //OPEN IF ACCOUNT NOT FOUND
                    //openAccountNotFoundDialogue();
                    //CALL REWARD HISTORY
                    callRewardHistory(0);

                }
            }

            @Override
            public void onFailure(Call<GetAccountDetailsModel> call, Throwable t) {

                CommonMethods.printLogE("Response @ callWalletDetails : ", "" + t);

                //CLOSE DIALOGUE
                //pDialog.cancel();

            }
        });

    }
    private void openAccountNotFoundDialogue() {

        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getContext())

                .setTitle(Constants.APPLICATION_NAME)

                .setMessage(Constants.ACCOUNT_NOT_FOUND)

                .setPositiveButton(Constants.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // SEND USER TO LANDING SCREEN
                        Intent mIntent = new Intent(mActivity, HomeScreenTabLayout.class);
                        mIntent.putExtra(Constants.KEY_IS_FROM_REGISTRATION_SCREEN,false);
                        startActivity(mIntent);
                        mActivity.finish();

                    }
                })
                .show();
    }




    private void callRewardHistory(int i) {

//        //DISPLAY DIALOG
//        pDialog = new ProgressDialog(mContext);
//        pDialog.setMessage(mContext.getResources().getString(R.string.strFatchingRewardHistoryData));
//        pDialog.setCancelable(false);
//        pDialog.show();

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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
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
                //pDialog.cancel();

                CommonMethods.printLogE("Response @ callRewardHistory : ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callRewardHistory : ", "" + response.code());


                if (response.code() == Constants.API_CODE_200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ callRewardHistory : ", "" + gson.toJson(response.body()));

                    //CLEAR TABLE
                    mDatabaseHelper.clearRewardTable();

                    //SET TOTAL PAGE COUNT
                    intTotalPageCount = response.body().getMeta().getLastPage();

                    //SAVE DATA IN TO DATABASE
                    if (response.body().getData().size() != 0) {

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            mDatabaseHelper.insertRewardHistoryData("" + response.body().getData().get(i).getAmount(),
                                    "" + response.body().getData().get(i).getAction(),
                                    "" + response.body().getData().get(i).getDisplayDate());
                        }
                    }

                    //UPDATE UI
                    updateRewardHistoryUI();


                } else if (response.code() == Constants.API_CODE_401) {

                    Intent mLoginIntent = new Intent(mActivity, LoginActivityActivity.class);
                    mActivity.startActivity(mLoginIntent);
                    mActivity.finish();

                } else {

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                }

            }

            @Override
            public void onFailure(Call<RewardHistoryModel> call, Throwable t) {

                //pDialog.cancel();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });

    }

    private void apiGetMoreRewardData(int intPageCount) {


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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .client(httpClient1.newBuilder().connectTimeout(10, TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).build())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient1)
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);


        Call<RewardHistoryModel> call = patchService1.getMoreRewardData(intPageCount);

        call.enqueue(new Callback<RewardHistoryModel>() {
            @Override
            public void onResponse(Call<RewardHistoryModel> call, retrofit2.Response<RewardHistoryModel> response) {


                CommonMethods.printLogE("Response @ apiGetMoreRewardData : ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiGetMoreRewardData : ", "" + response.code());


                if (response.code() == Constants.API_CODE_200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ apiGetMoreRewardData : ", "" + gson.toJson(response.body()));


                    //SAVE DATA IN TO DATABASE
                    if (response.body().getData().size() != 0) {

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            mDatabaseHelper.insertRewardHistoryData("" + response.body().getData().get(i).getAmount(),
                                    "" + response.body().getData().get(i).getAction(),
                                    "" + response.body().getData().get(i).getDisplayDate());
                        }

                        //UPDATE UI
                        updateRewardHistoryUI();
                    }


                } else if (response.code() == Constants.API_CODE_401) {

                    Intent mLoginIntent = new Intent(mActivity, LoginActivityActivity.class);
                    mActivity.startActivity(mLoginIntent);
                    mActivity.finish();

                } else {

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                }

            }

            @Override
            public void onFailure(Call<RewardHistoryModel> call, Throwable t) {

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });

    }

    private void updateRewardHistoryUI() {


        //UPDATE UI
        relativeAccountDetails.setVisibility(View.VISIBLE);
        txtAmount.setText(mSessionManager.getPreference(Constants.ACCOUNT_BALANCE));
        txtCurrencyType.setText(mSessionManager.getPreference(Constants.ACCOUNT_CURRENCY_TYPE));

        //HIDE ANIMATED IMAGE VIEW WHEN AMOUNT DISPLAY
        imgLoadingBalance.animate().cancel();
        imgLoadingBalance.setImageDrawable(getResources().getDrawable(R.mipmap.balance_updated));

        //DISPLAY LOADER FOR 1 SECOND AND THAN DISPLAY BALANCE
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                imgLoadingBalance.setVisibility(View.GONE);
                txtAmount.setVisibility(View.VISIBLE);
            }
        }, 1000);


        //VISIBLE UI
        linearRewardHistory.setVisibility(View.VISIBLE);
        txtRefreshingHistory.setVisibility(View.GONE);

        //UPDATE SPONSOR IMAGE
        //SET BITE IMAGE
//        Glide.with(mActivity)
//                .load("https://apac.sgp1.digitaloceanspaces.com/glacier/9692/1e7c6277-4014-444f-be46-c37249c700e2.jpeg")
//                .apply(new RequestOptions().override(500, 225))
//                .placeholder(getResources().getDrawable(R.drawable.placeholder))
//                .into(imgSponsor);

        imgSponsor.setVisibility(View.GONE);


        //SET ADAPTER
        mRewardSummeryListAdapter2 = new RewardSummeryListAdapter2(mContext, mActivity, mDatabaseHelper.gettRewardHistoryData());
        recycleRewardHistory.setAdapter(mRewardSummeryListAdapter2);

        //CHECK IF THERE IS NO REWARD DATA AVAILABLE
        if(mDatabaseHelper.gettRewardHistoryData().size() == 0){
            txtRefreshingHistory.setVisibility(View.VISIBLE);
            txtRefreshingHistory.setText(getResources().getString(R.string.strNoRewardDataAvailable));

        }


    }


    public class RewardSummeryListAdapter2 extends RecyclerView.Adapter<RewardSummeryListAdapter2.ViewHolder> {


        private View view;
        private Context mContext;
        private Activity mActivity;
        private RewardSummeryListAdapter2.ViewHolder viewHolder;
        private ArrayList<RewardHistoryDatabaseModel.Datum> rewardHistoryDataList = new ArrayList<>();


        public RewardSummeryListAdapter2(Context mContext, Activity mActivity, ArrayList<RewardHistoryDatabaseModel.Datum> rewardHistoryDataList) {
            this.mContext = mContext;
            this.mActivity = mActivity;
            this.rewardHistoryDataList = rewardHistoryDataList;

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView txtType, txtWhen, txtAmount, txtLoadMore;


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

            holder.txtType.setText("" + rewardHistoryDataList.get(position).getAction());
            holder.txtAmount.setText("" + rewardHistoryDataList.get(position).getAmount());
            holder.txtWhen.setText("" + rewardHistoryDataList.get(position).getDisplayDate());

            //IF TOTAL PAGE AND CURRENT PAGE ARE SAME
            //HIDE LOAD MORE BUTTON
            if (position == (rewardHistoryDataList.size() - 1) && intPageCount != intTotalPageCount) {
                holder.txtLoadMore.setVisibility(View.VISIBLE);
            } else {
                holder.txtLoadMore.setVisibility(View.GONE);
            }

            holder.txtLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //CHANGE TEXT
                    holder.txtLoadMore.setText(mContext.getResources().getString(R.string.strFetchingData));

                    //INCREASE PAGE COUNT
                    intPageCount++;

                    apiGetMoreRewardData(intPageCount);
                }
            });

        }


        @Override
        public int getItemCount() {

            return rewardHistoryDataList.size();
        }
    }


    public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.ViewHolder> {


        private View view;
        private Context mContext;
        private Activity mActivity;
        private CurrencyListAdapter.ViewHolder viewHolder;
        private ArrayList<GetAccountDetailsModel.Token.Values> currencyList = new ArrayList<>();
        private Dialog dialog;


        public CurrencyListAdapter(Context mContext, Activity mActivity, ArrayList<GetAccountDetailsModel.Token.Values> currencyList, Dialog dialog) {
            this.mContext = mContext;
            this.mActivity = mActivity;
            this.currencyList = currencyList;
            this.dialog = dialog;

            Collections.sort(this.currencyList, new Comparator() {
                @Override
                public int compare(Object alphacurrency, Object softDrinkTwo) {
                    return ((GetAccountDetailsModel.Token.Values) alphacurrency).getSymbol()
                            .compareTo(((GetAccountDetailsModel.Token.Values) softDrinkTwo).getSymbol());
                }
            });


        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView txtName, txtCurrencyTitle;
            private final ImageView imgCurrencyIcon;
            private final LinearLayout linearMain;


            public ViewHolder(View mView) {

                super(mView);

                imgCurrencyIcon = (ImageView) mView.findViewById(R.id.imgCurrencyIcon);
                txtCurrencyTitle = (TextView) mView.findViewById(R.id.txtCurrencyTitle);
                txtName = (TextView) mView.findViewById(R.id.txtName);
                linearMain = (LinearLayout) mView.findViewById(R.id.linearMain);


            }
        }

        @Override
        public CurrencyListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            view = LayoutInflater.from(mActivity).inflate(R.layout.raw_currency_selection_list, parent, false);

            view.setTag(viewHolder);
            viewHolder = new CurrencyListAdapter.ViewHolder(view);


            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CurrencyListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            holder.txtCurrencyTitle.setText(currencyList.get(position).getSymbol());
            holder.txtName.setText(currencyList.get(position).getName());

            //SET BITE IMAGE
            Glide.with(mActivity)
                    .load(currencyList.get(position).getIcon())
                    .into(holder.imgCurrencyIcon);

            if (mSessionManager.getPreference(Constants.ACCOUNT_CURRENCY_TYPE).equals(currencyList.get(position).getSymbol())) {
                //TINT IMAGE ICON ON THE BASIS OF ALREADY SELECTION
                holder.imgCurrencyIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorButtonEnabled), android.graphics.PorterDuff.Mode.MULTIPLY);
                holder.txtCurrencyTitle.setTextColor(mContext.getResources().getColor(R.color.colorCurrency));
                holder.txtName.setTextColor(mContext.getResources().getColor(R.color.colorCurrencyNameSelected));
                holder.linearMain.setBackground(mContext.getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

            }

            holder.linearMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //DISMISS DIALOG
                    dialog.cancel();

                    //SET BALANCE AMOUNT AND SYMBOL
                    txtAmount.setText(currencyList.get(position).getValue());
                    txtCurrencyType.setText(currencyList.get(position).getSymbol());

                    //GET INITIAL VALUES IN DOLLAR
                    mSessionManager.updatePreferenceString(Constants.ACCOUNT_BALANCE, currencyList.get(position).getValue());
                    mSessionManager.updatePreferenceString(Constants.ACCOUNT_CURRENCY_TYPE, currencyList.get(position).getSymbol());
                }
            });
        }


        @Override
        public int getItemCount() {

            return currencyList.size();
        }
    }


}
