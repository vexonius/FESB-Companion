package com.tstudioz.fax.fme.models.data

import android.util.Log
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Korisnik
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

class UserDao(private val dbManager: DatabaseManagerInterface): UserDaoInterface {

    override suspend fun insert(user: Korisnik) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.writeBlocking { copyToRealm(user) }

        Log.d("UserDao", "User inserted: ${user.username}")
        realm.close()
    }

}
