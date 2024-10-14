package com.tstudioz.fax.fme.feature.studomat.repository

import android.content.SharedPreferences
import android.util.Log
import com.tstudioz.fax.fme.feature.studomat.dao.StudomatDao
import com.tstudioz.fax.fme.feature.studomat.data.parseCurrentYear
import com.tstudioz.fax.fme.feature.studomat.data.parseStudent
import com.tstudioz.fax.fme.feature.studomat.data.parseYears
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.Year
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.feature.studomat.services.StudomatService
import com.tstudioz.fax.fme.models.NetworkServiceResult

class StudomatRepository(
    private val studomatService: StudomatService,
    private val studomatDao: StudomatDao,
    private val sharedPreferences: SharedPreferences
) {

    suspend fun getStudomatDataAndYears(): StudomatRepositoryResult.StudentAndYearsResult {
        val studentUnparsed = studomatService.getStudomatData()
        val student = parseStudent(studentUnparsed)

        return when (val result = studomatService.getYears()) {
            is NetworkServiceResult.StudomatResult.Success -> {
                val resultGetYears = parseYears(result.data)
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

    suspend fun getChosenYear(year: Year): StudomatRepositoryResult.ChosenYearResult {
        return when (val data = studomatService.getChosenYear(year.href)) {
            is NetworkServiceResult.StudomatResult.Success -> {
                val resultGetChosenYear = parseCurrentYear(data.data)
                studomatDao.insert(resultGetChosenYear.first)
                sharedPreferences.edit().putString("gen" + year.title, resultGetChosenYear.second).apply()
                Log.d("StudomatRepository", "getOdabranuGodinu: ${resultGetChosenYear.first}")
                StudomatRepositoryResult.ChosenYearResult.Success(resultGetChosenYear)
            }

            is NetworkServiceResult.StudomatResult.Failure -> {
                Log.d("StudomatRepository", "getOdabranuGodinu: ${data.throwable.message}")
                StudomatRepositoryResult.ChosenYearResult.Failure("Failure getting data:${data.throwable.message}")
            }
        }
    }

    suspend fun readYears(): List<Year> {
        return studomatDao.readYears()
    }

    suspend fun read(year: String): List<StudomatSubject> {
        return studomatDao.read(year)
    }

}