package com.Revealit.Fragments;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.Activities.QrCodeScannerActivity;
import com.Revealit.Adapter.RevealItHistoryListAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.CommonClasse.SwipeHelper;
import com.Revealit.CustomViews.RippleBackground;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ListenFragment extends Fragment implements View.OnClickListener, RemoveListenHistory {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "ListenFragment";
    private View mView;
    private String path = "";

    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView imgScanQRcode,imgListen;
    private TextView txtReveal,txtRevealCount;
    private RecyclerView recycleRevealList;
    private LinearLayoutManager recylerViewLayoutManager;
    private RippleBackground rippleBackground;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private RemoveListenHistory mRemoveListenHistory;
    private int tapCount = 1;// IGONORE FIRST COUNT AND THEN START GETTING DATA FROM ITEMS TO END ITEMS.
    private ArrayList<CategoryWisePlayListModel.DataBean> mCategoryWisePlayListModel = new ArrayList<>();
    private ArrayList<Long> mLongRevealTime = new ArrayList<>();
    private Activity homeScreenTabLayout;
    private LinearLayout linearWavingBackground;
    private RevealItHistoryListAdapter mRevealItHistoryListAdapter;

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
        };
    };

    enum ButtonsState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }
    private boolean swipeBack = false;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
    private static final float buttonWidth = 300;


    public ListenFragment(HomeScreenTabLayout homeScreenTabLayout) {
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
        mView = inflater.inflate(R.layout.fragment_listen, container, false);

        //SET IDS
        setIds();
        setOnClicks();


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
        imgScanQRcode =(ImageView)mView.findViewById(R.id.imgScanQRcode);


        txtRevealCount = (TextView) mView.findViewById(R.id.txtRevealCount);
        txtReveal = (TextView) mView.findViewById(R.id.txtReveal);

        rippleBackground = (RippleBackground) mView.findViewById(R.id.content);

        linearWavingBackground =(LinearLayout)mView.findViewById(R.id.linearWavingBackground);

        recycleRevealList = (RecyclerView) mView.findViewById(R.id.recycleRevealList);
        recylerViewLayoutManager = new LinearLayoutManager(mActivity);
        recycleRevealList.setLayoutManager(recylerViewLayoutManager);

        //INITIALIZE INTERFACE
        mRemoveListenHistory = (RemoveListenHistory) this;


        if(mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)){

            //INITIALIZED ARC CLOUD LIBRARY
            ACRCloudExtrTool.setDebug();

        }else{

            //GET WATCH DATA FROM DATABASE
            mCategoryWisePlayListModel = mDatabaseHelper.getCategoryWisePlayList();

            //CREATE LONG REAVEAL IT TIME STAMP ARRAY
            mLongRevealTime = new ArrayList<>();
            mLongRevealTime.clear();

            //SET INITIAL COUNT FOR GETTING SUB LIST FROM DATABASE
            tapCount = 1;

            //RELOAD HISTORY LIST IF NOT EMPTY
            updateRevealitHistoryListSimulation();


        }




    }
    @Override
    public void setMenuVisibility(boolean menuVisible) {


        if (menuVisible  ) {

            //SET IDS
            setIds();
            setOnClicks();

            //GET REVEALIT HISTORY DATA ON FRAGMENT LOAD
            if(mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)){
            callGetRevealitVideoHistory();
            }
        }
        super.setMenuVisibility(menuVisible);
    }


    @Override
    public void onResume() {
        super.onResume();

        //SET IDS
        setIds();
        setOnClicks();

    }

    private void setOnClicks() {

        imgListen.setOnClickListener(this);
        imgScanQRcode.setOnClickListener(this);
    }




    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {
            case R.id.imgScanQRcode:

                Intent mIntent = new Intent(mActivity, QrCodeScannerActivity.class);
                mActivity.startActivity(mIntent);


                break;

            case R.id.imgListen:

                //IF TRUE APP MODE IS LIVE
                //ELSE APP MODE IS SIMULATION
                if (mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {

                    //IMAGE CLICK FALSE
                    imgListen.setClickable(false);

                    //START RECORDING
                    startRecording();
                }else{

                    //SET IMAGE CLICKBLE FALSE
                    imgListen.setClickable(false);

                    //CHECK IF TAPCOUNT IS MUST LESS THAN DATA
                    if(tapCount < (mDatabaseHelper.getCategoryWisePlayList().size()+1)) {

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


                            }
                        }, 2000);

                    }
                }

        }

    }

    private void startRecording() {

        if (CheckPermissions()) {

            //START RIPPLE ANIMATION
            rippleBackground.startRippleAnimation();
            linearWavingBackground.setVisibility(View.VISIBLE);
            txtReveal.setVisibility(View.INVISIBLE);

            //CREATE FILE PATH WHERE WE WILL STORE RECORDERD AUDIO FOR IDENTIFY
            String filePath =getActivity().getApplicationContext().getFilesDir().getPath();

            File file= new File(filePath);

            if (!file.exists()){
                file.mkdirs();
            }

            mFileName= file + Constants.SAVED_AUDIOFILE_NAME;

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
                //PREPAIR AUDIO
                mRecorder.prepare();
            } catch (IOException e) {

                //SHOW DIALOGUE IF AUDIO THROW ANY ERROR
                CommonMethods.buildDialog(mContext , Constants.MICROPHONE_CANNOT_WORK);

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
                    imgListen.setClickable(true);



                }
            }, 5000);

        } else {
            RequestPermissions();
        }
    }

    // INTERFACE METHOF TO ACHIEVE EVENTS FROM ADAPTER
    @Override
    public void removeListenHistory(boolean isFromLivemode) {

        if(isFromLivemode) {
            //GET REVEALIT HISTORY DATA ON FRAGMENT LOAD
            callGetRevealitVideoHistory();
        }else{
            //START COUNT FROM 1ST
            tapCount = 1;

            //UPDATE LIST AS PER USERS CHOICE
            updateRevealitHistoryListSimulation();
        }
    }

    class RecThread extends Thread {

        public void run() {

            File file = new File(mFileName);
            if (file.canRead()) {
                getAudioIdentityFromACRCloud();
            } else {
                CommonMethods.buildDialog(mContext , Constants.AUDIO_CAN_NOT_RECORD);

                return;
            }

        }
    }


    private void getAudioIdentityFromACRCloud() {

        Map<String, Object> config = new HashMap<String, Object>();
        config.put(Constants.KEY_ACCESS_KEY, Constants.KEY_ACCESS_KEY_VALUE);
        config.put(Constants.KEY_ACCESS_SECRET, Constants.KEY_ACCESS_SECRET_VALUE);
        config.put( Constants.KEY_HOST, Constants.KEY_HOST_VALUE);
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

            CommonMethods.buildDialog(mContext , Constants.AUDIO_NOT_RECOGNISED);

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

        Call<JsonElement> call = patchService1.getVideoDetailsFromACRID(Constants.API_GET_VIDEO_DETAILS_LISTEN_SCREEN_FROM_ACRID+strACRID+"?play_offset_ms="+strPlayOffsetMS);

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

                CommonMethods.printLogE("Response @ getVideoDetails Error", "" +t.getMessage());



            }
        });

    }

    public void callGetRevealitVideoHistory() {

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

                CommonMethods.printLogE("Response @ callGetRevealitVideoHistory: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callGetRevealitVideoHistory: ", "" + response.code());



                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

                    //CLEAR TABLE AND INSERT NEW DATA
                    mDatabaseHelper.clearRevealitHistoryListTable();

                    //INSERT DATA IN TO DATABASE
                    for (int i =0 ; i < response.body().getData().size() ; i++){


                        if(mDatabaseHelper.getRevealitHistoryItemFromMediaID(response.body().getData().get(i).getMedia_id()).size() != 0){

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

                        }else{


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

                //RELOAD HISTORY LIST IF NOT EMPTY
                updateRevealitHistoryList();

                //HIDE PROGRESSBAR
                pDialog.cancel();
            }

            @Override
            public void onFailure(Call<RevealitHistoryModel> call, Throwable t) {

                CommonMethods.printLogE("Response @ callGetRevealitVideoHistory Error", "" +t.getMessage());

                //BUILD ERROR DIALOG DIALOG
                CommonMethods.buildDialog(mContext, Constants.SOMETHING_WENT_WRONG);


                //HIDE PROGRESSBAR
                pDialog.cancel();


            }
        });


    }




    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_AUDIO_PERMISSION_CODE);
    }

    private void updateRevealitHistoryList() {


        //SET REVEAL IT HISTORY FOR LIVE MODE
        mRevealItHistoryListAdapter = new RevealItHistoryListAdapter(mContext, mActivity, mDatabaseHelper.getRevealitHistoryData(),mRemoveListenHistory);
        recycleRevealList.setAdapter(mRevealItHistoryListAdapter);
        new SwipeHelper(getContext(), recycleRevealList) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {


                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Share",
                        R.mipmap.icn_share,
                        Color.parseColor("#F5F5F5"),
                        getContext(),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: OnTransfer
                                CommonMethods.displayToast(mContext,"Share button clicked!");
                            }
                        }
                ));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Delete",
                        R.mipmap.icn_delete,
                        Color.parseColor("#F5F5F5"),
                        getContext(),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: onDelete
                                CommonMethods.displayToast(mContext,"Delete button clicked!");

                            }
                        }
                ));

            }
        };


        //SET SIZE TO REVEAL IT
        txtRevealCount.setText(""+mDatabaseHelper.getRevealitHistoryData().size()+" reveals");

        //ENABLE BUTTON
        imgListen.setClickable(true);


    }

    private void updateRevealitHistoryListSimulation() {


        //SET REVEAL IT HISTORY FOR SIMULATION MODE
        RevealItHistoryListAdapter mRevealItHistoryListAdapter = new RevealItHistoryListAdapter(mContext, mActivity, mDatabaseHelper.getRevealitHistoryDataSimulation(),mRemoveListenHistory);
        recycleRevealList.setAdapter(mRevealItHistoryListAdapter);

        //SET SIZE TO REVEAL IT
        txtRevealCount.setText(""+mDatabaseHelper.getRevealitHistoryData().size()+" reveals");

        //ENABLE BUTTON
        imgListen.setClickable(true);


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
                        CommonMethods.buildDialog(mContext , "Permission denied!");
                    }
                }
                break;
        }
    }

    private void updateUI(int from, int to) {

        //GET SUBLIST BASED ON COUNT AND THAN SAVE DUMMY DATA TO THE DATABASE
        for(int i= 0; i < mCategoryWisePlayListModel.subList(from, to).size() ; i++){

            if(mDatabaseHelper.getRevealitHistoryItemFromMediaIDSimulation(mCategoryWisePlayListModel.subList(from, to).get(i).getMediaID()).size() == 0)
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
                    "0:00:"+(to+5),
                    ""+(to+5));

        }

        //UPDATE DATA
        updateRevealitHistoryListSimulation();

    }


}
