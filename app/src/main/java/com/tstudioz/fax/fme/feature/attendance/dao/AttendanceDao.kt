package com.tstudioz.fax.fme.feature.attendance.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Dolazak
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy

class AttendanceDao(private val dbManager: DatabaseManagerInterface) : AttendanceDaoInterface {

    override suspend fun insert(attendance: List<Dolazak>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            delete(Dolazak::class)
            attendance.forEach {
                this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

}