package com.devshubhpatel.quicksend;

import android.app.Application;
import android.content.res.Configuration;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by patel on 20-07-2017.
 */

public class InitClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

    public static long getUniqueId(Realm realm, Class className) {
        Number number = realm.where(className).max("_id");
        if (number == null) return 1;
        else return (long) number + 1;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
