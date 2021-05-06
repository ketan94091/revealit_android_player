package com.Revealit.CommonClasse;

public class Constants {


    //API_END POINT
    public static String API_END_POINTS = "https://staging.api.revealit.io/";
    public static String API_END_POINTS_IVA_TEST = "https://tcapi.revealit.io/";

    //API'S
    public static final String API_AUTHENTICATION = "auth/login";
    public static final String API_PLAY_CATEGORIES = "api/watch";
    public static final String API_GET_DOTS_LOCATIONS = "api/locations/fetch/";
    public static final String API_CHECK_EMAIL_FOR_PROTON_REGISTRATION = "api/user/check";
    public static final String API_SEND_VERIFICATION_CODE = "api/user/sendverifycode";
    public static final String API_VERIFY_CODE = "api/user/verify";


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
    public static final String IS_ALLOW_BIOMETRIC = "isUserAllowBioMetric";
    public static final String IS_FIRST_LOGIN = "isFirstLogin";



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
