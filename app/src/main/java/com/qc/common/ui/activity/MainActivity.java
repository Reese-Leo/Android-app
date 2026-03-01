package com.qc.common.ui.activity;

import android.os.Bundle;
import android.util.Log;

import com.qc.common.ui.fragment.MyHomeFragment;

import the.one.base.ui.activity.BaseFragmentActivity;
import the.one.base.ui.fragment.BaseFragment;

/**
 * @author LuQiChuang
 * @desc
 * @date 2020/8/12 15:25
 * @ver 1.0
 */
public class MainActivity extends BaseFragmentActivity {

    private static final String TAG = "MainActivity";
    private static MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Log.d(TAG, "onCreate called");
            Log.d(TAG, "Before super.onCreate()");
            super.onCreate(savedInstanceState);
            Log.d(TAG, "After super.onCreate()");
            activity = this;
            Log.d(TAG, "onCreate completed, activity instance set");
            
            // 检查布局是否已设置
            if (getWindow().getDecorView().getRootView() != null) {
                Log.d(TAG, "Root view exists");
            } else {
                Log.w(TAG, "Root view is null!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            e.printStackTrace();
            // 不抛出异常，尝试继续运行
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    public static MainActivity getInstance() {
        return activity;
    }

    @Override
    protected BaseFragment getFirstFragment() {
        try {
            Log.d(TAG, "getFirstFragment called");
            BaseFragment fragment = new MyHomeFragment();
            Log.d(TAG, "MyHomeFragment created");
            return fragment;
        } catch (Exception e) {
            Log.e(TAG, "Error creating MyHomeFragment", e);
            throw e;
        }
    }

}