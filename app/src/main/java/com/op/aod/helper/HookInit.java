package com.op.aod.helper;

import android.view.MotionEvent;
import de.robv.android.xposed.IXposedHookLoadPackage;
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
        // 1. 外门锁：常亮显示设置卡片入口
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

        // 2. 内门锁：破除天玑机型隐藏全天常亮模式列表
        try {
            XposedHelpers.findAndHookMethod(
                "com.oplus.content.OplusFeatureConfigManager",
                classLoader,
                "hasFeature",
                String.class,
                new FeatureConfigHook()
            );
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    private static void hookSystemUi(ClassLoader classLoader) {
        SmoothTransitionHook transitionHook = new SmoothTransitionHook(classLoader);

        // 1. 屏幕底层 Panel Feature 补齐（2:平滑过渡, 4:全景, 8:全天常亮）
        try {
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.aod.display.SmoothTransitionController",
                classLoader,
                "getDisplayPanelFeature",
                int.class,
                int[].class,
                transitionHook
            );
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        // 2. 全景息屏常亮初始化与远端激活
        try {
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
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.aod.display.SmoothTransitionController",
                classLoader,
                "setPanoramicSupportAllDayForApplication",
                boolean.class,
                transitionHook
            );
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        // 3. 规避全景息屏误判为节能状态
        try {
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.aod.display.AODDisplayUtil",
                classLoader,
                "isPanoramicProcessTypeInEnergySaveSituation",
                transitionHook
            );
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        // 4. 灭屏闹钟智能防护（全天常亮模式下绝不黑屏）
        try {
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.aod.aodclock.off.AodUpdateManager",
                classLoader,
                "setHideAlarm",
                transitionHook
            );
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        // 5. 规避全景息屏多余手势监听冲突
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

        // 6. 拦截双击息屏组件中的单次误触信号
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

        // 7. 精准拦截息屏过渡期的锁屏前一秒误唤醒
        try {
            WakeupScreenHelperHook wakeupHook = new WakeupScreenHelperHook(classLoader);
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.notification.interruption.wakeup.WakeupScreenHelper",
                classLoader,
                "powerOnScreen",
                wakeupHook
            );
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        // 8. 屏蔽全景息屏原生处理流程中的多余唤醒判定
        try {
            Class<?> clickSleepClass = XposedHelpers.findClass("com.oplus.systemui.keyguard.gesture.OplusDoubleClickSleep", classLoader);
            for (Method m : clickSleepClass.getDeclaredMethods()) {
                if (m.getName().contains("processPanoramicWakeup")) {
                    XposedBridge.hookMethod(m, XC_MethodReplacement.returnConstant(Boolean.FALSE));
                }
            }
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }
}
