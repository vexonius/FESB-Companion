package com.tstudioz.fax.fme.database

import com.tstudioz.fax.fme.database.models.*
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.Year
import com.tstudioz.fax.fme.feature.iksica.models.ReceiptRealm
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataRealm
import com.tstudioz.fax.fme.feature.menza.models.MenzaRealm
import io.realm.kotlin.RealmConfiguration

class DatabaseManager: DatabaseManagerInterface {

    override fun getDefaultConfiguration(): RealmConfiguration {
        return RealmConfiguration.Builder(
            setOf(
                UserRealm::class,
                NoteRealm::class,
                AttendanceEntry::class,
                MenzaRealm::class,
                EventRealm::class,
                StudomatSubject::class,
                Year::class,
                IksicaBalance::class,
                ReceiptRealm::class,
                StudentDataRealm::class))
            .name("default.realm")
            .schemaVersion(1)
            .encryptionKey("nekikljuckojicemopromjenitiubuducnostialisadjetujernedamisebolje".toByteArray())
            .deleteRealmIfMigrationNeeded()
            .build()
    }

}