package com.min.hybrid.library.container;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.min.hybrid.library.HybridConstants;
import com.min.hybrid.library.HybridManager;
import com.min.hybrid.library.R;
import com.min.hybrid.library.activity.HybridDebugActivity;
import com.min.hybrid.library.bean.ContainerBean;
import com.min.hybrid.library.bean.HybridEvent;
import com.min.hybrid.library.bridge.BridgeCallback;
import com.min.hybrid.library.bridge.CallbackEventHandler;
import com.min.hybrid.library.bridge.JSBridge;
import com.min.hybrid.library.bridge.api.AuthApi;
import com.min.hybrid.library.bridge.api.DeviceApi;
import com.min.hybrid.library.bridge.api.NavigatorApi;
import com.min.hybrid.library.bridge.api.PageApi;
import com.min.hybrid.library.bridge.api.RuntimeApi;
import com.min.hybrid.library.bridge.api.UIApi;
import com.min.hybrid.library.bridge.api.UtilApi;
import com.min.hybrid.library.util.EventUtil;
import com.min.hybrid.library.util.FileUtil;
import com.min.hybrid.library.util.HybridUtil;
import com.min.hybrid.library.util.L;
import com.min.hybrid.library.util.SharePreferenceUtil;
import com.min.hybrid.library.widget.HudDialog;
import com.min.hybrid.library.widget.NavigationBar;
import com.min.hybrid.library.widget.WebViewProgressBar;

import java.io.File;

import de.greenrobot.event.Subscribe;

/**
 * Created by minyangcheng on 2018/2/6.
 */

public class WebViewContainerFragment extends Fragment {

    private HybridWebView mWebView;
    private NavigationBar mNavigationBar;
    private WebViewProgressBar mProgressBar;
    private View mErrorContentView;
    private View mRetryView;

    private HybridWebViewClient mWebviewClient;
    private HybridWebChromeClient mChromeClient;
    private JSBridge mJSBridge;
    private CallbackEventHandler mEventHandler;

    private ContainerBean mData;
    private boolean mContinueFlag;

    private HudDialog mHudDialog;

    private long mStartTime;
    private int mCounter;

    public static WebViewContainerFragment newInstance(ContainerBean data) {
        WebViewContainerFragment fragment = new WebViewContainerFragment();
        fragment.setData(data);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_container_webview, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventUtil.register(this);
        findView(view);
        initView();
        registerApi();
        setDataToView();
        loadUrl();
    }

    @Override
    public void onResume() {
        mWebView.onResume();
        mWebView.resumeTimers();
        super.onResume();
        if (mEventHandler.hasPort(CallbackEventHandler.OnPageResume)) {
            mEventHandler.onPageResume();
        }
    }

    @Override
    public void onPause() {
        mWebView.onPause();
        mWebView.pauseTimers();
        super.onPause();
        if (mEventHandler.hasPort(CallbackEventHandler.OnPagePause)) {
            mEventHandler.onPagePause();
        }
    }

