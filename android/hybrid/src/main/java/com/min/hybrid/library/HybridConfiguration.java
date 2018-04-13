package com.min.hybrid.library;

import android.content.Context;
import android.text.TextUtils;

import com.min.hybrid.library.resource.CheckApiHandler;
import com.min.hybrid.library.util.SharePreferenceUtil;

/**
 * Created by minyangcheng on 2018/1/17.
 */

public class HybridConfiguration {

    private Context context;
    private String pageHostUrl;
    private CheckApiHandler checkApiHandler;

    public HybridConfiguration(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getPageHostUrl() {
        String devPageUrl = SharePreferenceUtil.getPageDevHostUrl(context);
        if (TextUtils.isEmpty(devPageUrl)) {
            return pageHostUrl;
        } else {
            return devPageUrl;
        }
    }

    public HybridConfiguration setPageHostUrl(String pageHostUrl) {
        this.pageHostUrl = pageHostUrl;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public CheckApiHandler getCheckApiHandler() {
        return checkApiHandler;
    }

    public HybridConfiguration setCheckApiHandler(CheckApiHandler checkApiHandler) {
        this.checkApiHandler = checkApiHandler;
        return this;
    }

}
