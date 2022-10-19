package com.Revealit.CommonClasse;

public class Constants {


    //B CURATOR
    public static String API_END_POINTS_MOBILE = "https://beta.revealit.io";
    public static String API_END_POINTS_REGISTRATION = "https://bcapi.revealit.io/";


  //END POINTS FOR SELECTIONS
  //KEY
  public static final String API_END_POINTS_MOBILE_KEY = "endPointMobile";
  public static final String API_END_POINTS_REGISTRATION_KEY = "endPointRegistration";
  public static final String API_END_POINTS_SERVER_NAME = "endPointServerName";

  //BETA CURATOR
  public static final String API_END_POINTS_MOBILE_B_CURATOR = "https://beta.revealit.io";
  public static final String API_END_POINTS_REGISTRATION_B_CURATOR = "https://bcapi.revealit.io/";

  //STAGING CURATOR
  public static final String API_END_POINTS_MOBILE_S_CURATOR = "https://staging.revealit.io/";
  public static final String API_END_POINTS_REGISTRATION_S_CURATOR = "https://scapi.revealit.io/";

  //TESTING1  CURATOR
  public static final String API_END_POINTS_MOBILE_T1_CURATOR = "https://testing1.revealit.io/";
  public static final String API_END_POINTS_REGISTRATION_T1_CURATOR = "https://tcapi.revealit.io/";

  //TESTING2  CURATOR
  public static final String API_END_POINTS_MOBILE_T2_CURATOR = "https://testing2.revealit.io/";
  public static final String API_END_POINTS_REGISTRATION_T2_CURATOR = "https://tcapi2.revealit.io/";

  //INTEGRATION  CURATOR
  public static final String API_END_POINTS_MOBILE_INTEGRATION_CURATOR = "https://integration.revealit.io/";
  public static final String API_END_POINTS_REGISTRATION_INTEGRATION_CURATOR = "https://icapi.revealit.io/";

  //PROTON SDK APIS
  //MAIN API
  public static  final String API_PROTON_MAIN_NET ="https://api.protonchain.com";
  //TEST NET API
  public static final  String API_PROTON_TEST_NET ="https://api-dev.protonchain.com";





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
    public static final String API_GET_VIDEO_DETAILS_LISTEN_SCREEN_FROM_ACRID = "api/video/acrid/";
  public static final String API_GET_REVEALIT_HISTORY = "api/video/matchhistory";
  public  static  final  String API_GET_ITEMS_FROM_ITEM_ID ="api/video/matchhistory/items";
  public  static  final  String API_REMOVE_LISTEN_HISTORY ="api/video/matchhistory/remove";
  public  static  final  String API_GET_USER_DETAILS ="user";
  public static final String API_NEW_AUTH_PHONE_VERIFY = "/api/challenge";
  public static final String API_NEW_AUTH_OTP_VERIFY = "api/otpcode";
  public static final String API_NEW_AUTH_CREATE_PROTON = "api/proton/create_account";
  public static final String API_NEW_AUTH_CREATE_TOKENS = "api/callback";
  public static final String API_NEW_AUTH_PUSHER_API_USERID_REGISTRATION = "api/pusher_callback";
  public static final String API_NEW_AUTH_INVITE_SETTINGS = "api/invitesetting";
  public static final String API_NEW_AUTH_USERNAME_EXIST = "api/usernotexists";
  public static final String API_NEW_AUTH_SUBMIT_PROFILE = "api/submitprofile";
  public static final String API_NEW_AUTH_LOGIN = "api/authlogin";
  public static final String API_NEW_AUTH_CALLBACK = "/api/callback";


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
    public  static  final  String VIDEO_SEEK_TO ="videoSeekTo";
  public  static  final  String IS_VIDEO_SEEK ="isVideoSeek";
    public static final String PROTON_EMAIL = "email";
    public static final String PROTON_PASSWORD = "password";
    public static final String PROTON_VERIFICATION_CODE = "verify_code";
    public static final String PROTON_USERNAME = "friendly_name";
    public static final String IS_ALLOW_BIOMETRIC = "isUserAllowBioMetric";
    public static final String IS_FIRST_LOGIN = "isFirstLogin";
    public static final String PROTON_ACCOUNT_NAME = "protonAccountNAme";
    public  static  final String KEY_USERNAME ="userName";
    public  static  final String KEY_IS_USER_ACTIVE ="keyIsUserActive";
    public  static  final String KEY_IS_USER_IS_ADMIN ="keyIsUserIsAdmin";
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
  public static final String VIDEO_OFFSET_MS = "videoOffsetMS";
  public  static  final  String SOMETHING_WENT_WRONG= "Something went wrong. Please try after sometimes!";
  public  static  final  String MEDIA_ID_FOR_ITEMS ="media_id";
  public  static  final  String PLAYBACK_OFFSET_FOR_ITEMS ="playback_offset";
  public  static  final  String APPLICATION_NAME ="Revealit";
  public  static  final  String LOGOUT_FROM_APP ="Logout from Revealit?";
  public  static  final String YES ="YES";
  public  static  final  String NO ="NO";
  public  static  final  String OK ="OK";
  public  static  final  String ACCOUNT_NOT_FOUND ="Wallet was not available";
  public  static  final  String   SAVED_AUDIOFILE_NAME ="/savedAudio.3gp";
  public  static  final  String AUDIO_CAN_NOT_RECORD ="Oops!! Something went wrong please try after sometimes.";
  public  static  final  String AUDIO_NOT_RECOGNISED ="Video not recognized!";
  public  static  final  String MICROPHONE_CANNOT_WORK ="Can not record audio!";

