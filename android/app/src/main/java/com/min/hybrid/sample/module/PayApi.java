package com.min.hybrid.sample.module;

import com.alibaba.fastjson.JSONObject;
import com.min.hybrid.library.bridge.BridgeCallback;
import com.min.hybrid.library.bridge.IBridgeImpl;
import com.min.hybrid.library.container.WebViewContainerFragment;

public class PayApi implements IBridgeImpl {

    public static final String RegisterName = "pay";

    public static void payMoney(WebViewContainerFragment container, JSONObject param, BridgeCallback callback) {
        JSONObject data = new JSONObject();
        data.put("result", "付款成功");
        data.put("amount", "100");
        callback.applySuccess(data);
    }

}
