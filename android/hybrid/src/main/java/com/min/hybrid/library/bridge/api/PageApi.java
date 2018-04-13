package com.min.hybrid.library.bridge.api;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.min.hybrid.library.HybridConstants;
import com.min.hybrid.library.R;
import com.min.hybrid.library.bean.ContainerBean;
import com.min.hybrid.library.bean.HybridEvent;
import com.min.hybrid.library.bridge.BridgeCallback;
import com.min.hybrid.library.bridge.CallbackEventHandler;
import com.min.hybrid.library.bridge.IBridgeImpl;
import com.min.hybrid.library.container.WebViewContainerActivity;
import com.min.hybrid.library.container.WebViewContainerFragment;
import com.min.hybrid.library.util.EventUtil;
import com.min.hybrid.library.util.HybridUtil;

public class PageApi implements IBridgeImpl {

    public static final String RegisterName = "page";

    /**
     * 同步activity pageResume事件
     */
    public static void pageResume(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        container.getCallbackEventHandler().addPort(CallbackEventHandler.OnPageResume, callback.getPort());
    }

    /**
     * 同步activity pagePause事件
     */
    public static void pagePause(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        container.getCallbackEventHandler().addPort(CallbackEventHandler.OnPagePause, callback.getPort());
    }

    /**
     * 打开新的H5页面
     */
    public static void open(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        ContainerBean bean = JSON.parseObject(param.toJSONString(), ContainerBean.class);
        Intent intent = new Intent();
        intent.setClass(container.getContext(), WebViewContainerActivity.class);
        if (bean == null) {
            callback.applyFail(container.getContext().getString(R.string.status_request_error));
        } else {
            intent.putExtra(HybridConstants.KEY_ACTIVITY_DATA, bean);
            intent.putExtra(HybridConstants.BridgeApi.SCREEN_ORIENTATION, bean.orientation);
            container.getContext().startActivity(intent);
        }
    }

    /**
     * 打开原生页面
     */
    public static void openLocal(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        try {
            String activityName = param.getString("className");
            String jsonStr = param.getString("data");
            Class clz = Class.forName(activityName);
            Intent intent = new Intent(container.getContext(), clz);
            intent.putExtra("from", "quick");
            if (!TextUtils.isEmpty(jsonStr)) {
                if (jsonStr.startsWith("[")) {
                    HybridUtil.putIntentExtra(param.getJSONArray("data"), intent);
                } else if (jsonStr.startsWith("{")) {
                    HybridUtil.putIntentExtra(param.getJSONObject("data"), intent);
                }
            }
            container.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            callback.applyFail(e.toString());
        }
    }

    /**
     * 关闭当前Activity
     */
    public static void close(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        container.finish();
    }

    /**
     * 重载页面
     */
    public static void reload(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        container.getWebView().reload();
        callback.applySuccess();
    }

    /**
     * 发送通知
     */
    public static void postEvent(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        HybridEvent event = new HybridEvent();
        event.data = param.getJSONObject("data");
        event.type = param.getString("type");
        EventUtil.post(event);
        callback.applySuccess();
    }

}
