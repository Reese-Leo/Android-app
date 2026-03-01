package com.qc.common.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.qc.common.ui.presenter.UpdatePresenter;
import com.qc.common.util.DBUtil;
import com.qc.common.util.EntityUtil;
import com.qc.common.util.VersionUtil;
import com.qmuiteam.qmui.arch.QMUILatestVisit;

import org.litepal.LitePal;

/**
 * @author LuQiChuang
 * @desc
 * @date 2020/8/12 15:25
 * @ver 1.0
 */
public class LauncherActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 10;
    private static final String[] PERMISSIONS_REQUIRED = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String TAG = LauncherActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        Log.d(TAG, "Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT);
        
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            Log.d(TAG, "Activity brought to front, finishing");
            finish();
            return;
        }

        // 无论是否检查权限，必须先执行初始化，否则 MainActivity 会因为缺少数据源而白屏
        doSomeThing();

        // Android 13+ (API 33+) 权限处理
        if (Build.VERSION.SDK_INT >= 33) {
            Log.d(TAG, "Android 13+, jumping to MainActivity");
            startMainActivity();
        } else if (allPermissionsGranted()) {
            Log.d(TAG, "Permissions already granted");
            startMainActivity();
        } else {
            Log.d(TAG, "Requesting permissions");
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE);
        }
    }

    private void startMainActivity() {
        try {
            Intent intent = QMUILatestVisit.intentOfLatestVisit(this);
            if (intent == null) {
                intent = new Intent(this, MainActivity.class);
            }
            Log.d(TAG, "Starting MainActivity");
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error starting MainActivity", e);
            Toast.makeText(this, "启动失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: requestCode=" + requestCode);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            // 无论权限是否允许，都尝试进入主界面，避免卡死在白屏
            startMainActivity();
        }
    }

    private void doSomeThing() {
        try {
            Log.d(TAG, "Initializing app components");
            VersionUtil.initVersion(this);
            new UpdatePresenter().checkApkUpdate();
            LitePal.initialize(this);
            EntityUtil.initEntityList(EntityUtil.STATUS_ALL);
            new Thread(() -> {
                try {
                    DBUtil.autoBackup(this);
                } catch (Exception e) {
                    Log.e(TAG, "Error in autoBackup", e);
                }
            }).start();
            Log.d(TAG, "App components initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error in doSomeThing", e);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : PERMISSIONS_REQUIRED) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}