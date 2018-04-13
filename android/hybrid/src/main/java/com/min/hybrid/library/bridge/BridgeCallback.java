package com.min.hybrid.library.bridge;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSONObject;
import com.min.hybrid.library.HybridConstants;
import com.min.hybrid.library.container.HybridWebView;
import com.min.hybrid.library.util.L;

import java.lang.ref.WeakReference;

public class BridgeCallback {

    public static final String ERROR_PORT = "3000";//非API错误回调

    public static String JS_FUNCTION = "javascript:JSBridge._handleMessageFromNative(%s);";

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    private String port;

    private WeakReference<HybridWebView> webViewRef;

    public BridgeCallback(String port, HybridWebView webView) {
        this.port = port;
        if (webView != null) {
            webViewRef = new WeakReference<>(webView);
        }
    }

    public void applySuccess() {
        apply(1, "", new JSONObject());
    }

    public void applySuccess(JSONObject result) {
        apply(1, "", result == null ? (new JSONObject()) : result);
    }

    public void applyFail(String msg) {
        apply(0, msg, new JSONObject());
    }

    private void apply(int code, String msg, JSONObject result) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        jsonObject.put("result", result);
        apply(jsonObject);
    }

    private void apply(JSONObject responseData) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("responseId", port);
        jsonObject.put("responseData", responseData);
        String execJs = String.format(JS_FUNCTION, jsonObject.toJSONString());
        callJS(execJs);
    }

    public void applyAuthError(JSONObject jsonObject) {
        postEventToJs("authError", jsonObject);
    }

    public void applyNativeError(String errorUrl, String errorDescription) {
        JSONObject data = new JSONObject();
        data.put("errorDescription", errorDescription);
        data.put("errorCode", port);
        data.put("errorUrl", errorUrl);
        postEventToJs("handleError", data);
    }

    public void postEventToJs(String handlerName, JSONObject data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("handlerName", handlerName == null ? "" : handlerName);
        jsonObject.put("data", data == null ? (new JSONObject()) : data);
        callJS(String.format(JS_FUNCTION, jsonObject.toJSONString()));
    }

    private void callJS(final String js) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                HybridWebView webView = webViewRef.get();
                if (webView != null && checkContext(webView.getContext()) && webView.getParent() != null) {
                    L.d(HybridConstants.LOG_TAG, String.format("callJs-->%s", js));
                    webView.loadUrl(js);
                }
            }
        });
    }

    private boolean checkContext(Context context) {
        if (context == null) {
            return false;
        }
        Activity activity = (Activity) context;
        return !activity.isFinishing();
    }

    public String getPort() {
        return port;
    }

}
