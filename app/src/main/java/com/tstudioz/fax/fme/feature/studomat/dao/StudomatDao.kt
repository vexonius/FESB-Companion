package com.tstudioz.fax.fme.feature.studomat.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.feature.studomat.dataclasses.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.dataclasses.Year
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy

class StudomatDao(private val dbManager: DatabaseManagerInterface) :StudomatDaoInterface {
    override suspend fun insert(subjects: List<StudomatSubject>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            delete(query(StudomatSubject::class, "year==$0", subjects.firstOrNull()?.year).find())
            subjects.forEach {
                this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    override suspend fun insertYears(years: List<Year>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            delete(Year::class)
            years.forEach {
                this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    override suspend fun read(year: String): List<StudomatSubject> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val result = realm.query(StudomatSubject::class, "year==$0", year).find()

        return result.sortedBy { it.name }
    }

    override suspend fun readYears(): List<Year> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val result = realm.query(Year::class).find()

        return result.sortedBy { it.title }
    }
}