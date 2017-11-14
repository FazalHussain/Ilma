package com.ilma.testing.ilmaapp.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by asif on 2/24/2016.
 */
public class Utils {
    static Context context;
    public static final String TAG = "Utils";
    public static final String UserName = "UserName";
    public SharedPreferences preferences;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    SharedPreferences.Editor editor;
    public Utils(Context context) {

        Utils.context = context;
        preferences = context.getSharedPreferences("gcmdemo", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void savedata(String key, String val){
        editor.putString(key,val).commit();
    }

    public String getdata(String key){
        String value = preferences.getString(key, "");
        if (value.isEmpty()) {
            Log.i(TAG, key + " not found.");
            return "";
        }
        return value;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()){
            return true;
        }
        return false;
    }

    private static String LIST_SEPARATOR = ",";

    public static String convertListToString(Integer[] intList) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Integer str : intList) {
            stringBuffer.append(String.valueOf(str)).append(LIST_SEPARATOR);
        }

        // Remove last separator
        int lastIndex = stringBuffer.lastIndexOf(LIST_SEPARATOR);
        stringBuffer.delete(lastIndex, lastIndex + LIST_SEPARATOR.length() + 1);

        return stringBuffer.toString();
    }

    public static List<String> convertStringToList(String str) {
        return Arrays.asList(String.valueOf(str).split(LIST_SEPARATOR));
    }


}
