package cn.zp.handleball;

import android.accessibilityservice.AccessibilityService;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

/**
 * Created by wangxiandeng on 2016/11/25.
 */

public class AccessibilityUtil {
    private static DevicePolicyManager policyManager;
    private static ComponentName componentName;

    /**
     * 单击返回功能
     *
     * @param service
     */
    public static void doBack(AccessibilityService service) {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    /**
     * 下拉打开通知栏
     *
     * @param service
     */
    public static void doPullDown(AccessibilityService service) {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    /**
     * 上拉返回桌面
     *
     * @param service
     */
    public static void doPullUp(AccessibilityService service) {
//        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
        lockScreen(service.getApplicationContext());
    }

    /**
     * 左右滑动打开多任务
     *
     * @param service
     */
    public static void doLeftOrRight(AccessibilityService service) {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }

        return false;
    }

    public static void lockScreen(Context context) {
        policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(context, LockReceiver.class);
        Log.d("aaaa", "isAdminActive : " + policyManager.isAdminActive(componentName));
        if (policyManager.isAdminActive(componentName)) {
            //判断是否有权限(激活了设备管理器)
            policyManager.lockNow();// 直接锁屏
//            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            activeManager(context);//激活设备管理器获取权限
        }
    }

    private static void activeManager(Context context) {
        //使用隐式意图调用系统方法来激活指定的设备管理器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏");
        context.startActivity(intent);
    }
}


