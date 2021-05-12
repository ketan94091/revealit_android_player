package com.Revealit.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.LoginActivityActivity;
import com.Revealit.Activities.VideoViewActivity;
import com.Revealit.Adapter.PlayCategoryListAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlayFragment extends Fragment implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "HomeFragment";

    private View mView;

    private GridLayoutManager mGridLayoutManager;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView imgBiteBrandLogo, imgBiteBanner;
    private TextView txtPlay, txtSubTitleThree, txtSubTitleTwo, txtSubTitleOne, txtBiteTitle;
    private PlayCategoryListAdapter mPlayCategoryListAdapter;
    private LinearLayoutManager recylerViewLayoutManager;
    private RecyclerView recycleCategoryList;
    private RelativeLayout ralativeMain;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setTitle(getString(R.string.app_name));
        mView = inflater.inflate(R.layout.fragment_play, container, false);


        setIds();
        setOnClicks();

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

        imgBiteBanner = (ImageView) mView.findViewById(R.id.imgBiteBanner);
        imgBiteBrandLogo = (ImageView) mView.findViewById(R.id.imgBiteBrandLogo);

        txtBiteTitle = (TextView) mView.findViewById(R.id.txtBiteTitle);
        txtSubTitleOne = (TextView) mView.findViewById(R.id.txtSubTitleOne);
        txtSubTitleTwo = (TextView) mView.findViewById(R.id.txtSubTitleTwo);
        txtSubTitleThree = (TextView) mView.findViewById(R.id.txtSubTitleThree);
        txtPlay = (TextView) mView.findViewById(R.id.txtPlay);

        ralativeMain = (RelativeLayout) mView.findViewById(R.id.ralativeMain);

        recycleCategoryList = (RecyclerView) mView.findViewById(R.id.recycleCategoryList);
        recylerViewLayoutManager = new LinearLayoutManager(mActivity);
        recycleCategoryList.setLayoutManager(recylerViewLayoutManager);


        //CALL HOME SCREEN PLAY DATA API
        getVideoPlayerRawData();


    }

    private void setOnClicks() {

        txtSubTitleOne.setOnClickListener(this);
        txtSubTitleTwo.setOnClickListener(this);
        txtSubTitleThree.setOnClickListener(this);
        txtPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {

            case R.id.txtSubTitleOne:

                break;
            case R.id.txtSubTitleTwo:

                break;
            case R.id.txtSubTitleThree:

                break;
            case R.id.txtPlay:

                Intent mIntent = new Intent(mActivity, VideoViewActivity.class);
                mIntent.putExtra(Constants.MEDIA_URL ,""+mDatabaseHelper.getCategoryWisePlayList().get(0).getMediaUrl());
                mIntent.putExtra(Constants.MEDIA_ID ,""+mDatabaseHelper.getCategoryWisePlayList().get(0).getMediaID());
                mIntent.putExtra(Constants.VIDEO_NAME ,""+mDatabaseHelper.getCategoryWisePlayList().get(0).getMediaShowTitle());
                mActivity.startActivity(mIntent);

                //CommonMethods.displayToast(mContext, "CLICKED ON : SAVE BUTTON");

                break;

        }

    }
    private void getVideoPlayerRawData() {

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


        Call<JsonElement> call = patchService1.getPlayData(Constants.API_PLAY_CATEGORIES);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {


                if (response.code() == Constants.API_SUCCESS) {

                    //DELETE ALL TABLE AND INSERT NEW DATA ONLY
                    mDatabaseHelper.clearAllTables();


                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    //GET API RESPONSE IN THE FORM OF JSON ELEMENT
                    // 1- RE ITERATE KEY NAME OF OBJECT CAUSE ITS DYNAMIC AND UN-KNOWN  BASED ON OBJECT NAME GET DATA FOR THAT OBJECT
                    for (int i = 0; i < response.body().getAsJsonArray().size(); i++) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(String.valueOf(response.body().getAsJsonArray().get(i)));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Iterator<String> keys = jsonObject.keys();

                        while (keys.hasNext()) {

                            String strCategoryName = keys.next();

                            try {
                                if (jsonObject.get(strCategoryName) instanceof JSONObject) {

                                   // CommonMethods.printLogE("KEY : ", "" + strCategoryName);

                                    // CommonMethods.printLogE(" MEDIA DATA TYPE", " : " + gson.toJson(response.body().getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get("media_type")).replaceAll("^\"|\"$", ""));


                                    //INSERT DATA IN TO DATABASE
                                    //FURTHER WE WILL ONLY USE IT FROM DATABASE



                                    String strSlug = gson.toJson(response.body().getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("slug")).replaceAll("^\"|\"$", "");

                                    //CommonMethods.printLogE("SLUG : ", "" + strSlug);

                                    //INSERT CATEGORY NAMES
                                    mDatabaseHelper.insertCategoryNames(strCategoryName, strSlug);


                                    for (int j = 0; j < Integer.valueOf(gson.toJson(response.body().getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().size())); j++) {

                                        int intMediaID = Integer.valueOf(gson.toJson(response.body().getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_id")).replaceAll("^\"|\"$", ""));
                                        String strMediaShowTitle = gson.toJson(response.body().getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_show_title")).replaceAll("^\"|\"$", "");
                                        String strMediaTitle = gson.toJson(response.body().getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_title")).replaceAll("^\"|\"$", "");
                                        String strMediaType = gson.toJson(response.body().getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_type")).replaceAll("^\"|\"$", "");
                                        String strMediaUrl = gson.toJson(response.body().getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_url")).replaceAll("^\"|\"$", "");
                                        String strMediaCoverArt = gson.toJson(response.body().getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_cover_art")).replaceAll("^\"|\"$", "");


                                        //CommonMethods.printLogE("MEDIA ID : ", "" + intMediaID);
                                        //CommonMethods.printLogE("MEDIA SHOW TITLE : ", "" + strMediaShowTitle);
                                        //CommonMethods.printLogE("MEDIA TITLE : ", "" + strMediaTitle);
                                        //CommonMethods.printLogE("MEDIYA TYPE : ", "" + strMediaType);
                                        //CommonMethods.printLogE("MEDIA URL : ", "" + strMediaUrl);
                                        //CommonMethods.printLogE("MEDIA COVER ART : ", "" + strMediaCoverArt);


                                        mDatabaseHelper.insertCategoryWisePlayData(strCategoryName,
                                                strSlug,
                                                intMediaID,
                                                strMediaShowTitle,
                                                strMediaTitle,
                                                strMediaType,
                                                strMediaUrl,
                                                strMediaCoverArt);


                                    }


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }


                    //CLOSE DIALOG
                    CommonMethods.closeDialog();

                    //BIND UI
                    updateUI();

                } else if (response.code() == Constants.API_USER_UNAUTHORIZED) {

                    //CLOSE DIALOG
                    CommonMethods.closeDialog();

                    Intent mLoginIntent = new Intent(mActivity, LoginActivityActivity.class);
                    mActivity.startActivity(mLoginIntent);
                    mActivity.finish();

                } else {

                    //CLOSE DIALOG
                    CommonMethods.closeDialog();

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                //CLOSE DIALOG
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });

    }

    private void updateUI() {

        if (mDatabaseHelper.getCategoryList().size() != 0) {

            ralativeMain.setVisibility(View.VISIBLE);

            //SET CATEGORY LIST
            mPlayCategoryListAdapter = new PlayCategoryListAdapter(mContext, mActivity, mDatabaseHelper.getCategoryList(), mDatabaseHelper);
            recycleCategoryList.setAdapter(mPlayCategoryListAdapter);

            //SET BITE IMAGE
            Glide.with(mActivity)
                    .load(""+mDatabaseHelper.getCategoryWisePlayList().get(0).getMediaCoverArt())
                    .apply(new RequestOptions().override(1000, 500))
                    .placeholder(getResources().getDrawable(R.drawable.bite_templet))
                    .into(imgBiteBanner);

            //SET BUT NAME
            txtBiteTitle.setText(""+mDatabaseHelper.getCategoryWisePlayList().get(0).getMediaShowTitle());

        }


    }

}
