package com.tstudioz.fax.fme.feature.login.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.UserRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

class UserDao(private val dbManager: DatabaseManagerInterface): UserDaoInterface {

    override suspend fun insert(user: UserRealm) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write { copyToRealm(user) }

        realm.close()
    }

    override suspend fun getUser(): UserRealm {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        return realm.query<UserRealm>().find().first()
    }

}
