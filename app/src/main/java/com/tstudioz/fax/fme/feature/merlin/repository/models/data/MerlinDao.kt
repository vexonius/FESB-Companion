package com.tstudioz.fax.fme.feature.merlin.repository.models.data

import android.util.Log
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Korisnik
import io.realm.kotlin.Realm

class MerlinDao(private val dbManager: DatabaseManagerInterface): MerlinDaoInterface {

    override suspend fun insert(user: Korisnik) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.writeBlocking { copyToRealm(user) }

        Log.d("MerlinDao", "User inserted: ${user.username}")
        realm.close()
    }

}
