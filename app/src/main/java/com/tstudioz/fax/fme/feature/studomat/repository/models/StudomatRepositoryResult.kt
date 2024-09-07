package com.tstudioz.fax.fme.feature.studomat.repository.models

import com.tstudioz.fax.fme.feature.studomat.dataclasses.Student

sealed class StudomatRepositoryResult {

    sealed class LoginResult : StudomatRepositoryResult() {
        data class Success(val data: Student) : LoginResult()
        class Failure(throwable: String) : LoginResult()
    }

}
