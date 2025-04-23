package com.tstudioz.fax.fme.feature.menza.repository

import android.util.Log
import com.tstudioz.fax.fme.feature.menza.MenzaResult
import com.tstudioz.fax.fme.feature.menza.parseMenza
import com.tstudioz.fax.fme.feature.menza.service.MenzaServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult

class MenzaRepository(
    private val menzaNetworkService: MenzaServiceInterface,
) : MenzaRepositoryInterface {
    override suspend fun fetchMenzaDetails(): MenzaResult {
        return when (val result = menzaNetworkService.fetchMenza()) {
            is NetworkServiceResult.MenzaResult.Success -> {
                val parsed = parseMenza(result.data)
                if (parsed != null) {
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
}