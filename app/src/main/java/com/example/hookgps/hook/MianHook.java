package com.example.hookgps.hook;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.example.hookgps.ICustomService;
import com.example.hookgps.model.LatLng;
import com.example.hookgps.server.CustomService;
import com.example.hookgps.utils.HKLocationGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MianHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("android")) {
            XposedHelpers.findAndHookMethod(
                    "com.android.server.location.provider.LocationProviderManager",
                    lpparam.classLoader,
                    "onReportLocation",
                    "android.location.LocationResult",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            ICustomService service = CustomService.getService();
                            if (service == null) return;
                            LatLng latLng = service.getLatng();

                            // 1. 获取原始的 LocationResult 对象
                            Object originalResult = param.args[0];
                            if (originalResult == null) return;
                            // 2. 提取内部的 Location 列表
                            List<Location> locations = (List<Location>) XposedHelpers.getObjectField(originalResult, "mLocations");
                            if (locations == null || locations.isEmpty()) return;
                            // 3. 遍历并修改 Location 对象的经纬度
                            for (Location location : locations) {
                                location.setLatitude(latLng.latitude);      // 替换为你的目标纬度
                                location.setLongitude(latLng.longitude);  // 替换为你的目标经度
                                // 注意：强烈建议同时修改时间戳和精度，防止被系统判定为异常数据
                                location.setTime(System.currentTimeMillis());
                                location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                            }

                            // 4. 【最关键的一步】使用修改后的列表，重新创建一个新的 LocationResult
                            Class<?> locationResultClass = XposedHelpers.findClass("android.location.LocationResult", lpparam.classLoader);
                            Object newResult = XposedHelpers.callStaticMethod(locationResultClass, "create", locations);
                            // 5. 将新构建的 LocationResult 替换掉原始参数
                            param.args[0] = newResult;
                        }
                    }
            );
        }
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        try {
            createServer();
        } catch (Throwable e) {
            XposedBridge.log("注册系统服务失败:" + e.getMessage());
        }
    }

    private void createServer() throws ClassNotFoundException {
        // android 5.0+
        XposedBridge.hookAllMethods(Class.forName("android.app.ActivityThread"),
                "systemMain", new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                        Class<?> ams = XposedHelpers.findClass("com.android.server.am.ActivityManagerService", classLoader);
                        XposedBridge.hookAllConstructors(ams, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                if (param.args[0] instanceof Context context) {
                                    CustomService.register(context, classLoader);
                                }
                            }
                        });
                    }
                });
    }
}