    @Override
    public void onDestroy() {
        mWebView.stopLoading();
        try {
            if (mWebView != null) {
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if (parent != null) {
                    parent.removeView(mWebView);
                }
                mWebView.removeAllViews();
                mWebView.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventUtil.unregister(this);
        super.onDestroy();
    }

    private void findView(View view) {
        mNavigationBar = view.findViewById(R.id.view_nb);
        mWebView = view.findViewById(R.id.wv);
        mProgressBar = view.findViewById(R.id.pb);
        mErrorContentView = view.findViewById(R.id.view_error);
        mRetryView = view.findViewById(R.id.view_retry);
        mRetryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.reload();
                mErrorContentView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initView() {
        mNavigationBar.setOnNavigationBarListener(new NavigationBar.INbOnClick() {
            @Override
            public void onNbBack() {
                backPress(CallbackEventHandler.OnClickNbBack);
            }

            @Override
            public void onNbLeft(View view) {
                if (view.getTag() != null && "close".equals(view.getTag().toString())) {
                    onNbBack();
                } else {
                    mEventHandler.onClickNbLeft();
                }
            }

            @Override
            public void onNbRight(View view, int which) {
                mEventHandler.onClickNbRight(which);
            }

            @Override
            public void onNbTitle(View view) {
                mEventHandler.onClickNbTitle(0);
            }
        });
        mNavigationBar.setColorFilter(HybridUtil.getColorPrimary(getContext()));
        if (!mData.showBackBtn) {
            mNavigationBar.hideNbBack();
        }
        initWebView();
        setDebugMode();
    }

    private void initWebView() {
        mJSBridge = new JSBridge(this);
        mEventHandler = new CallbackEventHandler(mWebView);
        mWebviewClient = new HybridWebViewClient(this);
        mWebView.setWebViewClient(mWebviewClient);
        mChromeClient = new HybridWebChromeClient(this);
        mWebView.setWebChromeClient(mChromeClient);
    }

    private void setDebugMode() {
        mNavigationBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCounter == 0) {
                    mStartTime = System.currentTimeMillis();
                }
                mCounter++;
                if (mCounter % 6 == 0) {
                    if (System.currentTimeMillis() - mStartTime < 2000) {
                        HybridDebugActivity.startActivity(getContext());
                    }
                    mCounter = 0;
                }
            }
        });
    }

    private void registerApi() {
        mJSBridge.register(UIApi.RegisterName, UIApi.class);
        mJSBridge.register(PageApi.RegisterName, PageApi.class);
        mJSBridge.register(DeviceApi.RegisterName, DeviceApi.class);
        mJSBridge.register(RuntimeApi.RegisterName, RuntimeApi.class);
        mJSBridge.register(UtilApi.RegisterName, UtilApi.class);
        mJSBridge.register(NavigatorApi.RegisterName, NavigatorApi.class);
        mJSBridge.register(AuthApi.RegisterName, AuthApi.class);
    }

    private void setDataToView() {
        if (mData != null) {
            if (mData.orientation >= ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED && mData.orientation <= ActivityInfo.SCREEN_ORIENTATION_LOCKED) {
                getActivity().setRequestedOrientation(mData.orientation);
            }
            if (!TextUtils.isEmpty(mData.title)) {
                mNavigationBar.setNbTitle(mData.title);
            }
            if (mData.pageStyle == -1) {
                mNavigationBar.hide();
            }
        }
    }

    private void loadUrl() {
        if (mData == null || TextUtils.isEmpty(mData.pageUrl)) {
            Toast.makeText(getContext(), "请求参数错误", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            String resultUrl = mData.pageUrl;
            String pageHost = HybridManager.getInstance().getConfiguration().getPageHostUrl();
            if ((SharePreferenceUtil.getInterceptorActive(getContext()) && !TextUtils.isEmpty(pageHost) && mData.pageUrl.startsWith(pageHost))
                    || mData.pageUrl.startsWith("file")) {
                resultUrl = getLocalUrl(mData.pageUrl);
            }
            L.d(HybridConstants.LOG_TAG, "loadUrl=%s", resultUrl);
            mWebView.loadUrl(resultUrl);
        }
    }

    public HybridWebView getWebView() {
        return mWebView;
    }

    public JSBridge getJSBridge() {
        return mJSBridge;
    }

    private void setData(ContainerBean data) {
        this.mData = data;
    }

    public void finish() {
        if (!getActivity().isFinishing()) {
            getActivity().finish();
        }
    }

    public NavigationBar getNavigationBar() {
        return mNavigationBar;
    }

    public CallbackEventHandler getCallbackEventHandler() {
        return mEventHandler;
    }

    public void onBackPressed() {
        backPress(CallbackEventHandler.OnClickBack);
    }

    public void backPress(String eventType) {
        if (mEventHandler.hasPort(eventType)) {
            if (eventType == CallbackEventHandler.OnClickNbBack) {
                mEventHandler.onClickNbBack();
            } else {
                mEventHandler.onClickBack();
            }
        } else {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                getActivity().finish();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JSONObject object = new JSONObject();
        object.put("resultCode", resultCode);
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            String ewmString = result.getContents();
            object.put(HybridConstants.RESULT_DATA, ewmString == null ? "" : ewmString);
            mEventHandler.onScanCode(object);
        }
    }

    protected String getLocalUrl(String url) {
        File webAppDir = FileUtil.getBundleDir(getContext());
        File indexFile = new File(webAppDir, "index.html");
        if (!indexFile.exists()) {
            return url;
        }
        String urlTemplate = "file:///%s/index.html#/%s";
        String[] arr = url.split("#");
        String s = "";
        if (arr.length > 1) {
            s = arr[1].substring(1);
        }
        url = String.format(urlTemplate, webAppDir.getAbsolutePath(), s);
        return url;
    }

    @Subscribe
    public void onEvent(HybridEvent event) {
        BridgeCallback callback = new BridgeCallback("", getWebView());
        callback.postEventToJs(event.type, event.data);
    }

    public void handleProgressChanged(int newProgress) {
        L.d(HybridConstants.LOG_TAG, "handleProgressChanged newProgress=%s", newProgress);
        mProgressBar.setProgress(newProgress);
        if (newProgress == 100) {
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            if (mProgressBar.getVisibility() == View.INVISIBLE)
                mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void handleError(String description, String failingUrl) {
        L.d(HybridConstants.LOG_TAG, "handleError description=%s , failingUrl=%s", description, failingUrl);
        mErrorContentView.setVisibility(View.VISIBLE);
    }

    public void handleFinish(String url) {
        L.d(HybridConstants.LOG_TAG, "handleFinish", url);
    }

    public void showHudDialog(String message, boolean cancelable) {
        if (mHudDialog == null) {
            mHudDialog = HudDialog.createProgressHud(getContext());
        }
        mHudDialog.setCancelable(cancelable);
        mHudDialog.setMessage(message);
        if (!mHudDialog.isShowing()) {
            mHudDialog.show();
        }
    }

    public void hideHudDialog() {
        if (mHudDialog != null && mHudDialog.isShowing()) {
            mHudDialog.dismiss();
        }
    }

}
