package com.tstudioz.fax.fme.feature.studomat.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.feature.studomat.data.sortedByStudomat
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy

class StudomatDao(private val dbManager: DatabaseManagerInterface) : StudomatDaoInterface {
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
            years.forEach {
                delete(query(StudomatYearInfo::class, "year==$0 AND courseName==$1", it.year, it.courseName).find())
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

    override suspend fun readData(): List<Pair<StudomatYearInfo, List<StudomatSubject>>> {
        val yearsRealmSubjects = read()
            .sortedByStudomat()
            .groupBy { it.year to it.course }
        val groupedData = readYearNames()
            .sortedByDescending { it.year }
            .mapNotNull { yearInfo ->
                yearsRealmSubjects[yearInfo.year to yearInfo.courseName]?.let { subjectsForYearAndCourse ->
                    yearInfo to subjectsForYearAndCourse
                }
            }
        return groupedData
    }
}