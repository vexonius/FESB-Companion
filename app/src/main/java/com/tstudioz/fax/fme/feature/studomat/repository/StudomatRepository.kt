package com.tstudioz.fax.fme.feature.studomat.repository

import android.content.SharedPreferences
import android.util.Log
import com.tstudioz.fax.fme.feature.studomat.dao.StudomatDao
import com.tstudioz.fax.fme.feature.studomat.data.parseCurrentYear
import com.tstudioz.fax.fme.feature.studomat.data.parseStudent
import com.tstudioz.fax.fme.feature.studomat.data.parseYears
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.feature.studomat.services.StudomatService
import com.tstudioz.fax.fme.models.NetworkServiceResult

class StudomatRepository(
    private val studomatService: StudomatService,
    private val studomatDao: StudomatDao,
    private val sharedPreferences: SharedPreferences
) {

    suspend fun getStudomatDataAndYears(): StudomatRepositoryResult.StudentAndYearsResult {
        val student = parseStudent(studomatService.getStudomatData())

        return when (val result = studomatService.getYearNames()) {
            is NetworkServiceResult.StudomatResult.Success -> {
                val resultGetYears = parseYears(result.data)
                //studomatDao.insertYears(resultGetYears)
                Log.d("StudomatRepository", "getYears: $resultGetYears")
                StudomatRepositoryResult.StudentAndYearsResult.Success(resultGetYears, student)
            }

            is NetworkServiceResult.StudomatResult.Failure -> {
                Log.d("StudomatRepository", "getYears: ${result.throwable.message}")
                StudomatRepositoryResult.StudentAndYearsResult.Failure("Failure getting data:${result.throwable.message}")
            }
        }
    }

    suspend fun getYear(year: StudomatYearInfo): StudomatRepositoryResult.ChosenYearResult {
        return when (val data = studomatService.getYearSubjects(year.href)) {
            is NetworkServiceResult.StudomatResult.Success -> {
                val parsedSubjects = parseCurrentYear(data.data, year)
                studomatDao.insert(parsedSubjects.second)
                //studomatDao.insertYears(listOf(parsedSubjects.first))
                Log.d("StudomatRepository", "getOdabranuGodinu: $parsedSubjects")
                StudomatRepositoryResult.ChosenYearResult.Success(parsedSubjects)
            }

            is NetworkServiceResult.StudomatResult.Failure -> {
                Log.d("StudomatRepository", "getOdabranuGodinu: ${data.throwable.message}")
                StudomatRepositoryResult.ChosenYearResult.Failure("Failure getting data:${data.throwable.message}")
            }
        }
    }

    suspend fun insertYears(years: List<StudomatYearInfo>) {
        studomatDao.insertYears(years)
    }

    suspend fun readYearNames(): List<StudomatYearInfo> {
        return studomatDao.readYearNames()
    }

    suspend fun read(): List<StudomatSubject> {
        return studomatDao.read()
    }
}