package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ImportAccountFromPrivateKey;
import com.Revealit.Adapter.SilosAvailableAccountsListAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.DriveServiceHelper;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.CommonClasse.SwipeHelper;
import com.Revealit.ModelClasses.KeyStoreServerInstancesModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListOfActiveAccountsActivity extends AppCompatActivity implements View.OnClickListener {
    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "ListOfActiveAccountsActivity";

    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private LinearLayout linearImgCancel;
    private RecyclerView recycleAccountList;
    ArrayList<KeyStoreServerInstancesModel.Data> selectedSilosAccountsList = new ArrayList<>();
    private RelativeLayout relativeRestoreFromCloud,relativeImportKey;
    private SilosAvailableAccountsListAdapter mSilosAvailableAccountsListAdapter;
    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient client;
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2;
    private DriveServiceHelper mDriveServiceHelper;
    private String strToken,mOpenFileId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_active_accounts);

    }

    @Override
    protected void onResume() {
        setIds();
        setOnClicks();
        super.onResume();
    }

    private void setIds() {

        mActivity = ListOfActiveAccountsActivity.this;
        mContext = ListOfActiveAccountsActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        linearImgCancel = (LinearLayout) findViewById(R.id.linearImgCancel);
        recycleAccountList = (RecyclerView) findViewById(R.id.recycleAccountList);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        recycleAccountList.setLayoutManager(mLinearLayoutManager);

        relativeImportKey = (RelativeLayout) findViewById(R.id.relativeImportKey);
        relativeRestoreFromCloud = (RelativeLayout) findViewById(R.id.relativeRestoreFromCloud);

        //FETCH USER'S SELECTED SILOS DATA
        bindRecyclerView();



        //HIDE SHOW ADMIN DETAILS
        //CHECK USER IS ADMIN USER OR SUPER ADMIN USER
        //SUPER ADMIN -> IF ANY BETA USER IS ADMIN THAN CONSIDER USER AS ADMIN
        if(CommonMethods.checkIfUserIsSuperAdmin(mSessionManager,mContext)){
            relativeRestoreFromCloud.setVisibility(View.VISIBLE);
        }else{
            relativeRestoreFromCloud.setVisibility(View.GONE);
        }
        relativeRestoreFromCloud.setVisibility(View.VISIBLE);



    }

    private void bindRecyclerView() {

        ArrayList<KeyStoreServerInstancesModel.Data> selectedSilosAccountsList  = null;
        try {
            selectedSilosAccountsList = new FetchDataFromAndroidKeyStoreTask().execute(mSessionManager).get();
            //CHECK IF THERE IS DATA AVAILABLE FOR SELECTED SILOS
            if(selectedSilosAccountsList != null && selectedSilosAccountsList.size() != 0){

                if(mSilosAvailableAccountsListAdapter == null){


                    mSilosAvailableAccountsListAdapter = new SilosAvailableAccountsListAdapter(mContext, this, selectedSilosAccountsList, mSessionManager);
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
                                            displayWarningDialogue(mSelectedSilosAccountsList.get(pos).getSubmitProfileModel().getrevealit_private_key(),mSelectedSilosAccountsList.get(pos).getSubmitProfileModel().getProton().getPrivateKey(),mSelectedSilosAccountsList.get(pos).getSubmitProfileModel().getProton().getAccountName());

                                        }
                                    }
                            ));

                        }
                    };


                } else {
                    mSilosAvailableAccountsListAdapter.updateListData(selectedSilosAccountsList);
                }

            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }




    private void displayWarningDialogue(String revealitPrivateKey,String strPrivateKey, String strAccountName) {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_user_warning_dailoague, null);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBuilder.setView(dialogView);


        final AlertDialog mAlertDialog = dialogBuilder.create();
        TextView txtOk = (TextView) dialogView.findViewById(R.id.txtOk);
        TextView txtCancel = (TextView) dialogView.findViewById(R.id.txtCancel);
        LinearLayout linearCancel = (LinearLayout) dialogView.findViewById(R.id.linearCancel);


        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

                Intent mIntent = new Intent(ListOfActiveAccountsActivity.this, BiomatricAuthenticationDeleteAccontActivity.class);
                mIntent.putExtra(Constants.KEY_REVEALIT_PRIVATE_KEY, revealitPrivateKey);
                mIntent.putExtra(Constants.KEY_PRIVATE_KEY, strPrivateKey);
                mIntent.putExtra(Constants.KEY_PROTON_ACCOUNTNAME, strAccountName);
                startActivity(mIntent);



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

    private void setOnClicks() {

        linearImgCancel.setOnClickListener(this);
        relativeRestoreFromCloud.setOnClickListener(this);
        relativeImportKey.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {
        switch (mView.getId()){

            case R.id.linearImgCancel:
                finish();
                break;
            case R.id.relativeRestoreFromCloud:

                requestSignIn();

                break;
            case R.id.relativeImportKey:
                Intent mIntent = new Intent(this, ImportAccountFromPrivateKey.class);
                startActivity(mIntent);
                break;
        }

    }
    private void requestSignIn() {

        signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;

            case REQUEST_CODE_OPEN_DOCUMENT:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    Uri uri = resultData.getData();
                    if (uri != null) {

                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }
    private void handleSignInResult(Intent result) {


        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());


                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("Drive API Migration")
                                    .build();

                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                strToken = credential.getToken();



                                //REFRESH DRIVE
                                checkIfFileExists();


                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (GoogleAuthException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                })
                .addOnFailureListener(exception -> Log.e("ERROR", "Unable to sign in.", exception));
    }
    private void checkIfFileExists() {

        //OPEN DIALOGUE
        CommonMethods.showDialogWithCustomMessage(mContext , "Hold on! We are checking if you already registered user");

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request original = chain.request();

                okhttp3.Request request = original.newBuilder()
                        .header("Authorization","Bearer "+strToken)
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
                .baseUrl("https://www.googleapis.com/drive/v3/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        Call<JsonElement> call = patchService1.checkFileOnGoogleDrive("files?q=name%3D%22Revealit.tv.io%22&key=[AIzaSyCMRiL96W6rLyXrUM49ysPr8soEEcIexdg] HTTP/1.1");

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                Log.e("checkIfFileExists: ", "" + response.isSuccessful());
                Log.e("checkIfFileExists: ", "" + response.code());

                if (response.isSuccessful() && response.code() == 200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    Log.e("checkIfFileExists: ", "" + gson.toJson(response.body()));

                    if(response.body().getAsJsonObject().getAsJsonArray("files").size() != 0){
                        mOpenFileId = response.body().getAsJsonObject().getAsJsonArray("files").get(0).getAsJsonObject().get("id").toString();
                        readFile(mOpenFileId);
                    }

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        CommonMethods.buildDialog(mContext,"Error : "+ jObjError.getString("message"));
                    } catch (Exception e) {
                        CommonMethods.buildDialog(mContext,"Error : "+e.getMessage());

                    }
                }
                //CLOSED DIALOGUE
                CommonMethods.closeDialog();
            }


            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

            }
        });

        //CLOSED DIALOGUE
        CommonMethods.closeDialog();


    }
    private void readFile(String fileId) {

        if (mDriveServiceHelper != null) {

            mDriveServiceHelper.readFile(fileId.substring( 1, fileId.length() - 1 ))
                    .addOnSuccessListener(nameAndContent -> {
                        String name = nameAndContent.first;
                        String content = nameAndContent.second;

                        //PRINT DATA CONTENT
                        //CommonMethods.printLogE(TAG,"" +content);

                        //STORE DATA IN TO KEY STORE
                        //STORE WHOLE ARRAY IN TO STRING FORMAT IN KEYSTORE
                        CommonMethods.encryptKey(content,  Constants.KEY_SILOS_DATA,Constants.KEY_SILOS_ALIAS, mSessionManager);


                        //SIGN OUT GOOGLE ACCOUNT
                        client.signOut();

                        //CHANGE GOOGLE BACKUP FLOW TO FALSE
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_GOOGLE_DRIVE_BACKUP_DONE, true);

                        //RELOAD PAGE AND DISPLAY EXISTING DATA
                        Intent mIntent = new Intent(this, ListOfActiveAccountsActivity.class);
                        startActivity(mIntent);
                        finishAffinity();


                    })
                    .addOnFailureListener(exception ->
                            CommonMethods.displayToast(mContext, "Could not restore credentials! Please contact developer"));
        }

    }
}
class FetchDataFromAndroidKeyStoreTask extends AsyncTask<SessionManager, Integer, ArrayList<KeyStoreServerInstancesModel.Data>> {


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
