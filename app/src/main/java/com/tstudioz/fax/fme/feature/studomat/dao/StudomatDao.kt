package com.tstudioz.fax.fme.feature.studomat.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo
import com.tstudioz.fax.fme.feature.studomat.models.Year
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

    override suspend fun insertYears(years: List<StudomatYearInfo>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            delete(query(StudomatYearInfo::class).find())
            years.forEach {
                this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    override suspend fun read(): List<StudomatSubject> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val result = realm.query(StudomatSubject::class).find()

        return result.sortedBy { it.name }
    }

    override suspend fun readYearNames(): List<StudomatYearInfo> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val result = realm.query(StudomatYearInfo::class).find()

        return result.sortedBy { it.year }
    }
}