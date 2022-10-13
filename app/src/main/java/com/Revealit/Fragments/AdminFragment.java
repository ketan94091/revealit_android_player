package com.Revealit.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.NewAuthLogin;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AdminFragment";
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private View mView, viewBottomDefault, viewHelp, viewSettings, viewSavedItems, viewAccount;
    private HomeScreenTabLayout mHomeScreenTabLayout;
    private OnFragmentInteractionListener mListener;
    private RelativeLayout relativeBack;
    private String strServerName, strMobileKey,strRegistrationKey;
    private int intEnvironmetID;


    public AdminFragment(HomeScreenTabLayout homeScreenTabLayout) {
        this.mHomeScreenTabLayout = homeScreenTabLayout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.admin_fragment, container, false);

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

        relativeBack =(RelativeLayout)mView.findViewById(R.id.relativeBack);

        SwitchCompat switchModeOfApp =(SwitchCompat) mView.findViewById(R.id.switchModeOfApp);

        LinearLayout linearBetacurator = (LinearLayout) mView.findViewById(R.id.linearBetacurator);
        LinearLayout linearStaging = (LinearLayout) mView.findViewById(R.id.linearStaging);
        LinearLayout linearTesting1 = (LinearLayout) mView.findViewById(R.id.linearTesting1);
        LinearLayout linearTesting2 = (LinearLayout) mView.findViewById(R.id.linearTesting2);
        LinearLayout linearIntegration = (LinearLayout) mView.findViewById(R.id.linearIntegration);

        TextView txtBetaCuratorMobile = (TextView) mView.findViewById(R.id.txtBetaCuratorMobile);
        TextView txtBetaCuratorRegistration = (TextView) mView.findViewById(R.id.txtBetaCuratorRegistration);
        TextView txtSimulation = (TextView) mView.findViewById(R.id.txtSimulation);
        TextView txtLive = (TextView) mView.findViewById(R.id.txtLive);

        TextView txtStagingMobile = (TextView) mView.findViewById(R.id.txtStagingMobile);
        TextView txtStagingRegistration = (TextView) mView.findViewById(R.id.txtStagingRegistration);

        TextView txtTesting1Mobile = (TextView) mView.findViewById(R.id.txtTesting1Mobile);
        TextView txtTesting1Registration = (TextView) mView.findViewById(R.id.txtTesting1Registration);

        TextView txtTesting2Mobile = (TextView) mView.findViewById(R.id.txtTesting2Mobile);
        TextView txtTesting2Registration = (TextView) mView.findViewById(R.id.txtTesting2Registration);

        TextView txtIntegrationMobile = (TextView) mView.findViewById(R.id.txtIntegrationMobile);
        TextView txtIntegrationRegistration = (TextView) mView.findViewById(R.id.txtIntegrationRegistration);



        //SET SELECT ENVIRONMENT
        strMobileKey = mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY);
        strRegistrationKey = mSessionManager.getPreference(Constants.API_END_POINTS_REGISTRATION_KEY);
        strServerName = mSessionManager.getPreference(Constants.API_END_POINTS_SERVER_NAME);
        intEnvironmetID = mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID);


        //CHECKBOX TRUE IF APP MODE IS LIVE
        if(mSessionManager.getPreferenceBoolean(Constants.KEY_APP_MODE)) {
            switchModeOfApp.setChecked(true);
            txtSimulation.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
            txtLive.setTextColor(getResources().getColor(R.color.colorBlack));
        }else {
            switchModeOfApp.setChecked(false);
            txtSimulation.setTextColor(getResources().getColor(R.color.colorBlack));
            txtLive.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
        }

        switchModeOfApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



                if (isChecked){
                    //UPDATE FLAG FOR APPLICATION MODE
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);
                    mHomeScreenTabLayout.tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                    mHomeScreenTabLayout.tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                    mHomeScreenTabLayout.tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorNewAppGreen), PorterDuff.Mode.SRC_IN);
                    mHomeScreenTabLayout.tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                    mHomeScreenTabLayout.tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey) , getResources().getColor(R.color.colorNewAppGreen));

                    mHomeScreenTabLayout.viewBottom.setBackgroundColor(getResources().getColor(R.color.colorNewAppGreen));

                    txtSimulation.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
                    txtLive.setTextColor(getResources().getColor(R.color.colorBlack));

                }else{
                    //CLEAR DUMMY DATA
                    mDatabaseHelper.clearSimulationHistoryDataTable();

                    //UPDATE FLAG FOR APPLICATION MODE
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, false);
                    mHomeScreenTabLayout.tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                    mHomeScreenTabLayout.tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                    mHomeScreenTabLayout.tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorBlueBottomBar), PorterDuff.Mode.SRC_IN);
                    mHomeScreenTabLayout.tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorBottomBarActiveGrey), PorterDuff.Mode.SRC_IN);
                    mHomeScreenTabLayout.tabLayout.setTabTextColors(getResources().getColor(R.color.colorBottomBarActiveGrey) , getResources().getColor(R.color.colorBlueBottomBar));

                    mHomeScreenTabLayout.viewBottom.setBackgroundColor(getResources().getColor(R.color.colorBlueBottomBar));

                    txtSimulation.setTextColor(getResources().getColor(R.color.colorBlack));
                    txtLive.setTextColor(getResources().getColor(R.color.colorBottomBarActiveGrey));
                }

            }


        });

        switch (mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID)) {
            case 1:

                linearBetacurator.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;
            case 2:

                linearStaging.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;

            case 3:

                linearTesting1.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;

            case 4:

                linearTesting2.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;

            case 5:

                linearIntegration.setBackground(getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));

                break;
        }

        linearBetacurator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_B_CURATOR,Constants.API_END_POINTS_REGISTRATION_B_CURATOR, mActivity.getResources().getString(R.string.strBeta),1);


                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                checkUserAvailable();


            }
        });
        linearStaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_S_CURATOR,Constants.API_END_POINTS_REGISTRATION_S_CURATOR, mActivity.getResources().getString(R.string.strStaging),2);


                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                checkUserAvailable();

            }
        });
        linearTesting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_T1_CURATOR,Constants.API_END_POINTS_REGISTRATION_T1_CURATOR, mActivity.getResources().getString(R.string.strTesting1),2);


                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                checkUserAvailable();

            }
        });

        linearTesting2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_T2_CURATOR,Constants.API_END_POINTS_REGISTRATION_T2_CURATOR, mActivity.getResources().getString(R.string.strTesting2),4);


                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                checkUserAvailable();


            }
        });

        linearIntegration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UPDATE ENVIRONMENT
                updateEnvironment(Constants.API_END_POINTS_MOBILE_INTEGRATION_CURATOR,Constants.API_END_POINTS_REGISTRATION_INTEGRATION_CURATOR, mActivity.getResources().getString(R.string.strIntegration),5);

                //CHECK IF THE SAME USER IS AVAILABLE IN SELECTED CURATOR
                //IF AVAIlABlE THEN LOGIN API CALL AND SAVE NEW TOKEN
                checkUserAvailable();

            }
        });
      

    }

    private void checkUserAvailable() {

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
        paramObject.addProperty("revealit_private_key",mSessionManager.getPreference(Constants.KEY_REVEALIT_PRIVATE_KEY));
        paramObject.addProperty("name", mSessionManager.getPreference(Constants.PROTON_ACCOUNT_NAME));


        Call<NewAuthLogin> call = patchService1.newAuthLogin(paramObject);

        call.enqueue(new Callback<NewAuthLogin>() {
            @Override
            public void onResponse(Call<NewAuthLogin> call, Response<NewAuthLogin> response) {

                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callAuthenticationAPI: ", "" + response.code());

                //CLOSE DIALOG
                CommonMethods.closeDialog();


                if (response.isSuccessful() && response.code() == Constants.API_SUCCESS) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ Login: ", "" + gson.toJson(response.body()));

                    //SAVE AUTHENTICATION DATA
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN, response.body().getToken());
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE, response.body().getToken_type());
                    mSessionManager.updatePreferenceBoolean(Constants.USER_LOGGED_IN, true);
                    mSessionManager.updatePreferenceBoolean(Constants.IS_FIRST_LOGIN, true);


                    //SEND USER TO HOME SCREEN
                    Intent mIntent = new Intent(mHomeScreenTabLayout, HomeScreenTabLayout.class);
                    mIntent.putExtra(Constants.KEY_IS_FROM_REGISTRATION_SCREEN,false);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mIntent);
                    mHomeScreenTabLayout.finish();



                } else {

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strUsernotfound));

                    //UPDATE DEFAULT ENVIRONMENT IF THERE IS NO USER AVAILABLE TO THE SELECTED ENVIRONMENT
                    //IN CASE ERROR SET DEFAULT ENVIRONMENT
                    updateEnvironment(strMobileKey,strRegistrationKey, strServerName,intEnvironmetID);

                }
            }

            @Override
            public void onFailure(Call<NewAuthLogin> call, Throwable t) {


                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                //UPDATE DEFAULT ENVIRONMENT IF THERE IS NO USER AVAILABLE TO THE SELECTED ENVIRONMENT
                //IN CASE ERROR SET DEFAULT ENVIRONMENT
                updateEnvironment(strMobileKey,strRegistrationKey, strServerName,intEnvironmetID);


                CommonMethods.closeDialog();

            }
        });

    }


    private void setOnClicks() {
        relativeBack.setOnClickListener(this);
    }

    private void updateEnvironment(String strMobileKey, String strRegistrationKey, String strServerName, int intEnvironmetID){

        //CHANGE API END POINT TO BETA
        mSessionManager.updatePreferenceString(Constants.API_END_POINTS_MOBILE_KEY, strMobileKey);
        mSessionManager.updatePreferenceString(Constants.API_END_POINTS_REGISTRATION_KEY, strRegistrationKey);
        mSessionManager.updatePreferenceInteger(Constants.TESTING_ENVIRONMENT_ID, intEnvironmetID);

        //UPDATE END POINT NAME
        mSessionManager.updatePreferenceString(Constants.API_END_POINTS_SERVER_NAME, strServerName);



    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){
            case R.id.relativeBack:
                //GO BACK TO PREVIOUS PAGE
                getFragmentManager().popBackStackImmediate();
                break;
                
        }

    }
   

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void messageFromParentFragment(Uri uri);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setIds();
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        if (menuVisible) {
            
        }
        super.setMenuVisibility(menuVisible);
    }


}
