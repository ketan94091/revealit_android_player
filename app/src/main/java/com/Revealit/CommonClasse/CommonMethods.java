package com.Revealit.CommonClasse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.Revealit.BuildConfig;
import com.Revealit.R;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonMethods {

    private static AlertDialog dialog;
    private static ProgressDialog pDialog;

   /* public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }*/
   public static void hideKeyboard(Activity activity) {
       InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
       //Find the currently focused view, so we can grab the correct window token from it.
       View view = activity.getCurrentFocus();
       //If no view currently has focus, create a new one, just so we can grab a window token from it
       if (view == null) {
           view = new View(activity);
       }
       imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
   }


    public static void buildDialog(Context mContext, String strMessege) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(mContext.getResources().getString(R.string.app_name));
                builder.setMessage(strMessege);
                builder.setNegativeButton(mContext.getResources().getString(R.string.strOk), null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }


    public static void displayToast(Context mContext, String strMessege) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, strMessege, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void displayLog(Context mContext, String strTag, String strMessege) {

        Log.e(strTag, strMessege);
    }

    public static void printLogE(String strTAG, String strMessage) {

        if (true) {
            Log.e(strTAG, strMessage);
        }

    }

    ;

    public static void showDialog(Context mContext) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                pDialog = new ProgressDialog(mContext);
                pDialog.setMessage(mContext.getResources().getString(R.string.strPleaseWait));
                pDialog.setCancelable(false);
                pDialog.show();
            }
        });


    }

    public static void showDialogWithCustomMessage(Context mContext, String strMsg) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                pDialog = new ProgressDialog(mContext);
                pDialog.setMessage(strMsg);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        });


    }


    public static void closeDialog() {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        });

    }

    public static String getDeviceToken(Activity mActivity) {
        return Settings.Secure.getString(mActivity.getContentResolver(),
                Settings.Secure.ANDROID_ID);


    }

    public static String getDateFromLong(Long lngDate) {

        long millisecond = Long.parseLong(String.valueOf(lngDate));
        // or you already have long value of date, use this instead of milliseconds variable.
        String dateString = DateFormat.format(Constants.COMMON_DATE_FORMAT, new Date(millisecond)).toString();

        return dateString;
    }

    public static Long getLongDateFromStringDate(String date) {
        long startDate = 0;
        try {
            String dateString = date;
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT);
            Date mDate1 = sdf.parse(dateString);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mDate1);
            Calendar timeInstance = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, timeInstance.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeInstance.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, timeInstance.get(Calendar.SECOND));

            //startDate = mDate1.getTime();

            startDate = calendar.getTime().getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return startDate;
    }

    public static boolean isValidLicenseNo(String str) {
        String regex
                = "^(([A-Z]{2}[0-9]{2})"
                + "( )|([A-Z]{2}-[0-9]"
                + "{2}))((19|20)[0-9]"
                + "[0-9])[0-9]{7}$";

        Pattern p
                = Pattern.compile(regex);

        if (str == null) {
            return false;
        }


        Matcher m = p.matcher(str);

        return m.matches();
    }

    public static String getStringDays(int day) {
        String strDay = "";
        if (day < 10) {
            strDay += "0" + day;
        } else {
            strDay += day;
        }
        return strDay;
    }

    public static String getStringMonths(int intMonth) {

        String strMonth = "Jan";

        switch (intMonth) {

            case 1:
                strMonth = "Jan";
                break;
            case 2:
                strMonth = "Feb";
                break;
            case 3:
                strMonth = "March";
                break;
            case 4:
                strMonth = "Apr";
                break;
            case 5:
                strMonth = "May";
                break;
            case 6:
                strMonth = "Jun";
                break;
            case 7:
                strMonth = "Jul";
                break;
            case 8:
                strMonth = "Aug";
                break;
            case 9:
                strMonth = "Sep";
                break;
            case 10:
                strMonth = "Oct";
                break;
            case 11:
                strMonth = "Nov";
                break;
            case 12:
                strMonth = "Dec";
                break;

        }


        return strMonth;

    }

    //---------(Convert Long Date to Date formate mm/dd/yyyy)--------//
    public static String convertLongDateToMMDDYYYY(Long convertDate) {
        Date date = new Date(convertDate);
        SimpleDateFormat df2 = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT);
        String dateText = df2.format(date);

        return dateText;
    }

    public static boolean isIdenticalConsequtive(String strPassword) {

        String regex = "\\b([a-zA-Z0-9])\\1\\1+\\b";

        Pattern p = Pattern.compile(regex);
        if (strPassword == null) {
            return false;
        }

        Matcher m = p.matcher(strPassword);

        return m.matches();
    }

    public static String timeDifference(Long lngTimeone) {

        String strTimeString = "0 Seconds ago";

        long diff = lngTimeone - System.currentTimeMillis();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        Log.e("Time : ", "HOUR :" + hours + "MINUTES :" + minutes + "SECOND :" + seconds);

        if (hours != 0) {
            strTimeString = hours + " Hours ago";
            return strTimeString.replace("-", "");
        } else if (minutes != 0) {
            strTimeString = minutes + " Minutes ago";
            return strTimeString.replace("-", "");
        } else if (seconds != 0) {
            strTimeString = seconds + " Seconds ago";
            return strTimeString.replace("-", "");
        } else {
            return strTimeString;
        }

    }

