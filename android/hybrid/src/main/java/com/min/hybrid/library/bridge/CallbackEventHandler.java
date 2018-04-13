package com.min.hybrid.library.bridge;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.min.hybrid.library.container.HybridWebView;

import java.util.HashMap;

public class CallbackEventHandler {

    public static String OnPageResume = "OnPageResume";//页面重新可见时主动回调
    public static String OnPagePause = "OnPagePause";//页面不可见时主动回调

    public static String OnClickNbBack = "OnClickNbBack";//导航栏返回按钮
    public static String OnClickNbLeft = "OnClickNbLeft";//导航栏左侧按钮(非返回按钮)
    public static String OnClickNbTitle = "OnClickNbTitle";//导航栏标题按钮
    public static String OnClickNbRight = "OnClickNbRight";//导航栏最右侧按钮
    public static String OnClickBack = "OnClickBack";//系统返回按钮（物理返回键）

    public static String OnScanCode = "OnScanCode";//扫描二维码
    public static String OnChoosePic = "OnChoosePic";//选择或者预览图片,包括拍照、选择相册

    private HashMap<String, String> portMap = new HashMap<>();

    private HybridWebView webView;

    public CallbackEventHandler(HybridWebView webView) {
        this.webView = webView;
    }

    public void onPageResume() {
        callJS(OnPageResume, webView, null);
    }

    public void onPagePause() {
        callJS(OnPagePause, webView, null);
    }

    public void onClickNbBack() {
        callJS(OnClickNbBack, webView, null);
    }

    public void onClickBack() {
        callJS(OnClickBack, webView, null);
    }

    public void onClickNbLeft() {
        callJS(OnClickNbLeft, webView, null);
    }

    public void onClickNbTitle(int which) {
        JSONObject object = new JSONObject();
        object.put("which", which);
        callJS(OnClickNbTitle, webView, object);
    }

    public void onClickNbRight(int which) {
        JSONObject object = new JSONObject();
        object.put("which", which);
        callJS(OnClickNbRight + which, webView, object);
    }

    public void onScanCode(JSONObject object) {
        callJS(OnScanCode, webView, object);
    }

    public void onChoosePic(JSONObject object) {
        callJS(OnChoosePic, webView, object);
    }

    private void callJS(String key, HybridWebView webView, JSONObject object) {
        if (webView == null) {
            return;
        }
        String port = portMap.get(key);
        if (TextUtils.isEmpty(port)) {
            BridgeCallback callback = new BridgeCallback(BridgeCallback.ERROR_PORT, webView);
            callback.applyNativeError(webView.getUrl(), key + "未注册");
        } else {
            BridgeCallback callback = new BridgeCallback(port, webView);
            callback.applySuccess(object);
        }
    }

    public void addPort(String key, String port) {
        portMap.put(key, port);
    }

    public void removePort(String key) {
        portMap.remove(key);
    }

    public boolean hasPort(String key) {
        return portMap.containsKey(key);
    }

}
