package com.Revealit.Activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.BLockChain.ec.EosPrivateKey;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.GetProtonUsername;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import one.block.eosiojava.error.serializationProvider.SerializationProviderError;
import one.block.eosiojava.error.signatureProvider.GetAvailableKeysError;
import one.block.eosiojava.implementations.ABIProviderImpl;
import one.block.eosiojava.interfaces.IABIProvider;
import one.block.eosiojava.interfaces.IRPCProvider;
import one.block.eosiojava.interfaces.ISerializationProvider;
import one.block.eosiojava.interfaces.ISignatureProvider;
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

public class ImportAccountFromPrivateKey extends AppCompatActivity implements View.OnClickListener {

    private ImportAccountFromPrivateKey mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView imgScanQRcode,imgCancel;
    private EditText edtImportKey;
    private TextView txtPublicPem,txtPrivatePem,txtPubicKey,txtUserAccountName,txtContinueEnabled,txtContinueDisable;
    private String privateKeyPem , publicKeyPem, publicKey, accountName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_private_key);

        setIds();
        setOnClicks();
    }

    private void setIds() {

        mActivity = ImportAccountFromPrivateKey.this;
        mContext = ImportAccountFromPrivateKey.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgCancel =(ImageView)findViewById(R.id.imgCancel);
        imgScanQRcode=(ImageView)findViewById(R.id.imgScanQRcode);

        edtImportKey=(EditText)findViewById(R.id.edtImportKey);

        txtContinueDisable=(TextView)findViewById(R.id.txtContinueDisable);
        txtContinueEnabled=(TextView)findViewById(R.id.txtContinueEnabled);
        txtUserAccountName=(TextView)findViewById(R.id.txtUserAccountName);
        txtPubicKey=(TextView)findViewById(R.id.txtPubicKey);
        txtPrivatePem=(TextView)findViewById(R.id.txtPrivatePem);
        txtPublicPem=(TextView)findViewById(R.id.txtPublicPem);

        edtImportKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() != 0){
                    txtContinueEnabled.setVisibility(View.VISIBLE);
                    txtContinueDisable.setVisibility(View.GONE);
                }else {
                    txtContinueEnabled.setVisibility(View.GONE);
                    txtContinueDisable.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void setOnClicks() {

        txtContinueDisable.setOnClickListener(this);
        txtContinueEnabled.setOnClickListener(this);
        imgCancel.setOnClickListener(this);
        imgScanQRcode.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.txtContinueDisable:
                break;
            case R.id.txtContinueEnabled:

                if(checkPrivateKeyIsEmpty()){

                    fetchUsername();
                }
                break;
            case R.id.imgCancel:
                finish();
                break;
            case R.id.imgScanQRcode:
                break;
        }

    }

    private void fetchUsername() {

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
                    .baseUrl(Constants.GET_PROTON_ACCOUNT_NAME_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                    .build();


            UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("public_key", publicKeyPem);


            Call<GetProtonUsername> call = patchService1.getProtonAccountName(paramObject);

            call.enqueue(new Callback<GetProtonUsername>() {
                @Override
                public void onResponse(Call<GetProtonUsername> call, Response<GetProtonUsername> response) {

                    CommonMethods.printLogE("Response @ fetchUsername: ", "" + response.isSuccessful());
                    CommonMethods.printLogE("Response @ fetchUsername: ", "" + response.code());
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();


                    //CLOSED DIALOGUE
                    CommonMethods.closeDialog();

                    if (response.isSuccessful() && response.code() == Constants.API_CODE_200  ) {
                        CommonMethods.printLogE("Response @ fetchUsername: ", "" + gson.toJson(response.body().getAccount_names().get(0)));

                        txtUserAccountName.setText("" + gson.toJson(response.body().getAccount_names().get(0)));
                        txtPrivatePem.setText("PRIVATE_PEM : "+privateKeyPem);
                        txtPublicPem.setText("PUBLIC_PEM : "+publicKeyPem);
                        txtPubicKey.setText("PUBLIC_KEY : "+publicKey);

                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            CommonMethods.buildDialog(mContext,"Error : "+ jObjError.getString("message"));
                        } catch (Exception e) {
                            CommonMethods.buildDialog(mContext,"Error : "+e.getMessage());

                        }
                    }

                }

                @Override
                public void onFailure(Call<GetProtonUsername> call, Throwable t) {

                    //CLOSED DIALOGUE
                    CommonMethods.closeDialog();

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strApiCallFailure));


                }
            });

    }

    private boolean checkPrivateKeyIsEmpty() {
        if(edtImportKey.getText().toString().isEmpty()){
            CommonMethods.buildDialog(mContext,"Please enter private key!");
            return false;
        }else if(edtImportKey.getText().toString().length() < 10){
            CommonMethods.buildDialog(mContext,"Please enter valid private key!");
           return false;
        }else{
            try {
                EosPrivateKey mEosPrivateKeyPem = new EosPrivateKey(edtImportKey.getText().toString());
                privateKeyPem = ""+mEosPrivateKeyPem;
                publicKeyPem = ""+mEosPrivateKeyPem.getPublicKey().toString();

                Log.e("private_pem",""+mEosPrivateKeyPem);
                Log.e("public_pem",""+mEosPrivateKeyPem.getPublicKey().toString());

                // Creating serialization provider
                ISerializationProvider serializationProvider;
                serializationProvider = new AbiEosSerializationProviderImpl();

                // Creating RPC Provider
                IRPCProvider rpcProvider;
                rpcProvider = new EosioJavaRpcProviderImpl(Constants.PROTON_BASE_URL, true);

                // Creating ABI provider
                IABIProvider abiProvider = new ABIProviderImpl(rpcProvider, serializationProvider);

                // Creating Signature provider
                ISignatureProvider signatureProvider = new SoftKeySignatureProviderImpl();

                ((SoftKeySignatureProviderImpl) signatureProvider).importKey(""+mEosPrivateKeyPem);

                // Creating TransactionProcess
                TransactionSession session = new TransactionSession(serializationProvider, rpcProvider, abiProvider, signatureProvider);

                //GET PUBLIC KEY FROM THE SESSION
                publicKey= ""+session.getSignatureProvider().getAvailableKeys().get(0);

                Log.e("PUB_KEY",""+session.getSignatureProvider().getAvailableKeys().get(0));

                return true;

            } catch (IllegalArgumentException | SerializationProviderError | EosioJavaRpcProviderInitializerError | ImportKeyError | GetAvailableKeysError exception) {
                CommonMethods.buildDialog(mContext,"Exception Occurred @"+exception);
                return false;
            }
        }

    }
}