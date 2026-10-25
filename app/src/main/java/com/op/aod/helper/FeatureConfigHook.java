package com.op.aod.helper;

import de.robv.android.xposed.XC_MethodHook;

/**
 * 针对 ColorOS 平台特性开关的拦截器（破开天玑机型内门锁）
 * 拦截 disable_aod_all_day_mode 和 video_mode_aod_support，直接返回 false，
 * 从而在天玑机型上完整展现全景息屏的“节能显示 / 定时显示 / 全天显示”单选控制面板。
 */
public class FeatureConfigHook extends XC_MethodHook {

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        if (param.args != null && param.args.length > 0) {
            Object arg0 = param.args[0];
            if (arg0 instanceof String) {
                String feature = (String) arg0;
                if ("oplus.software.disable_aod_all_day_mode".equals(feature)
                        || "oplus.software.display.video_mode_aod_support".equals(feature)) {
                    param.setResult(Boolean.FALSE);
                }
            }
        }
    }
}
