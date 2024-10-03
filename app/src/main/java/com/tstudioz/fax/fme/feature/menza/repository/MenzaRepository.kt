package com.tstudioz.fax.fme.feature.menza.repository

import android.util.Log
import com.tstudioz.fax.fme.feature.menza.MenzaResult
import com.tstudioz.fax.fme.feature.menza.dao.MenzaDaoInterface
import com.tstudioz.fax.fme.feature.menza.models.Menza
import com.tstudioz.fax.fme.feature.menza.parseMenza
import com.tstudioz.fax.fme.feature.menza.service.MenzaServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult

class MenzaRepository(
    private val menzaNetworkService: MenzaServiceInterface,
    private val menzaDao: MenzaDaoInterface,
) : MenzaRepositoryInterface {
    override suspend fun fetchMenzaDetails(url: String): MenzaResult {
        return when (val result = menzaNetworkService.fetchMenza(url)) {
            is NetworkServiceResult.MenzaResult.Success -> {
                val parsed = parseMenza(result.data)
                if (parsed != null) {
                    menzaDao.insert(parsed)
                    MenzaResult.Success(parsed)
                } else {
                    Log.e(this.javaClass.canonicalName, "Menies parsing error")
                    MenzaResult.Failure(Throwable("Menies parsing error"))
                }
            }

            is NetworkServiceResult.MenzaResult.Failure -> {
                Log.e(this.javaClass.canonicalName, "Menies fetching error")
                MenzaResult.Failure(Throwable("Menies fetching error"))
            }
        }
    }

    override suspend fun readMenza(): Menza? {
        return menzaDao.getCachedMenza()
    }
}