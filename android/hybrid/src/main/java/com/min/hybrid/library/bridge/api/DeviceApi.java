package com.min.hybrid.library.bridge.api;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Vibrator;

import com.alibaba.fastjson.JSONObject;
import com.min.hybrid.library.bridge.BridgeCallback;
import com.min.hybrid.library.bridge.IBridgeImpl;
import com.min.hybrid.library.container.WebViewContainerFragment;
import com.min.hybrid.library.util.DeviceUtil;

public class DeviceApi implements IBridgeImpl {

    public static final String RegisterName = "device";

    /**
     * 设置横竖屏
     * orientation：1表示竖屏，0表示横屏，其他表示跟随系统
     */
    public static void setOrientation(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        Integer orientation = param.getInteger("orientation");
        if (orientation == null) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        if (orientation >= ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED && orientation <= ActivityInfo.SCREEN_ORIENTATION_LOCKED) {
            container.getActivity().setRequestedOrientation(orientation);
            callback.applySuccess();
        } else {
            callback.applyFail("orientation值超出范围，请设置-1到14");
        }
    }

    /**
     * 获取设备唯一编码
     */
    public static void getDeviceId(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        JSONObject data = new JSONObject();
        data.put("deviceId", DeviceUtil.getDeviceId(container.getContext()));
        callback.applySuccess(data);
    }

    /**
     * 获取设备基础信息
     * UAInfo
     * pixel
     * deviceId
     * netWorkType
     */
    public static void getVendorInfo(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        JSONObject data = new JSONObject();
        //设备厂商以及型号
        String type = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
        data.put("uaInfo", "android " + type);
        //设备分辨率
        Point point = DeviceUtil.getPhonePixel(container.getContext());
        data.put("pixel", point.x + "*" + point.y);
        //唯一标识(机器码或者MAC地址)
        data.put("deviceId", DeviceUtil.getDeviceId(container.getContext()));
        //网络状态 -1:无网络1：wifi 0：移动网络
        data.put("netWorkType", DeviceUtil.getNetWorkType(container.getContext()));
        callback.applySuccess(data);
    }

    /**
     * 打电话
     * phoneNum：电话号码
     */
    public static void callPhone(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String phoneNum = param.getString("phoneNum");
        DeviceUtil.callPhone(container.getContext(), phoneNum);
        callback.applySuccess();
    }

    /**
     * 发短信
     * phoneNum：电话号码
     * message:短信内容
     */
    public static void sendMsg(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String phoneNum = param.getString("phoneNum");
        String message = param.getString("message");
        DeviceUtil.sendMsg(container.getContext(), phoneNum, message);
        callback.applySuccess();
    }

    /**
     * 控制键盘显隐，如果输入法在窗口上已经显示，则隐藏，反之则显示
     */
    public static void closeInputKeyboard(final WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        container.getWebView().postDelayed(new Runnable() {
            public void run() {
                DeviceUtil.hideInputKeyboard(container.getActivity());
                callback.applySuccess();
            }
        }, 200);
    }


    /**
     * 获取当前网络状态
     * netWorkType：-1:无网络，1：wifi，0：移动网络
     */
    public static void getNetWorkInfo(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        JSONObject data = new JSONObject();
        data.put("netWorkType", DeviceUtil.getNetWorkType(container.getContext()));
        callback.applySuccess(data);
    }


    /**
     * 手机震动
     * duration：持续时间
     */
    @SuppressLint("MissingPermission")
    public static void vibrate(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        long time = param.getLongValue("duration");
        Vibrator vib = (Vibrator) (container.getContext().getSystemService(Service.VIBRATOR_SERVICE));
        vib.vibrate(time);
        callback.applySuccess();
    }

}
