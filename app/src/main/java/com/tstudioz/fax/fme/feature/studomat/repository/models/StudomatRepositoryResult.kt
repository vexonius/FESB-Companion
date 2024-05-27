package com.tstudioz.fax.fme.feature.studomat.repository.models

import com.example.studomatisvu.model.dataclasses.Student

sealed class StudomatRepositoryResult {

    sealed class LoginResult : StudomatRepositoryResult() {
        data class Success(val data: Student) : LoginResult()
        class Failure(throwable: String) : LoginResult()
    }

}
