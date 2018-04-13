package com.min.hybrid.library.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.min.hybrid.library.R;
import com.min.hybrid.library.util.DeviceUtil;

public class ScanCaptureActivity extends CaptureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtil.setStatusBarColor(this, R.color.text_black);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * Override to use a different layout.
     *
     * @return the DecoratedBarcodeView
     */
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.frm_scan_activity);
        DecoratedBarcodeView barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setStatusText("");
        return barcodeScannerView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0 && resultCode==RESULT_OK){
            setResult(RESULT_OK,data);
            finish();
        }
    }
}