  //ACR CLOUD SDK
  public  static  final  String KEY_ACCESS_KEY ="access_key";
  public  static  final  String KEY_ACCESS_SECRET ="access_secret";
  public  static  final  String KEY_HOST ="host";
  public  static  final  String KEY_TIMEOUT ="timeout";
  public  static  final  String KEY_ACCESS_KEY_VALUE ="b2583183181f895ad9b2989464cb31bf";
  public  static  final  String KEY_ACCESS_SECRET_VALUE ="OSBePAVIanAT6IsfRGRKVYbcbR5HPAnOevrbJ1rs";
  public  static  final  String KEY_HOST_VALUE ="identify-ap-southeast-1.acrcloud.com";
  public  static  final  int KEY_TIMEOUT_VALUE =30;
  public  static  final  String KEY_APP_MODE ="appMode";
  public  static  final  String KEY_PRIVATE_KEY ="keyPrivate";
  public  static  final  String KEY_PUBLIC_KEY ="keyPublic";
  public  static  final  String KEY_PROTON_ACCOUNTNAME ="keyProtonAccountName";
  public  static  final  String KEY_MNEMONICS ="keyMnemonics";
  public  static  final  String KEY_MOBILE_NUMBER ="keyMobileNumber";
  public  static  final  String KEY_COUNTRY_CODE ="keyCountryCode";
  public  static  final  String KEY_CAMPAIGNID ="keyCampaignId";
  public  static  final  String KEY_REFFERALID ="keyRefferalId";
  public  static  final  String KEY_NAMEOFINVITE ="keyNameOfInvite";
  public  static  final  String KEY_NEW_AUTH_USERNAME ="keyNewAuthUsername";
  public  static  final  String KEY_INVITE_MSG ="keyInviteMsg";
  public  static  final  String KEY_IS_FROM_REGISTRATION_SCREEN ="keyIsFromRegistrationScreen";
  public  static  final  String KEY_IS_GOOGLE_DRIVE_BACKUP_DONE ="keyIsGoogleDriveBackupDone";
  public  static  final  String KEY_USER_DATA ="keyUserData";
  public  static  final  String GOOGLE_DRIVE_FOLDER_NAME="Revealit.tv.io";
  public  static  final  String KEY_PRON_WALLET_DETAILS="keyProtonWalletDetails";
  public  static  final  String KEY_REVEALIT_PRIVATE_KEY="keyRevealitPrivatekey";
  public  static  final  String KEY_ISFROM_LOGIN="keyIsFromLogin";
  public  static  final  String KEY_ISFROM_LOGOUT="keyIsUserIsFromLogout";
  public  static  final  String KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_VERSION="keyMinimumAcceptableVersion";
  public  static  final  String KEY_PUBLIC_SETTING_MINIMUM_ACCEPTABLE_API_VERSION="keyMinimumAcceptableApiVersion";
  public  static  final  String KEY_PUBLIC_SETTING_MINIMUM_PROFILE_REMINDER="keyProfileReminder";
  public  static  final  String KEY_PUBLIC_SETTING_BACKUP_REMINDER="keyBackupReminder";


    public static String API_KEY = "AIzaSyC4elGdU9Oxi2DPIShG4pRW2gRBZOeE0pM";
    public static String VIDEO_VIEW = "videoView";
    public static String PUSHER_INSTANCE_ID = "b8896e62-3c00-43b7-adfd-59960ee48a1d";




    public static final String COMMON_TIME_FORMAT = "HH:mm a";
    public static final String COMMON_DATE_TIME_FORMAT = "dd/MM/yyyy" + COMMON_TIME_FORMAT;
    public static final String COMMON_DATE_FORMAT = "dd/MM/yyyy";
    public static final String EMAIL_VALIDATION_REGEX = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String PASSWORD_VALIDATION_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?!.*__).{6,}$";


    //CONSTANT DATA WHICH SHOULD NOT CHANGE
    public static final int API_SUCCESS = 200;
  public static final int API_ELEMENT_CREATED = 201;
    public static final int API_USER_UNAUTHORIZED = 401;
    public static final int API_CODE_NOTFOUND = 404;


}
