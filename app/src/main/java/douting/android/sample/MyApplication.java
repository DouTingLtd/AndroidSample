package douting.android.sample;

import android.app.Application;

import douting.hearing.core.Hearing;

/**
 * Created by WuXiang on 2016/9/21.
 * ..
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Hearing.init(this, "8a2bbc595a5aaf07015a6a276a3b5cf2");
    }
}
