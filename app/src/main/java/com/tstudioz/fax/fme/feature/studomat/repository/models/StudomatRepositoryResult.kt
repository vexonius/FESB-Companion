package com.tstudioz.fax.fme.feature.studomat.repository.models

import com.tstudioz.fax.fme.feature.studomat.models.Student
import com.tstudioz.fax.fme.feature.studomat.models.Year

sealed class StudomatRepositoryResult {

    sealed class LoginResult : StudomatRepositoryResult() {
        data class Success(val data: Student) : LoginResult()
        class Failure(val throwable: String) : LoginResult()
    }

    sealed class YearsResult : StudomatRepositoryResult() {
        data class Success(val data: List<Year>) : YearsResult()

        class Failure(val throwable: String) : YearsResult()

    }

}
