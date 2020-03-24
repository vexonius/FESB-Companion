package com.tstudioz.fax.fme.Application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.multidex.MultiDex;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.android.gms.ads.MobileAds;
import com.orhanobut.hawk.Hawk;
import com.tstudioz.fax.fme.migrations.CredMigration;

import java.io.File;
import java.security.SecureRandom;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;

public class FESBCompanion extends Application {
    RealmConfiguration CredRealmCf;

    private static OkHttpClient okHttpClient;
    private static FESBCompanion instance;
    private static SharedPreferences shPref;

    public static FESBCompanion getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;

        Realm.init(this);

        CredRealmCf = new RealmConfiguration.Builder()
                .name("encryptedv2.realm")
                .schemaVersion(7)
                .migration(new CredMigration())
                .encryptionKey(getRealmKey())
                .build();
        Realm.setDefaultConfiguration(CredRealmCf);

        checkOldVersion();

        MobileAds.initialize(this, "ca-app-pub-5944203368510130~8955475006");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void checkOldVersion() {

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

    private byte[] getRealmKey() {

        Hawk.init(this).build();

        if (Hawk.contains("masterKey")) {
            byte[] array = Hawk.get("masterKey");
            return array;
        }

        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);

        Hawk.put("masterKey", bytes);

        return bytes;
    }

    public SharedPreferences getSP() {
        if (shPref == null)
            shPref = getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE);

        return shPref;
    }

    public OkHttpClient getOkHttpInstance() {

        if (okHttpClient == null) {

            CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(),
                    new SharedPrefsCookiePersistor(getApplicationContext()));

            okHttpClient = new OkHttpClient().newBuilder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .cookieJar(cookieJar)
                    .build();
        }

        return okHttpClient;
    }
}