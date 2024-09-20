package com.tstudioz.fax.fme.database

import com.tstudioz.fax.fme.database.models.*
import com.tstudioz.fax.fme.feature.iksica.models.IksicaBalanceRealm
import com.tstudioz.fax.fme.feature.iksica.models.ReceiptRealm
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataIksicaRealm
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
                IksicaBalanceRealm::class,
                ReceiptRealm::class,
                StudentDataIksicaRealm::class))
            .name("default.realm")
            .schemaVersion(1)
            .encryptionKey("nekikljuckojicemopromjenitiubuducnostialisadjetujernedamisebolje".toByteArray())
            .deleteRealmIfMigrationNeeded()
            .build()
    }

}