package com.tstudioz.fax.fme.feature.merlin.repository

import android.content.SharedPreferences
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import com.tstudioz.fax.fme.feature.merlin.repository.models.MerlinRepositoryResult
import com.tstudioz.fax.fme.feature.login.services.UserServiceInterface
import com.tstudioz.fax.fme.feature.merlin.services.MerlinNetworkServiceResult
import com.tstudioz.fax.fme.feature.merlin.services.MerlinServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.UserDaoInterface
import com.tstudioz.fax.fme.models.interfaces.WeatherNetworkInterface
import com.tstudioz.fax.fme.models.util.PreferenceHelper.set
import com.tstudioz.fax.fme.models.util.SPKey

class MerlinRepository(
    private val merlinService: MerlinServiceInterface,
    private val weatherNetworkService: WeatherNetworkInterface,
    private val userDao: UserDaoInterface,
    private val sharedPreferences: SharedPreferences
    ) : MerlinRepositoryInterface {

    override suspend fun login(email: String, password: String): MerlinNetworkServiceResult.MerlinNetworkResult {
        return merlinService.runSegmented( email, password)
    }

    override suspend fun getCourseDetails(courseID: Int): MerlinNetworkServiceResult.MerlinNetworkResult {
        return merlinService.getCourseDetails(courseID)
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }

}
