package com.Revealit.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Adapter.UserSavedListsAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.SavedProductListModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserSavedListActivity extends AppCompatActivity implements View.OnClickListener {

    private UserSavedListActivity mActivity;
    private UserSavedListActivity mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView imgBackArrow;
    private RecyclerView recycleAccountList;
    private TextView txtCreateNewList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_saved_list);

        setId();
        setOnClicks();



    }


    private void setId() {

        mActivity = UserSavedListActivity.this;
        mContext = UserSavedListActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgBackArrow =(ImageView) findViewById(R.id.imgBackArrow);


        recycleAccountList = (RecyclerView) findViewById(R.id.recycleSavedProductList);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        recycleAccountList.setLayoutManager(mLinearLayoutManager);

        txtCreateNewList = (TextView)findViewById(R.id.txtCreateNewList);

        openUserProductList();
    }

    private void setOnClicks() {

        imgBackArrow.setOnClickListener(this);

    }
    private void openUserProductList() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                okhttp3.Request requestOriginal = chain.request();

                okhttp3.Request request = requestOriginal.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + " " + mSessionManager.getPreference(Constants.AUTH_TOKEN))
                        .method(requestOriginal.method(), requestOriginal.body())
                        .build();


                return chain.proceed(request);
            }
        });
        final OkHttpClient httpClient1 = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .client(httpClient1.newBuilder().connectTimeout(10, TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).build())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient1)
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

        Call<SavedProductListModel> call = patchService1.getUserSavedList(Constants.API_GET_USER_SAVED_LISTS);

        call.enqueue(new Callback<SavedProductListModel>() {
            @Override
            public void onResponse(Call<SavedProductListModel> call, retrofit2.Response<SavedProductListModel> response) {


                CommonMethods.printLogE("Response @ openUserProductList : ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ openUserProductList : ", "" + response.code());

                if (response.code() == Constants.API_CODE_200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ openUserProductList : ", "" + gson.toJson(response.body()));

                    //UPDATE UI
                    displaySavedProductList(response.body());



                } else {


                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }

            }

            @Override
            public void onFailure(Call<SavedProductListModel> call, Throwable t) {


                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

            }
        });
    }

    private void displaySavedProductList(SavedProductListModel body) {



        if(body.getData().size() == 0){
            txtCreateNewList.setVisibility(View.VISIBLE);
            recycleAccountList.setVisibility(View.GONE);
        }else{

            //BIND RECYCLER VIEW
            UserSavedListsAdapter mUserSavedListsAdapter = new UserSavedListsAdapter(mContext,UserSavedListActivity.this, body.getData());
            recycleAccountList.setAdapter(mUserSavedListsAdapter);

        }


    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.imgBackArrow:

                finish();

                break;
        }
    }
}