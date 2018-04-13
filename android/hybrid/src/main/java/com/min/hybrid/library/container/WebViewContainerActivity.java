package com.min.hybrid.library.container;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.min.hybrid.library.HybridConstants;
import com.min.hybrid.library.R;
import com.min.hybrid.library.bean.ContainerBean;

public class WebViewContainerActivity extends AppCompatActivity {

    private ContainerBean mData;
    private WebViewContainerFragment mFragment;

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, WebViewContainerActivity.class);
        ContainerBean data = new ContainerBean(url);
        intent.putExtra(HybridConstants.KEY_ACTIVITY_DATA, data);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, ContainerBean data) {
        Intent intent = new Intent(context, WebViewContainerActivity.class);
        intent.putExtra(HybridConstants.KEY_ACTIVITY_DATA, data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_container);
        getDataFromIntent(savedInstanceState);
        mFragment = WebViewContainerFragment.newInstance(mData);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.view_root, mFragment)
                .commitAllowingStateLoss();
    }

    public void getDataFromIntent(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(HybridConstants.KEY_ACTIVITY_DATA)) {
            mData = (ContainerBean) savedInstanceState.getSerializable(HybridConstants.KEY_ACTIVITY_DATA);
        } else if (getIntent().hasExtra(HybridConstants.KEY_ACTIVITY_DATA)) {
            mData = (ContainerBean) getIntent().getSerializableExtra(HybridConstants.KEY_ACTIVITY_DATA);
        }
    }

    @Override
    public void onBackPressed() {
        mFragment.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mData != null) {
            outState.putSerializable(HybridConstants.KEY_ACTIVITY_DATA, mData);
        }
        super.onSaveInstanceState(outState);
    }

}
