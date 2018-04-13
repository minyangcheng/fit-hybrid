package com.min.hybrid.library.bridge.api;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.min.hybrid.library.HybridConstants;
import com.min.hybrid.library.R;
import com.min.hybrid.library.activity.ScanCaptureActivity;
import com.min.hybrid.library.bridge.BridgeCallback;
import com.min.hybrid.library.bridge.CallbackEventHandler;
import com.min.hybrid.library.bridge.IBridgeImpl;
import com.min.hybrid.library.container.WebViewContainerFragment;
import com.min.hybrid.library.util.FileUtil;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.util.ArrayList;

public class UtilApi implements IBridgeImpl {

    public static final String RegisterName = "util";

    /**
     * 打开二维码
     * videoUrl：视频地址
     */
    public static void scan(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(container);
        if (integrator != null) {
            integrator.setCaptureActivity(ScanCaptureActivity.class);
            integrator.initiateScan();
            container.getCallbackEventHandler().addPort(CallbackEventHandler.OnScanCode, callback.getPort());
        }
    }


    /**
     * 多图片选择
     * photoCount:可选图片的最大数,默认为9
     * showCamera:是否允许拍照，1：允许；0：不允许，默认不允许
     * selectedPhotos:已选图片,json数组
     */
    public static void selectImage(final WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        int photoCount = param.getIntValue("photoCount");
        boolean showCamera = "1".equals(param.getString("showCamera"));
        JSONArray itemsJsonObject = param.getJSONArray("selectedPhotos");
        String[] items = new String[itemsJsonObject.size()];
        items = itemsJsonObject.toArray(items);
        ArrayList selectedPhotos = new ArrayList<AlbumFile>();
        AlbumFile albumFile = null;
        for (String s : items) {
            albumFile = new AlbumFile();
            albumFile.setPath(s);
            selectedPhotos.add(albumFile);
        }

        Action sureAction = new Action<ArrayList<AlbumFile>>() {
            @Override
            public void onAction(int requestCode, @NonNull ArrayList<AlbumFile> result) {
                JSONObject data = new JSONObject();
                data.put(HybridConstants.REQUEST_CODE, requestCode);
                ArrayList<String> dataList = new ArrayList<>();
                for (AlbumFile file : result) {
                    dataList.add(file.getPath());
                }
                data.put(HybridConstants.RESULT_DATA, dataList);
                container.getCallbackEventHandler().onChoosePic(data);
            }
        };

        Action cancelAction = new Action<String>() {
            @Override
            public void onAction(int requestCode, @NonNull String result) {
            }
        };

        if (photoCount > 1) {
            Album.image(container)
                    .multipleChoice()
                    .requestCode(HybridConstants.BridgeApi.REQUEST_CODE_ALBUM)
                    .camera(showCamera)
                    .columnCount(3)
                    .selectCount(photoCount)
                    .checkedList(selectedPhotos)
                    .onResult(sureAction)
                    .onCancel(cancelAction)
                    .start();
        } else {
            Album.image(container)
                    .singleChoice()
                    .requestCode(HybridConstants.BridgeApi.REQUEST_CODE_ALBUM)
                    .camera(showCamera)
                    .columnCount(3)
                    .onResult(sureAction)
                    .onCancel(cancelAction)
                    .start();
        }

        container.getCallbackEventHandler().addPort(CallbackEventHandler.OnChoosePic, callback.getPort());
    }

    /**
     * 打开摄像机拍照
     */
    public static void cameraImage(final WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        Album.camera(container)
                .image()
                .requestCode(HybridConstants.BridgeApi.REQUEST_CODE_ALBUM)
                .onResult(new Action<String>() {
                    @Override
                    public void onAction(int requestCode, @NonNull String result) {
                        JSONObject data = new JSONObject();
                        data.put(HybridConstants.REQUEST_CODE, requestCode);
                        data.put(HybridConstants.RESULT_DATA, result);
                        container.getCallbackEventHandler().onChoosePic(data);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(int requestCode, @NonNull String result) {
                    }
                })
                .start();
        container.getCallbackEventHandler().addPort(CallbackEventHandler.OnChoosePic, callback.getPort());
    }

    /**
     * 打开文件
     * path:文件本地路径
     */
    public static void openFile(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String path = param.getString("path");
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            callback.applyFail(container.getContext().getString(R.string.status_request_error));
        } else {
            FileUtil.openFile(container.getContext(), file);
            callback.applySuccess();
        }
    }

}
