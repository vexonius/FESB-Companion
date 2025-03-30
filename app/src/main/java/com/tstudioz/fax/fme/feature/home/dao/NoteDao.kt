package com.tstudioz.fax.fme.feature.home.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.NoteRoom
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query

/*class NoteDao1(private val dbManager: DatabaseManagerInterface) : NoteDaoInterface {

    override suspend fun getNotes(): List<NoteRoom> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        return realm.query<NoteRoom>().find()
    }

    override suspend fun insert(note: NoteRoom) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        realm.write {
            this.copyToRealm(note, updatePolicy = UpdatePolicy.ALL)
        }
    }

    override suspend fun delete(note: NoteRoom) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        realm.write {
            this.delete(this.query<NoteRoom>("id = $0", note.id).find())
        }
    }

}*/

@Dao
interface NoteDao{
    @Query("SELECT * FROM noteroom")
    fun getNotes(): List<NoteRoom>

    @Insert(onConflict = REPLACE)
    fun insert(note:NoteRoom)

    @Delete
    fun delete(note: NoteRoom)
}
