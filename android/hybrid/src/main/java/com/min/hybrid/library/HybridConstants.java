package com.min.hybrid.library;

/**
 * Created by minyangcheng on 2018/1/10.
 */

public class HybridConstants {

    public static final String LOG_TAG = "hybrid_log";

    public static final String BRIDGE_SCHEME = "QuickHybridJSBridge";

    public static final String ACTION_WEB_VIEW_CONTAINER_ACTIVITY = "open.hybrid.activity";

    public static final String KEY_ACTIVITY_DATA = "activityDataKey";

    public static final String REQUEST_CODE="request_code";
    public static final String RESULT_DATA = "resultData";

    public static class BridgeApi {

        public static final String SCREEN_ORIENTATION = "screenOrientationKey";
        public static final int REQUEST_CODE_ALBUM = 10001;

    }

    public static class Resource {
        public static final String BASE_DIR = "WebApp";
        public static final String BUNDLE_NAME = "bundle.zip";
        public static final String TEMP_BUNDLE_NAME = "temp_bundle.zip";
        public static final String JS_BUNDLE = "/.bundle";
        public static final String JS_BUNDLE_ZIP = "/.bundle_zip";
    }

    public static class Version {
        public static final int UPDATING = 0;
        public static final int SLEEP = 1;
    }

    public static class SP {
        public static final String NATIVE_NAME = "HYBIRD_NATIVE_SP";
        public static final String VERSION = "VERSION";
        public static final String DOWNLOAD_VERSION = "DOWNLOAD_VERSION";
        public static final String INTERCEPTOR_ACTIVE = "INTERCEPTOR_ACTIVE";
        public static final String PAGE_DEV_HOST_URL = "PAGE_DEV_HOST_URL";
    }

}
