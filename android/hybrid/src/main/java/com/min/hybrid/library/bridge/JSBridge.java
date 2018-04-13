package com.min.hybrid.library.bridge;

import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.min.hybrid.library.HybridConstants;
import com.min.hybrid.library.container.WebViewContainerFragment;
import com.min.hybrid.library.util.HybridUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class JSBridge {

    private Map<String, HashMap<String, Method>> mExposedMethods = new HashMap<>();

    private WebViewContainerFragment mContainer;

    public JSBridge(WebViewContainerFragment container) {
        this.mContainer = container;
    }

    public void register(String apiModelName, Class<? extends IBridgeImpl> clazz) {
        try {
            mExposedMethods.put(apiModelName, getAllMethod(clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRegister(String apiModelName) {
        return mExposedMethods.containsKey(apiModelName);
    }

    private HashMap<String, Method> getAllMethod(Class injectedCls) throws Exception {
        HashMap<String, Method> mMethodsMap = new HashMap<>();
        Method[] methods = injectedCls.getDeclaredMethods();
        for (Method method : methods) {
            String name;
            if (method.getModifiers() != (Modifier.PUBLIC | Modifier.STATIC) || (name = method.getName()) == null) {
                continue;
            }
            Class[] parameters = method.getParameterTypes();
            if (parameters != null && parameters.length == 3) {
                if (parameters[1] == JSONObject.class && parameters[2] == BridgeCallback.class) {
                    mMethodsMap.put(name, method);
                }
            }
        }
        return mMethodsMap;
    }

    public String callJava(String url) {
        BridgeCallback callback = null;
        String error = null;
        String methodName = null;
        String apiName = null;
        String param = null;
        String port = null;

        boolean parseSuccess = false;
        while (!parseSuccess) {
            if (url.contains("#")) {
                error = "url不能包涵特殊字符'#'";
                break;
            }
            if (!url.startsWith(HybridConstants.BRIDGE_SCHEME)) {
                error = "scheme错误";
                break;
            }
            if (TextUtils.isEmpty(url)) {
                error = "url不能为空";
                break;
            }
            Uri uri = Uri.parse(url);
            if (uri == null) {
                error = "url解析失败";
                break;
            }
            apiName = uri.getHost();
            if (TextUtils.isEmpty(apiName)) {
                error = "API_Nam为空";
                break;
            }
            port = uri.getPort() + "";
            if (TextUtils.isEmpty(port)) {
                error = "port为空";
                break;
            }
            callback = new BridgeCallback(port, mContainer.getWebView());
            methodName = uri.getPath();
            methodName = methodName.replace("/", "");
            if (TextUtils.isEmpty(methodName)) {
                error = "方法名为空";
                break;
            }
            param = uri.getQuery();
            if (TextUtils.isEmpty(param)) {
                param = "{}";
            }
            parseSuccess = true;
        }
        if (!parseSuccess) {
            if (callback == null) {
                new BridgeCallback(BridgeCallback.ERROR_PORT, mContainer.getWebView()).applyNativeError(url, error);
            } else {
                callback.applyFail(error);
            }
            return error;
        }
        if (mExposedMethods.containsKey(apiName)) {
            HashMap<String, Method> methodHashMap = mExposedMethods.get(apiName);
            if (methodHashMap != null && methodHashMap.containsKey(methodName)) {
                Method method = methodHashMap.get(methodName);
                execute(method, param, callback);
            } else {
                error = apiName + "." + methodName + "未找到";
                callback.applyFail(error);
                return error;
            }
        } else {
            error = apiName + "未注册";
            callback.applyFail(error);
            return error;
        }
        return null;
    }

    private void execute(final Method method, final String param, final BridgeCallback callback) {
        HybridUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (method != null) {
                    try {
                        method.invoke(null, mContainer, JSON.parseObject(param), callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.applyFail(e.toString());
                    }
                }
            }
        });
    }

}
