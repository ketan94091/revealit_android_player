package com.Revealit.RetrofitClass;


import com.Revealit.CommonClasse.Constants;
import com.Revealit.ModelClasses.CheckEmailModel;
import com.Revealit.ModelClasses.DotsLocationsModel;
import com.Revealit.ModelClasses.GetAccountDetailsModel;
import com.Revealit.ModelClasses.GetMultiColorGLB;
import com.Revealit.ModelClasses.GetProductDetailsModel;
import com.Revealit.ModelClasses.GetRecipesDetails;
import com.Revealit.ModelClasses.InfluencersModel;
import com.Revealit.ModelClasses.LoginAuthModel;
import com.Revealit.ModelClasses.RewardHistoryModel;
import com.Revealit.ModelClasses.UserRegistrationModel;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


/**
 * Created by baps on 29-03-2017.
 */

public interface UpdateAllAPI {

    //LOGIN API WITH RETROFIT
    @POST(Constants.API_AUTHENTICATION)
    Call<LoginAuthModel> loginAuth(@Body JsonObject body);

    //GET ALL PLAYER VIDEOS
    @GET()
    Call<JsonElement> getPlayData(@Url String url);

    //GET VIDEO DOTS LOCATIONS
    @POST(Constants.API_GET_DOTS_LOCATIONS+"{videoID}")
    Call<DotsLocationsModel> getVideoDotsLocation(@Path("videoID") String videoID, @Query("time") String time);

    //CHECK EMAIL ID FOR PROTON WALLET REGISTRATION
    @POST(Constants.API_CHECK_EMAIL_FOR_PROTON_REGISTRATION)
    Call<CheckEmailModel> checkEmailid(@Body JsonObject body);

    //SEND VERIFICATION CODE TO EMAIL
    @POST(Constants.API_SEND_VERIFICATION_CODE)
    Call<CheckEmailModel> sendVerificationEmail(@Body JsonObject body);


    //VERIFY CODE
    @POST(Constants.API_VERIFY_CODE)
    Call<CheckEmailModel> verifyCode(@Body JsonObject body);

    //VERIFY CODE
    @POST(Constants.API_USER_REGISTRAION_TO_PROTON_AND_REVEALIT)
    Call<UserRegistrationModel> userRegistration(@Body JsonObject body);

    //GET ACCOUNT DETAILS API WITH RETROFIT
    @POST(Constants.API_GET_USER_ACCOUNT_DETAILS)
    Call<GetAccountDetailsModel> getUserAccountDetails(@Query("account_name") String account_name);

    //GET ACCOUNT DETAILS API WITH RETROFIT
    @POST(Constants.API_GET_REWARD_HISTORY)
    Call<RewardHistoryModel> getRewardHistory();

    //GET MORE REWARD DATA
    @POST(Constants.API_GET_MORE_REWARD_HISTORY)
    Call<RewardHistoryModel> getMoreRewardData(@Query("page") int page);

    //GET PRODUCT DETAILS
    @GET()
    Call<GetProductDetailsModel> getProductDetails(@Url String url);

    //GET RECIPES DEATILS
    @GET()
    Call<GetRecipesDetails> getRecipesDetails(@Url String url);

    //GET INFLUENCERS DEATILS
    @GET()
    Call<InfluencersModel> getInfluencers(@Url String url);

    //GENERATE REWARD DATA
    @POST(Constants.API_GENERATE_REWARD_DATA)
    Call<JsonElement> rewardData(@Query(Constants.REWARD_TYPE) String type,
                                 @Query(Constants.REWARD_TYPE_ID) String ID);

    //GET MULTICOLOR GLBS
    @GET()
    Call<GetMultiColorGLB> getMulticolorGlbs(@Url String url);
}
