package com.tstudioz.fax.fme.feature.menza.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.feature.menza.models.Menza
import com.tstudioz.fax.fme.feature.menza.models.MenzaRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query

class MenzaDao(private val dbManager: DatabaseManagerInterface) : MenzaDaoInterface {

    override suspend fun insert(menza: Menza?) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val realmified = menza?.toRealm() ?: return
        realm.write {
            val oldClasses = this.query<MenzaRealm>().find()
            this.delete(oldClasses)
            this.copyToRealm(realmified, updatePolicy = UpdatePolicy.ALL)
        }
    }

    override suspend fun getCachedMenza(): Menza? {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        return realm.query<MenzaRealm>().find().first().fromRealm()
    }

}