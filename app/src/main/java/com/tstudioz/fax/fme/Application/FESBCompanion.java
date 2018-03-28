package com.tstudioz.fax.fme.Application;

import android.app.Application;


import com.google.android.gms.ads.MobileAds;
import com.orhanobut.hawk.Hawk;
import com.tstudioz.fax.fme.migrations.CredMigration;

import java.io.File;
import java.security.SecureRandom;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by amarthus on 11-Sep-17.
 */

public class FESBCompanion extends Application {
    RealmConfiguration CredRealmCf;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

         CredRealmCf = new RealmConfiguration.Builder()
                .name("encryptedv2.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .encryptionKey(getRealmKey())
                .build();
        Realm.setDefaultConfiguration(CredRealmCf);

        checkOldVersion();

       // MobileAds.initialize(this, "ca-app-pub-5944203368510130~8955475006");
    }

    public void checkOldVersion(){

        File newRealmFile = new File(CredRealmCf.getPath());
        if (!newRealmFile.exists()) {
            // Migrate old Realm and delete old
            RealmConfiguration old = new RealmConfiguration.Builder()
                    .name("encrypted.realm")
                    .schemaVersion(7)
                    .migration(new CredMigration())
                    .build();

            Realm realm = Realm.getInstance(old);
            realm.writeEncryptedCopyTo(newRealmFile, getRealmKey());
            realm.close();
            Realm.deleteRealm(old);
        }
    }

    private byte[] getRealmKey(){

        Hawk.init(getApplicationContext()).build();

        if(Hawk.contains("masterKey")){
            byte[] array = Hawk.get("masterKey");
            return array;
        }

        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);

        Hawk.put("masterKey", bytes);

        return bytes;
    }
}