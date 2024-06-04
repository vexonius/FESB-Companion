package com.tstudioz.fax.fme.feature.timetable.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.EventRealm
import com.tstudioz.fax.fme.database.models.fromRealmObject
import com.tstudioz.fax.fme.database.models.toRealmObject
import com.tstudioz.fax.fme.feature.timetable.dao.interfaces.TimeTableDaoInterface
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query

class TimeTableDao(private val dbManager: DatabaseManagerInterface) : TimeTableDaoInterface {

    override suspend fun insert(classes: List<Event>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            val oldClasses = this.query<EventRealm>().find()
            this.delete(oldClasses)
            classes.forEach {
                this.copyToRealm(toRealmObject(it), updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    override suspend fun getCachedEvents(): List<Event> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        return realm.query<EventRealm>().find().map { fromRealmObject(it) }
    }

}