package com.tstudioz.fax.fme.database

import android.app.Application
import androidx.room.Database
import androidx.room.RoomDatabase
import com.tstudioz.fax.fme.database.models.EventRealm
import com.tstudioz.fax.fme.database.models.NoteRoom
import com.tstudioz.fax.fme.database.models.UserRealm
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDao
import com.tstudioz.fax.fme.feature.attendance.models.AttendanceEntry
import com.tstudioz.fax.fme.feature.home.dao.NoteDao
import com.tstudioz.fax.fme.feature.iksica.dao.IksicaDao
import com.tstudioz.fax.fme.feature.iksica.models.ReceiptRoom
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataRoom
import com.tstudioz.fax.fme.feature.menza.models.MenzaRealm
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.Year
import io.realm.kotlin.RealmConfiguration

class DatabaseManager(private val keystoreManager: KeystoreManagerInterface, private val application: Application) :
    DatabaseManagerInterface {

    override fun getDefaultConfiguration(): RealmConfiguration {
        return RealmConfiguration.Builder(
            setOf(
                UserRealm::class,
                MenzaRealm::class,
                EventRealm::class,
                StudomatSubject::class,
                Year::class
            )
        )
            .name("default.realm")
            .schemaVersion(1)
            .encryptionKey(keystoreManager.getOrCreateEncryptionKey())
            .deleteRealmIfMigrationNeeded()
            .build()
    }
}

@Database(
    entities = [/*User::class,*/ AttendanceEntry::class, NoteRoom::class, StudentDataRoom::class, ReceiptRoom::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    //abstract fun userDao(): UserDao
    abstract fun AttendanceDaoRoom(): AttendanceDao
    abstract fun NoteRoom(): NoteDao
    abstract fun IksicaDao(): IksicaDao
}