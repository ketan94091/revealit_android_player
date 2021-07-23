package com.Revealit.CommonClasse;

public class Constants {


  //API_END POINT STAGING GROUP(ZACH & GARRY) //S CURATOR
    /*public static String API_END_POINTS_MOBILE = "https://staging.api.revealit.io/";
    public static String API_END_POINTS_REGISTRATION = "https://scapi.revealit.io/";*/

      //API_END POINT ALPHA GROUP(RIDDHI) //T CURATOR
    public static String API_END_POINTS_MOBILE = "https://alpha.revealit.io/";
    public static String API_END_POINTS_REGISTRATION = "https://tcapi.revealit.io/";


    //API_END POINT BETA GROUP(JOHN) // B CURATOR
   /* public static String API_END_POINTS_MOBILE = "https://beta.revealit.io";
    public static String API_END_POINTS_REGISTRATION = "https://bcapi.revealit.io/";*/



  //END POINTS FOR SELECTIONS
  //KEY
  public static final String API_END_POINTS_MOBILE_KEY = "endPointMobile";
  public static final String API_END_POINTS_REGISTRATION_KEY = "endPointRegistration";


  public static final String API_END_POINTS_MOBILE_S_CURATOR = "https://staging.api.revealit.io/";
  public static final String API_END_POINTS_REGISTRATION_S_CURATOR = "https://scapi.revealit.io/";

  //API_END POINT ALPHA GROUP(RIDDHI) //T CURATOR
  public static final String API_END_POINTS_MOBILE_T_CURATOR = "https://alpha.revealit.io/";
  public static final String API_END_POINTS_REGISTRATION_T_CURATOR = "https://tcapi.revealit.io/";


//API_END POINT BETA GROUP(JOHN) // B CURATOR
    public static final String API_END_POINTS_MOBILE_B_CURATOR = "https://beta.revealit.io";
    public static final String API_END_POINTS_REGISTRATION_B_CURATOR = "https://bcapi.revealit.io/";


  //API'S
    public static final String API_AUTHENTICATION = "auth/login";
    public static final String API_PLAY_CATEGORIES = "api/watch";
    public static final String API_GET_DOTS_LOCATIONS = "api/locations/fetch/";
    public static final String API_CHECK_EMAIL_FOR_PROTON_REGISTRATION = "api/user/check";
    public static final String API_SEND_VERIFICATION_CODE = "api/user/sendverifycode";
    public static final String API_VERIFY_CODE = "api/user/verify";
    public static final String API_USER_REGISTRAION_TO_PROTON_AND_REVEALIT = "api/user/register";
    public static final String API_GET_USER_ACCOUNT_DETAILS = "/api/proton/get_account";
    public static final String API_GET_REWARD_HISTORY = "/api/rewards/history";
    public static final String API_GET_MORE_REWARD_HISTORY = "/api/rewards/history";
    public static final String API_GET_PRODUCT_DETAILS = "api/items/fetch/";
    public static final String API_GET_RECIPES_DETAILS = "/api/videos/";
    public static final String API_GET_INFLUENCERS_DETAILS = "/api/video/";
    public static final String API_GENERATE_REWARD_DATA = "api/rewards";
    public static final String API_GET_MULTICOLOR_GLBS = "api/ar/glb/";


    public static final String AUTH_TOKEN = "access_token";
    public static final String AUTH_TOKEN_TYPE = "token_type";
    public static final String AUTH_TOKEN_EXPIRES_IN = "expires_in";
    public static final String USERNAME = "user";
    public static final String USER_FIRST_NAME = "first_name";
    public static final String USER_PROFILE_PICTURE = "profile_image";
    public static final String USER_FULL_NAME = "name";
    public static final String USER_USER_ID = "usid";
    public static final String USER_LOGGED_IN = "isUserLoggedIN";
    public static final String AUTH_USERNAME = "email";
    public static final String AUTH_PASSWORD = "password";
    public static final String MEDIA_URL = "mediaURL";
    public static final String MEDIA_ID = "mediaID";
    public static final String PARAM_TIME_FOR_DOTS_LOCATIONS = "time";
    public static final String VIDEO_NAME = "videoShowTitle";
    public static final String PROTON_EMAIL = "email";
    public static final String PROTON_PASSWORD = "password";
    public static final String PROTON_VERIFICATION_CODE = "verify_code";
    public static final String PROTON_USERNAME = "friendly_name";
    public static final String IS_ALLOW_BIOMETRIC = "isUserAllowBioMetric";
    public static final String IS_FIRST_LOGIN = "isFirstLogin";
    public static final String PROTON_ACCOUNT_NAME = "protonAccountNAme";
    public static final String IS_FIRST_TIME_ACCOUNT_SYNC = "isFirstTimeAccountSync";
    public static final String ACCOUNT_BALANCE = "accountBalance";
    public static final String ACCOUNT_CURRENCY_TYPE = "CurrencyType";
    public static final String RESEARCH_URL = "researchURL";
    public static final String RESEARCH_URL_SPONSER = "researchURLSponser";
    public static final String READ_WRITE_PERMISSION = "readWritePermission";
    public static final String AR_VIEW_URL = "arViewURL";
    public static final String AR_VIEW_MODEL_NAME = "arViewModelURL";
    public static final String AR_VIEW_MODEL_URL = "arViewModelURL";
    public static final String AR_MODEL_ID = "arModelID";
    public static final String SAVED_IMAGE_FILE_NAME = "savedImageFileName";
    public static final String AR_MODEL_URL = "arModelURL";
    public static final String REWARD_TYPE = "type";
    public static final String REWARD_TYPE_ID = "id";
    public static final String REWARD_TYPE_PAUSE = "REWARD_VIDEO_PAUSED";
    public static final String REWARD_TYPE_INTERECTION_OR_CLICK = "REWARD_DOT_CLICK";
    public static final String REWARD_TYPE_SWIPE = "REWARD_DOT_SWIPE";
    public static final String LOGIN_USENAME = "loggedInUserName";
    public static final String LOGIN_PASSWORD = "loggedInPassword";
    public static final String TESTING_ENVIRONMENT_ID = "EnvironmentID";
    public static final String IS_APP_OPEN_FIRST_TIME = "isAppOpenFirstTime";



    public static String API_KEY = "AIzaSyC4elGdU9Oxi2DPIShG4pRW2gRBZOeE0pM";
    public static String VIDEO_VIEW = "videoView";


    public static final String COMMON_TIME_FORMAT = "HH:mm a";
    public static final String COMMON_DATE_TIME_FORMAT = "dd/MM/yyyy" + COMMON_TIME_FORMAT;
    public static final String COMMON_DATE_FORMAT = "dd/MM/yyyy";
    public static final String EMAIL_VALIDATION_REGEX = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String PASSWORD_VALIDATION_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?!.*__).{6,}$";


    //CONSTANT DATA WHICH SHOULD NOT CHANGE
    public static final int API_SUCCESS = 200;
    public static final int API_USER_UNAUTHORIZED = 401;


}
