package com.tstudioz.fax.fme.feature.home.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.NoteRealm
import com.tstudioz.fax.fme.feature.menza.dao.interfaces.NoteDaoInterface
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query

class NoteDao(private val dbManager: DatabaseManagerInterface) : NoteDaoInterface {

    override suspend fun getNotes(): List<NoteRealm> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        return realm.query<NoteRealm>().find()
    }

    override suspend fun insert(note: NoteRealm) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        realm.write {
            this.copyToRealm(note, updatePolicy = UpdatePolicy.ALL)
        }
    }

    override suspend fun delete(note: NoteRealm) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        realm.write {
            this.delete(this.query<NoteRealm>("id = $0", note.id).find())
        }
    }

}
