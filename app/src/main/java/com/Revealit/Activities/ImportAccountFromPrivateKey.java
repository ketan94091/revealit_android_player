package com.Revealit.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.BLockChain.ec.EosPrivateKey;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.GetProtonUsername;
import com.Revealit.ModelClasses.KeyStoreServerInstancesModel;
import com.Revealit.ModelClasses.SubmitProfileModel;
import com.Revealit.ModelClasses.UserDetailsFromPublicKeyModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.UserOnboardingProcess.NewAuthBiomatricAuthenticationActivity;
import com.Revealit.UserOnboardingProcess.NewAuthMobileAndPromoActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private ImageView imgCancel;
    private RelativeLayout imgClearKey;
    private EditText edtImportKey;
    private TextView txtContinueEnabled,txtContinueDisable;
    private String privateKeyPem , publicKeyPem, publicKey, accountName;
    private Gson mGson;
    ArrayList<KeyStoreServerInstancesModel.Data> selectedSilosAccountsList = new ArrayList<>();
    private String edtPrivateKey;

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
        imgClearKey=(RelativeLayout)findViewById(R.id.imgClearKey);

        edtImportKey=(EditText)findViewById(R.id.edtImportKey);

        txtContinueDisable=(TextView)findViewById(R.id.txtContinueDisable);
        txtContinueEnabled=(TextView)findViewById(R.id.txtContinueEnabled);

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

        mGson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();

    }

    private void setOnClicks() {

        txtContinueDisable.setOnClickListener(this);
        txtContinueEnabled.setOnClickListener(this);
        imgCancel.setOnClickListener(this);
        imgClearKey.setOnClickListener(this);
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
            case R.id.imgClearKey:
                //CLEAR EDIT TEXT
                edtImportKey.setText("");

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
                    CommonMethods.printLogE("Response @ fetchUsername: ", "" + gson.toJson(response.body()));


                    //CLOSED DIALOGUE
                    CommonMethods.closeDialog();

                    if (response.isSuccessful() && response.code() == Constants.API_CODE_200  ) {

                        //CHECK IF PROTON ACCOUNT BELONGS TO REVEALIT
                        if(response.body().getAccount_names().size() != 0  && response.body().getAccount_names().get(0).contains(".rtv")){

                            fetchUserDetailsFromPubkeyAndUsername(publicKey,gson.toJson(response.body().getAccount_names().get(0)));
                        }else{
                            //
                            openAccountNotValidDialogue();
                        }

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
    private void fetchUserDetailsFromPubkeyAndUsername(String publicKey, String username) {

        //OPEN DIALOGUE
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
        Call<UserDetailsFromPublicKeyModel> call = patchService1.getUserDetailsFromPubKeyAndName("api/userdetail?user_name="+username.replaceAll("^\"|\"$", "").replaceAll("u0027", "'").replaceAll("\\\\", "")+"&public_key="+publicKey);

        call.enqueue(new Callback<UserDetailsFromPublicKeyModel>() {
            @Override
            public void onResponse(Call<UserDetailsFromPublicKeyModel> call, Response<UserDetailsFromPublicKeyModel> response) {

                CommonMethods.printLogE("Response @ fetchUserDetailsFromPubkeyAndUsername: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ fetchUserDetailsFromPubkeyAndUsername: ", "" + response.code());
                CommonMethods.printLogE("Response @ fetchUserDetailsFromPubkeyAndUsername: ", "" + gson.toJson(response.body()));

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                //SAVE IMPORT KEY USERNAME AND PUBLIC KEY
                mSessionManager.updatePreferenceString(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_USERNAME, username.replaceAll("^\"|\"$", "").replaceAll("u0027", "'").replaceAll("\\\\", ""));
                mSessionManager.updatePreferenceString(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PUBLICKEY, publicKey);
                mSessionManager.updatePreferenceString(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PUBLICKEY_PEM, publicKeyPem);
                mSessionManager.updatePreferenceString(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY_PEM, privateKeyPem);
                mSessionManager.updatePreferenceString(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY, edtImportKey.getText().toString());
                mSessionManager.updatePreferenceBoolean(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_IS_USER_DELETED, false);


                if (response.isSuccessful() && response.code() == Constants.API_CODE_200  ) {
                    saveDataToTheAndroidKeyStore(response.body(),username.replaceAll("^\"|\"$", "").replaceAll("u0027", "'").replaceAll("\\\\", ""));

                }else if(response.code() == Constants.API_CODE_404){
                      displayMoreInfoNeededDialogue();
                } else {
                    //displayAlertDialogue();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        CommonMethods.buildDialog(mContext,"Error : "+ jObjError.getString("message"));
                        if(jObjError.getInt("error_code") == 604){
                            displayMoreInfoNeededDialogue();
                        }
                    } catch (Exception e) {
                        CommonMethods.buildDialog(mContext,"Error : "+e.getMessage());

                    }
                }

            }

            @Override
            public void onFailure(Call<UserDetailsFromPublicKeyModel> call, Throwable t) {

                //CLOSED DIALOGUE
                CommonMethods.closeDialog();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strApiCallFailure));


            }
        });

    }


    private void saveDataToTheAndroidKeyStore(UserDetailsFromPublicKeyModel body, String username) {


        if(body != null ){

            //MAKE SubmitProfileModel CLASS FROM ALL DATA
            SubmitProfileModel mSubmitModel = new SubmitProfileModel();
            mSubmitModel.setStatus(body.getStatus());
            mSubmitModel.setrevealit_private_key(body.getRevealit_private_key());
            mSubmitModel.setRole(body.getRole());
            mSubmitModel.setauth_token(body.getAuth_token());
            mSubmitModel.setAudience(body.getAudience());

            //SET PROTON DATA
            SubmitProfileModel.Proton mProton = new SubmitProfileModel.Proton();
            mProton.setAccountName(username);
            mProton.setPublicKey(publicKey);
            mProton.setPrivateKey(edtImportKey.getText().toString());
            mProton.setMnemonic("");
            mProton.setPublic_pem(publicKeyPem);
            mProton.setPrivate_pem(privateKeyPem);
            mSubmitModel.setProton(mProton);

            mSubmitModel.setIs_activated(""+body.getIs_activated());
            mSubmitModel.setMessage("");
            mSubmitModel.setError_code(0);
            mSubmitModel.setServerInstance("");



            mSessionManager.updatePreferenceString(Constants.KEY_USER_DATA, ""+mGson.toJson(mSubmitModel));
            mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN ,body.getAuth_token());
            mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE ,getResources().getString(R.string.strTokenBearer));
            mSessionManager.updatePreferenceString(Constants.PROTON_ACCOUNT_NAME ,username);
            //mSessionManager.updatePreferenceString(Constants.KEY_PROTON_WALLET_DETAILS,gson.toJson(body.getProton()));
            mSessionManager.updatePreferenceString(Constants.KEY_REVEALIT_PRIVATE_KEY ,body.getRevealit_private_key());
            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_REGISTRATION_DONE ,true);
            mSessionManager.updatePreferenceString(Constants.KEY_MOBILE_NUMBER,mSessionManager.getPreference(Constants.KEY_MOBILE_NUMBER));


            //STORE DATA IN TO KEYSTORE
            //FOR DISPLAY PURPOSE
            CommonMethods.encryptKey(edtPrivateKey, Constants.KEY_PRIVATE_KEY ,body.getRevealit_private_key(), mSessionManager);
            CommonMethods.encryptKey(publicKey,Constants.KEY_PUBLIC_KEY,body.getRevealit_private_key(), mSessionManager);
            CommonMethods.encryptKey("xyz",Constants.KEY_MNEMONICS,body.getRevealit_private_key(), mSessionManager);
            CommonMethods.encryptKey(privateKeyPem,Constants.KEY_PRIVATE_KEY_PEM,body.getRevealit_private_key(), mSessionManager);
            CommonMethods.encryptKey(publicKeyPem,Constants.KEY_PUBLIC_KEY_PEM,body.getRevealit_private_key(), mSessionManager);


            //UPDATE FLAG IF USER IS ADMIN OR NOT
            if(body.getRole().equals(getResources().getString(R.string.strAdmin))){
                mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,true);
            }else{
                mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN ,false);
            }

            //UPDATE ACTIVE FLAG
            if(body.getIs_activated().equals("1")){
                mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,true);
            }else{
                mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE ,false);
            }

            //UPDATE FLAG FOR APPLICATION MODE
            mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);

            //SET KEY STORE INSTANCE DATA
            storeKeyStoreInstances(mSubmitModel);

        }


    }

    private void storeKeyStoreInstances(SubmitProfileModel body) {

        //STORE DATA FOR SWAPPING SILOS
        //THIS IS TEMPORARY FOR ADMIN USERS
        //OVERRIDE EXISTING SILOS IF ADMIN CREATE NEW WITH FOR EXISTING SAVED SILOS
        //encryptKey(""+mGson.toJson(body),  Constants.KEY_SILOS_DATA+""+mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID),Constants.KEY_SILOS_ALIAS);

        //CREATE LIST WHICH COULD ENCRYPT AND THAN STORE IN THE KEY STORE AS A STRING
        ArrayList<KeyStoreServerInstancesModel.Data> mInstancesModel = new ArrayList<>();
        KeyStoreServerInstancesModel.Data mModelData = new KeyStoreServerInstancesModel.Data();
        mModelData.setServerInstanceName(mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME));
        mModelData.setMobileNumber(mSessionManager.getPreference(Constants.KEY_MOBILE_NUMBER));
        mModelData.setServerInstanceId(mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID));
        mModelData.setSubmitProfileModel(body);
        mInstancesModel.add(mModelData);

        //CHECK IF DATA IS ALREADY STORED IN TO KEYSTORE
        //IF STORED -> FETCH FROM KEYSTORE, CONVERT TO LIST, CREATE NEW OBJECT AND ADD THAT OBJECT TO LIST.
        //ELSE -> TREAT AS FIRST USER
        if(!CommonMethods.checkIfInstanceKeyStoreData(mSessionManager).isEmpty()){

            //CONVERT DATA TO JSON ARRAY
            //CREATE NEW ARRAY FROM THE STRING ARRAY
            //AFTER ADDING ALL SAVED DATA ADD NEWLY CREATED USER DATA
            try {
                JSONArray jsonArray =new JSONArray(CommonMethods.checkIfInstanceKeyStoreData(mSessionManager));
                ArrayList<KeyStoreServerInstancesModel.Data> dataArrayList = new ArrayList<>();

                for (int i=0 ;i < jsonArray.length();i++){

                    KeyStoreServerInstancesModel.Data mModel = new KeyStoreServerInstancesModel.Data();
                    mModel.setServerInstanceName(jsonArray.getJSONObject(i).getString("serverInstanceName"));
                    mModel.setMobileNumber(jsonArray.getJSONObject(i).getString("mobileNumber"));
                    mModel.setServerInstanceId(jsonArray.getJSONObject(i).getInt("serverInstanceId"));
                    mModel.setIsAccountRemoved(jsonArray.getJSONObject(i).getInt("isAccountRemoved"));

                    Log.e("ISSSS",""+jsonArray.getJSONObject(i).getInt("isAccountRemoved"));


                    SubmitProfileModel mSubmitProfileModel = new SubmitProfileModel();
                    mSubmitProfileModel.setAudience(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("audience"));

                    if(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("token") != null){
                        mSubmitProfileModel.setauth_token(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("token"));
                    }else{
                        mSubmitProfileModel.setauth_token("");
                    }



                    mSubmitProfileModel.setError_code(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getInt("error_code"));
                    mSubmitProfileModel.setIs_activated(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("is_activated"));
                    mSubmitProfileModel.setMessage(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("message"));
                    mSubmitProfileModel.setrevealit_private_key(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("revealit_private_key"));
                    mSubmitProfileModel.setRole(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("role"));
                    mSubmitProfileModel.setServerInstance(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("serverInstance"));
                    mSubmitProfileModel.setStatus(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getString("status"));

                    SubmitProfileModel.Proton mProton = new SubmitProfileModel.Proton();
                    mProton.setAccountName(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("account_name"));
                    mProton.setMnemonic(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("mnemonic"));
                    mProton.setPrivateKey(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("private_key"));
                    mProton.setPrivate_pem(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("private_pem"));
                    mProton.setPublicKey(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("public_key"));
                    mProton.setPublic_pem(jsonArray.getJSONObject(i).getJSONObject("submitProfileModel").getJSONObject("proton").getString("public_pem"));
                    mSubmitProfileModel.setProton(mProton);

                    mModel.setSubmitProfileModel(mSubmitProfileModel);

                    dataArrayList.add(mModel);
                }

                //ADD NEWLY CREATED INSTANCE OBJECT TO THE ARRAYLIST
                dataArrayList.add(mModelData);


                //STORE WHOLE ARRAY IN TO STRING FORMAT IN KEYSTORE
                CommonMethods.encryptKey(""+mGson.toJson(dataArrayList),  Constants.KEY_SILOS_DATA,Constants.KEY_SILOS_ALIAS, mSessionManager);



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            //STORE WHOLE JSON IN TO STRING
            CommonMethods.encryptKey(""+mGson.toJson(mInstancesModel),  Constants.KEY_SILOS_DATA,Constants.KEY_SILOS_ALIAS, mSessionManager);
        }

        //UPDATE GOOGLE DRIVE BACKUP FLAG
        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_GOOGLE_DRIVE_BACKUP_DONE, false);

        //GO TO NEXT ACTIVITY
        Intent mIntent = new Intent(ImportAccountFromPrivateKey.this, HomeScreenTabLayout.class);
        startActivity(mIntent);
        finishAffinity();

    }

    private boolean checkPrivateKeyIsEmpty() {

        edtPrivateKey  = edtImportKey.getText().toString().replaceAll("\\s","");

        if(edtPrivateKey.contains(" ")){
           CommonMethods.buildDialog(mContext,getResources().getString(R.string.strInvalidPrivateKey));
           return false;
        }

        if(edtPrivateKey.isEmpty()){
            CommonMethods.buildDialog(mContext,getResources().getString(R.string.strEnterPrivateKey));
            return false;
        }else if(edtPrivateKey.length() < 10){
            CommonMethods.buildDialog(mContext,getResources().getString(R.string.strInvalidPrivateKey));
           return false;
        }else if(CommonMethods.checkEnterPrivateKeyIsFromOtherSilos(mSessionManager,edtPrivateKey)) {
           if(CommonMethods.checkEnterPrivateKeyUserHasDeletedAccount(mSessionManager,edtPrivateKey)){
               openDeletedUserInfoNeededDialogue();
           }else{
               openUserAlreadyAvailableDialogue();
           }

            return false;

       }else{
            try {
                EosPrivateKey mEosPrivateKeyPem = new EosPrivateKey(edtPrivateKey);
                privateKeyPem = ""+mEosPrivateKeyPem;
                publicKeyPem = ""+mEosPrivateKeyPem.getPublicKey().toString();

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

                return true;

            } catch (IllegalArgumentException | SerializationProviderError | EosioJavaRpcProviderInitializerError | ImportKeyError | GetAvailableKeysError exception) {
                displayErrorDialogue();
                return false;
            }
        }

    }

    private void openDeletedUserInfoNeededDialogue() {

        try {
            selectedSilosAccountsList = new FetchDataFromAndroidKeyStoreTask(edtImportKey.getText().toString()).execute(mSessionManager).get();

            //CHECK IF THERE IS DATA AVAILABLE FOR SELECTED SILOS
            if(selectedSilosAccountsList != null && selectedSilosAccountsList.size() != 0){


                //SAVE IMPORT KEY USERNAME AND PUBLICK KEY
                mSessionManager.updatePreferenceString(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_USERNAME, selectedSilosAccountsList.get(0).getSubmitProfileModel().getProton().getAccountName());
                mSessionManager.updatePreferenceString(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PUBLICKEY, selectedSilosAccountsList.get(0).getSubmitProfileModel().getProton().getPublicKey());
                mSessionManager.updatePreferenceString(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PUBLICKEY_PEM, selectedSilosAccountsList.get(0).getSubmitProfileModel().getProton().getPublic_pem());
                mSessionManager.updatePreferenceString(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY_PEM, selectedSilosAccountsList.get(0).getSubmitProfileModel().getProton().getPrivate_pem());
                mSessionManager.updatePreferenceString(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY, selectedSilosAccountsList.get(0).getSubmitProfileModel().getProton().getPrivateKey());
                mSessionManager.updatePreferenceBoolean(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_IS_USER_DELETED, true);

                //OPEN MORE INFO NEEDED DIALOGUE
                displayMoreInfoNeededDialogue();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void displayErrorDialogue() {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.invalid_private_key, null);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBuilder.setView(dialogView);

        final AlertDialog mAlertDialog = dialogBuilder.create();
        TextView txtOk = (TextView) dialogView.findViewById(R.id.txtOk);


        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

            }
        });


        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                edtImportKey.setText("");
                mAlertDialog.dismiss();


            }
        });

        mAlertDialog.show();
    }

    private void openAccountNotValidDialogue() {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.account_not_valid_dialogue, null);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBuilder.setView(dialogView);

        final AlertDialog mAlertDialog = dialogBuilder.create();
        TextView txtOk = (TextView) dialogView.findViewById(R.id.txtOk);
        LinearLayout linearCancel =(LinearLayout)dialogView.findViewById(R.id.linearCancel);


        linearCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();


                //CLEAR EDIT TEXT
                edtImportKey.setText("");

            }
        });


        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                edtImportKey.setText("");
                mAlertDialog.dismiss();


            }
        });

        mAlertDialog.show();
    }


    private void openUserAlreadyAvailableDialogue() {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.import_key_user_already_exist_dialogue, null);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBuilder.setView(dialogView);

        final AlertDialog mAlertDialog = dialogBuilder.create();
        TextView txtYes = (TextView) dialogView.findViewById(R.id.txtYes);
        TextView txtNo = (TextView) dialogView.findViewById(R.id.txtNo);


        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

            }
        });


        txtNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();
                finish();

            }
        });

        txtYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    selectedSilosAccountsList = new FetchDataFromAndroidKeyStoreTask(edtImportKey.getText().toString()).execute(mSessionManager).get();

                    //CHECK IF THERE IS DATA AVAILABLE FOR SELECTED SILOS
                    if(selectedSilosAccountsList != null && selectedSilosAccountsList.size() != 0){
                        mSessionManager.updatePreferenceString(Constants.KEY_USER_DATA, mSessionManager.getPreference("" + mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID)));
                        mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN, selectedSilosAccountsList.get(0).getSubmitProfileModel().getauth_token());
                        mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE, mContext.getResources().getString(R.string.strTokenBearer));
                        mSessionManager.updatePreferenceString(Constants.PROTON_ACCOUNT_NAME, selectedSilosAccountsList.get(0).getSubmitProfileModel().getProton().getAccountName());
                        //mSessionManager.updatePreferenceString(Constants.KEY_PROTON_WALLET_DETAILS,gson.toJson(body.getProton()));
                        mSessionManager.updatePreferenceString(Constants.KEY_REVEALIT_PRIVATE_KEY, selectedSilosAccountsList.get(0).getSubmitProfileModel().getrevealit_private_key());
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_REGISTRATION_DONE, true);
                        mSessionManager.updatePreferenceString(Constants.KEY_MOBILE_NUMBER,selectedSilosAccountsList.get(0).getMobileNumber());

                        //STORE DATA IN TO KEYSTORE
                        CommonMethods.encryptKey(selectedSilosAccountsList.get(0).getSubmitProfileModel().getProton().getPrivateKey(), Constants.KEY_PRIVATE_KEY,selectedSilosAccountsList.get(0).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);
                        CommonMethods.encryptKey(selectedSilosAccountsList.get(0).getSubmitProfileModel().getProton().getPublicKey(),Constants.KEY_PUBLIC_KEY, selectedSilosAccountsList.get(0).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);
                        CommonMethods.encryptKey(selectedSilosAccountsList.get(0).getSubmitProfileModel().getProton().getMnemonic(),Constants.KEY_MNEMONICS, selectedSilosAccountsList.get(0).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);
                        CommonMethods.encryptKey(selectedSilosAccountsList.get(0).getSubmitProfileModel().getProton().getPrivate_pem(),Constants.KEY_PRIVATE_KEY_PEM, selectedSilosAccountsList.get(0).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);
                        CommonMethods.encryptKey(selectedSilosAccountsList.get(0).getSubmitProfileModel().getProton().getPublic_pem(), Constants.KEY_PUBLIC_KEY_PEM,selectedSilosAccountsList.get(0).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);


                        //UPDATE FLAG IF USER IS ADMIN OR NOT
                        if (selectedSilosAccountsList.get(0).getSubmitProfileModel().getRole().equals(mContext.getResources().getString(R.string.strAdmin))) {
                            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN, true);
                        } else {
                            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN, false);
                        }

                        //UPDATE ACTIVE FLAG
                        if (selectedSilosAccountsList.get(0).getSubmitProfileModel().getIs_activated().equals("1")) {
                            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE, true);
                        } else {
                            mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE, false);
                        }

                        //UPDATE FLAG FOR APPLICATION MODE
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);

                        //GO TO BIOMETRIC CONFIRMATION ACTIVITY
                        Intent mIntent = new Intent(mActivity, NewAuthBiomatricAuthenticationActivity.class);
                        mIntent.putExtra(Constants.KEY_NEW_AUTH_USERNAME, selectedSilosAccountsList.get(0).getSubmitProfileModel().getProton().getAccountName());
                        mActivity.startActivity(mIntent);
                        finishAffinity();
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mAlertDialog.dismiss();

            }
        });
        mAlertDialog.show();
    }
    private void displayMoreInfoNeededDialogue() {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.import_key_user_notfound, null);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBuilder.setView(dialogView);

        final AlertDialog mAlertDialog = dialogBuilder.create();
        TextView txtContinue = (TextView) dialogView.findViewById(R.id.txtContinue);
        LinearLayout linearCancel = (LinearLayout) dialogView.findViewById(R.id.linearCancel);


        linearCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

            }
        });


        txtContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //OPEN ENTER MOBILE NUMBER APP
                Intent mIntent = new Intent(ImportAccountFromPrivateKey.this,NewAuthMobileAndPromoActivity.class);
                mIntent.putExtra(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY, true);
                startActivity(mIntent);


                mAlertDialog.dismiss();

            }
        });

        mAlertDialog.show();

    }

}
class FetchDataFromAndroidKeyStoreTask extends AsyncTask<SessionManager, Integer, ArrayList<KeyStoreServerInstancesModel.Data>> {

    String strPrivateKey ="";

    public FetchDataFromAndroidKeyStoreTask(String strPrivateKey) {

        this.strPrivateKey =strPrivateKey;
    }

    @Override
    protected ArrayList<KeyStoreServerInstancesModel.Data> doInBackground(SessionManager... mSessionManager) {
        return CommonMethods.fetchUserFromPrivateKey(mSessionManager[0],strPrivateKey);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(ArrayList<KeyStoreServerInstancesModel.Data> searchResults) {
        super.onPostExecute(searchResults);

    }
}