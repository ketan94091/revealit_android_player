package com.Revealit.CommonClasse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.Revealit.BuildConfig;
import com.Revealit.R;

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

    public static void buildDialog(Context mContext, String strMessege) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.app_name));
        builder.setMessage(strMessege);
        builder.setNegativeButton(mContext.getResources().getString(R.string.strOk), null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static void displayToast(Context mContext, String strMessege) {

        Toast.makeText(mContext, strMessege, Toast.LENGTH_SHORT).show();
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
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage(mContext.getResources().getString(R.string.strPleaseWait));
        pDialog.setCancelable(false);
        pDialog.show();
    }


    public static void closeDialog() {
        pDialog.dismiss();
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

    public static boolean isAppInstalled(Context mContext, String packageName) {
        try {
            ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public static String installedAppVersion(Context mContext) {

        String versionName = BuildConfig.VERSION_NAME;

        return "App Version : " + versionName;

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

    public static boolean isDeviceSupportAR(Activity mActivity){

     return false;

    }

}
