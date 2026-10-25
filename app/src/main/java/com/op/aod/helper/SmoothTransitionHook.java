package com.op.aod.helper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;

/**
 * 全景息屏全天常亮平滑过渡与灭屏闹钟智能防护
 */
public class SmoothTransitionHook extends XC_MethodHook {

    private final ClassLoader mClassLoader;

    public SmoothTransitionHook(ClassLoader classLoader) {
        this.mClassLoader = classLoader;
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        String methodName = param.method.getName();
        if ("setPanoramicSupportAllDayForApplication".equals(methodName)) {
            // 拦截任何试图将系统全景全天支持变量置 0 的行为
            if (param.args != null && param.args.length > 0) {
                param.args[0] = Boolean.TRUE;
            }
        } else if ("setHideAlarm".equals(methodName)) {
            // 终极灭屏闹钟拦截：全景息屏且用户配置为全天常亮时，绝不设置 5 秒灭屏闹钟
            try {
                Class<?> aodDataClass = XposedHelpers.findClass("com.oplus.systemui.aod.aodclock.constant.AodData", mClassLoader);
                Object sAodData = XposedHelpers.getStaticObjectField(aodDataClass, "sAodData");
                if (sAodData != null) {
                    boolean isPanoramic = (boolean) XposedHelpers.callMethod(sAodData, "isPanoramicAod");
                    if (isPanoramic) {
                        Object optionsMgr = XposedHelpers.getObjectField(sAodData, "mAodOptionsMgr");
                        if (optionsMgr != null) {
                            boolean isEnergySaving = (boolean) XposedHelpers.callMethod(optionsMgr, "isCurrentEnergySavingOpen");
                            if (!isEnergySaving) {
                                // 当前为全天常亮，拦截灭屏定时器
                                param.setResult(null);
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        String methodName = param.method.getName();
        if ("getDisplayPanelFeature".equals(methodName)) {
            try {
                if (param.args != null && param.args.length > 0 && Integer.valueOf(217).equals(param.args[0])) {
                    int[] res = (int[]) param.getResult();
                    if (res == null || res.length == 0) {
                        param.setResult(new int[]{14}); // 2 | 4 | 8
                    } else {
                        res[0] |= 14;
                        param.setResult(res);
                    }
                }
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
            return;
        }

        if ("isPanoramicProcessTypeInEnergySaveSituation".equals(methodName)) {
            try {
                // 确保直板机全景息屏不被误判为节能状态
                param.setResult(Boolean.FALSE);
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
            return;
        }

        // initSmoothTransitionState 或 setPanoramicSupportedByRemote
        Object obj = param.thisObject;
        if (obj == null) return;

        String[] fields = {
            "isSupportPanoramicAllDay",
            "isSupportPanoramicAllDayByPanelFeature",
            "isSupportPanoramicByPanelFeature",
            "isSupportPanoramic",
            "isSupportSmoothTransition"
        };
        for (String fieldName : fields) {
            try {
                Field f = XposedHelpers.findField(obj.getClass(), fieldName);
                if (f != null) {
                    f.setAccessible(true);
                    f.setBoolean(obj, true);
                }
            } catch (Throwable ignored) {
            }
        }

        try {
            XposedHelpers.callMethod(obj, "setPanoramicSupportAllDayForApplication", true);
        } catch (Throwable ignored) {
        }

        try {
            XposedHelpers.callMethod(obj, "setDisplayStateByCurrentAodStyleType");
        } catch (Throwable ignored) {
        }
    }
}
