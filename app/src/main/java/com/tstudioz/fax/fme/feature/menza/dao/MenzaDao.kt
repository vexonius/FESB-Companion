package com.tstudioz.fax.fme.feature.menza.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Meni
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query

class MenzaDao(private val dbManager: DatabaseManagerInterface) : MenzaDaoInterface {

    override suspend fun insert(classes: List<Meni>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            val oldClasses = this.query<Meni>().find()
            this.delete(oldClasses)
            classes.forEach {
                this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    override suspend fun getCachedMenza(): List<Meni> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        return realm.query<Meni>().find()
    }

}