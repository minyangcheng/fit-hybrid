package com.min.hybrid.library.bridge.api;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.min.hybrid.library.BuildConfig;
import com.min.hybrid.library.R;
import com.min.hybrid.library.bridge.BridgeCallback;
import com.min.hybrid.library.bridge.IBridgeImpl;
import com.min.hybrid.library.container.WebViewContainerFragment;
import com.min.hybrid.library.util.FileUtil;
import com.min.hybrid.library.util.RuntimeUtil;

import java.io.File;

public class RuntimeApi implements IBridgeImpl {

    public static final String RegisterName = "runtime";

    /**
     * 打开第三方app
     * packageName ：applicationId
     * className   ：指定打开的页面类名，可为空
     * actionName  ：manifest中activity设置的actionname
     * scheme      ：manifest中activity设置的scheme
     * data        ：传给页面的参数
     */
    public static void launchApp(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String packageName = param.getString("packageName");
        String className = param.getString("className");
        String actionName = param.getString("actionName");
        String scheme = param.getString("scheme");
        String data = param.getString("data");
        try {
            Intent intent = null;
            if (!TextUtils.isEmpty(packageName)) {
                intent = RuntimeUtil.getLaunchAppIntent(container.getContext(), packageName, className);
            } else if (!TextUtils.isEmpty(actionName)) {
                intent = new Intent(actionName);
            } else if (!TextUtils.isEmpty(scheme)) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme + "://"));
            }
            if (intent != null) {
                if (!TextUtils.isEmpty(data)) {
                    intent.putExtra("data", data);
                }
                container.getContext().startActivity(intent);
                callback.applySuccess();
            } else {
                callback.applyFail(container.getContext().getString(R.string.status_request_error));
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.applyFail(e.getMessage());
        }
    }

    /**
     * 获取APP版本号
     * version
     */
    public static void getAppVersion(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        JSONObject data = new JSONObject();
        data.put("version", RuntimeUtil.getVersionName(container.getContext()));
        callback.applySuccess(data);
    }

    /**
     * 获取Quick版本号
     * version
     */
    public static void getQuickVersion(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        JSONObject data = new JSONObject();
        data.put("version", BuildConfig.VERSION_NAME);
        callback.applySuccess(data);
    }

    /**
     * 清空webview缓存
     */
    public static void clearCache(final WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        container.getWebView().clearHistory();
        container.getWebView().clearCache(true);
        container.getWebView().clearFormData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtil.deleteFile(new File(container.getContext().getCacheDir().getAbsolutePath()));
                container.getContext().deleteDatabase("webview.db");
                container.getContext().deleteDatabase("webviewCache.db");
                callback.applySuccess();
            }
        }).start();
    }

    /**
     * 复制到剪贴板
     * text：复制信息
     */
    public static void clipboard(final WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String text = param.getString("text");
        RuntimeUtil.clipboard(container.getContext(), text);
        callback.applySuccess();
    }

    /**
     * 外部浏览器打开网页
     * url：页面地址
     */
    public static void openUrl(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String url = param.getString("url");
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            container.getContext().startActivity(intent);
        } else {
            callback.applyFail(container.getContext().getString(R.string.status_request_error));
        }
    }

}
