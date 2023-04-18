package com.Revealit.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.InviteModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.UserOnboardingProcess.NewAuthGetStartedActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MaintanaceActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MaintanaceActivity";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private TextView txtReload;
    private boolean isFromCallBackApi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintanace);

        setIds();
        setOnClicks();
    }
    private void setIds() {
        mActivity = MaintanaceActivity.this;
        mContext = MaintanaceActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        txtReload=(TextView)findViewById(R.id.txtReload);



    }

    private void setOnClicks() {

        txtReload.setOnClickListener(this);

    }


    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.txtReload:

                apiInviteSettings();

                break;
        }

    }

    private void apiInviteSettings(){

        //OPEN PROGRESS BAR
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
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(3000, TimeUnit.SECONDS).readTimeout(3000, TimeUnit.SECONDS).writeTimeout(3000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        String url=" ";


        Call<InviteModel> call = patchService1.getCampaignDetails(Constants.API_NEW_AUTH_INVITE_SETTINGS+url);

        call.enqueue(new Callback<InviteModel>() {
            @Override
            public void onResponse(Call<InviteModel> call, Response<InviteModel> response) {

                CommonMethods.printLogE("Response @ apiSendInvites: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ apiSendInvites: ", "" + response.code());
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ apiSendInvites: ", "" + gson.toJson(response.body()));

                //CLOSE DIALOGUE
                CommonMethods.closeDialog();

                if(response.code() == 200){
                    //UPDATE INVITE UI
                    openNextActivity(response.body());
                }else
                {
                    openMaintenanceActivity();
                }

            }

            @Override
            public void onFailure(Call<InviteModel> call, Throwable t) {

                //CLOSE DIALOGUE
                CommonMethods.closeDialog();

                openMaintenanceActivity();


            }
        });


    }

    private void openMaintenanceActivity() {
        Intent mIntent = new Intent(MaintanaceActivity.this, MaintanaceActivity.class);
        startActivity(mIntent);
        finishAffinity();
    }

    private void openNextActivity(InviteModel mInviteModel) {


        //UPDATE PREFERENCE FOR APP SETTINGS
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_MSG ,""+mInviteModel.getInvitation_message());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_COPY_CLIPBOARD ,""+mInviteModel.getInvitation_message_clipboard());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_BIOMETRIC_PERMISSION ,""+getResources().getString(R.string.strBiomatricPermissionTwo));
        mSessionManager.updatePreferenceString(Constants.KEY_CALL_FOR_INVITE_MSG ,""+mInviteModel.getInvitation_message());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_CYPTO_CURRNCY ,""+mInviteModel.getCrypto_currency());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_CURRNCY ,""+mInviteModel.getCurrency());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_CURRNCY_AMOUNT ,""+mInviteModel.getCurrency_amount());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_PLACEHOLDER ,""+mInviteModel.getPlace_holder());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_CURRENCY_ICON ,""+mInviteModel.getCurrency_icon_url());
        mSessionManager.updatePreferenceString(Constants.KEY_INVITE_QUESTION ,""+mInviteModel.getQuestion());

        //INTENT
        //CHECK IF USER IS ALREADY LOGGED IN OR NOT
        if (!mSessionManager.getPreferenceBoolean(Constants.USER_LOGGED_IN)) {
            Intent mIntent = new Intent(MaintanaceActivity.this, NewAuthGetStartedActivity.class);
            startActivity(mIntent);
            finishAffinity();
        }else {
            //CALL CALLBACK API
            Intent mIntent = new Intent(MaintanaceActivity.this, HomeScreenTabLayout.class);
            mIntent.putExtra(Constants.KEY_ISFROM_LOGIN, false);
            startActivity(mIntent);
            finishAffinity();

        }


    }
}