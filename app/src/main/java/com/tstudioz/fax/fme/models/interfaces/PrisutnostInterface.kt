package com.tstudioz.fax.fme.models.interfaces

import com.tstudioz.fax.fme.models.Result

interface PrisutnostInterface {
    suspend fun fetchPrisutnost(): Result.PrisutnostResult

}