package com.tstudioz.fax.fme.feature.studomat.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.feature.studomat.data.sortedByNameAndSemester
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYear
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy

class StudomatDao(private val dbManager: DatabaseManagerInterface) : StudomatDaoInterface {
    override suspend fun insert(year: StudomatYear) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            year.yearInfo.let {
                delete(query(StudomatYearInfo::class, "academicYear==$0 AND courseName==$1", it.academicYear, it.courseName).find())
                this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
            }
            year.subjects.let{
                delete(query(StudomatSubject::class, "year==$0", it.firstOrNull()?.year).find())
                it.forEach {
                    this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
                }
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

        return result.sortedBy { it.academicYear }
    }

    override suspend fun readData(): List<StudomatYear> {
        val yearsRealmSubjects = read()
            .sortedByNameAndSemester()
            .groupBy { it.year to it.course }
        return readYearNames()
            .sortedByDescending { it.academicYear }
            .mapNotNull { yearInfo ->
                yearsRealmSubjects[yearInfo.academicYear to yearInfo.courseName]?.let { subjectsForYearAndCourse ->
                    StudomatYear(yearInfo, subjectsForYearAndCourse)
                }
            }
    }
}