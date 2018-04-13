package com.min.hybrid.library.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.min.hybrid.library.HybridConstants;

public class SharePreferenceUtil {

    public static String getPageDevHostUrl(Context context) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HybridConstants
                    .SP.NATIVE_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(HybridConstants.SP.PAGE_DEV_HOST_URL, null);
        }
        return null;
    }


    public static void setPageDevHostUrl(Context context, String pageUrl) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HybridConstants
                    .SP.NATIVE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(HybridConstants.SP.PAGE_DEV_HOST_URL, pageUrl).apply();
        }
    }

    public static String getVersion(Context context) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HybridConstants
                    .SP.NATIVE_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(HybridConstants.SP.VERSION, null);
        }
        return null;
    }


    public static void setVersion(Context context, String version) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HybridConstants
                    .SP.NATIVE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(HybridConstants.SP.VERSION, version).apply();
        }
    }


    public static void setDownLoadVersion(Context context, String version) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HybridConstants
                    .SP.NATIVE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(HybridConstants.SP.DOWNLOAD_VERSION, version).apply();
        }
    }

    public static String getDownLoadVersion(Context context) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HybridConstants
                    .SP.NATIVE_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(HybridConstants.SP.DOWNLOAD_VERSION, null);
        }
        return null;
    }


    public static boolean getInterceptorActive(Context context) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HybridConstants.SP.NATIVE_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(HybridConstants.SP.INTERCEPTOR_ACTIVE, true);
        }
        return false;
    }

    public static void setInterceptorActive(Context context, boolean active) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HybridConstants.SP.NATIVE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(HybridConstants.SP.INTERCEPTOR_ACTIVE, active).apply();
        }
    }

}
