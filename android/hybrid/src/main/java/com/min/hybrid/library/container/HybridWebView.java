package com.min.hybrid.library.container;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.min.hybrid.library.BuildConfig;

public class HybridWebView extends WebView {

    public HybridWebView(Context context) {
        super(context);
        settingWebView();
    }

    public HybridWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        settingWebView();
    }

    public HybridWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        settingWebView();
    }

    public void settingWebView() {
        WebSettings settings = getSettings();
        String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua + " QuickHybridJs/" + BuildConfig.VERSION_NAME);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        String appCachePath = getContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }
    }

}
