package com.tstudioz.fax.fme.feature.timetable.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.tstudioz.fax.fme.database.models.EventRoom
import kotlinx.coroutines.flow.Flow

/*class TimeTableDao(private val dbManager: DatabaseManagerInterface) : TimeTableDaoInterface {

    override suspend fun insert(classes: List<Event>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            val oldClasses = this.query<EventRoom>().find()
            this.delete(oldClasses)
            classes.forEach {
                this.copyToRealm(toRoomObject(it), updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    override suspend fun getEvents(): List<Event> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        return realm.query<EventRoom>().find().map { fromRoomObject(it) }
    }

    override suspend fun getEventsAsync(): Flow<List<Event>> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val events = realm.query(EventRoom::class)

        return  events
            .asFlow()
            .map { it.list }
            .map { results -> results.map { fromRoomObject(it) } }
    }

}*/

@Dao
interface TimeTableDao{
    @Query("DELETE FROM eventroom")
    fun deleteAll()

    @Insert(onConflict = REPLACE)
    fun insert(classes: List<EventRoom>)

    @Query("SELECT * FROM eventroom")
    fun getEvents(): List<EventRoom>

    @Query("SELECT * FROM eventroom")
    fun getEventsAsync(): Flow<List<EventRoom>>
}
