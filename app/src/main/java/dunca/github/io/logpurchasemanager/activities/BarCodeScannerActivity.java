package dunca.github.io.logpurchasemanager.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import dunca.github.io.logpurchasemanager.fragments.AcquisitionItemFragment;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public static final int ACTION_REQUEST_CAMERA_PERMISSIONS = 1;

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZXingScannerView(this);
        mScannerView.setAutoFocus(true);
        setContentView(mScannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this);

        if (!hasCameraPermissions()) {
            promptForCameraPermissions();
        } else {
            mScannerView.startCamera();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String scannedBarCode = result.getText();

        getIntent().putExtra(AcquisitionItemFragment.EXTRA_BAR_CODE, scannedBarCode);
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACTION_REQUEST_CAMERA_PERMISSIONS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mScannerView.startCamera();
        }
    }

    private boolean hasCameraPermissions() {
        return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void promptForCameraPermissions() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, ACTION_REQUEST_CAMERA_PERMISSIONS);
    }
}
