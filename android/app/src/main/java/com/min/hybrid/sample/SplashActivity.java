package com.min.hybrid.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.min.hybrid.library.HybridManager;
import com.min.hybrid.library.bean.ContainerBean;
import com.min.hybrid.library.container.WebViewContainerActivity;

public class SplashActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long prepareTime = HybridManager.getInstance().prepareJsBundle(SplashActivity.this);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toHome();
                    }
                }, 2500 - prepareTime);
            }
        }).start();
    }

    private void toHome() {
        ContainerBean bean = new ContainerBean(HybridManager.getInstance().getConfiguration().getPageHostUrl());
        bean.showBackBtn = false;
        WebViewContainerActivity.startActivity(this, bean);
        finish();
    }

}
