package com.kyee.iwis.nana;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.kyee.iwis.nana.ThirdPartyInfusion.InfusionFloatAction;
import com.kyee.iwis.nana.monitor.CrashCatcher;
import com.kyee.iwis.nana.videoplayer.manager.Player;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.smtt.sdk.QbSdk;

import cn.feng.skin.manager.loader.SkinManager;
import xcrash.ICrashCallback;
import xcrash.XCrash;

public class LeakApplication extends Application {
    public static RefWatcher getRefWatcher(Context context) {
        LeakApplication application = (LeakApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;
    private static LeakApplication application;

    public static LeakApplication getContext() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
            BootUpReceiver bootReceiver = new BootUpReceiver();
            this.registerReceiver(bootReceiver, intentFilter);
        }
        application = this;
        CrashCatcher.init();
        refWatcher = LeakCanary.install(this);
        StatusConstants.initStatus();
        try {
            initNativeCrashHandle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //BlockDetectByChoreographer.start();
        Player.init(this);

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.i("x5", "x5内核初始化完成");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.i("x5", "x5View初始化完成");
                InfusionFloatAction.getInstance().start(application);
                InfusionFloatAction.getInstance().hide();
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(this, cb);


        //初始化换肤组件Android-Skin-loader,并加载上次使用的皮肤
        SkinManager.getInstance().init(this);
        SkinManager.getInstance().load();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    private void initNativeCrashHandle() {

        try {
            XCrash.InitParameters initParameters = new XCrash.InitParameters();
            String dir = "";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                dir = Environment.getExternalStorageDirectory().getPath() + "/crash";
            } else {
                dir = Environment.getExternalStoragePublicDirectory("") + "/crash";
            }
            initParameters.setLogDir(dir);
            initParameters.disableJavaCrashHandler();
            initParameters.setNativeCallback(new ICrashCallback() {
                @Override
                public void onCrash(String logPath, String emergency) {
                    try {
                        CrashCatcher.handleException(new Throwable("native crash"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            xcrash.XCrash.init(this, initParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
