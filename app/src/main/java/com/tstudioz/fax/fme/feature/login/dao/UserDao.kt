package com.tstudioz.fax.fme.feature.login.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.UserRealm
import io.realm.kotlin.Realm

class UserDao(private val dbManager: DatabaseManagerInterface): UserDaoInterface {

    override suspend fun insert(user: UserRealm) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write { copyToRealm(user) }

        realm.close()
    }

}
