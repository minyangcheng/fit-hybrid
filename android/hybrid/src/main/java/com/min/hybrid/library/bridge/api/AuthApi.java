package com.min.hybrid.library.bridge.api;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.min.hybrid.library.bridge.BridgeCallback;
import com.min.hybrid.library.bridge.IBridgeImpl;
import com.min.hybrid.library.container.WebViewContainerFragment;
import com.min.hybrid.library.util.AssetsUtil;

import java.io.IOException;

public class AuthApi implements IBridgeImpl {

    public static final String RegisterName = "auth";

    public static void getToken(WebViewContainerFragment container, JSONObject param, BridgeCallback callback) {
        JSONObject data = new JSONObject();
        data.put("access_token", "123456");
        callback.applySuccess(data);
    }

    public static void config(final WebViewContainerFragment container, final JSONObject param, final BridgeCallback callback) {
        try {
            JSONArray apiJsonArray = param.getJSONArray("jsApiList");
            String moduleJsonStr = AssetsUtil.getFromAssets(container.getContext(), "module.json");
            if (!TextUtils.isEmpty(moduleJsonStr)) {
                JSONObject moduleJsonObj = JSON.parseObject(moduleJsonStr);
                for (int i = 0; i < apiJsonArray.size(); i++) {
                    String apiName = apiJsonArray.getString(i);
                    String apiPath = moduleJsonObj.getString(apiName);
                    if (!TextUtils.isEmpty(apiPath)) {
                        Class clazz = Class.forName(apiPath);
                        container.getJSBridge().register(apiName, clazz);
                    }
                }
            }

            callback.applySuccess();
        } catch (Exception e) {
            e.printStackTrace();
            callback.applyFail(e.toString());
        }
    }

}
