package com.Revealit.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.NewAuthStatusModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pusher.pushnotifications.BeamsCallback;
import com.pusher.pushnotifications.PushNotificationReceivedListener;
import com.pusher.pushnotifications.PushNotifications;
import com.pusher.pushnotifications.PusherCallbackError;
import com.pusher.pushnotifications.auth.AuthData;
import com.pusher.pushnotifications.auth.AuthDataGetter;
import com.pusher.pushnotifications.auth.BeamsTokenProvider;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NewAuthActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NewAuthActivity";
    private NewAuthActivity mActivity;
    private NewAuthActivity mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView imgScanQRcode;
    private EditText edtScanedUsername, edtCreateProtonName, edtVerifyOTP, edtPhoneNumber;
    private TextView txtBarcodeValue, txtBackupCredentials, txtClearData, txtStatusCloud, txtStatusKeyChain, txtVerifyPush, txtStatusKeysBackuped, txtCreateProton, txtSendOTP, txtSendPhoneNumber;
    private boolean isOtpVarified;

    private String strUserData,strProtonUserName ="patel";
    private SurfaceView surfaceViewScanQRCode;
    private RelativeLayout relativeSurface, relativeControler;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private BeamsTokenProvider tokenProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_auth);

        setIds();
        setOnClicks();
    }

    private void setIds() {
        mActivity = NewAuthActivity.this;
        mContext = NewAuthActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgScanQRcode = (ImageView) findViewById(R.id.imgScanQRcode);

        txtSendPhoneNumber = (TextView) findViewById(R.id.txtSendPhoneNumber);
        txtSendOTP = (TextView) findViewById(R.id.txtSendOTP);
        txtCreateProton = (TextView) findViewById(R.id.txtCreateProton);
        txtStatusKeysBackuped = (TextView) findViewById(R.id.txtStatusKeysBackuped);
        txtVerifyPush = (TextView) findViewById(R.id.txtVerifyPush);
        txtStatusKeyChain = (TextView) findViewById(R.id.txtStatusKeyChain);
        txtStatusCloud = (TextView) findViewById(R.id.txtStatusCloud);
        txtClearData = (TextView) findViewById(R.id.txtClearData);
        txtBackupCredentials = (TextView) findViewById(R.id.txtBackupCredentials);
        txtBarcodeValue = (TextView) findViewById(R.id.txtBarcodeValue);

        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtVerifyOTP = (EditText) findViewById(R.id.edtVerifyOTP);
        edtCreateProtonName = (EditText) findViewById(R.id.edtCreateProtonName);
        edtScanedUsername = (EditText) findViewById(R.id.edtScanedUsername);

        surfaceViewScanQRCode = (SurfaceView) findViewById(R.id.surfaceViewScanQRCode);

        relativeControler = (RelativeLayout) findViewById(R.id.relativeControler);
        relativeSurface = (RelativeLayout) findViewById(R.id.relativeSurface);

        //PUSHER SDK
        PushNotifications.start(getApplicationContext(), Constants.PUSHER_INSTANCE_ID);

    }

    private void setOnClicks() {
        txtSendPhoneNumber.setOnClickListener(this);
        txtSendOTP.setOnClickListener(this);
        txtClearData.setOnClickListener(this);
        txtCreateProton.setOnClickListener(this);
        txtBackupCredentials.setOnClickListener(this);
        imgScanQRcode.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {

            case R.id.txtSendPhoneNumber:

                if (checkValidationForPhone()) {

                    apiCallToGetOTP();
                }

                break;

            case R.id.txtSendOTP:

                if (checkValidationForOTP()) {

                    apiCallVerifyOTPMobile();
                }

                break;

            case R.id.txtClearData:

//                IAbiProvider abiProvider = new SimpleABIProvider("https://eos.greymass.com");
//                SigningRequest signingRequest = new SigningRequest(new ESR(this, abiProvider));
//                String esrUri = "esr://hexstring";
//                signingRequest.load(esrUri);
//
//                // get info pairs
//                Map<String, String> info = signingRequest.getInfo();
//                String foo = info.get("foo");
//
//                // check if this is a identity request
//                if (signingRequest.isIdentity()) {
//                    ResolvedSigningRequest resolved = signingRequest.resolve(new PermissionLevel("myaccount", "active"), new TransactionContext());
//                    ResolvedCallback callback = resolved.getCallback(new ArrayList<String>());
//                    // call the callback to notify of the request
//                } else {
//                    // it's a signing request
//                    signingRequest.sign(new ISignatureProvider() {
//                        @Override
//                        public Signature sign(String message) {
//                            // sign it
//                            return new Signature("myaccount", "SIG_abc123");
//                        }
//                    });
//
//                    ResolvedSigningRequest resolved = signingRequest.resolve(new PermissionLevel("myaccount", "active"), new TransactionContext());
//                    if (signingRequest.getRequestFlag().isBroadcast()) {
//                        // broadcast
//                    } else {
//                        // call the callback so requestor can broadcast
//                    }
//                }


                break;

            case R.id.txtCreateProton:

                createProtonAccount();

                break;
            case R.id.imgScanQRcode:


                requestCamaraPermission();

                break;

            case R.id.txtBackupCredentials:

                //CommonMethods.displayToast(mContext, "Comming Soon");

                if(!strProtonUserName.isEmpty()){
                    tokenProvider = new BeamsTokenProvider(

                            Constants.API_END_POINTS_MOBILE_T1_CURATOR + Constants.API_NEW_AUTH_PUSHER_API_USERID_REGISTRATION +"?name="+strProtonUserName,

                            new AuthDataGetter() {
                                @Override
                                public AuthData getAuthData() {
                                    // Headers and URL query params your auth endpoint needs to
                                    // request a Beams Token for a given user
                                    HashMap<String, String> headers = new HashMap<>();
                                    //for example:
                                    headers.put("Content-Type", "application/json");
                                    headers.put("Accept", "application/json");
                                    HashMap<String, String> queryParams = new HashMap<>();
                                    return new AuthData(
                                            headers,
                                            queryParams
                                    );
                                }
                            }
                    );

                    //REGISTER PUSHER NOTIFICATION
                    new PusherUserIdRegistraionTask().execute();

                }else{
                    CommonMethods.displayToast(mContext,"Proton username should not be empty!");
                }



                break;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCamaraPermission() {

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            initialiseDetectorsAndSources();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initialiseDetectorsAndSources();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createProtonAccount() {

        if (checkValidationFroCreateProtonAccount()) {

            apiCallCreateProtonAccount();

        }

    }


    private boolean checkValidationFroCreateProtonAccount() {

        if (edtPhoneNumber.getText().toString().isEmpty()) {
            CommonMethods.displayToast(mContext, "Please enter mobile number");
            return false;
        } else if (edtCreateProtonName.getText().toString().isEmpty()) {
            CommonMethods.displayToast(mContext, "Please Enter your username");
            return false;
        } else if (!isOtpVarified) {
            CommonMethods.displayToast(mContext, "Please Verify your OTP");
            return false;
        }

        return true;
    }


    private void apiCallVerifyOTPMobile() {

        //OPEN DIALOGUE
        CommonMethods.showDialog(mContext);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
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
                .baseUrl("https://testing1.revealit.io/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("receiver_number", edtPhoneNumber.getText().toString());
        paramObject.addProperty("code", edtVerifyOTP.getText().toString());

        Call<NewAuthStatusModel> call = patchService1.verifyOTPPhone(paramObject);

        call.enqueue(new Callback<NewAuthStatusModel>() {
            @Override
            public void onResponse(Call<NewAuthStatusModel> call, Response<NewAuthStatusModel> response) {

                CommonMethods.printLogE("Response @ apiCallVerifyOTPMobile: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiCallVerifyOTPMobile: ", "" + response.code());

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ apiCallVerifyOTPMobile: ", "" + gson.toJson(response.body()));


                    CommonMethods.displayToast(mContext, response.body().getStatus());

                    isOtpVarified = true;


                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                    isOtpVarified = false;

                }
            }

            @Override
            public void onFailure(Call<NewAuthStatusModel> call, Throwable t) {

                isOtpVarified = false;

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });

    }

    private void apiCallToGetOTP() {

        //OPEN DIALOGUE
        CommonMethods.showDialog(mContext);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
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
                .baseUrl(Constants.API_END_POINTS_MOBILE_T1_CURATOR)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("receiver_number", edtPhoneNumber.getText().toString());

        Call<NewAuthStatusModel> call = patchService1.verifyPhone(paramObject);

        call.enqueue(new Callback<NewAuthStatusModel>() {
            @Override
            public void onResponse(Call<NewAuthStatusModel> call, Response<NewAuthStatusModel> response) {

                CommonMethods.printLogE("Response @ apiCallToGetOTP: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiCallToGetOTP: ", "" + response.code());

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ apiCallToGetOTP: ", "" + gson.toJson(response.body()));


                    CommonMethods.displayToast(mContext, "Please check your inbox, We sent you an OTP");


                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }
            }

            @Override
            public void onFailure(Call<NewAuthStatusModel> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));


            }
        });


    }

    private void apiCallCreateProtonAccount() {

        //OPEN DIALOGUE
        CommonMethods.showDialog(mContext);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
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
                .baseUrl(Constants.API_END_POINTS_REGISTRATION_T1_CURATOR)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("account_name", edtCreateProtonName.getText().toString());
        paramObject.addProperty("mobile_id", 1);
        paramObject.addProperty("phone_number", edtPhoneNumber.getText().toString());

        Call<JsonElement> call = patchService1.createProtonAccount(paramObject);

        call.enqueue(new Callback<JsonElement>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ apiCallCreateProtonAccount: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiCallCreateProtonAccount: ", "" + response.code());

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    //CommonMethods.printLogE("Response @ apiCallCreateProtonAccount: ", "" + gson.toJson(response.body()));


                    CommonMethods.displayToast(mContext, "Account create successfully!");

                    strUserData = "" + gson.toJson(response.body());
                    strProtonUserName = ""+response.body().getAsJsonArray().get(0).getAsJsonObject().get("account_name");

                    //Log.e(TAG ,""+response.body().getAsJsonArray().get(0).getAsJsonObject().get("account_name"));

                    //CREATE JWT TOKEN AND PUSHER TOKEN
                    createJwtToken(response.body().getAsJsonArray().get(0).getAsJsonObject().get("account_name").toString());


                } else {

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                CommonMethods.printLogE("ERROR ", " " + t.getMessage());


            }
        });


    }

    private void createJwtToken(String account_name) {

        //OPEN DIALOGUE
        CommonMethods.showDialogWithCustomMessage(mContext,"Create Tokens");

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
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
                .baseUrl(Constants.API_END_POINTS_MOBILE_T1_CURATOR)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("name", account_name);


        Call<JsonElement> call = patchService1.createTokens(paramObject);

        call.enqueue(new Callback<JsonElement>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ createJwtToken: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ createJwtToken: ", "" + response.code());

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ apiCallCreateProtonAccount: ", "" + gson.toJson(response.body()));


                } else {

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                CommonMethods.printLogE("ERROR ", " " + t.getMessage());


            }
        });


    }

    private boolean checkValidationForPhone() {
        if (edtPhoneNumber.getText().toString().isEmpty()) {
            CommonMethods.displayToast(mContext, "Please enter mobile number");
            return false;
        }

        return true;
    }

    private boolean checkValidationForOTP() {
        if (edtPhoneNumber.getText().toString().isEmpty()) {
            CommonMethods.displayToast(mContext, "Please enter mobile number and get OTP");
            return false;
        } else if (edtVerifyOTP.getText().toString().isEmpty()) {
            CommonMethods.displayToast(mContext, "Please enter received OTP");
            return false;
        }

        return true;
    }

    private void initialiseDetectorsAndSources() {

        relativeControler.setVisibility(View.GONE);
        relativeSurface.setVisibility(View.VISIBLE);


        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceViewScanQRCode.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(NewAuthActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    cameraSource.start(surfaceViewScanQRCode.getHolder());

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                CommonMethods.printLogE(TAG ,"Camera release!");
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    txtBarcodeValue.post(new Runnable() {

                        private String intentData;

                        @Override
                        public void run() {

                            //Log.e("DATA : ", intentData);

                            if (barcodes.valueAt(0).email != null) {
                                txtBarcodeValue.removeCallbacks(null);
                                intentData = barcodes.valueAt(0).email.address;
                                CommonMethods.displayToast(mContext ,intentData );
                                relativeControler.setVisibility(View.VISIBLE);
                                relativeSurface.setVisibility(View.GONE);


                            } else {
                                intentData = barcodes.valueAt(0).displayValue;
                                CommonMethods.displayToast(mContext ,intentData );
                                relativeControler.setVisibility(View.VISIBLE);
                                relativeSurface.setVisibility(View.GONE);

                            }

                        }

                    });


                }
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(cameraSource != null)
        cameraSource.release();
    }

    class PusherUserIdRegistraionTask extends AsyncTask<String, Void, Void> {

        protected void onPostExecute() {
            // TODO: check this.exception
            // TODO: do something with the feed
        }

        @Override
        protected Void doInBackground(String... strings) {

           //Log.e(TAG , tokenProvider.fetchToken("ketan"));

            //REGISTER PUSHER USER
            PushNotifications.setUserId(strProtonUserName, tokenProvider, new BeamsCallback<Void, PusherCallbackError>(){
                @Override
                public void onSuccess(Void... values) {
                    Log.e("PusherBeams", "Successfully authenticated with Pusher Beams");
                }

                @Override
                public void onFailure(PusherCallbackError error) {
                    Log.e("PusherBeams", "Pusher Beams authentication failed: " + error.getMessage());
                }
            });

            //START NOTIFICATION FOR THIS DEVICE
            PushNotifications.start(mContext,Constants.PUSHER_INSTANCE_ID);


            return null;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        PushNotifications.setOnMessageReceivedListenerForVisibleActivity(this, new PushNotificationReceivedListener() {
            @Override
            public void onMessageReceived(RemoteMessage remoteMessage) {
                String messagePayload = remoteMessage.getData().get("inAppNotificationMessage");
                Log.e(TAG ,""+remoteMessage.getData());
                if (messagePayload == null) {
                    // Message payload was not set for this notification
                    Log.i(TAG, "Payload was missing");
                } else {
                    Log.i(TAG, messagePayload);
                    // Now update the UI based on your message payload!
                }
            }
        });
    }

}
