package com.tstudioz.fax.fme.database

import com.tstudioz.fax.fme.database.models.*
import com.tstudioz.fax.fme.feature.studomat.dataclasses.StudomatSubject
import io.realm.kotlin.RealmConfiguration

class DatabaseManager: DatabaseManagerInterface {

    override fun getDefaultConfiguration(): RealmConfiguration {
        return RealmConfiguration.Builder(
            setOf(
                Korisnik::class,
                Note::class,
                AttendanceEntry::class,
                Meni::class,
                EventRealm::class,
                StudomatSubject::class))
            .name("default.realm")
            .schemaVersion(1)
            .encryptionKey("nekikljuckojicemopromjenitiubuducnostialisadjetujernedamisebolje".toByteArray())
            .deleteRealmIfMigrationNeeded()
            .build()
    }

}