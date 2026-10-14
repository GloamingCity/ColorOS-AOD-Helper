package com.op.aod.enhance;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * ColorOS AOD Helper - 功能 1：全景息屏解锁全天常亮显示
 * Hook OplusAodUtils 判断逻辑，解锁全天常亮选项并允许全景息屏始终显示
 */
public class HookEntryXposed implements IXposedHookLoadPackage {

    private static final String TAG = "ColorOS_AOD_Helper";
    private static final String TARGET_AOD = "com.oplus.aod";
    private static final String TARGET_SYSUI = "com.android.systemui";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!TARGET_AOD.equals(lpparam.packageName) && !TARGET_SYSUI.equals(lpparam.packageName)) {
            return;
        }

        try {
            hookAodUtils(lpparam.classLoader);
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": Hook AodUtils error: " + t.getMessage());
        }
    }

    private void hookAodUtils(ClassLoader classLoader) {
        // 强制解锁全景息屏全天常亮功能开关与运行时判定
        findAndHook(classLoader, "com.oplus.aod.utils.OplusAodUtils", "isSupportFullScreenAod", true);
        findAndHook(classLoader, "com.oplus.aod.utils.OplusAodUtils", "isAlwaysOnAod", true);
        findAndHook(classLoader, "com.oplus.aod.utils.OplusAodUtils", "isSupportAod", true);
        findAndHook(classLoader, "com.oplus.systemui.aod.utils.OplusAodUtils", "isSupportFullScreenAod", true);
        findAndHook(classLoader, "com.oplus.systemui.aod.utils.OplusAodUtils", "isAlwaysOnAod", true);
    }

    private void findAndHook(ClassLoader cl, String className, String methodName, final Object returnValue) {
        try {
            Class<?> clazz = XposedHelpers.findClassIfExists(className, cl);
            if (clazz != null) {
                XposedBridge.hookAllMethods(clazz, methodName, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        param.setResult(returnValue);
                    }
                });
            }
        } catch (Throwable ignored) {
        }
    }
}
