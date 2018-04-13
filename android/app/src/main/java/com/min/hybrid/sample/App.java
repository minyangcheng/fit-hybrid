package com.min.hybrid.sample;

import android.app.Application;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.min.hybrid.library.HybridConfiguration;
import com.min.hybrid.library.HybridManager;
import com.min.hybrid.library.net.HttpManager;
import com.min.hybrid.library.resource.CheckApiHandler;
import com.min.hybrid.library.resource.ResourceCheck;
import com.min.hybrid.library.util.HybridUtil;
import com.min.hybrid.sample.util.LifecycleCallBack;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class App extends Application {

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        if (HybridUtil.shouldInit(this)) {
            mInstance = this;
            initHybrid();
            registerLifecycle();
        }
    }

    private void initHybrid() {
        HybridConfiguration configuration = new HybridConfiguration(this)
                .setPageHostUrl("http://10.10.12.148:8080")
                .setCheckApiHandler(new CheckApiHandler() {
                    @Override
                    public void checkRequest(ResourceCheck resourceCheck) {
                        checkApiRequest(resourceCheck);
                    }
                });
        HybridManager.getInstance()
                .init(configuration);
    }

    private void checkApiRequest(final ResourceCheck resourceCheck) {
        Request request = new Request.Builder()
                .url("http://10.10.12.148:8080/static/updateJson?version=" + HybridManager.getInstance().getVersion())
                .get()
                .build();
        Call call = HttpManager.getHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                resourceCheck.setCheckApiFailResp(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jsonObject = JSON.parseObject(response.body().string());
                if (jsonObject != null) {
                    resourceCheck.setCheckApiSuccessResp(jsonObject.getString("version"),
                            jsonObject.getString("md5"),
                            jsonObject.getString("dist"));
                }
            }
        });
    }

    private void registerLifecycle() {
        LifecycleCallBack lifecycleManager = new LifecycleCallBack();
        lifecycleManager.register(this).setOnTaskSwitchListenner(new LifecycleCallBack
                .OnTaskSwitchListener() {

            @Override
            public void onTaskSwitchToForeground() {
                HybridManager.getInstance()
                        .checkVersion();
            }

            @Override
            public void onTaskSwitchToBackground() {
            }
        });
    }

    public static App getApplication() {
        return mInstance;
    }

}
