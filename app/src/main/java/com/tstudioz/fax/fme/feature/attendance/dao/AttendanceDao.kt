package com.tstudioz.fax.fme.feature.attendance.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.AttendanceEntry
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.runBlocking

class AttendanceDao(private val dbManager: DatabaseManagerInterface) : AttendanceDaoInterface {

    override suspend fun insert(attendance: List<AttendanceEntry>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            delete(AttendanceEntry::class)
            attendance.forEach {
                this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    override suspend fun read(): List<List<AttendanceEntry>> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val result = realm.query(AttendanceEntry::class).find()
        val grouped = result.groupBy { it.`class` }.values.toList()

        return grouped.sortedBy { it.first().`class` }.sortedBy { it.first().semester }
    }

}