package cn.zp.handleball;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Button mBtnStart;
    private Button mBtnQuit;
    private Button mBtnRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
                Toast.makeText(this, "请先允许FloatBall出现在顶部", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initView() {
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnQuit = (Button) findViewById(R.id.btn_quit);
        mBtnRequest = (Button) findViewById(R.id.btn_request);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAccessibility();
                Intent intent = new Intent(MainActivity.this, FloatBallService.class);
                Bundle data = new Bundle();
                data.putInt("type", FloatBallService.TYPE_ADD);
                intent.putExtras(data);
                startService(intent);
            }
        });
        mBtnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FloatBallService.class);
                Bundle data = new Bundle();
                data.putInt("type", FloatBallService.TYPE_DEL);
                intent.putExtras(data);
                startService(intent);
            }
        });
        mBtnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                componentName = new ComponentName(MainActivity.this, LockReceiver.class);
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏");
                startActivity(intent);
            }
        });
    }

    private void checkAccessibility() {
        // 判断辅助功能是否开启
        if (!AccessibilityUtil.isAccessibilitySettingsOn(this)) {
            // 引导至辅助功能设置页面
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            Toast.makeText(this, "请先开启HandleBall辅助功能", Toast.LENGTH_SHORT).show();
        }
    }

    private DevicePolicyManager policyManager;
    private ComponentName componentName;
    @Override
    protected void onResume() {

        super.onResume();
    }
}
