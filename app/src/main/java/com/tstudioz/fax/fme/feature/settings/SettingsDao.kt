package com.tstudioz.fax.fme.feature.settings

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import io.realm.kotlin.Realm

class SettingsDao(private val dbManager: DatabaseManagerInterface) {

    suspend fun deleteAll() {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            deleteAll()
        }
    }
}