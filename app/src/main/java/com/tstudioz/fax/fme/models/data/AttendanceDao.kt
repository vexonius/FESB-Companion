package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.models.Dolazak
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import org.koin.core.KoinComponent
import org.koin.core.inject

class AttendanceDao : KoinComponent {

    private val dbManager: DatabaseManager by inject()

    suspend fun insert(attendance: List<Dolazak>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            this.deleteAll()
            attendance.forEach {
                this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

}