package com.example.hookgps.server;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.example.hookgps.ICustomService;
import com.example.hookgps.model.LatLng;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedHelpers;

public class CustomService extends ICustomService.Stub {
    private final Context context;
    private static ICustomService mClient;
    private LatLng latLng;

    public CustomService(Context context) {
        this.context = context;
        latLng = new LatLng(22.439353513701644, 114.25195720590678);
    }

    public static ICustomService getService() {
        if (mClient == null) {
            try {
                Class<?> svcManager = Class.forName("android.os.ServiceManager");
                Method getServiceMethod = svcManager.getDeclaredMethod("getService", String.class);
                IBinder binder = (IBinder) getServiceMethod.invoke(null, getServiceName());
                mClient = ICustomService.Stub.asInterface(binder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mClient;
    }

    public static void register(Context context, ClassLoader classLoader) {
        Class<?> svcManager = XposedHelpers.findClass("android.os.ServiceManager", classLoader);
        CustomService customService = new CustomService(context);
        XposedHelpers.callStaticMethod(svcManager,
                /* methodName */"addService",
                /* name       */getServiceName(),
                /* service    */ customService,
                /* allowIsolated */ true);
    }

    private static String getServiceName() {
        // 5.0 之后，selinux "user." 前缀
        return "user." + "customservice";
    }


    @Override
    public void setLatng(LatLng par) throws RemoteException {
        this.latLng = par;
    }

    @Override
    public LatLng getLatng() throws RemoteException {
        return this.latLng;
    }
}
