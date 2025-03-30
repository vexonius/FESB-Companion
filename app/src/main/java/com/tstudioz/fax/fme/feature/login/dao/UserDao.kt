package com.tstudioz.fax.fme.feature.login.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.tstudioz.fax.fme.common.user.models.UserRoom
import kotlinx.coroutines.flow.Flow

/*
class UserDao1(private val dbManager: DatabaseManagerInterface): UserDaoInterface {

    override fun observeUserChanges(): Flow<Unit> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val user = realm.query(UserRoom::class).first()

        return user
            .asFlow()
            .map { it is DeletedObject }
            .filter { it }
            .map {  }
    }

    override suspend fun insert(user: UserRoom) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write { copyToRealm(user) }

        realm.close()
    }

    override suspend fun getUser(): UserRoom {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        return realm.query<UserRoom>().find().first()
    }

    override suspend fun deleteAllUserData() {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            deleteAll()
        }
    }

}
*/

@Dao
interface UserDao{

    @Insert(onConflict = REPLACE)
    fun insert(user: UserRoom)

    @Query("SELECT * FROM userroom")
    fun getUser(): UserRoom

    @Query("DELETE FROM userroom")
    fun deleteAllUserData()

    @Query("SELECT * FROM userroom LIMIT 1")
    fun observeUserChanges(): Flow<UserRoom?>
}
