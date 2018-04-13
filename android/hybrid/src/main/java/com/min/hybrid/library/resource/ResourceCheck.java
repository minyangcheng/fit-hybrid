package com.min.hybrid.library.resource;

import android.content.Context;
import android.text.TextUtils;

import com.min.hybrid.library.HybridConstants;
import com.min.hybrid.library.net.DownloadManager;
import com.min.hybrid.library.net.FileCallBack;
import com.min.hybrid.library.util.FileUtil;
import com.min.hybrid.library.util.HybridUtil;
import com.min.hybrid.library.util.L;
import com.min.hybrid.library.util.Md5Util;
import com.min.hybrid.library.util.SharePreferenceUtil;

import java.io.File;

public class ResourceCheck {

    private Context mContext;
    private int mCurrentStatus = HybridConstants.Version.SLEEP;
    private CheckApiHandler mCheckApiHandler;

    public ResourceCheck(Context context, CheckApiHandler checkApiHandler) {
        this.mContext = context;
        this.mCheckApiHandler = checkApiHandler;
    }

    public void checkVersion() {
        if (mCurrentStatus == HybridConstants.Version.UPDATING || TextUtils.isEmpty(SharePreferenceUtil.getVersion(mContext))) {
            return;
        }
        if (SharePreferenceUtil.getInterceptorActive(mContext)) {
            if (mCheckApiHandler != null) {
                startRequestCheckApi();
                mCheckApiHandler.checkRequest(this);
            }
        }
    }

    private void startRequestCheckApi() {
        mCurrentStatus = HybridConstants.Version.UPDATING;
    }

    public void setCheckApiFailResp(Exception e) {
        L.e(HybridConstants.LOG_TAG, e);
        mCurrentStatus = HybridConstants.Version.SLEEP;
    }

    public void setCheckApiSuccessResp(String remoteVersion, String md5, String dist) {
        try {
            String localVersion = SharePreferenceUtil.getVersion(mContext);
            L.d(HybridConstants.LOG_TAG, "localVersion=%s,remoteVersion=%s,md5=%s,dist=%s", localVersion, remoteVersion, md5, dist);
            if (HybridUtil.compareVersion(remoteVersion, localVersion) > 0) {
                download(remoteVersion, md5, dist);
            } else {
                mCurrentStatus = HybridConstants.Version.SLEEP;
            }
        } catch (Exception e) {
            mCurrentStatus = HybridConstants.Version.SLEEP;
            e.printStackTrace();
        }
    }

    private void download(final String remoteVersion, final String md5, String dist) {
        try {
            if (hasDownloadVersion(remoteVersion)) {
                L.d(HybridConstants.LOG_TAG, "this version=%s has been download", remoteVersion);
                mCurrentStatus = HybridConstants.Version.SLEEP;
                return;
            }
            File destination = new File(FileUtil.getTempBundleDir(mContext), HybridConstants.Resource.TEMP_BUNDLE_NAME);
            DownloadManager.getInstance()
                    .downloadFile(dist, new FileCallBack(destination) {
                        @Override
                        public void onStart(String url) {
                            L.d(HybridConstants.LOG_TAG, "startDownload url=%s", url);
                        }

                        @Override
                        public void onProgress(String url, long progress, long total) {
                        }

                        @Override
                        public void onSuccess(String url, File file) {
                            L.d(HybridConstants.LOG_TAG, "complete download url=%s", url);
                            if (validateZip(file, md5)) {
                                RenameDeleteFile();
                                SharePreferenceUtil.setDownLoadVersion(mContext, remoteVersion);
                                L.d(HybridConstants.LOG_TAG, "set download version=%s", remoteVersion);
                            } else {
                                FileUtil.deleteFile(new File(FileUtil.getTempBundleDir(mContext), HybridConstants.Resource.TEMP_BUNDLE_NAME));
                            }
                            mCurrentStatus = HybridConstants.Version.SLEEP;
                        }

                        @Override
                        public void onFail(String url, Throwable t) {
                            L.e(HybridConstants.LOG_TAG, t);
                            mCurrentStatus = HybridConstants.Version.SLEEP;
                        }
                    });
        } catch (Exception e) {
            mCurrentStatus = HybridConstants.Version.SLEEP;
            e.printStackTrace();
        }
    }

    private boolean validateZip(File file, String md5) {
        try {
            if (file.exists() && Md5Util.getFileMD5(file).equals(md5)) {
                return true;
            }
            //todo
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean hasDownloadVersion(String version) {
        String downLoadVersion = SharePreferenceUtil.getDownLoadVersion(mContext);
        if (!TextUtils.isEmpty(downLoadVersion)) {
            if (version.equals(downLoadVersion)) {
                return true;
            }
        }
        return false;
    }

    private void RenameDeleteFile() {
        FileUtil.deleteFile(new File(FileUtil.getTempBundleDir(mContext), HybridConstants.Resource.BUNDLE_NAME));
        FileUtil.renameFile(FileUtil.getTempBundleDir(mContext), HybridConstants.Resource.TEMP_BUNDLE_NAME, HybridConstants.Resource.BUNDLE_NAME);
    }

}
