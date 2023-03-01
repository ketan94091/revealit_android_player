package com.Revealit.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.DriveServiceHelper;
import com.Revealit.CommonClasse.SessionManager;
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
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CloudBackupActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "GoogleCloudBackupActivity";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private TextView txtBackupNow;
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2;
    private String strToken,mOpenFileId;
    private boolean isFolderAvailable;
    private DriveServiceHelper mDriveServiceHelper;
    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient client;
    private RelativeLayout relativeBack;
    private String userData;
    private Drive googleDriveService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_backup);

        setId();
        setOnClicks();
    }

    private void setId() {
        mActivity = CloudBackupActivity.this;
        mContext = CloudBackupActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        txtBackupNow =(TextView)findViewById(R.id.txtBackupNow);

        relativeBack =(RelativeLayout)findViewById(R.id.relativeBack);

        //CHECK IF CLOUD BACKUP IS ALREADY DONE
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_IS_GOOGLE_DRIVE_BACKUP_DONE)){
            txtBackupNow.setVisibility(View.GONE);
        }else{
            txtBackupNow.setVisibility(View.VISIBLE);
        }

        //txtBackupNow.setVisibility(View.VISIBLE);


    }

    private void setOnClicks() {
        txtBackupNow.setOnClickListener(this);
        relativeBack.setOnClickListener(this);

    }


    @Override
    public void onClick(View mView) {


        switch (mView.getId()){

            case R.id.txtBackupNow:

                requestSignIn();

                break;
            case R.id.relativeBack:

                finish();

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


                    googleDriveService =
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
                        @RequiresApi(api = Build.VERSION_CODES.M)
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

    private boolean checkIfFileExists() {

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

                    if(response.body().getAsJsonObject().getAsJsonArray("files").size() != 0){
                        mOpenFileId = response.body().getAsJsonObject().getAsJsonArray("files").get(0).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "").replaceAll("u0027", "'").replaceAll("\\\\", "");
                        readFile(mOpenFileId);

                        //CHECK IF FILE IS THERE ON DRIVE
                        //IF AVAILABLE THAN DELETE FIRST AND CREATE NEW FILE
                        new DeleteDriveFileTask(mOpenFileId, googleDriveService).execute();

                        //AFTER DELETING EXISTING FILE CREATE NEW FILE
                        createFile();

                    }else{
                        createFile();
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

                //createFile();

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.showDialogWithCustomMessage(mContext, "Could not complete backup! please try after sometimes.");

            }
        });

        //CLOSED DIALOGUE
        CommonMethods.closeDialog();

        return  isFolderAvailable;

    }


    private void createFile() {


        if (mDriveServiceHelper != null) {

            CommonMethods.showDialogWithCustomMessage(mContext ,"Hold on! taking backup");

            mDriveServiceHelper.createFile()
                    .addOnSuccessListener(fileId -> saveFile(fileId)
                    )
                    .addOnFailureListener(exception ->

                            CommonMethods.displayToast(mContext ,"Hold on! could not create back up. Please contact developer"));

        }

        CommonMethods.closeDialog();
    }
    private void saveFile(String mOpenFileId) {

        if (mDriveServiceHelper != null && mOpenFileId != null) {


            try {
                String strKeystoreData = new FetchKeystoreData().execute(mSessionManager).get();


                mDriveServiceHelper.saveFile(mOpenFileId, Constants.GOOGLE_DRIVE_FOLDER_NAME, strKeystoreData)
                        .addOnFailureListener(exception ->
                                displayErrorDialogue(exception)).addOnSuccessListener(
                                success -> //DISPLAY SUCCESS MSG
                                        displaySuccessDialogue()

                        );
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void displaySuccessDialogue() {

        CommonMethods.displayToast(mContext ,getString(R.string.strBckupSuccessfully));


        //UPDATE GOOGLE DRIVE BACKUP FLAG
        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_GOOGLE_DRIVE_BACKUP_DONE, true);

        //SIGN OUT GOOGLE ACCOUNT
        //client.signOut();

        finish();
    }

    private void displayErrorDialogue(Exception exception) {
        CommonMethods.showDialogWithCustomMessage(mContext ,getString(R.string.strErrorInBckup));
    }

    private void readFile(String fileId) {


        if (mDriveServiceHelper != null) {

            mDriveServiceHelper.readFile(fileId.substring( 1, fileId.length() - 1 ))
                    .addOnSuccessListener(nameAndContent -> {
                        String name = nameAndContent.first;
                        String content = nameAndContent.second;

                        //PRINT DATA CONTENT
                        CommonMethods.printLogE(TAG,"" +content);

                        //SIGN OUT GOOGLE ACCOUNT
                        client.signOut();


                    })
                    .addOnFailureListener(exception ->
                            CommonMethods.displayToast(mContext, "Could not restore credentials! Please contact developer"));
        }

    }

}
class FetchKeystoreData extends AsyncTask<SessionManager, Integer, String> {


    @Override
    protected String doInBackground(SessionManager... mSessionManager) {
        return CommonMethods.checkIfInstanceKeyStoreData(mSessionManager[0]);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String searchResults) {
        super.onPostExecute(searchResults);

    }
}

class DeleteDriveFileTask extends AsyncTask<String, Void, Void> {

    String strFileID;
    Drive googleDriveService;


    public DeleteDriveFileTask(String strFileID, Drive googleDriveService) {

     this.strFileID = strFileID;
     this.googleDriveService= googleDriveService;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected Void doInBackground(String... urls) {

        try {
            //DELETE FILE FROM GOOGLE DRIVE
            googleDriveService.files().delete(strFileID).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute() {
        Log.e("POST","POST");
    }
}