package com.tstudioz.fax.fme.feature.studomat.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.feature.studomat.dataclasses.StudomatSubject
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy

class StudomatDao(private val dbManager: DatabaseManagerInterface) :StudomatDaoInterface {
    override suspend fun insert(subjects: List<StudomatSubject>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            delete(StudomatSubject::class)
            subjects.forEach {
                this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    override suspend fun read(): List<StudomatSubject> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val result = realm.query(StudomatSubject::class).find()

        return result.sortedBy { it.name }
    }
}