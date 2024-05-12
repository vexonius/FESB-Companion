package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Predavanja
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import org.koin.core.KoinComponent
import org.koin.core.inject

class TimeTableDao(private val dbManager: DatabaseManagerInterface) : TimeTableDaoInterface {

    override suspend fun insert(classes: List<Predavanja>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            val oldClasses = this.query<Predavanja>().find()
            this.delete(oldClasses)
            classes.forEach {
                this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

}