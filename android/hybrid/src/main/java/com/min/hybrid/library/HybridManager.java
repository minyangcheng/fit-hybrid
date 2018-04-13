package com.min.hybrid.library;

import android.content.Context;

import com.min.hybrid.library.resource.ResourceCheck;
import com.min.hybrid.library.resource.ResourceParse;
import com.min.hybrid.library.util.ImageLoaderWrap;
import com.min.hybrid.library.util.SharePreferenceUtil;

/**
 * Created by minyangcheng on 2018/1/17.
 */

public class HybridManager {

    private static HybridManager hybridManager;

    private HybridConfiguration configuration;
    private ResourceCheck resourceCheck;
    private ResourceParse resourceParse;

    private HybridManager() {
    }

    public static HybridManager getInstance() {
        if (hybridManager == null) {
            synchronized (HybridManager.class) {
                if (hybridManager == null) {
                    hybridManager = new HybridManager();
                }
            }
        }
        return hybridManager;
    }

    public void init(HybridConfiguration configuration) {
        this.configuration = configuration;
        check();
        init();
    }

    private void check() {
        if (configuration == null) {
            throw new RuntimeException("hybrid config can not be null");
        }
        if (configuration.getContext() == null) {
            throw new RuntimeException("hybrid config context can not be null");
        }
        if (configuration.getPageHostUrl() == null) {
            throw new RuntimeException("hybrid config pageHostUrl can not be null");
        }
    }

    private void init() {
        initUtil();
        resourceCheck = new ResourceCheck(configuration.getContext(), configuration.getCheckApiHandler());
        resourceParse = new ResourceParse();
    }

    private void initUtil() {
        ImageLoaderWrap.initImageLoader(configuration.getContext());
    }

    public ResourceParse getResourceParse() {
        check();
        return resourceParse;
    }

    public ResourceCheck getResourceCheck() {
        check();
        return resourceCheck;
    }

    public HybridConfiguration getConfiguration() {
        check();
        return configuration;
    }

    public void checkVersion() {
        getResourceCheck().checkVersion();
    }

    public long prepareJsBundle(Context context) {
        return getResourceParse().prepareJsBundle(context);
    }

    public String getVersion() {
        return SharePreferenceUtil.getVersion(configuration.getContext());
    }

}
