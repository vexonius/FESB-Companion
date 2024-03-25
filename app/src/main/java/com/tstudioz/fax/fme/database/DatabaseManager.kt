package com.tstudioz.fax.fme.database

import com.tstudioz.fax.fme.database.models.*
import io.realm.kotlin.RealmConfiguration

class DatabaseManager {

    fun getDefaultConfiguration(): RealmConfiguration {
        return RealmConfiguration.Builder(
            setOf(
                Korisnik::class,
                Predavanja::class,
                LeanTask::class,
                Dolazak::class,
                Materijal::class,
                Meni::class,
                Racun::class,
                Kolegij::class,
                KolegijTjedan::class))
            .name("default.realm")
            .schemaVersion(1)
            .encryptionKey("nekikljuckojicemopromjenitiubuducnostialisadjetujernedamisebolje".toByteArray())
            .deleteRealmIfMigrationNeeded()
            .build()
    }

}