package com.op.aod.helper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * 修复灭屏缓冲期（锁屏前一秒）误唤醒的关键拦截器
 */
public final class WakeupScreenHelperHook extends XC_MethodHook {

    private final ClassLoader classLoader;

    public WakeupScreenHelperHook(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        try {
            Class<?> aodDataClass = XposedHelpers.findClass("com.oplus.systemui.aod.aodclock.constant.AodData", this.classLoader);
            Object sAodData = XposedHelpers.getStaticObjectField(aodDataClass, "sAodData");
            if (sAodData == null) {
                return;
            }
            Boolean isPanoramic = (Boolean) XposedHelpers.callMethod(sAodData, "isPanoramicAod");
            if (isPanoramic == null || !isPanoramic) {
                return;
            }

            // 拦截误唤醒流程
            param.setResult(null);

            Class<?> wakeUpControllerClass = XposedHelpers.findClass("com.oplus.systemui.aod.display.OplusWakeUpController", this.classLoader);
            Object controllerInstance = XposedHelpers.getStaticObjectField(wakeUpControllerClass, "instance");
            if (controllerInstance == null) {
                return;
            }

            boolean isUpsideDown = XposedHelpers.getBooleanField(controllerInstance, "isUpsideDown");
            if (!isUpsideDown) {
                XposedHelpers.callMethod(controllerInstance, "notifyWakeUpCallback", 0);
            }
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }
}
