package org.dehaxsoft.vk.onlinelogger;

import android.app.Application;

import com.vk.sdk.VKSdk;

/**
 * Created by Dehax on 13.11.2015.
 */
public class OnlineLoggerApplication extends Application {

    private static OnlineLoggerApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        VKSdk.initialize(this);
    }

    public static OnlineLoggerApplication getInstance() {
        return mInstance;
    }
}
