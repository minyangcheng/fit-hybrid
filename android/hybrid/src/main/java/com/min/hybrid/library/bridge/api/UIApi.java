package com.min.hybrid.library.bridge.api;

import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baoyz.actionsheet.ActionSheet;
import com.min.hybrid.library.R;
import com.min.hybrid.library.bridge.BridgeCallback;
import com.min.hybrid.library.bridge.IBridgeImpl;
import com.min.hybrid.library.container.WebViewContainerFragment;
import com.min.hybrid.library.util.DialogUtil;
import com.min.hybrid.library.widget.popmenu.FrmPopMenu;
import com.min.hybrid.library.widget.popmenu.PopClickListener;


public class UIApi implements IBridgeImpl {

    public static final String RegisterName = "ui";

    /**
     * 消息提示
     * message： 需要提示的消息内容
     * duration：显示时长,long或short
     */
    public static void toast(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String message = param.getString("message");
        String duration = param.getString("duration");
        if (!TextUtils.isEmpty(message)) {
            if ("long".equalsIgnoreCase(duration)) {
                Toast.makeText(container.getContext(), message, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(container.getContext(), message, Toast.LENGTH_SHORT).show();
            }
        }
        callback.applySuccess();
    }

    /**
     * 弹出确认对话框
     * title：标题
     * message：消息
     * cancelable：是否可取消
     * buttonLabels：按钮数组，最多设置2个按钮
     * 返回：
     * which：按钮id
     */
    public static void alert(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String title = param.getString("title");
        String message = param.getString("message");
        JSONArray jsonArray = param.getJSONArray("buttonLabels");
        String btnName_1 = null;
        String btnName_2 = null;
        if (jsonArray != null && jsonArray.size() > 0) {
            if (jsonArray.size() == 1) {
                btnName_1 = jsonArray.getString(0);
            } else {
                btnName_1 = jsonArray.getString(0);
                btnName_2 = jsonArray.getString(1);
            }
        } else {
            btnName_1 = "确定";
        }
        boolean cancelable = !"0".equals(param.getString("cancelable"));
        DialogUtil.showAlertDialog(container.getContext(), title, message, btnName_1, btnName_2, cancelable, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                JSONObject data = new JSONObject();
                data.put("which", 0);
                callback.applySuccess(data);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                JSONObject data = new JSONObject();
                data.put("which", 1);
                callback.applySuccess(data);
            }
        });
    }

    /**
     * 弹出日期选择对话框
     * 参数：
     * title： 标题
     * datetime： 指定日期 yyyy-MM-dd
     * date： 格式：yyyy-MM-dd
     */
    public static void pickDate(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String title = param.getString("title");
        String dateFormatStr = param.getString("dateFormat");
        String date = param.getString("datetime");
        DialogUtil.showDateDialog(container.getContext(), title, dateFormatStr, date, true, new DialogUtil.OnDateDialogListener() {
            @Override
            public void onDataSelect(String dateStr) {
                JSONObject result = new JSONObject();
                result.put("date", dateStr);
                callback.applySuccess(result);
            }
        });
    }

    /**
     * 弹出年月选择对话框
     * title： 标题
     * datetime： 指定日期 yyyy-MM
     * 返回：
     * month： 格式：yyyy-MM
     */
    public static void pickMonth(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String title = param.getString("title");
        String dateFormatStr = param.getString("dateFormat");
        String date = param.getString("datetime");
        DialogUtil.showDateDialog(container.getContext(), title, dateFormatStr, date, false, new DialogUtil.OnDateDialogListener() {
            @Override
            public void onDataSelect(String dateStr) {
                JSONObject result = new JSONObject();
                result.put("date", dateStr);
                callback.applySuccess(result);
            }
        });
    }

    /**
     * 弹出时间选择对话框
     * 参数：
     * title：标题
     * datetime 指定时间 yyyy-MM-dd HH:mm或者HH:mm
     * 返回：
     * time：格式：HH:mm
     */
    public static void pickTime(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String title = param.getString("title");
        String timeFormat = param.getString("timeFormat");
        final String time = param.getString("time");
        DialogUtil.showTimeDialog(container.getContext(), title, timeFormat, time, new DialogUtil.OnTimeDialogListener() {
            @Override
            public void onTimeSelect(String timeStr) {
                JSONObject result = new JSONObject();
                result.put("time", timeStr);
                callback.applySuccess(result);
            }
        });
    }

    /**
     * 弹出底部选项按钮
     * items：多个选项用,隔开
     * cancelable: 是否可取消
     * 返回：
     * which：选中的按钮id
     */
    public static void actionSheet(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        boolean cancelable = !"0".equals(param.getString("cancelable"));
        String cancelBtnName = param.getString("cancelBtnName");
        JSONArray jsonArr = param.getJSONArray("items");
        if (jsonArr == null && jsonArr.size() == 0) {
            callback.applyFail(container.getContext().getString(R.string.status_request_error));
            return;
        }
        String[] items = new String[jsonArr.size()];
        items = jsonArr.toArray(items);
        ActionSheet.createBuilder(container.getContext(), container.getFragmentManager())
                .setCancelButtonTitle(cancelBtnName)
                .setOtherButtonTitles(items)
                .setCancelableOnTouchOutside(cancelable)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                        JSONObject result = new JSONObject();
                        result.put("which", -1);
                        callback.applySuccess(result);
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        JSONObject result = new JSONObject();
                        result.put("which", index);
                        callback.applySuccess(result);
                    }
                });
    }

    /**
     * 弹出顶部选项按钮
     * iconFilterColor：图标过滤色
     * titleItems：多个选项用,隔开
     * iconItems: 图标 多个,隔开
     * which：点击按钮id
     */
    public static void popWindow(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String iconFilterColor = param.getString("iconFilterColor");
        JSONArray titleJsonObject = param.getJSONArray("titleItems");
        JSONArray iconJsonObject = param.getJSONArray("iconItems");
        if (titleJsonObject == null) {
            callback.applyFail("请求参数错误");
            return;
        }
        String[] titleItems = new String[titleJsonObject.size()];
        String[] iconItems = new String[iconJsonObject.size()];
        titleItems = titleJsonObject.toArray(titleItems);
        iconItems = iconJsonObject.toArray(iconItems);
        if (iconItems != null && titleItems.length != iconItems.length) {
            callback.applyFail(container.getContext().getString(R.string.status_request_error));
            return;
        }

        int iconColor = 0;
        if (!TextUtils.isEmpty(iconFilterColor)) {
            iconColor = Color.parseColor("#" + iconFilterColor);
        }
        FrmPopMenu popupWindow = new FrmPopMenu(container.getActivity(), container.getNavigationBar().getNavigationView(), titleItems, iconItems, new PopClickListener() {
            @Override
            public void onClick(int index) {
                JSONObject data = new JSONObject();
                data.put("which", index);
                callback.applySuccess(data);
            }
        });
        popupWindow.setIconFilterColor(iconColor);
        popupWindow.show();
    }

    /**
     * 显示loading
     */
    public static void showWaiting(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String message = param.getString("message");
        boolean cancelable = true;
        if (param.containsKey("cancelable")) {
            cancelable = !"0".equals(param.getString("cancelable"));
        }
        container.showHudDialog(message, cancelable);
        callback.applySuccess();
    }

    /**
     * 隐藏loading
     */
    public static void closeWaiting(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        container.hideHudDialog();
    }

}
