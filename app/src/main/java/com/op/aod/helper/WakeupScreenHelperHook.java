package com.op.aod.helper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

/**
 * 修复灭屏缓冲期（锁屏前一秒）误唤醒的关键拦截器
 */
public class WakeupScreenHelperHook extends XC_MethodHook {

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        try {
            String methodName = param.method.getName();
            if ("getPreventScreenOnInSleep".equals(methodName)) {
                param.setResult(Boolean.TRUE);
            } else if ("preventScreenOn".equals(methodName)) {
                param.setResult(Boolean.TRUE);
            }
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }
}
