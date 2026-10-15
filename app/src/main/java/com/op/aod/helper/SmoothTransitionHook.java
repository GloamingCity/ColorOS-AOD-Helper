package com.op.aod.helper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * 强制激活系统界面全景息屏平滑过渡支持
 */
public class SmoothTransitionHook extends XC_MethodHook {

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        try {
            XposedHelpers.setBooleanField(param.thisObject, "mIsPanoramicSupported", true);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }
}
