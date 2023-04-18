package com.Revealit.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.Activities.ImportAccountFromPrivateKey;
import com.Revealit.Activities.QrCodeScannerActivity;
import com.Revealit.Adapter.SilosAvailableAccountsListAdapterPopup;
import com.Revealit.BuildConfig;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.CommonClasse.SwipeHelper;
import com.Revealit.ModelClasses.KeyStoreServerInstancesModel;
import com.Revealit.ModelClasses.NewAuthLogin;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.UserOnboardingProcess.NewAuthGetStartedActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "LogoutFragment";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private View mView,  viewAdmin,viewHelp, viewSettings, viewSavedItems, viewAccount;
    private HomeScreenTabLayout mHomeScreenTabLayout;
    private TextView txtUsername, txtAdmin,txtHelp, txtSettings, txtMySavedItems, txtAccount, txtStatusMsg, txtStatus, txtCopyToClibBoard, txtMsgCopy;
    private String strUsername, strCopymsg,strPrivateKey,strAccountName,strRevealitPrivateKey;
    private boolean isUserIsActive =false;
    private LinearLayout linearAccount,linearSettings,linearSavedItems,linearUserStatus,linearAdmin,linearHelp;
    private ImageView imgLogo,imgScanQRcode;
    private RecyclerView recycleAccountList;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    public UserProfileFragment(HomeScreenTabLayout homeScreenTabLayout) {
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
        mView = inflater.inflate(R.layout.userprofile_fragment, container, false);

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

        txtMsgCopy = (TextView) mView.findViewById(R.id.txtMsgCopy);
        txtCopyToClibBoard = (TextView) mView.findViewById(R.id.txtCopyToClibBoard);
        txtUsername = (TextView) mView.findViewById(R.id.txtUsername);
        txtStatus = (TextView) mView.findViewById(R.id.txtStatus);
        txtStatusMsg = (TextView) mView.findViewById(R.id.txtStatusMsg);
        txtAccount = (TextView) mView.findViewById(R.id.txtAccount);
        txtMySavedItems = (TextView) mView.findViewById(R.id.txtMySavedItems);
        txtSettings = (TextView) mView.findViewById(R.id.txtSettings);
        txtHelp = (TextView) mView.findViewById(R.id.txtHelp);
        txtAdmin = (TextView) mView.findViewById(R.id.txtAdmin);

        viewAccount = (View) mView.findViewById(R.id.viewAccount);
        viewSavedItems = (View) mView.findViewById(R.id.viewSavedItems);
        viewSettings = (View) mView.findViewById(R.id.viewSettings);
        viewHelp = (View) mView.findViewById(R.id.viewHelp);
        viewAdmin = (View) mView.findViewById(R.id.viewAdmin);


        linearAccount=(LinearLayout)mView.findViewById(R.id.linearAccount);
        linearSavedItems=(LinearLayout)mView.findViewById(R.id.linearSavedItems);
        linearSettings=(LinearLayout)mView.findViewById(R.id.linearSettings);
        linearHelp=(LinearLayout)mView.findViewById(R.id.linearHelp);
        linearAdmin=(LinearLayout)mView.findViewById(R.id.linearAdmin);
        linearUserStatus=(LinearLayout)mView.findViewById(R.id.linearUserStatus);

        imgScanQRcode =(ImageView)mView.findViewById(R.id.imgScanQRcode);
        imgLogo =(ImageView)mView.findViewById(R.id.imgLogo);

        //GET USER STATUS
        //TRUE = USER IS ACTIVE AND VERIFIED
        //FLASE= USER IS NOT ACTIVE AND NOT YET VERIFIED
        isUserIsActive = mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_ACTIVE);

        //GET INTENT DATA
        strUsername = mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME);
        strCopymsg = mSessionManager.getPreference(Constants.KEY_INVITE_MSG);

        //SET USERNAME
        txtUsername.setText(""+strUsername);


        if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_CANCEL_REFERRAL)){
            linearAdmin.setVisibility(View.GONE);
            linearSettings.setVisibility(View.GONE);
            linearAccount.setVisibility(View.VISIBLE);
            linearHelp.setVisibility(View.VISIBLE);
        }else{
            //UPDATE UI BASED ON USER STATUS
            updateUI(isUserIsActive);

            //SET APPLICATION INSTALLED VERSION NAME AND SERVER NAME
            txtAdmin.setText(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME) +" Server : "+  BuildConfig.VERSION_NAME);


            //HIDE SHOW ADMIN DETAILS
            //CHECK USER IS ADMIN USER OR SUPER ADMIN USER
            //ADMIN -> USER MUST BE ADMIN FROM BACKEND
            //SUPER ADMIN -> IF ANY BETA USER IS ADMIN THAN CONSIDER USER AS ADMIN
            if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN)  | CommonMethods.checkIfUserIsSuperAdmin(mSessionManager,mContext)){
                linearSettings.setVisibility(View.VISIBLE);
            }else{
                linearSettings.setVisibility(View.GONE);
            }

            if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN)){
                linearAdmin.setVisibility(View.VISIBLE);
            }else{
                linearAdmin.setVisibility(View.INVISIBLE);
            }
        }




    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setIds();
    }

    private void setOnClicks() {

        txtCopyToClibBoard.setOnClickListener(this);
        imgLogo.setOnClickListener(this);

        //IF USER IS ACTIVE ONLY
        if(isUserIsActive){
            linearAccount.setOnClickListener(this);
            linearSavedItems.setOnClickListener(this);
            linearSettings.setOnClickListener(this);
            linearHelp.setOnClickListener(this);
            linearAdmin.setOnClickListener(this);
            imgScanQRcode.setOnClickListener(this);

        }else if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_CANCEL_REFERRAL)){
            linearAccount.setOnClickListener(this);
            linearHelp.setOnClickListener(this);

        }


    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        if (menuVisible) {
            setIds();
            setOnClicks();

        }
        super.setMenuVisibility(menuVisible);
    }


    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {

            case R.id.txtCopyToClibBoard:

                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.strUsername), getResources().getString(R.string.strCopyMsg).replace("xxxx",strUsername));
                clipboard.setPrimaryClip(clip);

                //TOAST MSG
                CommonMethods.displayToast(mContext, getString(R.string.strUsernameCopied));

                break;
            case R.id.linearAccount:

                loadFragments(new AccountFragmentContainer(mHomeScreenTabLayout));

                break;
            case R.id.linearHelp:

                loadFragments(new HelpFragmentContainer(mHomeScreenTabLayout));

                break;

            case R.id.linearSettings:

                loadFragments(new SettingsFragmentContainer(mHomeScreenTabLayout));

                break;
            case R.id.linearAdmin:

                loadFragments(new AdminFragment(mHomeScreenTabLayout));

                break;
            case R.id.imgScanQRcode:

                if(CommonMethods.areNotificationsEnabled(mContext)){
                    Intent mIntentQRCodeActivity = new Intent(mActivity, QrCodeScannerActivity.class);
                    mActivity.startActivity(mIntentQRCodeActivity);
                }else{
                    CommonMethods.openNotificationSettings(mActivity);

                }

                break;
            case R.id.imgLogo:

                mHomeScreenTabLayout.openBottomBarForSwitchAccount();

