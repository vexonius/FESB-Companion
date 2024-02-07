package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Korisnik
import io.realm.kotlin.Realm

class UserDao(private val dbManager: DatabaseManagerInterface): UserDaoInterface {

    override suspend fun insert(user: Korisnik) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write { copyToRealm(user) }
    }

}