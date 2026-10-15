package com.op.aod.helper;

import android.view.MotionEvent;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Method;

/**
 * ColorOS AOD Helper - 纯净原生 Xposed 钩子入口
 * 完全由 Gemini 3.8 flash 基于“ColorOS AOD增强”与“别他妈亮了”二次开发重构。
 */
public class HookInit implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if ("com.oplus.aod".equals(lpparam.packageName)) {
            hookAod(lpparam.classLoader);
        } else if ("com.android.systemui".equals(lpparam.packageName)) {
            hookSystemUi(lpparam.classLoader);
        }
    }

    private static void hookAod(ClassLoader classLoader) {
        try {
            Class<?> clazz = XposedHelpers.findClass("com.oplus.aod.util.SettingsUtils", classLoader);
            for (Method m : clazz.getDeclaredMethods()) {
                if ("getKeyAodAllDaySupportSettings".equals(m.getName())) {
                    XposedBridge.hookMethod(m, XC_MethodReplacement.returnConstant(Integer.valueOf(1)));
                }
            }
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    private static void hookSystemUi(ClassLoader classLoader) {
        // 1. 全景息屏常亮支持注入
        try {
            SmoothTransitionHook transitionHook = new SmoothTransitionHook();
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.aod.display.SmoothTransitionController",
                classLoader,
                "initSmoothTransitionState",
                transitionHook
            );
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.aod.display.SmoothTransitionController",
                classLoader,
                "setPanoramicSupportedByRemote",
                transitionHook
            );
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        // 2. 规避全景息屏多余手势监听冲突
        try {
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.aod.scene.PanoramicAodGestureController",
                classLoader,
                "registerPanoramicAodGestureMonitor",
                XC_MethodReplacement.DO_NOTHING
            );
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        // 3. 拦截双击息屏组件中的单次误触信号
        try {
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.keyguard.gesture.OplusDoubleClickSleep$OnDoubleClickListener",
                classLoader,
                "onSingleTapConfirmed",
                MotionEvent.class,
                XC_MethodReplacement.returnConstant(Boolean.FALSE)
            );
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        // 4. 精准拦截息屏过渡期的锁屏前一秒误唤醒
        try {
            WakeupScreenHelperHook wakeupHook = new WakeupScreenHelperHook();
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.aod.WakeupScreenHelper",
                classLoader,
                "getPreventScreenOnInSleep",
                wakeupHook
            );
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.aod.WakeupScreenHelper",
                classLoader,
                "preventScreenOn",
                wakeupHook
            );
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }
}
