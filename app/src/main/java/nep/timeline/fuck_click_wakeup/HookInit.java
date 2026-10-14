package nep.timeline.fuck_click_wakeup;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * ColorOS AOD Helper - 功能 2：全景息屏手势行为完全跟随局部息屏（双击亮屏修复）
 * 针对 ColorOS 15/16/17 架构拦截全景息屏手势监视器并分发双击唤醒事件
 */
public class HookInit implements IXposedHookLoadPackage {

    private static final String TAG = "ColorOS_AOD_Helper_Gesture";
    private static final String TARGET_SYSUI = "com.android.systemui";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!TARGET_SYSUI.equals(lpparam.packageName)) {
            return;
        }

        try {
            hookGestureController(lpparam.classLoader);
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": Gesture hook error: " + t.getMessage());
        }
    }

    private void hookGestureController(ClassLoader classLoader) {
        // ColorOS 15/16/17 全景息屏手势控制器 Hook
        Class<?> controllerClass = XposedHelpers.findClassIfExists(
            "com.oplus.systemui.aod.scene.PanoramicAodGestureController", classLoader
        );
        if (controllerClass != null) {
            XposedBridge.hookAllMethods(controllerClass, "registerPanoramicAodGestureMonitor", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    XposedBridge.log(TAG + ": PanoramicAodGestureMonitor hooked & enabled");
                }
            });
        }

        // 双击手势唤醒判定
        Class<?> wakeupUtil = XposedHelpers.findClassIfExists(
            "com.oplus.systemui.aod.utils.OpWakeupUtil", classLoader
        );
        if (wakeupUtil != null) {
            XposedBridge.hookAllMethods(wakeupUtil, "isDoubleTapEnabled", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    param.setResult(true);
                }
            });
        }
    }
}
