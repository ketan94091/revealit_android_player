package com.Revealit.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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
import com.Revealit.ModelClasses.JsonDataTransfer;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.greymass.esr.ESR;
import com.greymass.esr.ESRException;
import com.greymass.esr.ResolvedSigningRequest;
import com.greymass.esr.SigningRequest;
import com.greymass.esr.implementation.SimpleABIProvider;
import com.greymass.esr.interfaces.IAbiProvider;
import com.greymass.esr.models.PermissionLevel;
import com.greymass.esr.models.ResolvedCallback;
import com.greymass.esr.models.TransactionContext;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import one.block.eosiojava.error.serializationProvider.SerializationProviderError;
import one.block.eosiojava.error.session.TransactionPrepareError;
import one.block.eosiojava.error.session.TransactionSignError;
import one.block.eosiojava.implementations.ABIProviderImpl;
import one.block.eosiojava.interfaces.IABIProvider;
import one.block.eosiojava.interfaces.IRPCProvider;
import one.block.eosiojava.interfaces.ISerializationProvider;
import one.block.eosiojava.interfaces.ISignatureProvider;
import one.block.eosiojava.models.rpcProvider.Action;
import one.block.eosiojava.models.rpcProvider.Authorization;
import one.block.eosiojava.models.rpcProvider.TransactionConfig;
import one.block.eosiojava.session.TransactionProcessor;
import one.block.eosiojava.session.TransactionSession;
import one.block.eosiojavaabieosserializationprovider.AbiEosSerializationProviderImpl;
import one.block.eosiojavarpcprovider.error.EosioJavaRpcProviderInitializerError;
import one.block.eosiojavarpcprovider.implementations.EosioJavaRpcProviderImpl;
import one.block.eosiosoftkeysignatureprovider.SoftKeySignatureProviderImpl;
import one.block.eosiosoftkeysignatureprovider.error.ImportKeyError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QrCodeScannerActivity extends AppCompatActivity {

    private static final String TAG = "QrCodeScannerActivity";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private SurfaceView surfaceViewScanQRCode;
    private RelativeLayout relativeSurface;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private TextView txtBarcodeValue;
    private String intentData;
    private boolean isBarcodeScanned = false;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);

        setId();
        setOnClicks();
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setId() {

        mActivity = QrCodeScannerActivity.this;
        mContext = QrCodeScannerActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        surfaceViewScanQRCode = (SurfaceView) findViewById(R.id.surfaceViewScanQRCode);

        relativeSurface = (RelativeLayout) findViewById(R.id.relativeSurface);
        txtBarcodeValue = (TextView) findViewById(R.id.txtBarcodeValue);

        //REQUEST CAMARA PERMISSION
       requestCamaraPermission();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        requestCamaraPermission();
    }

    private void setOnClicks() {

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCamaraPermission() {

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
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
                Toast.makeText(this, getResources().getString(R.string.strCameraPermissionDenied), Toast.LENGTH_LONG).show();
            }
        }
    }
    private void initialiseDetectorsAndSources() {

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
                    if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                        @Override
                        public void run() {

                            if(!isBarcodeScanned){

                                if (barcodes.valueAt(0).email != null) {

                                    txtBarcodeValue.removeCallbacks(null);
                                    intentData = barcodes.valueAt(0).email.address;
                                    finish();

                                } else {

                                    intentData = barcodes.valueAt(0).displayValue;



                                    //CALL PROTON SIGNING REQUEST
                                    if(intentData.contains("proton")){
                                        callProtonStuff(intentData);

                                        //UPDATE FLAG IF CODE SCANNED SUCCESSFULLY
                                        isBarcodeScanned= true;
                                    }else{
                                        CommonMethods.displayToast(mContext, getResources().getString(R.string.strIncorrectQRcodeData));
                                        finish();
                                    }


                                }

                            }

                        }

                    });


                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void callProtonStuff(String intentData) {

        new RetrieveFeedTask(intentData).execute();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraSource != null)
            cameraSource.release();
    }
    class RetrieveFeedTask extends AsyncTask<String, Void, Void> {

        String qrCodeData, strESRdata;
        String strPEMprivateKey ="5Kj7AAZ8n2Bc2gC4yehhVP1cVeLKncNiSSbDrTZy8xz1Z2B3yL1";
        String privateKey1 = "PVT_K1_2upaV73hZccmfonwTFoQLGVH3WQaUWdidXSdHXDu68NPQSP9AY";
        String publicKey1 = "PUB_K1_6HZByCD9WtFfU8oPcJRzLey2WhNc819EpZoZr6Q9MPRcuVBJoC";
        String account_name1 = "revdev";
        String baseURl ="https://proton.eosusa.news";
        String permission="active";
        String strActionAccount ="eosio";
        String strVoteProducer ="voteproducer";
        String strGreyMassVote ="greymassvote";

        private static final boolean ENABLE_NETWORK_LOG = true;

        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();


        public RetrieveFeedTask(String intentData) {
            this.qrCodeData = intentData.replace("proton:","proton://");
            this.strESRdata = intentData.replace("proton:","esr://");

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        protected Void doInBackground(String... urls) {

            //OPEN DIALOGUE
            CommonMethods.showDialog(mContext);

            //CREATE IABI FOR THE QR CODE SIGNING REQUEST
            IAbiProvider abiProvider1 = new SimpleABIProvider(baseURl);
            SigningRequest signingRequest = new SigningRequest(new ESR(QrCodeScannerActivity.this, abiProvider1));

            try {
                signingRequest.load(strESRdata);
            } catch (ESRException e) {
                e.printStackTrace();
            }

            // get info pairs
            Map<String, String> info = signingRequest.getInfo();
            String strAccount = info.get("req_account");
            try {
                //SET SIGNING REQUEST ACCOUNT
                signingRequest.setInfoKey("req_account",account_name1);
            } catch (ESRException e) {
                e.printStackTrace();
            }

            //RESOLVED THE IDENTITY REQUEST
            if (signingRequest.isIdentity()) {

                // Creating serialization provider
                ISerializationProvider serializationProvider;
                try {
                    serializationProvider = new AbiEosSerializationProviderImpl();
                } catch (SerializationProviderError serializationProviderError) {
                    serializationProviderError.printStackTrace();
                    return null;
                }

                // Creating RPC Provider
                IRPCProvider rpcProvider;
                try {
                    rpcProvider = new EosioJavaRpcProviderImpl(baseURl, ENABLE_NETWORK_LOG);
                } catch (EosioJavaRpcProviderInitializerError eosioJavaRpcProviderInitializerError) {
                    eosioJavaRpcProviderInitializerError.printStackTrace();
                    return null;
                }

                // Creating ABI provider
                IABIProvider abiProvider = new ABIProviderImpl(rpcProvider, serializationProvider);

                // Creating Signature provider
                ISignatureProvider signatureProvider = new SoftKeySignatureProviderImpl();





                try {
                    ((SoftKeySignatureProviderImpl) signatureProvider).importKey(strPEMprivateKey);
                } catch (ImportKeyError importKeyError) {
                    importKeyError.printStackTrace();
                    return null;
                }

                // Creating TransactionProcess
                TransactionSession session = new TransactionSession(serializationProvider, rpcProvider, abiProvider, signatureProvider);
                TransactionProcessor processor = session.getTransactionProcessor();

                // Now the TransactionConfig can be altered, if desired
                TransactionConfig transactionConfig = processor.getTransactionConfig();

                // Use blocksBehind (default 3) the current head block to calculate TAPOS
                transactionConfig.setUseLastIrreversible(false);
                // Set the expiration time of transactions 600 seconds later than the timestamp

                // of the block used to calculate TAPOS
                transactionConfig.setExpiresSeconds(6);

                // Update the TransactionProcessor with the config changes
                processor.setTransactionConfig(transactionConfig);



                // Apply transaction data to Action's data
                //String jsonData = "{ \"from\": \"revdev\", \"to\": \"ziptied\", \"quantity\": \"0.0001 XPR\", \"memo\": \"backend\" }";
                //String jsonData = "{\"voter\": \"revdev\",\"proxy\": \"greymassvote\"}";


                //CREATE EMPTY LIST FOR PRODUCER
                List<String> list = Collections.<String>emptyList();


                // Creating action with action's data, eosio contract and transfer action.
                //Action action = new Action(""+signingRequest.getChainId().getChainName(), account_name1, Collections.singletonList(new Authorization(account_name1, permission)), ""+gson.toJson( mJsonDataTransfer));
                Action action = new Action(strActionAccount, strVoteProducer, Collections.singletonList(new Authorization(account_name1, permission)),gson.toJson( new JsonDataTransfer(account_name1,strGreyMassVote,list)));

                //PRINT CONTRACT
                CommonMethods.printLogE("SIGNING_CONTRACT_ACTION", "" + gson.toJson(action));


                try {

                    // Prepare transaction with above action. A transaction can be executed with multiple action.
                    processor.prepare(Collections.singletonList(action));

                    //Sign and broadcast the transaction.
                    boolean sendTransactionResponse = processor.sign();

                    //Log.e("RESPONSE" ," "+gson.toJson(sendTransactionResponse)+"  " + processor.getSignatures() + " "+ gson.toJson(signingRequest.getChainId()) );


                    //RESOLVED SIGNING REQUEST
                    ResolvedSigningRequest resolved = null;
                    try {
                        resolved = signingRequest.resolve(new PermissionLevel(account_name1, permission), new TransactionContext());

                    } catch (ESRException e) {
                        e.printStackTrace();
                    }
                    //GET CURRENT DATE
                    Date today = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String dateToStr = format.format(today);
                    System.out.println(dateToStr);

                    //GET CALLBACK FROM SIGNING REQUEST
                    ResolvedCallback callback = resolved.getCallback( processor.getSignatures(),signingRequest.getChainId().getChainId(),dateToStr,qrCodeData);


                    //CALL CALL BACK URL IF SIGNATURE AND RESOLVED REQUEST NOT NULL
                    Log.e("CALLBACK_response", gson.toJson(callback));

                    callSignInCallBack(callback);


                } catch (TransactionPrepareError | TransactionSignError | ESRException transactionPrepareError) {
                    // Happens if preparing transaction unsuccessful
                    transactionPrepareError.printStackTrace();

                    CommonMethods.buildDialog(mContext, transactionPrepareError.getMessage());

                    //OPEN DIALOGUE
                    CommonMethods.closeDialog();

                    finish();
                }




            } else {

            }

            return null;
        }

        protected void onPostExecute() {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    private void callSignInCallBack(ResolvedCallback callback) {


        //SEPARATE URL
        List<String> baseUrlParts = Arrays.asList(callback.getUrl().split("link/"));
        Log.e("STRING_1",""+baseUrlParts.get(0));
        Log.e("STRING_2",""+baseUrlParts.get(1));


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
                .baseUrl(baseUrlParts.get(0)+"link/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

        JsonObject paramObject = new JsonObject();
        paramObject.addProperty(ResolvedCallback.SIG, callback.getPayload().get(ResolvedCallback.SIG));
        paramObject.addProperty(ResolvedCallback.TX, callback.getPayload().get(ResolvedCallback.TX));
        paramObject.addProperty(ResolvedCallback.RBN, callback.getPayload().get(ResolvedCallback.RBN));
        paramObject.addProperty(ResolvedCallback.RID, callback.getPayload().get(ResolvedCallback.RID));
        paramObject.addProperty(ResolvedCallback.EX, callback.getPayload().get(ResolvedCallback.EX));
        paramObject.addProperty(ResolvedCallback.REQ, callback.getPayload().get(ResolvedCallback.REQ));
        paramObject.addProperty(ResolvedCallback.SA, callback.getPayload().get(ResolvedCallback.SA));
        paramObject.addProperty(ResolvedCallback.SP, callback.getPayload().get(ResolvedCallback.SP));
        paramObject.addProperty(ResolvedCallback.BN, callback.getPayload().get(ResolvedCallback.BN));
        paramObject.addProperty(ResolvedCallback.CID, callback.getPayload().get(ResolvedCallback.CID));


        Call<JsonElement> call = patchService1.signInCallback(baseUrlParts.get(1),paramObject);

        Log.e("OBJECT" ,gson.toJson(paramObject));

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ callSignInCallBack: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callSignInCallBack: ", "" + response.code());

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                if (response.isSuccessful() && response.code() == Constants.API_CODE_200) {

                    CommonMethods.printLogE("Response @ callSignInCallBack: ", "Login Successfull!" );

                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }
                //FINISHED ACTIVITY
                finish();
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                //FINISHED ACTIVITY
                finish();


            }
        });
    }

}