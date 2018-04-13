package com.min.hybrid.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.min.hybrid.library.container.WebViewContainerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openHybridPage(View view) {
        WebViewContainerActivity.startActivity(this, " http://10.10.12.153:8080/");
    }

}
