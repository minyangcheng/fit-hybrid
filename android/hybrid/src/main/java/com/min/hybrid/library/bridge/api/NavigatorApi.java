package com.min.hybrid.library.bridge.api;

import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.min.hybrid.library.R;
import com.min.hybrid.library.bridge.BridgeCallback;
import com.min.hybrid.library.bridge.CallbackEventHandler;
import com.min.hybrid.library.bridge.IBridgeImpl;
import com.min.hybrid.library.container.WebViewContainerFragment;
import com.min.hybrid.library.util.DeviceUtil;

public class NavigatorApi implements IBridgeImpl {

    public static final String RegisterName = "navigator";

    /**
     * 隐藏导航栏
     */
    public static void hide(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        container.getNavigationBar().hide();
        callback.applySuccess();
    }

    /**
     * 显示导航栏
     */
    public static void show(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        container.getNavigationBar().show();
        callback.applySuccess();
    }

    /**
     * 显示状态栏
     */
    public static void showStatusBar(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        DeviceUtil.setStatusBarVisibility(container.getActivity(), true);
        callback.applySuccess();
    }

    /**
     * 隐藏状态栏
     */
    public static void hideStatusBar(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        DeviceUtil.setStatusBarVisibility(container.getActivity(), false);
        callback.applySuccess();
    }

    /**
     * 隐藏导航栏返回按钮,只能在首页使用
     */
    public static void hideBackBtn(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        container.getNavigationBar().hideNbBack();
        callback.applySuccess();
    }

    /**
     * 监听系统返回按钮
     */
    public static void hookSysBack(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        container.getCallbackEventHandler().addPort(CallbackEventHandler.OnClickBack, callback.getPort());
    }

    /**
     * 监听导航栏返回按钮
     */
    public static void hookBackBtn(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        container.getCallbackEventHandler().addPort(CallbackEventHandler.OnClickNbBack, callback.getPort());
    }

    /**
     * 设置标题
     * title:   标题
     * subTitle:副标题
     * clickable：是否可点击，1：是，并且显示小箭头 其他：否
     * direction：箭头方向 top：向上 bottom：向下
     */
    public static void setTitle(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String title = param.getString("title");
        String subTitle = param.getString("subTitle");
        boolean clickable = "1".equals(param.getIntValue("clickable"));
        String direction = param.getString("direction");
        container.getNavigationBar().nbCustomTitleLayout.removeAllViews();
        container.getNavigationBar().titleParent.setVisibility(View.VISIBLE);
        container.getNavigationBar().setNbTitle(title, subTitle);
        if ("bottom".equals(direction)) {
            container.getNavigationBar().setTitleClickable(clickable, R.mipmap.img_arrow_black_down);
        } else {
            container.getNavigationBar().setTitleClickable(clickable, R.mipmap.img_arrow_black_up);
        }
        if (clickable) {
            container.getCallbackEventHandler().addPort(CallbackEventHandler.OnClickNbTitle, callback.getPort());
        } else {
            container.getCallbackEventHandler().removePort(CallbackEventHandler.OnClickNbTitle);
        }
    }

    /**
     * 设置导航栏最右侧按钮
     * isShow：是否显示，0：隐藏 其他：显示
     * text：文字按钮
     * imageUrl:图片按钮，格式为url地址,优先级高
     */
    public static void setRightBtn(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        int which = param.getIntValue("which");
        boolean isShow = !"0".equals(param.getString("isShow"));
        String text = param.getString("text");
        String imageUrl = param.getString("imageUrl");
        if (isShow) {
            container.getNavigationBar().setRightBtn(which, imageUrl, text);
            container.getCallbackEventHandler().addPort(CallbackEventHandler.OnClickNbRight + which, callback.getPort());
        } else {
            container.getNavigationBar().hideRightBtn(which);
            container.getCallbackEventHandler().removePort(CallbackEventHandler.OnClickNbRight + which);
        }
    }

    /**
     * 设置导航栏左侧按钮
     * isShow：是否显示，0：隐藏 其他：显示
     * text：文字按钮
     * imageUrl:图片按钮，格式为url地址,优先级高
     */
    public static void setLeftBtn(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        boolean isShow = !"0".equals(param.getString("isShow"));
        String text = param.getString("text");
        String imageUrl = param.getString("imageUrl");
        if (isShow) {
            container.getNavigationBar().setLeftBtn(imageUrl, text);
            container.getCallbackEventHandler().addPort(CallbackEventHandler.OnClickNbLeft, callback.getPort());
        } else {
            container.getNavigationBar().hideLeftBtn();
            container.getCallbackEventHandler().removePort(CallbackEventHandler.OnClickNbLeft);
        }
    }

}
