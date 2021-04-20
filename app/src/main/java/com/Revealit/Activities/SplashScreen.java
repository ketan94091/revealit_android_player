package com.Revealit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.GlobalLocationService;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.CoursesModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Modifier;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashScreen extends AppCompatActivity {

    private Handler handler;
    private SplashScreen mActivity;
    private SplashScreen mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        mActivity = SplashScreen.this;
        mContext = SplashScreen.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        /*Intent background = new Intent(mContext, GlobalLocationService.class);
        startService(background);*/

        //GET ALL COURESES
        //callGetCourses();




        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                //INTENT
                Intent mIntent= new Intent(SplashScreen.this,MainActivity.class);
                startActivity(mIntent);
                finish();
            }
        }, 3000);
    }


    private void callGetCourses() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    okhttp3.Request original = chain.request();

                    okhttp3.Request request = original.newBuilder()
                            .header("Content-Type", "application/json; charset=utf-8")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });

            final OkHttpClient client = httpClient.build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_END_POINTS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

            Call<CoursesModel> webCall = patchService1.callGetCourses(Constants.API_GET_COURSES_DETAILS);
            webCall.enqueue(new Callback<CoursesModel>() {
                @Override
                public void onResponse(Call<CoursesModel> call, Response<CoursesModel> response) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();


                    CommonMethods.printLogE("callGetCourses ", "Response @ : " + gson.toJson(response.body()));


                    if (response.isSuccessful()) {

                        switch (1) {
                            case 1:

                                insertInDatabase(response.body());


                                break;

                            case 2:


                                break;
                        }

                    }

                }

                @Override
                public void onFailure(Call<CoursesModel> call, Throwable t) {

                    call.cancel();

                }
            });
        }

    private void insertInDatabase(CoursesModel body) {

        //CLEAR TABLE
        mDatabaseHelper.clearCoursesTables();

        for (int i =0 ; i <body.getData().size() ;i++){

            mDatabaseHelper.insertCourseData(body.getData().get(i).getId().byteValue(),
                    body.getData().get(i).getStandard(),
                    body.getData().get(i).getSubject(),
                    body.getData().get(i).getChapterName(),
                    body.getData().get(i).getDescription(),
                    body.getData().get(i).getLink());
        }



    }
}