package com.tstudioz.fax.fme.feature.login.view

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.common.user.UserRepositoryInterface
import com.tstudioz.fax.fme.common.user.models.UserRepositoryResult
import com.tstudioz.fax.fme.database.AppDatabase
import com.tstudioz.fax.fme.util.PreferenceHelper.get
import com.tstudioz.fax.fme.util.PreferenceHelper.set
import com.tstudioz.fax.fme.util.SPKey
import com.tstudioz.fax.fme.util.SingleLiveEvent
import com.tstudioz.fax.fme.util.studentDataTestData
import com.tstudioz.fax.fme.util.attendanceTestData
import com.tstudioz.fax.fme.util.eventsTestData
import com.tstudioz.fax.fme.util.receiptsTestData
import com.tstudioz.fax.fme.util.studomatSubjectTestData
import com.tstudioz.fax.fme.util.studomatYearInfoTestData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

@InternalCoroutinesApi
class LoginViewModel(
    private val application: Application,
    private val repository: UserRepositoryInterface,
    private val sharedPreferences: SharedPreferences
) : AndroidViewModel(application) {

    var username = MutableLiveData("")
    var password = MutableLiveData("")
    val showLoading = MutableLiveData(false)
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
    var passwordHidden = MutableLiveData(true)

    var firstTimeInApp = MutableLiveData(false)
        private set

    var loggedIn = SingleLiveEvent<Unit>()
        private set

    private val handler = CoroutineExceptionHandler { _, exception ->
        showSnackbar(application.getString(R.string.login_error_generic))
    }

    private fun addTestData() {
        val db : AppDatabase by inject(AppDatabase::class.java)
        viewModelScope.launch(Dispatchers.IO + handler) {
            db.studomatDao().insert(studomatSubjectTestData)
            db.studomatDao().insertYears(studomatYearInfoTestData)
            db.timetableDao().insert(eventsTestData)
            db.attendanceDao().insert(attendanceTestData)
            db.iksicaDao().insert(studentDataTestData)
            db.iksicaDao().insert(receiptsTestData)
        }
    }

    fun tryUserLogin() {
        var username = username.value?.trim()?.lowercase()
        val password = password.value?.trim()

        if (username == "test" && password == "testpassword12421") {
            setTestMode(true)
            viewModelScope.launch(Dispatchers.IO + handler) {
                repository.insertDummyUser()
            }
            addTestData()
            loggedIn.postValue(Unit)
        }

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            showSnackbar(application.getString(R.string.login_error_empty_credentials))
            return
        } else if (isEmailValid(username)) {
            username = username.substringBefore("@")
        }
        showLoading.value = true

        viewModelScope.launch(Dispatchers.IO + handler) {
            when (repository.attemptLogin(username, password)) {
                is UserRepositoryResult.LoginResult.Success -> {
                    loggedIn.postValue(Unit)
                }

                is UserRepositoryResult.LoginResult.Failure -> {
                    showSnackbar(application.getString(R.string.login_error_invalid_credentials))
                }
            }
            showLoading.postValue(false)
        }
    }

    private fun showSnackbar(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            snackbarHostState.showSnackbar(message)
        }
    }

    fun setTestMode(isTestUser: Boolean) {
        sharedPreferences[SPKey.TEST_MODE] = isTestUser
    }

    fun checkIfFirstTimeInApp() {
        firstTimeInApp.value = sharedPreferences[SPKey.FIRST_TIME, true]
        sharedPreferences[SPKey.FIRST_TIME] = false
    }

    fun checkIfLoggedIn() {
        if (sharedPreferences[SPKey.LOGGED_IN, false]) {
            loggedIn.value = Unit
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
