package com.tstudioz.fax.fme.Application;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by amarthus on 11-Sep-17.
 */

public class FESBCompanion extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}