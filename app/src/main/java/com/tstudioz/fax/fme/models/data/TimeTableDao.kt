package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.EventRealm
import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.database.models.toRealmObject
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import org.koin.core.KoinComponent
import org.koin.core.inject

class TimeTableDao(private val dbManager: DatabaseManagerInterface) : TimeTableDaoInterface {

    override suspend fun insert(classes: List<Event>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            val oldClasses = this.query<EventRealm>().find()
            this.delete(oldClasses)
            classes.forEach {
                this.copyToRealm(toRealmObject(it) , updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

}