//    public static boolean isAppInstalled(Context mContext, String packageName) {
//        try {
//            ApplicationInfo pckInfo = mContext.getPackageManager().getApplicationInfo(packageName, 0);
//            if (pckInfo != null)
//                return true;
//            return true;
//        } catch (PackageManager.NameNotFoundException e) {
//            return false;
//        }
//    }
    public static boolean isAppInstalled(Context mContext, String targetPackage) {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }


    public static String installedAppVersion(Context mContext) {

        String versionName = BuildConfig.VERSION_NAME;

        return "" + versionName;

    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        if (inImage != null) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
        } else {

            return null;
        }

    }

    public static boolean isDeviceSupportAR(Activity mActivity) {

        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(mActivity);

        switch (availability) {
            case UNSUPPORTED_DEVICE_NOT_CAPABLE:

                displayToast(mActivity, "The device is not supported this feature");

                return false;
            case SUPPORTED_NOT_INSTALLED:

                return isUpdatedARserviceInstalled(mActivity);

            case SUPPORTED_INSTALLED:

                return true;

        }
        return false;
    }

    public static boolean isUpdatedARserviceInstalled(Activity mActivity) {

        boolean mUserRequestedInstall = true;


        try {

            switch (ArCoreApk.getInstance().requestInstall(mActivity, mUserRequestedInstall)) {
                case INSTALLED:
                    // Success: Safe to create the AR session.

                    return true;
                case INSTALL_REQUESTED:
                    // When this method returns `INSTALL_REQUESTED`:
                    // 1. ARCore pauses this activity.
                    // 2. ARCore prompts the user to install or update Google Play
                    //    Services for AR (market://details?id=com.google.ar.core).
                    // 3. ARCore downloads the latest device profile data.
                    // 4. ARCore resumes this activity. The next invocation of
                    //    requestInstall() will either return `INSTALLED` or throw an
                    //    exception if the installation or update did not succeed.

                    mUserRequestedInstall = false;
                    return false;
            }

        } catch (UnavailableUserDeclinedInstallationException e) {
            displayToast(mActivity, "User decline installation of Google Play Service!");
            return false;
        } catch (UnavailableDeviceNotCompatibleException e) {
            displayToast(mActivity, "The device is not supported this feature!xxx");
            e.printStackTrace();
            return false;
        }

        return true;


    }

    public static GradientDrawable drawCircle(int backgroundColor, int borderColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        shape.setColor(backgroundColor);
        shape.setStroke(1, borderColor);
        return shape;
    }

    public static String getHtMLdataForARmodelViewer(String strURL) {


    //strURL ="https://mturk.sgp1.cdn.digitaloceanspaces.com/0/2a.KitchenAid_StandMixer_CARED_680569095664.glb";
     // strURL ="https://revtesting.sgp1.digitaloceanspaces.com/iar_models/8/2_KitchenAid_Mixer_Pearl_2e1f5adc2bed7e19d1158be8519fddac.glb";


        String stringHtmlforAR = "<html>\n" +
                "    <head>\n" +
                "        <title>&lt;model-viewer&gt; Augmented Reality</title>\n" +
                "        <meta charset=\"utf-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, user-scalable=no,\n" +
                "        minimum-scale=1.0, maximum-scale=1.0\">\n" +
                "        \n" +
                "      \n" +
                "        <!-- \uD83D\uDC81 Include both scripts below to support all browsers! -->\n" +
                "      \n" +
                "        <!-- Loads <model-viewer> for modern browsers: -->\n" +
                "        <script type=\"module\" src=\"https://unpkg.com/@google/model-viewer/dist/model-viewer.js\">\n" +
                "        </script>\n" +
                "      \n" +
                "        <!-- Loads <model-viewer> for old browsers like IE11: -->\n" +
                "        <script nomodule=\"\" src=\"https://unpkg.com/@google/model-viewer/dist/model-viewer-legacy.js\">\n" +
                "        </script>\n" +
                "      \n" +
                "        <!-- The following libraries and polyfills are recommended to maximize browser support -->\n" +
                "        <!-- NOTE: you must adjust the paths as appropriate for your project -->\n" +
                "            \n" +
                "        <!-- \uD83D\uDEA8 REQUIRED: Web Components polyfill to support Edge and Firefox < 63 -->\n" +
                "        <script src=\"https://unpkg.com/@webcomponents/webcomponentsjs@2.1.3/webcomponents-loader.js\"></script>\n" +
                "      \n" +
                "        <!-- \uD83D\uDC81 OPTIONAL: Intersection Observer polyfill for better performance in Safari and IE11 -->\n" +
                "        <script src=\"https://unpkg.com/intersection-observer@0.5.1/intersection-observer.js\"></script>\n" +
                "      \n" +
                "        <!-- \uD83D\uDC81 OPTIONAL: Resize Observer polyfill improves resize behavior in non-Chrome browsers -->\n" +
                "        <script src=\"https://unpkg.com/resize-observer-polyfill@1.5.1/dist/ResizeObserver.js\"></script>\n" +
                "      \n" +
                "        <!-- \uD83D\uDC81 OPTIONAL: Fullscreen polyfill is required for experimental AR features in Canary -->\n" +
                "        <!--<script src=\"https://unpkg.com/fullscreen-polyfill@1.0.2/dist/fullscreen.polyfill.js\"></script>-->\n" +
                "      \n" +
                "        <!-- \uD83D\uDC81 OPTIONAL: Include prismatic.js for Magic Leap support -->\n" +
                "        <!--<script src=\"https://unpkg.com/@magicleap/prismatic@0.18.2/prismatic.min.js\"></script>-->\n" +
                "      <style type=\"text/css\">\n" +
                "        body {\n" +
                "  margin: 1em;\n" +
                "  padding: 0;\n" +
                "  font-family: Google Sans, Noto, Roboto, Helvetica Neue, sans-serif;\n" +
                "  color: #244376;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "#card {\n" +
                "  margin: 3em auto;\n" +
                "  display: flex;\n" +
                "  flex-direction: column;\n" +
                "  max-width: 600px;\n" +
                "  border-radius: 6px;\n" +
                "  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.25);\n" +
                "  overflow: hidden;\n" +
                "}\n" +
                "\n" +
                "model-viewer {\n" +
                "  width: 100%;\n" +
                "  height: 450px;\n" +
                "}\n" +
                "\n" +
                ".attribution {\n" +
                "  display: flex;\n" +
                "  flex-direction: row;\n" +
                "  margin: 1em;\n" +
                "}\n" +
                "\n" +
                ".attribution h1 {\n" +
                "  margin: 0 0 0.25em;\n" +
                "}\n" +
                "\n" +
                ".attribution img {\n" +
                "  opacity: 0.5;\n" +
                "  height: 2em;\n" +
                "}\n" +
                "\n" +
                ".attribution .cc {\n" +
                "  flex-shrink: 0;\n" +
                "  text-decoration: none;\n" +
                "}\n" +
                "\n" +
                "footer {\n" +
                "  display: flex;\n" +
                "  flex-direction: column;\n" +
                "  max-width: 600px;\n" +
                "  margin: auto;\n" +
                "  text-align: center;\n" +
                "  font-style: italic;\n" +
                "  line-height: 1.5em;\n" +
                "}\n" +
                "      </style>\n" +
                "      </head>\n" +
                "    <body>\n" +
                "        <model-viewer src=\"" + strURL + "\"+  auto-rotate=\"\" camera-controls=\"\" shadow-intensity=\"1\" alt=\"A 3D model of a rocket\" background-color=\"#70BCD1\" ar-status=\"not-presenting\">\n" +
                "                        \n" +
                "        </model-viewer>\n" +
                "      \n" +
                "      \n" +
                "    </body>\n" +
                "</html>\n";


        return stringHtmlforAR;

    }

}
