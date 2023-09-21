package com.Revealit.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ExoPlayerActivity;
import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.Activities.QrCodeScannerActivity;
import com.Revealit.Adapter.PlayCategoryListAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.UserOnboardingProcess.AddRefferalAndEarnActivity;
import com.Revealit.UserOnboardingProcess.NewAuthGetStartedActivity;
import com.Revealit.UserOnboardingProcess.NewAuthSplashScreen;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
    private static final String TAG = "PlayFragment";

    private View mView;

    private GridLayoutManager mGridLayoutManager;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView imgLogo,imgScanQRcode,imgPlay,imgBiteBrandLogo, imgBiteBanner;
    private TextView txtNoPublishedVideo, txtSubTitleThree, txtSubTitleTwo, txtSubTitleOne, txtBiteTitle;
    private PlayCategoryListAdapter mPlayCategoryListAdapter;
    private LinearLayoutManager recylerViewLayoutManager;
    private RecyclerView recycleCategoryList;
    private LinearLayout ralativeMain;
    private boolean isForFirstTime = true,isUserIsActive;
    String strFeaturedMediaCoverImage ="", strFeaturedMediaTitle = "" , strFeaturedMidiaID = "" ,strFeaturedMidiaURL = "";
    private HomeScreenTabLayout homeScreenTabLayout;

    public PlayFragment(HomeScreenTabLayout homeScreenTabLayout) {
        this.homeScreenTabLayout = homeScreenTabLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setTitle(getString(R.string.app_name));
        mView = inflater.inflate(R.layout.fragment_play, container, false);

        //SET IDS
        setIds();
        setOnClicks();

        return mView;

    }

    @Override
    public void onResume() {
        super.onResume();

        //CALL HOME SCREEN PLAY DATA API
        getVideoPlayerRawData();
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
        imgPlay = (ImageView) mView.findViewById(R.id.imgPlay);
        imgScanQRcode =(ImageView)mView.findViewById(R.id.imgScanQRcode);
        imgLogo =(ImageView)mView.findViewById(R.id.imgLogo);


        txtBiteTitle = (TextView) mView.findViewById(R.id.txtBiteTitle);
        txtSubTitleOne = (TextView) mView.findViewById(R.id.txtSubTitleOne);
        txtSubTitleTwo = (TextView) mView.findViewById(R.id.txtSubTitleTwo);
        txtSubTitleThree = (TextView) mView.findViewById(R.id.txtSubTitleThree);
        txtNoPublishedVideo = (TextView) mView.findViewById(R.id.txtNoPublishedVideo);

        ralativeMain = (LinearLayout) mView.findViewById(R.id.ralativeMain);

        recycleCategoryList = (RecyclerView) mView.findViewById(R.id.recycleCategoryList);
        recylerViewLayoutManager = new LinearLayoutManager(mActivity);
        recycleCategoryList.setLayoutManager(recylerViewLayoutManager);


        //SHOW WELCOME DIALOGUE
        //IF -> USER IS ACTIVE -> SHOW WELCOME DIALOGUE ONLY ONCE
        //ELSE -> USER IS IN ACTIVE -> DISPLAY WHITELISTED DIALOGUE
        //UPDATE FIRST OPEN FLAG
        isUserIsActive =mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_ACTIVE);


    }

    private void setOnClicks() {

        txtSubTitleOne.setOnClickListener(this);
        txtSubTitleTwo.setOnClickListener(this);
        txtSubTitleThree.setOnClickListener(this);
        imgPlay.setOnClickListener(this);
        imgScanQRcode.setOnClickListener(this);
        imgLogo.setOnClickListener(this);
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
            case R.id.imgPlay:


                if (!strFeaturedMidiaURL.isEmpty()) {

                    Intent mIntent = new Intent(mActivity, ExoPlayerActivity.class);
                    mIntent.putExtra(Constants.MEDIA_URL, strFeaturedMidiaURL);
                    mIntent.putExtra(Constants.MEDIA_ID, strFeaturedMidiaID);
                    mIntent.putExtra(Constants.VIDEO_NAME, strFeaturedMediaTitle);
                    mIntent.putExtra(Constants.VIDEO_SEEK_TO,"0");
                    mIntent.putExtra(Constants.IS_VIDEO_SEEK, false);
                    mActivity.startActivity(mIntent);

                } else {

                    CommonMethods.displayToast(mContext, getResources().getString(R.string.strNoFeatureVideos));
                }

                break;
            case R.id.imgScanQRcode:


                if(CommonMethods.areNotificationsEnabled(mContext)){
                    if(mSessionManager.getPreference(Constants.KEY_PUSHER_ID).isEmpty()){
                        CommonMethods.showPusherImplementDialogue(mContext);
                    }else{
                        Intent mIntentQRCodeActivity = new Intent(mActivity, QrCodeScannerActivity.class);
                        mActivity.startActivity(mIntentQRCodeActivity);
                    }
                }else{
                 CommonMethods.openNotificationSettings(mActivity);

                }



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

        }

    }



    private void getVideoPlayerRawData() {


        //DISPLAY DIALOG
        //DIALOG SHOULD DISPLAY ONLY FOR THE FIRST TIME IN SECOND TIME WHEN WE CAME BACK FROM TABLE BAR MENU ITS REFRESH BUT NOT DISPLAY ANY DIALOG
        if (isForFirstTime) {
            CommonMethods.showDialog(mContext);
        }

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


        Call<JsonElement> call = patchService1.getPlayData(Constants.API_PLAY_CATEGORIES);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

               // Log.e("JSON RESPONSE ",  ""+response.body());


                if (response.code() == Constants.API_CODE_200) {

                    //DELETE ALL TABLE AND INSERT NEW DATA ONLY
                    mDatabaseHelper.clearAllTables();



                    //GET API RESPONSE IN THE FORM OF JSON ELEMENT
                    // 1- RE ITERATE KEY NAME OF OBJECT CAUSE ITS DYNAMIC AND UN-KNOWN  BASED ON OBJECT NAME GET DATA FOR THAT OBJECT
                    for (int i = 0; i < response.body().getAsJsonObject().get("data").getAsJsonArray().size(); i++) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(String.valueOf(response.body().getAsJsonObject().get("data").getAsJsonArray().get(i)));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Iterator<String> keys = jsonObject.keys();

                        while (keys.hasNext()) {

                            String strCategoryName = keys.next();

                            try {
                                if (jsonObject.get(strCategoryName) instanceof JSONObject) {

                                    // CommonMethods.printLogE("KEY : ", "" + strCategoryName);
                                    // CommonMethods.printLogE(" MEDIA DATA TYPE", " : " + gson.toJson(response.body().getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get("media_type")).replaceAll("^\"|\"$", ""));

                                    //INSERT DATA IN TO DATABASE
                                    //FURTHER WE WILL ONLY USE IT FROM DATABASE
                                    String strSlug = gson.toJson(response.body().getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("slug")).replaceAll("^\"|\"$", "");


                                    //FEATURED VIDEO SHOULD NOT ADD IN DATABASE
                                   if(!strSlug.equalsIgnoreCase("Featured")) {

                                       //CHECK IF PARTICULAR CATEGORY DONT HAVE ANY DATA
                                       if(Integer.valueOf(gson.toJson(response.body().getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().size())) != 0){
                                           mDatabaseHelper.insertCategoryNames(strCategoryName, strSlug);
                                       }
                                   }

                                    String strMediaShowTitle, strMediaTitle;


                                    for (int j = 0; j < Integer.valueOf(gson.toJson(response.body().getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().size())); j++) {

                                        int intMediaID = Integer.valueOf(gson.toJson(response.body().getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_id")).replaceAll("^\"|\"$", ""));


                                        if (!gson.toJson(response.body().getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_show_title")).equalsIgnoreCase("null")) {
                                            strMediaShowTitle = gson.toJson(response.body().getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_show_title")).replaceAll("^\"|\"$", "").replaceAll("u0027", "'").replaceAll("\\\\", "");
                                        } else {
                                            strMediaShowTitle = "";
                                        }

                                        if (!gson.toJson(response.body().getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_title")).equalsIgnoreCase("null")) {
                                            strMediaTitle = gson.toJson(response.body().getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_title")).replaceAll("^\"|\"$", "").replaceAll("u0027", "'").replaceAll("\\\\", "");
                                        } else {
                                            strMediaTitle = "";
                                        }

                                        String strMediaType = gson.toJson(response.body().getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_type")).replaceAll("^\"|\"$", "");
                                        String strMediaUrl = gson.toJson(response.body().getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_url")).replaceAll("^\"|\"$", "");
                                        String strMediaCoverArt = gson.toJson(response.body().getAsJsonObject().get("data").getAsJsonArray().get(i).getAsJsonObject().get(strCategoryName).getAsJsonObject().get("data").getAsJsonArray().get(j).getAsJsonObject().get("media_cover_art")).replaceAll("^\"|\"$", "");


                                        //CommonMethods.printLogE("MEDIA ID : ", "" + intMediaID);
                                        //CommonMethods.printLogE("MEDIA SHOW TITLE : ", "" + strMediaShowTitle);
                                        //CommonMethods.printLogE("MEDIA TITLE : ", "" + strMediaTitle);
                                        //CommonMethods.printLogE("MEDIYA TYPE : ", "" + strMediaType);
                                        //CommonMethods.printLogE("MEDIA URL : ", "" + strMediaUrl);
                                        //CommonMethods.printLogE("MEDIA COVER ART : ", "" + strMediaCoverArt);

                                        //FEATURED VIDEO SHOULD NOT ADD IN DATABASE
                                        //IT SHOULD CONSIDER AS STATIC VIDEO
                                        if(strSlug.equalsIgnoreCase("Featured")) {

                                            //SET FEATURED VIDEO AS EDUCATION VIDEO UNTIL USER VIED THIS
                                            //IF -> TURE -> DISPLAY FEATURED VIDEO
                                            //ELSE -> DISPLAY -> REVEAL IT CURATOR ACADEMY VIDEO
                                            //UPDATE FLAG ON FEATURE VIDEO PLAY BUTTON

                                                strFeaturedMidiaURL = ""+strMediaUrl;
                                                strFeaturedMidiaID = ""+intMediaID;
                                                strFeaturedMediaTitle = ""+strMediaTitle;
                                                strFeaturedMediaCoverImage = ""+strMediaCoverArt;

                                        }else {
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


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }


                    //CLOSE DIALOG
                    CommonMethods.closeDialog();




                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {

                            //BIND UI
                            updateUI(response.body().getAsJsonObject().get("data").getAsJsonArray());

                        }
                    });

                } else if (response.code() == Constants.API_CODE_401) {

                    //CLOSE DIALOG
                    CommonMethods.closeDialog();

                    Intent mLoginIntent = new Intent(mActivity, NewAuthGetStartedActivity.class);
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

    private void updateUI(JsonArray asJsonArray) {

        if (asJsonArray.size() != 0) {

            ralativeMain.setVisibility(View.VISIBLE);
            txtNoPublishedVideo.setVisibility(View.GONE);


            //SET BITE IMAGE
            Glide.with(getContext().getApplicationContext())
                    .load(strFeaturedMediaCoverImage)
                    .transform(new CenterCrop(),new RoundedCorners(30))

                    //.load("https://beta.sgp1.digitaloceanspaces.com/featured/featured_header_iamkareno1.jpg")
                     //.apply(new RequestOptions().override(450, 200))
                    .placeholder(getResources().getDrawable(R.drawable.bite_templet))
                    .into(imgBiteBanner);

            //SET BITE IMAGE
            Glide.with(getContext().getApplicationContext())
                    // .load(strFeaturedMediaCoverImage)
                    .load("https://beta.sgp1.digitaloceanspaces.com/featured/sponsorship_@3x.png")
                    .placeholder(getResources().getDrawable(R.drawable.bite_templet))
                    .into(imgBiteBrandLogo);


            //SET BUT NAME
            txtBiteTitle.setText(strFeaturedMediaTitle);


            //SET CATEGORY LIST
            mPlayCategoryListAdapter = new PlayCategoryListAdapter(mContext, mActivity, mDatabaseHelper.getCategoryList(), mDatabaseHelper);
            recycleCategoryList.setAdapter(mPlayCategoryListAdapter);

        } else {

            //VISIBLE IF NO DATA AVAILABLE
            txtNoPublishedVideo.setVisibility(View.VISIBLE);
            ralativeMain.setVisibility(View.GONE);
        }

        //CHANGE VALUE
        isForFirstTime = false;

        //UPDATE FIRST OPEN FLAG
        if(!mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_CANCEL_REFERRAL)){
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    if(isUserIsActive){
                        if(!mSessionManager.getPreferenceBoolean(Constants.IS_USER_OPEN_APP_FIRST_TIME)){
                            showWelcomeDialogue(isUserIsActive);
                        }

                    }
                }
            });
        }


    }
    private void showWelcomeDialogue(boolean isUserIsActive) {


        Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.user_welcome_dialogue);


        //final AlertDialog mAlertDialog = dialogBuilder.create();
        TextView txtGetStarted = (TextView) dialog.findViewById(R.id.txtGetStarted);
        TextView txtWaitListed = (TextView) dialog.findViewById(R.id.txtWaitListed);
        TextView txtLogout = (TextView) dialog.findViewById(R.id.txtLogout);
        TextView txtHaveBeenWaitlisted = (TextView) dialog.findViewById(R.id.txtHaveBeenWaitlisted);
        ImageView imgCancel = (ImageView) dialog.findViewById(R.id.imgCancel);

        //HIDE SHOW BUTTON BASED ON USER ACTIVATION STATUS
        if(isUserIsActive){
            txtWaitListed.setVisibility(View.GONE);
            txtGetStarted.setVisibility(View.VISIBLE);
            txtLogout.setVisibility(View.GONE);
            imgCancel.setVisibility(View.GONE);
            txtHaveBeenWaitlisted.setVisibility(View.GONE);
        }else{
            txtWaitListed.setVisibility(View.VISIBLE);
            txtGetStarted.setVisibility(View.GONE);
            txtLogout.setVisibility(View.GONE);
            imgCancel.setVisibility(View.VISIBLE);
            txtHaveBeenWaitlisted.setVisibility(View.VISIBLE);
        }



        txtGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                //UPDATE FIRST OPEN FLAG
                mSessionManager.updatePreferenceBoolean(Constants.IS_USER_OPEN_APP_FIRST_TIME,true);

                //UPDATE EDUCATIONAL VIDEO FLAG
                mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_EDUCATION_VIDEO_PLAYED, true);

                Intent mIntent = new Intent(mActivity, ExoPlayerActivity.class);
                mIntent.putExtra(Constants.MEDIA_URL, Constants.EDUCATION_VIDEO_URL);
                mIntent.putExtra(Constants.MEDIA_ID, "0");
                mIntent.putExtra(Constants.VIDEO_NAME, Constants.EDUCATION_VIDEO_TITLE);
                mIntent.putExtra(Constants.VIDEO_SEEK_TO,"0");
                mIntent.putExtra(Constants.IS_VIDEO_SEEK, false);
                mActivity.startActivity(mIntent);

            }
        });


        txtWaitListed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                //EXIT FROM THE APP UNTIL USER TURN TO ACTIVE FROM THE BACKEND
                Intent mIntent = new Intent(mActivity, AddRefferalAndEarnActivity.class);
                startActivity(mIntent);
                mActivity.finishAffinity();


            }
        });
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                //CLEAR PUSHER NOTIFICATION INTEREST
                //PushNotifications.clearAllState();

                //UPDATE LOGIN FLAG
                mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN,false);
                mSessionManager.updatePreferenceBoolean(Constants.KEY_ISFROM_LOGOUT,true);
                mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN, false);
                mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_CANCEL_REFERRAL, false);

                // SEND USER TO LANDING SCREEN
                Intent mIntent = new Intent(mActivity, NewAuthSplashScreen.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mIntent);
                mActivity.finish();


            }
        });
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE APPLICATION BOTTOM BAR VISIBILITY FLAG
                //IF FLAG = TRUE -> ALLOW USER TO ONLY VIEW USER PROFILE SCREEN
                //IF FALSE -> ALLOW USER TO ACCESS ALL FOUR BOTTOM BAR PAGES
                mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_CANCEL_REFERRAL, true);

                dialog.dismiss();

                //EXIT FROM THE APP UNTIL USER TURN TO ACTIVE FROM THE BACKEND
                Intent mIntent = new Intent(mActivity, HomeScreenTabLayout.class);
                startActivity(mIntent);
                mActivity.finishAffinity();


            }
        });
        dialog.show();
    }


}

