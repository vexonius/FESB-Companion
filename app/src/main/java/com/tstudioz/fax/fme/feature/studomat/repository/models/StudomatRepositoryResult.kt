package com.tstudioz.fax.fme.feature.studomat.repository.models

import com.tstudioz.fax.fme.feature.studomat.models.Student
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo

sealed class StudomatRepositoryResult {

    sealed class LoginResult : StudomatRepositoryResult() {
        data class Success(val data: Student) : LoginResult()
        class Failure(val throwable: String) : LoginResult()
    }

    sealed class StudentAndYearsResult : StudomatRepositoryResult() {
        data class Success(val data: List<StudomatYearInfo>, val student:Student) : StudentAndYearsResult()

        class Failure(val throwable: String) : StudentAndYearsResult()

    }

    sealed class ChosenYearResult : StudomatRepositoryResult() {
        data class Success(val data: Pair<StudomatYearInfo, List<StudomatSubject>>) : ChosenYearResult()

        class Failure(val throwable: String) : ChosenYearResult()
    }

}
