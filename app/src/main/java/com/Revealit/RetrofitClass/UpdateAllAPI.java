package com.Revealit.RetrofitClass;


import com.Revealit.CommonClasse.Constants;
import com.Revealit.ModelClasses.CheckEmailModel;
import com.Revealit.ModelClasses.CheckUserNameStatusModel;
import com.Revealit.ModelClasses.CountryCodes;
import com.Revealit.ModelClasses.DotsLocationsModel;
import com.Revealit.ModelClasses.GetAccountDetailsModel;
import com.Revealit.ModelClasses.GetMultiColorGLB;
import com.Revealit.ModelClasses.GetProductDetailsModel;
import com.Revealit.ModelClasses.GetProtonUsername;
import com.Revealit.ModelClasses.GetRecipesDetails;
import com.Revealit.ModelClasses.InfluencersModel;
import com.Revealit.ModelClasses.InviteModel;
import com.Revealit.ModelClasses.ItemListFromItemIdModel;
import com.Revealit.ModelClasses.LoginAuthModel;
import com.Revealit.ModelClasses.NewAuthLogin;
import com.Revealit.ModelClasses.NewAuthLoginCallbackModel;
import com.Revealit.ModelClasses.NewAuthStatusModel;
import com.Revealit.ModelClasses.RevealitHistoryModel;
import com.Revealit.ModelClasses.RewardHistoryModel;
import com.Revealit.ModelClasses.SavedProductListModel;
import com.Revealit.ModelClasses.SubmitProfileModel;
import com.Revealit.ModelClasses.UserDetailsFromPublicKeyModel;
import com.Revealit.ModelClasses.UserDetailsModel;
import com.Revealit.ModelClasses.UserRegistrationModel;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

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

    //GET MULTICOLOR GLBS
    @GET()
    Call<JsonElement> test(@Url String url);

    //GET VIDEO DETAILS LISTEN SCREEN
    @GET()
    Call<JsonElement> getVideoDetailsFromACRID(@Url String url);

    //GET REVEALIT HISTORY
    @POST(Constants.API_GET_REVEALIT_HISTORY)
    Call<RevealitHistoryModel> getRevealitHistory();

    //GET VIDEO ITEMS
    @POST(Constants.API_GET_ITEMS_FROM_ITEM_ID)
    Call<ItemListFromItemIdModel> getItemListFromItemID(@Query(Constants.MEDIA_ID_FOR_ITEMS) int mediaID,
                                                        @Query(Constants.PLAYBACK_OFFSET_FOR_ITEMS) int playbackOffset);



    //REMOVE VIDEO FROM LISTEN HISTORY
    @POST(Constants.API_REMOVE_LISTEN_HISTORY)
    Call<JsonElement> removeHistory(@Query("media_ids[1]") List<Integer> media_id);

    //REMOVE VIDEO FROM LISTEN HISTORY
    @POST(Constants.API_REMOVE_LISTEN_HISTORY)
    Call<JsonElement> removeMultipleSelectedVidios(@Query("media_ids[]") List<Integer> media_id);

    //REMOVE ALL LISTEN HISTORY
    @POST(Constants.API_REMOVE_LISTEN_HISTORY)
    Call<JsonElement> removeWholeHistory(@Query("clear") boolean clear);

    //GET USER DETAILS
    @GET()
    Call<UserDetailsModel> getUserDetails(@Url String url);


    //NEW AUTH PHONE
    @POST(Constants.API_NEW_AUTH_PHONE_VERIFY)
    Call<NewAuthStatusModel> verifyPhone(@Body JsonObject body);

    //SUBMIT PROFILE
    @POST(Constants.API_NEW_AUTH_SUBMIT_PROFILE)
    Call<SubmitProfileModel> submitProfile(@Body JsonObject body);


    //CREATE PROTON ACCOUNT
    @POST(Constants.API_NEW_AUTH_CREATE_PROTON)
    Call<JsonElement> createProtonAccount(@Body JsonObject body);

    //CREATE TOKEN
    @POST(Constants.API_NEW_AUTH_CREATE_TOKENS)
    Call<JsonElement> createTokens(@Body JsonObject body);

    //VERIFY OTP
    @POST(Constants.API_NEW_AUTH_OTP_VERIFY)
    Call<NewAuthStatusModel> verifyOTPPhone(@Body JsonObject body);

    //GET USER DETAILS
    @GET()
    Call<JsonElement> checkFileOnGoogleDrive(@Url String url);

    //GET CAMPAIGN DETAILS FOR INVITES
    @GET()
    Call<InviteModel> getCampaignDetails(@Url String url);

    //GET COUNTRY CODE LIST
    @GET()
    Call<CountryCodes> getCountryList(@Url String url);

    //CHECK IF USER ALREADY EXIST
    @GET()
    Call<CheckUserNameStatusModel> checkIfUserExist(@Url String url);

    //LOGIN API WITH RETROFIT
    @POST(Constants.API_NEW_AUTH_LOGIN)
    Call<NewAuthLogin> newAuthLogin(@Body JsonObject body);

    //LOGIN API WITH RETROFIT
    @POST(Constants.API_NEW_AUTH_CALLBACK)
    Call<NewAuthLoginCallbackModel> newAuthCallback(@Body JsonObject body);

    //GET USER DETAILS
    @GET()
    Call<JsonElement> checkIfPhoneAlreadyRegistered(@Url String url);

    //VERIFY CODE
    @POST()
    Call<JsonElement> signInCallback(@Url String url,@Body JsonObject body);

    //VERIFY CODE
    @POST(Constants.API_PUSH_AUTHORISATION)
    Call<JsonElement> pushAuthorisation(@Body JsonObject body);

    //VERIFY CODE
    @POST(Constants.API_PUSH_AUTHORISATION_CANCEL)
    Call<JsonElement> pushAuthorisationCancel(@Body JsonObject body);

    //REMOVE SINGLE TIME STAMP
    @POST(Constants.API_REMOVE_SINGLE_TIMESTAMP)
    Call<JsonElement> removeSingleTimeStamp(@Body JsonObject body);

    //NEW AUTH PHONE
    @POST(Constants.API_FETCH_ACCOUNT_NAME_FROM_PROTON)
    Call<GetProtonUsername> getProtonAccountName(@Body JsonObject body);

    //GET USER DETAILS FROM API
    @GET()
    Call<UserDetailsFromPublicKeyModel> getUserDetailsFromPubKeyAndName(@Url String url);

    //SUBMIT PROFILE
    @POST(Constants.API_NEW_AUTH_RE_SUBMIT_PROFILE)
    Call<UserDetailsFromPublicKeyModel> reSubmitProfile(@Body JsonObject body);

    //DELETE USER
    @POST(Constants.API_DELETE_USER)
    Call<JsonElement> removeUser();

    //VERIFY CODE
    @POST(Constants.API_ADD_REFARAL)
    Call<JsonElement> addReferral(@Body JsonObject body);

    //GET USER DETAILS
    @GET()
    Call<JsonElement> getUser(@Url String url);

    //GET USER SAVED PRODUCT LIST
    @GET()
    Call<SavedProductListModel> getUserSavedList(@Url String url);

    //CREATE NEW LIST
    @POST(Constants.API_CREATE_NEW_PRODUCT_LIST)
    Call<JsonElement> createProductList(@Body JsonObject body);

}


