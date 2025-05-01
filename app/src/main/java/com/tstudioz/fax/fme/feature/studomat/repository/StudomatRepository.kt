package com.tstudioz.fax.fme.feature.studomat.repository

import android.util.Log
import com.tstudioz.fax.fme.feature.studomat.dao.StudomatDao
import com.tstudioz.fax.fme.feature.studomat.data.parseCurrentYear
import com.tstudioz.fax.fme.feature.studomat.data.parseStudent
import com.tstudioz.fax.fme.feature.studomat.data.parseYears
import com.tstudioz.fax.fme.feature.studomat.data.sortedByNameAndSemester
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYear
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.feature.studomat.services.StudomatService
import com.tstudioz.fax.fme.models.NetworkServiceResult

class StudomatRepository(
    private val studomatService: StudomatService,
    private val studomatDao: StudomatDao,
) {

    fun getStudomatDataAndYears(): StudomatRepositoryResult.StudentAndYearsResult {
        val student = parseStudent(studomatService.getStudomatData())

        return when (val result = studomatService.getYearNames()) {
            is NetworkServiceResult.StudomatResult.Success -> {
                val resultGetYears = parseYears(result.data)
                studomatDao.deleteYears()
                studomatDao.insertYears(resultGetYears)
                Log.d("StudomatRepository", "getYears: $resultGetYears")
                StudomatRepositoryResult.StudentAndYearsResult.Success(resultGetYears, student)
            }

            is NetworkServiceResult.StudomatResult.Failure -> {
                Log.d("StudomatRepository", "getYears: ${result.throwable.message}")
                StudomatRepositoryResult.StudentAndYearsResult.Failure("Failure getting data:${result.throwable.message}")
            }
        }
    }

    fun getYear(year: StudomatYearInfo): StudomatRepositoryResult.ChosenYearResult {
        return when (val data = studomatService.getYearSubjects(year.href)) {
            is NetworkServiceResult.StudomatResult.Success -> {
                val parsedSubjects = parseCurrentYear(data.data, year)
                Log.d("StudomatRepository", "getOdabranuGodinu: $parsedSubjects")
                StudomatRepositoryResult.ChosenYearResult.Success(parsedSubjects)
            }

            is NetworkServiceResult.StudomatResult.Failure -> {
                Log.d("StudomatRepository", "getOdabranuGodinu: ${data.throwable.message}")
                StudomatRepositoryResult.ChosenYearResult.Failure("Failure getting data:${data.throwable.message}")
            }
        }
    }

    fun insert(year: StudomatYear) {
        year.subjects.firstOrNull()?.year?.let { studomatDao.deleteAll(it) }
        studomatDao.insert(year.subjects)
        studomatDao.insertYears(listOf(year.yearInfo))
    }

    fun readData(): List<StudomatYear> {
        val years = studomatDao.readYears().sortedBy { it.academicYear }
        val subjects = studomatDao.read().sortedByNameAndSemester().groupBy { it.year  }
        return years.mapNotNull { yearInfo ->
            subjects[yearInfo.academicYear]?.let { subjectsForYearAndCourse ->
                StudomatYear(yearInfo, subjectsForYearAndCourse)
            }
        }
    }
}