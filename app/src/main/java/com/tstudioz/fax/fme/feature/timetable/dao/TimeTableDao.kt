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
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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

    override suspend fun getEvents(): List<Event> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        return realm.query<EventRealm>().find().map { fromRealmObject(it) }
    }

    override suspend fun getEventsAsync(): Flow<List<Event>> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val events = realm.query(EventRealm::class)

        return  events
            .asFlow()
            .map { it.list }
            .map { results -> results.map { fromRealmObject(it) } }
    }

}