//                Intent mIntent = new Intent(mActivity, ExoPlayerActivity.class);
//                mIntent.putExtra(Constants.MEDIA_URL, Constants.EDUCATION_VIDEO_URL);
//                mIntent.putExtra(Constants.MEDIA_ID, "0");
//                mIntent.putExtra(Constants.VIDEO_NAME,Constants.EDUCATION_VIDEO_TITLE);
//                mIntent.putExtra(Constants.VIDEO_SEEK_TO,"0");
//                mIntent.putExtra(Constants.IS_VIDEO_SEEK, false);
//                mActivity.startActivity(mIntent);

                //openBottomBarForReward();

                break;
        }

    }

    private void loadFragments(Fragment loadingFragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_fragment_container, loadingFragment ).commit();
        transaction.addToBackStack(loadingFragment.getTag());

    }

    private void updateUI(boolean isUserIsActive) {

        if (!isUserIsActive) {
            //SET VERIFIED MSG IF NOT ACTIVE USER
            txtStatus.setText(getResources().getString(R.string.strStatusPendingVerification));
            txtStatusMsg.setText(getResources().getString(R.string.strPendingVerificationMsg));
            txtStatus.setTextColor(getResources().getColor(R.color.colorCuratorAmber));


            txtAccount.setTextColor(getResources().getColor(R.color.colorInActiveGrey));
            txtMySavedItems.setTextColor(getResources().getColor(R.color.colorInActiveGrey));
            txtSettings.setTextColor(getResources().getColor(R.color.colorInActiveGrey));
            txtHelp.setTextColor(getResources().getColor(R.color.colorInActiveGrey));
            txtAdmin.setTextColor(getResources().getColor(R.color.colorInActiveGrey));

            viewAccount.setBackgroundColor(getResources().getColor(R.color.colorInActiveGrey));
            viewSavedItems.setBackgroundColor(getResources().getColor(R.color.colorInActiveGrey));
            viewSettings.setBackgroundColor(getResources().getColor(R.color.colorInActiveGrey));
            viewHelp.setBackgroundColor(getResources().getColor(R.color.colorInActiveGrey));
            viewAdmin.setBackgroundColor(getResources().getColor(R.color.colorInActiveGrey));




        } else if (isUserIsActive) {
            //SET VERIFIED MSG IF ACTIVE USER
            txtStatus.setText(getResources().getString(R.string.strVerified));
            txtStatusMsg.setText(getResources().getString(R.string.strThankYouForEnrolingPlatform));
            txtStatus.setTextColor(getResources().getColor(R.color.colorNewAppGreen));

            //HIDE USER STATUS AS STATUS IS ALREADY VERIFIED
            linearUserStatus.setVisibility(View.GONE);




            txtAccount.setTextColor(getResources().getColor(R.color.colorBlack));
            txtMySavedItems.setTextColor(getResources().getColor(R.color.colorBlack));
            txtSettings.setTextColor(getResources().getColor(R.color.colorBlack));
            txtHelp.setTextColor(getResources().getColor(R.color.colorBlack));
            txtAdmin.setTextColor(getResources().getColor(R.color.colorBlack));

            viewAccount.setBackgroundColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
            viewSavedItems.setBackgroundColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
            viewSettings.setBackgroundColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
            viewHelp.setBackgroundColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
            viewAdmin.setBackgroundColor(getResources().getColor(R.color.colorBottomBarActiveGrey));


        }

        //HIDE SHOW ADMIN DETAILS
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN)){
            linearAdmin.setVisibility(View.VISIBLE);
        }else{
            linearAdmin.setVisibility(View.INVISIBLE);
        }




    }

    private void openBottomBarForReward()  {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.activity_list_of_active_accounts_shortcut);
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setPeekHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        behavior.setHideable(false);

        ImageView imgCancel = bottomSheetDialog.findViewById(R.id.imgCancel);
        ImageView imgCreateNewUser = bottomSheetDialog.findViewById(R.id.imgCreateNewUser);
        LinearLayout linearImgCancel = (LinearLayout) bottomSheetDialog.findViewById(R.id.linearImgCancel);
        recycleAccountList = (RecyclerView) bottomSheetDialog.findViewById(R.id.recycleAccountList);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        recycleAccountList.setLayoutManager(mLinearLayoutManager);
        recycleAccountList.setAdapter(null);

        RelativeLayout relativeImportKey = (RelativeLayout) bottomSheetDialog.findViewById(R.id.relativeImportKey);

        //FETCH USER'S SELECTED SILOS DATA
        bindRecyclerView(bottomSheetDialog);

        relativeImportKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.cancel();

                Intent mIntent = new Intent(mHomeScreenTabLayout, ImportAccountFromPrivateKey.class);
                startActivity(mIntent);

            }
        });
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.cancel();

            }
        });
        imgCreateNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CREATE NEW USER
                //LOGOUT CURRENT USER AND MOVE USER TO SIGN UP SCREEN
                //CLEAR PUSHER NOTIFICATION INTEREST
                //PushNotifications.clearAllState();

                logoutUserAndMoveToStartScreen();


            }
        });

        bottomSheetDialog.show();
    }

    private void logoutUserAndMoveToStartScreen() {

        //UPDATE LOGIN FLAG
        mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN,false);
        mSessionManager.updatePreferenceBoolean(Constants.KEY_ISFROM_LOGOUT,true);
        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN, false);
        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_CANCEL_REFERRAL, false);

        // SEND USER TO LANDING SCREEN
        Intent mIntent = new Intent(mActivity, NewAuthGetStartedActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mIntent);
        mActivity.finish();
    }

    private void bindRecyclerView(BottomSheetDialog bottomSheetDialog) {

        ArrayList<KeyStoreServerInstancesModel.Data> selectedSilosAccountsList  = null;
        try {
            selectedSilosAccountsList = new FetchDataFromAndroidKeyStoreTaskPopup().execute(mSessionManager).get();
            //CHECK IF THERE IS DATA AVAILABLE FOR SELECTED SILOS
            if(selectedSilosAccountsList != null && selectedSilosAccountsList.size() != 0){

                    SilosAvailableAccountsListAdapterPopup  mSilosAvailableAccountsListAdapter = new SilosAvailableAccountsListAdapterPopup(mContext, mHomeScreenTabLayout, selectedSilosAccountsList, mSessionManager,strAccountName);
                    recycleAccountList.setAdapter(mSilosAvailableAccountsListAdapter);


                    //SWIPE HELPER SHOULD ATTACH FIRST TIME
                    ArrayList<KeyStoreServerInstancesModel.Data> mSelectedSilosAccountsList = selectedSilosAccountsList;

                    new SwipeHelper(mActivity, recycleAccountList) {
                        @Override
                        public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {

                            underlayButtons.add(new SwipeHelper.UnderlayButton(
                                    R.mipmap.icn_delete_with_text,
                                    mActivity,
                                    new SwipeHelper.UnderlayButtonClickListener() {
                                        @Override
                                        public void onClick(int pos) {

                                            bottomSheetDialog.cancel();

                                            displayWarningDialogue(mSelectedSilosAccountsList.get(pos).getSubmitProfileModel().getrevealit_private_key(), mSelectedSilosAccountsList.get(pos).getSubmitProfileModel().getProton().getPrivateKey(), mSelectedSilosAccountsList.get(pos).getSubmitProfileModel().getProton().getAccountName());

                                        }
                                    }
                            ));

                        }
                    };



            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void displayWarningDialogue(String revealitPrivateKey,String privateKey, String accountName) {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_user_warning_dailoague, null);
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBuilder.setView(dialogView);


        final AlertDialog mAlertDialog = dialogBuilder.create();
        TextView txtOk = (TextView) dialogView.findViewById(R.id.txtOk);
        TextView txtCancel = (TextView) dialogView.findViewById(R.id.txtCancel);
        LinearLayout linearCancel = (LinearLayout) dialogView.findViewById(R.id.linearCancel);


        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

                //OPEN BIOMETRIC PROMPT
                 strRevealitPrivateKey = revealitPrivateKey;
                 strPrivateKey = privateKey;
                 strAccountName = accountName;
                 loadBiomatricPrompt(revealitPrivateKey,privateKey,accountName);


            }
        });


        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

            }
        });

        linearCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

            }
        });
        mAlertDialog.show();
    }

    private void loadBiomatricPrompt(String revealitPrivateKey, String strPrivateKey, String strAccountName) {

        executor = ContextCompat.getMainExecutor(mHomeScreenTabLayout);
        biometricPrompt = new BiometricPrompt(mHomeScreenTabLayout,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);


            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);


                //CALL AUTHENTICATION API
                //TO GET AUTH TOKEN
                callAuthenticationAPI(revealitPrivateKey, strAccountName, strPrivateKey);

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login to Reveailit")
                .setSubtitle("Log in using your biometric credential")
                // .setNegativeButtonText("Use account password or Pin or Pattern")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .setConfirmationRequired(false)
                .build();

        biometricPrompt.authenticate(promptInfo);

    }
    private void callAuthenticationAPI(String revealItPrivateKey, String username, String strPrivateKey) {

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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("revealit_private_key",revealItPrivateKey);
        paramObject.addProperty("name", username);


        Call<NewAuthLogin> call = patchService1.newAuthLogin(paramObject);

        call.enqueue(new Callback<NewAuthLogin>() {
            @Override
            public void onResponse(Call<NewAuthLogin> call, Response<NewAuthLogin> response) {

                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + response.code());

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + gson.toJson(response.body()));

                //CLOSE DIALOG
                CommonMethods.closeDialog();


                if (response.isSuccessful() && response.code() == Constants.API_CODE_200 && response.body().getToken() != null) {

                    //IF - USER FOUND THAN CALL REMOVE USER API -> THAN DELETE USER FROM LOCAL ANDROID KEYSTORE
                    //ELSE - DELETE DIRECTLY FROM LOCAL
                    callDeleteUserApi(response.body().getToken());

                }else {

                    try {
                        deleteAccountFromAndroidKeyStore();
                    } catch (ExecutionException |InterruptedException  e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NewAuthLogin> call, Throwable t) {


                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                CommonMethods.closeDialog();

            }
        });

    }
    private void callDeleteUserApi(String token) {

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
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + " " +token)
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


        Call<JsonElement> call = patchService1.removeUser();

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ callDeleteUserApi: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callDeleteUserApi: ", "" + response.code());

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ callDeleteUserApi: ", "" + gson.toJson(response.body()));


                //CLOSE DIALOG
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {
                    try {
                        deleteAccountFromAndroidKeyStore();
                    } catch (ExecutionException |InterruptedException  e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {


                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


                CommonMethods.closeDialog();


            }
        });
    }

    private void deleteAccountFromAndroidKeyStore() throws ExecutionException, InterruptedException {

        if(new UpdateUserDataInAndroidKeyStoreTaskPopup(strPrivateKey,strAccountName).execute(mSessionManager).get()){

            if(strUsername.equals(strAccountName)){
                logoutUserAndMoveToStartScreen();
            }else {
                openBottomBarForReward();
            }
        }

    }


}
class UpdateUserDataInAndroidKeyStoreTaskPopup extends AsyncTask<SessionManager, Integer, Boolean> {

    String strPrivateKey ="";
    String strUsername = "";

    public UpdateUserDataInAndroidKeyStoreTaskPopup(String privateKey, String strUsername) {

        this.strPrivateKey =privateKey;
        this.strUsername = strUsername;
    }
    @Override
    protected Boolean doInBackground(SessionManager... mSessionManager) {
        return CommonMethods.updateUserAccountActivationFlag(mSessionManager[0],strPrivateKey,strUsername);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Boolean searchResults) {
        super.onPostExecute(searchResults);

    }
}
class FetchDataFromAndroidKeyStoreTaskPopup extends AsyncTask<SessionManager, Integer, ArrayList<KeyStoreServerInstancesModel.Data>> {


    @Override
    protected ArrayList<KeyStoreServerInstancesModel.Data> doInBackground(SessionManager... mSessionManager) {
        return CommonMethods.fetchUserSelectedSilosDataInList(mSessionManager[0]);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(ArrayList<KeyStoreServerInstancesModel.Data> searchResults) {
        super.onPostExecute(searchResults);

    }
}

