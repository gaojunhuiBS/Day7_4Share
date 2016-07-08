package com.gaojunhui.day7_4share;

import android.app.Application;

import com.umeng.socialize.PlatformConfig;

/**
 * Created by Administrator on 2016/7/7.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        // QQ和Qzone appid appkey
    }
}
