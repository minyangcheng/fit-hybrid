package com.min.hybrid.library.container;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.min.hybrid.library.util.HybridUtil;

public class HybridWebViewClient extends WebViewClient {

    private WebViewContainerFragment mContainer;

    public HybridWebViewClient(WebViewContainerFragment container) {
        this.mContainer = container;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, final String description, final String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        HybridUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContainer.handleError(description, failingUrl);
            }
        });
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mContainer.handleFinish(url);
    }
}
