package com.tstudioz.fax.fme.feature.login.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.UserRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.DeletedObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class UserDao(private val dbManager: DatabaseManagerInterface): UserDaoInterface {

    override fun observeUserChanges(): Flow<Unit> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val user = realm.query(UserRealm::class).first()

        return user
            .asFlow()
            .map { it is DeletedObject }
            .filter { it }
            .map {  }
    }

    override suspend fun insert(user: UserRealm) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write { copyToRealm(user) }

        realm.close()
    }

    override suspend fun getUser(): UserRealm? {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        return realm.query<UserRealm>().find().firstOrNull()
    }

    override suspend fun deleteAllUserData() {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            deleteAll()
        }
    }

}
