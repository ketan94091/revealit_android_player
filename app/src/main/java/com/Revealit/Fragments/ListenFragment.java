package com.Revealit.Fragments;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ExoPlayerActivity;
import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.Activities.QrCodeScannerActivity;
import com.Revealit.Adapter.RevealItHistoryListAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.CommonClasse.SwipeHelper;
import com.Revealit.CustomViews.RippleBackground;
import com.Revealit.Interfaces.ClearListHistory;
import com.Revealit.Interfaces.RemoveListenHistory;
import com.Revealit.ModelClasses.CategoryWisePlayListModel;
import com.Revealit.ModelClasses.RevealitHistoryModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.acrcloud.utils.ACRCloudExtrTool;
import com.acrcloud.utils.ACRCloudRecognizer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ListenFragment extends Fragment implements View.OnClickListener, RemoveListenHistory{

    private static Activity mActivity;
    private static Context mContext;
    private static final String TAG = "ListenFragment";
    private View mView;
    private String path = "";

    private static SessionManager mSessionManager;
    private static DatabaseHelper mDatabaseHelper;
    private ImageView imgLogo;
    private ImageView imgScanQRcode;
    private static ImageView imgListen;
    private static TextView txtRevealSelectedCount;
    private TextView txtSelectDeselectAll;
    private TextView txtSelectionCancel;
    private TextView txtSelect;
    private TextView txtReveal;
    private static TextView txtRevealCount;
    private static RecyclerView recycleRevealList;
    private LinearLayoutManager recylerViewLayoutManager;
    private RippleBackground rippleBackground;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private static RemoveListenHistory mRemoveListenHistory;
    private ClearListHistory mClearListHistory;
    private static int tapCount = 1;// IGONORE FIRST COUNT AND THEN START GETTING DATA FROM ITEMS TO END ITEMS.
    private static ArrayList<CategoryWisePlayListModel.DataBean> mCategoryWisePlayListModel = new ArrayList<>();
    private static ArrayList<Long> mLongRevealTime = new ArrayList<>();
    private static HomeScreenTabLayout mHomeScreenTabLayout;
    private static LinearLayout linearRevealTitle;
    private static RelativeLayout relativeTapToReveal;
    private static LinearLayout linearRevealSelection;
    private LinearLayout linearWavingBackground;
    private static RevealItHistoryListAdapter mRevealItHistoryListAdapter;
    public static ArrayList<Integer> selectedIdsList;
    public static   boolean isFromShareIntent = false;

    private static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private MediaRecorder mRecorder;
    private String mFileName;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String res = (String) msg.obj;
                    break;

                default:
                    break;
            }
        }

        ;
    };


    enum ButtonsState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

    private boolean swipeBack = false;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
    private static final float buttonWidth = 300;


    public ListenFragment(HomeScreenTabLayout mHomeScreenTabLayout) {
        this.mHomeScreenTabLayout = mHomeScreenTabLayout;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setTitle(getString(R.string.app_name));
        mView = inflater.inflate(R.layout.fragment_listen, container, false);

        return mView;

    }

    private void setIds() {


        mActivity = getActivity();
        mContext = getActivity();

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgListen = (ImageView) mView.findViewById(R.id.imgListen);
        imgScanQRcode = (ImageView) mView.findViewById(R.id.imgScanQRcode);
        imgLogo = (ImageView) mView.findViewById(R.id.imgLogo);


        txtRevealCount = (TextView) mView.findViewById(R.id.txtRevealCount);
        txtReveal = (TextView) mView.findViewById(R.id.txtReveal);
        txtSelect = (TextView) mView.findViewById(R.id.txtSelect);
        txtSelectionCancel = (TextView) mView.findViewById(R.id.txtSelectionCancel);
        txtSelectDeselectAll = (TextView) mView.findViewById(R.id.txtSelectDeselectAll);
        txtRevealSelectedCount = (TextView) mView.findViewById(R.id.txtRevealSelectedCount);

        rippleBackground = (RippleBackground) mView.findViewById(R.id.content);

        linearWavingBackground = (LinearLayout) mView.findViewById(R.id.linearWavingBackground);
        linearRevealSelection = (LinearLayout) mView.findViewById(R.id.linearRevealSelection);
        linearRevealTitle = (LinearLayout) mView.findViewById(R.id.linearRevealTitle);
        relativeTapToReveal = (RelativeLayout) mView.findViewById(R.id.relativeTapToReveal);

        recycleRevealList = (RecyclerView) mView.findViewById(R.id.recycleRevealList);
        recylerViewLayoutManager = new LinearLayoutManager(mActivity);
        recycleRevealList.setLayoutManager(recylerViewLayoutManager);

        //INITIALIZE INTERFACE
        mRemoveListenHistory = (RemoveListenHistory) this;


        if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {

            //INITIALIZED ARC CLOUD LIBRARY
            ACRCloudExtrTool.setDebug();
            //GET HISTORY
            callGetRevealitVideoHistory();

        } else {

          //BIND SIMULATION MODE
            bindSimulationMode();


        }


    }

    private static void bindSimulationMode() {
        //GET WATCH DATA FROM DATABASE
        mCategoryWisePlayListModel = mDatabaseHelper.getCategoryWisePlayList();

        //CREATE LONG REAVEAL IT TIME STAMP ARRAY
        mLongRevealTime = new ArrayList<>();
        mLongRevealTime.clear();

        //SET INITIAL COUNT FOR GETTING SUB LIST FROM DATABASE
        tapCount = 1;

        //RELOAD HISTORY LIST IF NOT EMPTY
        updateRevealitHistoryListSimulation(false, false);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {


        if (menuVisible  ) {

            //SET IDS
            setIds();
            setOnClicks();

        }
        super.setMenuVisibility(menuVisible);
    }


    private void setOnClicks() {

        imgListen.setOnClickListener(this);
        imgScanQRcode.setOnClickListener(this);
        txtSelect.setOnClickListener(this);
        txtSelectionCancel.setOnClickListener(this);
        txtSelectDeselectAll.setOnClickListener(this);
        imgLogo.setOnClickListener(this);
        relativeTapToReveal.setOnClickListener(this);
    }


    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {
            case R.id.imgLogo:

                Intent mIntent = new Intent(mActivity, ExoPlayerActivity.class);
                mIntent.putExtra(Constants.MEDIA_URL, Constants.EDUCATION_VIDEO_URL);
                mIntent.putExtra(Constants.MEDIA_ID, "0");
                mIntent.putExtra(Constants.VIDEO_NAME,Constants.EDUCATION_VIDEO_TITLE);
                mIntent.putExtra(Constants.VIDEO_SEEK_TO,"0");
                mIntent.putExtra(Constants.IS_VIDEO_SEEK, false);
                mActivity.startActivity(mIntent);

                break;
            case R.id.imgScanQRcode:

                if(CommonMethods.areNotificationsEnabled(mContext)){
                    Intent mIntentQRCodeActivity = new Intent(mActivity, QrCodeScannerActivity.class);
                    mActivity.startActivity(mIntentQRCodeActivity);
                }else{
                    CommonMethods.openNotificationSettings(mActivity);

                }

                break;

            case R.id.txtSelect:

                if (linearRevealTitle.getVisibility() == View.VISIBLE) {
                    linearRevealTitle.setVisibility(View.GONE);
                    linearRevealSelection.setVisibility(View.VISIBLE);

                    //CHANGE HOMEPAGE TAB BAR
                    resetBottomBarLayout(true);


                    //CHANGE SELECT ALL TO SELECT ALL
                    txtSelectDeselectAll.setText(getResources().getString(R.string.strSelectALl));

                    //DISPLAY LIST WITH CHECK BOX
                    //FIRST PARAM -> FALSE -> FOR CHECKBOX SELECTION
                    //SECOND PARAM -> TRUE -> FOR VISIBILITY OF CHECK BOX TRUE
                    //ELSE APP MODE IS SIMULATION
                    if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                        updateRevealitHistoryList(false, true);

                    } else {
                        updateRevealitHistoryListSimulation(false, true);
                    }
                }
                break;

            case R.id.txtSelectDeselectAll:

                if (txtSelectDeselectAll.getText().equals(getResources().getString(R.string.strSelectALl))) {

                    //DISPLAY LIST WITH CHECK BOX SELECTED TRUE
                    if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                        updateRevealitHistoryList(true, true);

                    } else {
                        updateRevealitHistoryListSimulation(true, true);
                    }

                    //CHANGE SELECT ALL TO DESELECT ALL
                    txtSelectDeselectAll.setText(getResources().getString(R.string.strDeselectALl));
                } else {

                    //DISPLAY LIST WITH CHECK BOX SELECTED FALSE
                    //DISPLAY LIST WITH CHECK BOX SELECTED TRUE
                    if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                        updateRevealitHistoryList(false, true);

                    } else {
                        updateRevealitHistoryListSimulation(false, true);
                    }

                    //CHANGE SELECT ALL TO SELECT ALL
                    txtSelectDeselectAll.setText(getResources().getString(R.string.strSelectALl));
                }
                break;

            case R.id.txtSelectionCancel:

                if (linearRevealSelection.getVisibility() == View.VISIBLE) {
                    linearRevealTitle.setVisibility(View.VISIBLE);
                    linearRevealSelection.setVisibility(View.GONE);

                    //CHANGE HOMEPAGE TAB BAR
                    resetBottomBarLayout(false);


                    //DISPLAY LIST WITH CHECK BOX SELECTED TRUE
                    if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
                        updateRevealitHistoryList(false, false);

                    } else {
                        updateRevealitHistoryListSimulation(false, false);
                    }
                }
                break;

            case R.id.relativeTapToReveal:

                doRecording();

                break;
            case R.id.imgListen:

                doRecording();

                break;


        }

    }

    private void doRecording() {

        //IF TRUE APP MODE IS LIVE
        //ELSE APP MODE IS SIMULATION
        if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {

            //START RECORDING
            startRecording();
        } else {

            //SET IMAGE CLICKBLE FALSE
            relativeTapToReveal.setClickable(false);
            imgListen.setClickable(false);

            //CHECK IF TAPCOUNT IS MUST LESS THAN DATA
            if (tapCount < (mDatabaseHelper.getCategoryWisePlayList().size() + 1)) {

                //START ANIMATION
                rippleBackground.startRippleAnimation();
                linearWavingBackground.setVisibility(View.VISIBLE);
                txtReveal.setVisibility(View.INVISIBLE);


                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        //ADD CURRENT TIME IN ARRAYLIST
                        mLongRevealTime.add(System.currentTimeMillis());

                        //FETCH SUBLIST FROM DATABASE
                        updateUI(0, tapCount);

                        //INCREASE TAP COUNT
                        tapCount++;

                        //START ANIMATION
                        rippleBackground.stopRippleAnimation();
                        linearWavingBackground.setVisibility(View.GONE);
                        txtReveal.setVisibility(View.VISIBLE);


                        //RESET BOTTOM BAR VIEW ON EACH NEW VIDEOS
                        resetBottomBarAndMyRevealsView();


                    }
                }, 1000);

            }
        }

    }

    private static void resetBottomBarLayout(boolean isLayoutReset) {
        mHomeScreenTabLayout.isVideoDeleteMultiSelectionActive(isLayoutReset);
    }

    public static void clearListenHistory(boolean isAppModeIsLive) {

        if(isAppModeIsLive){

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            removeSelectedVideos();
        }else{
            removeSelectedVideosSimulationMode();
        }

    }

    private static void removeSelectedVideosSimulationMode() {

        //REMOVE VIDEOS FROM LOCAL DATABASE UNTIL ALL SELECTED IDS LIST
        for (int i =0 ; i< selectedIdsList.size(); i++){
            //REMOVE SINGLE VIDEO FROM DATABASE
            mDatabaseHelper.clearSimulationHistoryItem(selectedIdsList.get(i));
        }

        //CHANGE HOMEPAGE TAB BAR
        resetBottomBarAndMyRevealsView();
    }

    private static void removeSelectedVideos() {
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


        Call<JsonElement> call = patchService1.removeMultipleSelectedVidios(selectedIdsList);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ clearListenHistory: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ clearListenHistory: ", "" + response.code());

                if(response.code() == 200){
                    //REFRESH WHOLE SCREEN
                    //GET HISTORY
                    callGetRevealitVideoHistory();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                CommonMethods.printLogE("Response @ clearListenHistory Error", "" + t.getMessage());


            }
        });
    }

    private void startRecording() {

        if (CheckPermissions()) {

            //IMAGE CLICK FALSE
            relativeTapToReveal.setClickable(false);
            imgListen.setClickable(false);

            //START RIPPLE ANIMATION
            rippleBackground.startRippleAnimation();
            linearWavingBackground.setVisibility(View.VISIBLE);
            txtReveal.setVisibility(View.INVISIBLE);

            //CREATE FILE PATH WHERE WE WILL STORE RECORDERD AUDIO FOR IDENTIFY
            String filePath = getActivity().getApplicationContext().getFilesDir().getPath();

            File file = new File(filePath);

            if (!file.exists()) {
                file.mkdirs();
            }

            mFileName = file + Constants.SAVED_AUDIOFILE_NAME;

            mRecorder = new MediaRecorder();

            //INITIALISE MEDIA RECORDER
            mRecorder = new MediaRecorder();

            //RECORD AUDIO FROM MIC
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            //SET OUTPUT MEDIA FILE TYPE
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            //AUDIO ANCODER
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            //SET OUTPUT AUDIO FILE
            mRecorder.setOutputFile(mFileName);


            try {
                //PREPARE AUDIO
                mRecorder.prepare();
            } catch (IOException e) {

                //SHOW DIALOGUE IF AUDIO THROW ANY ERROR
                CommonMethods.buildDialog(mContext, Constants.MICROPHONE_CANNOT_WORK);

            }

            //START AUDIO RECORDING
            mRecorder.start();

            //START RECORDING FOR 5SECOND THAN RELEASE MEDIA PLAYER AND GET ACR CLOUD DATA
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;


                    //START FINDING AUDIO
                    new RecThread().start();


                    //STOP RIPPLE ANIMATION
                    rippleBackground.stopRippleAnimation();
                    linearWavingBackground.setVisibility(View.GONE);
                    txtReveal.setVisibility(View.VISIBLE);

                    //MAKE IMAGE CLICKBLE TRUE
                    relativeTapToReveal.setClickable(true);
                    imgListen.setClickable(true);


                }
            }, 5000);

        } else {
            RequestPermissions();
        }
    }

    // INTERFACE METHOD TO ACHIEVE EVENTS FROM ADAPTER
    @Override
    public void removeListenHistory(boolean isFromLivemode) {

        if (isFromLivemode) {
            //GET REVEAL IT HISTORY DATA ON FRAGMENT LOAD
            callGetRevealitVideoHistory();
        } else {
            //START COUNT FROM 1ST
            tapCount = 1;

            //UPDATE LIST AS PER USERS CHOICE
            updateRevealitHistoryListSimulation(false,false);
        }
    }

    @Override
    public void getSelectedIds(ArrayList<Integer> selectedIdsList) {

        this.selectedIdsList =selectedIdsList;

        //UPDATE SELECTED IDS COUNTS AND TOTAL COUNTS
        txtRevealSelectedCount.setText(selectedIdsList.size()+"/"+mRevealItHistoryListAdapter.getItemCount()+ " "+getResources().getString(R.string.strRevealSelected) );

        Log.e("de_Select","LLLLLL");
    }

    @Override
    public void isSingleTimeStampDeleted(boolean isTimeStampDeleted) {
   if(isTimeStampDeleted){
       callGetRevealitVideoHistory();
   }
    }

    class RecThread extends Thread {

        public void run() {

            File file = new File(mFileName);
            if (file.canRead()) {
                getAudioIdentityFromACRCloud();
            } else {
                CommonMethods.buildDialog(mContext, Constants.AUDIO_CAN_NOT_RECORD);

                return;
            }

        }
    }


    private void getAudioIdentityFromACRCloud() {

        Map<String, Object> config = new HashMap<String, Object>();
        config.put(Constants.KEY_ACCESS_KEY, Constants.KEY_ACCESS_KEY_VALUE);
        config.put(Constants.KEY_ACCESS_SECRET, Constants.KEY_ACCESS_SECRET_VALUE);
        config.put(Constants.KEY_HOST, Constants.KEY_HOST_VALUE);
        config.put(Constants.KEY_TIMEOUT, Constants.KEY_TIMEOUT_VALUE);

        ACRCloudRecognizer re = new ACRCloudRecognizer(config);
        String result = re.recognizeByFile(mFileName, 0);

        try {
            JSONObject obj = new JSONObject(result);
            JSONObject objDetails = new JSONObject(obj.getJSONObject("metadata").getJSONArray("custom_files").getString(0));


            //CALL VIDEO DETAILS API
            //IF AUDIO RECOGNISED TRUE
            getVideoDetails(objDetails.getString("acrid").toString(), objDetails.getString("play_offset_ms").toString());


        } catch (Exception e) {

            CommonMethods.buildDialog(mContext, Constants.AUDIO_NOT_RECOGNISED);

        }

        try {
            Message msg = new Message();
            msg.obj = result;

            msg.what = 1;
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getVideoDetails(String strACRID, String strPlayOffsetMS) {

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

        Call<JsonElement> call = patchService1.getVideoDetailsFromACRID(Constants.API_GET_VIDEO_DETAILS_LISTEN_SCREEN_FROM_ACRID + strACRID + "?play_offset_ms=" + strPlayOffsetMS);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ getVideoDetails: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ getVideoDetails: ", "" + response.code());


                if (response.isSuccessful() && response.code() == Constants.API_CODE_201) {


                    callGetRevealitVideoHistory();


                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                CommonMethods.printLogE("Response @ getVideoDetails Error", "" + t.getMessage());


            }
        });

    }

    public static void callGetRevealitVideoHistory() {

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

        Call<RevealitHistoryModel> call = patchService1.getRevealitHistory();

        call.enqueue(new Callback<RevealitHistoryModel>() {
            @Override
            public void onResponse(Call<RevealitHistoryModel> call, Response<RevealitHistoryModel> response) {

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ callGetRevealitVideoHistory: ", "" + gson.toJson(response.body()));


                CommonMethods.printLogE("Response @ callGetRevealitVideoHistory: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callGetRevealitVideoHistory: ", "" + response.body().getData());

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

                    //CLEAR TABLE AND INSERT NEW DATA
                    mDatabaseHelper.clearRevealitHistoryListTable();

                    //INSERT DATA IN TO DATABASE
                    for (int i = 0; i < response.body().getData().size(); i++) {


                        if (mDatabaseHelper.getRevealitHistoryItemFromMediaID(response.body().getData().get(i).getMedia_id()).size() != 0) {

                            //GET STRING DISPLAY TIME STAMP DISPLAY
                            String mStringsAllTimeStamp = mDatabaseHelper.getRevealitHistoryItemFromMediaID(response.body().getData().get(i).getMedia_id()).get(0).getAllTimeStamp();

                            //CONVERT STRING TO ARRAYLIST
                            List<String> mListAllTimeStamp = new ArrayList<String>(Arrays.asList(mStringsAllTimeStamp));

                            //ADD TIME STAMP DISPLAY TO ARRAY LIST AND THAN UPDATE STRING IN TO DATABASE
                            mListAllTimeStamp.add(response.body().getData().get(i).getPlayback_display());


                            //GET STRING DISPLAY TIME STAMP OFFSET
                            String mStringsAllTimeStampOffset = mDatabaseHelper.getRevealitHistoryItemFromMediaID(response.body().getData().get(i).getMedia_id()).get(0).getAllTimeStampOffset();

                            //CONVERT STRING TO ARRAYLIST
                            List<String> mListAllTimeStampOffset = new ArrayList<String>(Arrays.asList(mStringsAllTimeStampOffset));

                            //ADD TIME STAMP OFFSET TO ARRAY LIST AND THAN UPDATE STRING IN TO DATABASE
                            mListAllTimeStampOffset.add(response.body().getData().get(i).getPlayback_offset());


                            mDatabaseHelper.updateRevealitHistoryList(response.body().getData().get(i).getMatch_id(),
                                    response.body().getData().get(i).getMedia_id(),
                                    response.body().getData().get(i).getMedia_title(),
                                    response.body().getData().get(i).getMedia_type(),
                                    response.body().getData().get(i).getMedia_url(),
                                    response.body().getData().get(i).getMedia_cover_art(),
                                    response.body().getData().get(i).getCurrent_time(),
                                    response.body().getData().get(i).getPlayback_offset(),
                                    response.body().getData().get(i).getPlayback_display(),
                                    response.body().getData().get(i).getMatch_time_stamp(),
                                    mListAllTimeStamp.toString(),
                                    mListAllTimeStampOffset.toString());

                        } else {

                            mDatabaseHelper.insertRevealitHistoryData(response.body().getData().get(i).getMatch_id(),
                                    response.body().getData().get(i).getMedia_id(),
                                    response.body().getData().get(i).getMedia_title(),
                                    response.body().getData().get(i).getMedia_type(),
                                    response.body().getData().get(i).getMedia_url(),
                                    response.body().getData().get(i).getMedia_cover_art(),
                                    response.body().getData().get(i).getCurrent_time(),
                                    response.body().getData().get(i).getPlayback_offset(),
                                    response.body().getData().get(i).getPlayback_display(),
                                    response.body().getData().get(i).getMatch_time_stamp(),
                                    response.body().getData().get(i).getPlayback_display(),
                                    response.body().getData().get(i).getPlayback_offset());

                        }

                    }

                }

                //RESET BOTTOMBAR VIEW AND MY REVEALS VIEW
                resetBottomBarAndMyRevealsView();


                //HIDE PROGRESSBAR
                pDialog.cancel();
            }

            @Override
            public void onFailure(Call<RevealitHistoryModel> call, Throwable t) {

                CommonMethods.printLogE("Response @ callGetRevealitVideoHistory Error", "" + t.getMessage());

                //BUILD ERROR DIALOG DIALOG
                CommonMethods.buildDialog(mContext, Constants.SOMETHING_WENT_WRONG);


                //HIDE PROGRESSBAR
                pDialog.cancel();


            }
        });


    }

    private static void resetBottomBarAndMyRevealsView() {


        linearRevealTitle.setVisibility(View.VISIBLE);
        linearRevealSelection.setVisibility(View.GONE);

        //RESET BOTTOM BAR
        //CHANGE HOMEPAGE TAB BAR
        resetBottomBarLayout(false);

        //RELOAD HISTORY LIST IF NOT EMPTY
        if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
        updateRevealitHistoryList(false, false);
        }else{
        updateRevealitHistoryListSimulation(false,false);
        }

    }

    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_AUDIO_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        startRecording();
                    } else {
                        CommonMethods.buildDialog(mContext, "Permission denied!");
                    }
                }
                break;
        }
    }



    private static void callRemoveVideo(boolean isClearHistory, int media_id) {

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
    public static void updateRevealitHistoryList(boolean isCheckBoxSelected, boolean shouldCheckBoxVisible) {



        ArrayList<RevealitHistoryModel.Data> mHistoryListFromDatabase = null;
        try {
            mHistoryListFromDatabase =  new FatchDataFromDatabaseTask().execute(mDatabaseHelper).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //SET REVEAL IT HISTORY FOR LIVE MODE
        //IF LIST ATTACH FOR THE FIRST TIME ELSE JUST NOTIFY LISTENER WITH FRESH LIST


        if (mRevealItHistoryListAdapter == null) {

            mRevealItHistoryListAdapter = new RevealItHistoryListAdapter(mContext, mHomeScreenTabLayout, mHistoryListFromDatabase, mRemoveListenHistory, isCheckBoxSelected, shouldCheckBoxVisible);
            recycleRevealList.setAdapter(mRevealItHistoryListAdapter);

            //SWIPE HELPER SHOULD ATTACH FIRST TIME
            ArrayList<RevealitHistoryModel.Data> finalMHistoryListFromDatabase = mHistoryListFromDatabase;

            new SwipeHelper(mActivity, recycleRevealList) {
                @Override
                public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {

                    underlayButtons.add(new UnderlayButton(
                            R.mipmap.icn_share_with_text,
                            mActivity,
                            new UnderlayButtonClickListener() {
                                @Override
                                public void onClick(int pos) {

                                    try {
                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                        shareIntent.setType("text/plain");
                                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getResources().getString(R.string.app_name));
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, finalMHistoryListFromDatabase.get(pos).getMedia_url());
                                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                                        mContext.startActivity(Intent.createChooser(shareIntent,mActivity.getResources().getString(R.string.strSelectSharingPreference) ));

                                        isFromShareIntent = true;
                                    } catch(Exception e) {
                                        //e.toString();
                                    }
                                }
                            }
                    ));

                    underlayButtons.add(new UnderlayButton(
                            R.mipmap.icn_delete_with_text,
                            mActivity,
                            new UnderlayButtonClickListener() {
                                @Override
                                public void onClick(int pos) {
                                    // TODO: onDelete
                                    //IF TRUE -> APP IS IN LIVE MODE - MEANS CALL API
                                    //ELSE -> APP IS IN SIMULATION MODE - MEANS SAVE AND DELETE DATA FROM LOCAL
                                    if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {

                                        //CALL API REMOVE SINGLE VIDEO
                                        callRemoveVideo(false, finalMHistoryListFromDatabase.get(pos).getMedia_id());

                                    } else {

                                        //REMOVE SINGLE VIDEO FROM DATABASE
                                        mDatabaseHelper.clearSimulationHistoryItem(finalMHistoryListFromDatabase.get(pos).getMedia_id());

                                        //UPDATE LIST THROUGH INTERFACE
                                        mRemoveListenHistory.removeListenHistory(false);

                                    }

                                }
                            }
                    ));

                }
            };


        } else {

            //ELSE -> NOTIFY LIST WITH LATEST LIST DATA
            mRevealItHistoryListAdapter.updateListData(mHistoryListFromDatabase, isCheckBoxSelected, shouldCheckBoxVisible);

        }


        //SET SIZE TO REVEAL IT
        txtRevealCount.setText("" + mHistoryListFromDatabase.size() + " reveals");

        //SET TOTAL SELECTED REVEALS
        txtRevealSelectedCount.setText("0/"+mRevealItHistoryListAdapter.getItemCount()+ " "+mActivity.getResources().getString(R.string.strRevealSelected) );


        //ENABLE BUTTON
        relativeTapToReveal.setClickable(true);

        imgListen.setClickable(true);


    }

    private static void updateRevealitHistoryListSimulation(boolean isCheckBoxSelected, boolean shouldCheckBoxVisible) {

        ArrayList<RevealitHistoryModel.Data> mHistoryListFromDatabase = null;
        try {
            mHistoryListFromDatabase = new FetchDataFromDatabaseSimulationTask().execute(mDatabaseHelper).get();

            //SET REVEAL IT HISTORY FOR LIVE MODE
            //IF LIST ATTACH FOR THE FIRST TIME ELSE JUST NOTIFY LISTENER WITH FRESH LIST
            if (mRevealItHistoryListAdapter == null) {

                mRevealItHistoryListAdapter = new RevealItHistoryListAdapter(mContext, mHomeScreenTabLayout, mHistoryListFromDatabase, mRemoveListenHistory, isCheckBoxSelected, shouldCheckBoxVisible);
                recycleRevealList.setAdapter(mRevealItHistoryListAdapter);

                //SWIPE HELPER SHOULD ATTACH FIRST TIME
                ArrayList<RevealitHistoryModel.Data> finalMHistoryListFromDatabase = mHistoryListFromDatabase;

                new SwipeHelper(mActivity, recycleRevealList) {
                    @Override
                    public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {

                        underlayButtons.add(new SwipeHelper.UnderlayButton(
                                R.mipmap.icn_share_with_text,
                                mActivity,
                                new UnderlayButtonClickListener() {
                                    @Override
                                    public void onClick(int pos) {

                                        try {
                                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                            shareIntent.setType("text/plain");
                                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getResources().getString(R.string.app_name));
                                            shareIntent.putExtra(Intent.EXTRA_TEXT, finalMHistoryListFromDatabase.get(pos).getMedia_url());
                                            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                                            mContext.startActivity(Intent.createChooser(shareIntent,mActivity.getResources().getString(R.string.strSelectSharingPreference) ));
                                        } catch(Exception e) {
                                            //e.toString();
                                        }                                    }
                                }
                        ));

                        underlayButtons.add(new SwipeHelper.UnderlayButton(
                                R.mipmap.icn_delete_with_text,
                                mActivity,
                                new SwipeHelper.UnderlayButtonClickListener() {
                                    @Override
                                    public void onClick(int pos) {
                                        // TODO: onDelete
                                        //IF TRUE -> APP IS IN LIVE MODE - MEANS CALL API
                                        //ELSE -> APP IS IN SIMULATION MODE - MEANS SAVE AND DELETE DATA FROM LOCAL
                                        if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {

                                            //CALL API REMOVE SINGLE VIDEO
                                            callRemoveVideo(false, finalMHistoryListFromDatabase.get(pos).getMedia_id());

                                        } else {

                                            //REMOVE SINGLE VIDEO FROM DATABASE
                                            mDatabaseHelper.clearSimulationHistoryItem(finalMHistoryListFromDatabase.get(pos).getMedia_id());

                                            //UPDATE LIST THROUGH INTERFACE
                                            mRemoveListenHistory.removeListenHistory(false);

                                        }

                                    }
                                }
                        ));

                    }
                };


            } else {

                //ELSE -> NOTIFY LIST WITH LATEST LIST DATA
                mRevealItHistoryListAdapter.updateListData(mHistoryListFromDatabase, isCheckBoxSelected, shouldCheckBoxVisible);
            }


            //SET SIZE TO REVEAL IT
            txtRevealCount.setText("" + mHistoryListFromDatabase.size() + " reveals");

            //SET TOTAL SELECTED REVEALS
            txtRevealSelectedCount.setText("0/" + mRevealItHistoryListAdapter.getItemCount() + " " +mActivity.getResources().getString(R.string.strRevealSelected));
            Log.e("de_Select","MMMMMM");

            //ENABLE BUTTON
            relativeTapToReveal.setClickable(true);
            imgListen.setClickable(true);


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //SET SIZE TO REVEAL IT
        txtRevealCount.setText("" + mDatabaseHelper.getRevealitHistoryDataSimulation().size() + " reveals");

        //ENABLE BUTTON
        relativeTapToReveal.setClickable(true);
        imgListen.setClickable(true);


    }


    private void updateUI(int from, int to) {

        //GET SUBLIST BASED ON COUNT AND THAN SAVE DUMMY DATA TO THE DATABASE
        for (int i = 0; i < mCategoryWisePlayListModel.subList(from, to).size(); i++) {

            if (mDatabaseHelper.getRevealitHistoryItemFromMediaIDSimulation(mCategoryWisePlayListModel.subList(from, to).get(i).getMediaID()).size() == 0)
                mDatabaseHelper.insertRevealitHistoryDataSimulation(mCategoryWisePlayListModel.subList(from, to).get(i).getMediaID(),
                        mCategoryWisePlayListModel.subList(from, to).get(i).getMediaID(),
                        mCategoryWisePlayListModel.subList(from, to).get(i).getMediaTitle(),
                        mCategoryWisePlayListModel.subList(from, to).get(i).getMediaType(),
                        mCategoryWisePlayListModel.subList(from, to).get(i).getMediaUrl(),
                        mCategoryWisePlayListModel.subList(from, to).get(i).getMediaCoverArt(),
                        "00",
                        "00",
                        "0:0:10",
                        "0",
                        "0:00:" + (to + 5),
                        "" + (to + 5));

        }

        //UPDATE DATA
        updateRevealitHistoryListSimulation(false, false);

    }



}

class FatchDataFromDatabaseTask extends AsyncTask<DatabaseHelper, Integer, ArrayList<RevealitHistoryModel.Data>> {


    @Override
    protected ArrayList<RevealitHistoryModel.Data> doInBackground(DatabaseHelper... databaseHelpers) {
        return databaseHelpers[0].getRevealitHistoryData();
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(ArrayList<RevealitHistoryModel.Data> searchResults) {
        super.onPostExecute(searchResults);

    }
}
class FetchDataFromDatabaseSimulationTask extends AsyncTask<DatabaseHelper, Integer, ArrayList<RevealitHistoryModel.Data>> {


    @Override
    protected ArrayList<RevealitHistoryModel.Data> doInBackground(DatabaseHelper... databaseHelpers) {
        return databaseHelpers[0].getRevealitHistoryDataSimulation();
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(ArrayList<RevealitHistoryModel.Data> searchResults) {
        super.onPostExecute(searchResults);

    }
}
