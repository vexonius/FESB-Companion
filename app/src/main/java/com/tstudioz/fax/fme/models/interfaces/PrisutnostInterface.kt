package com.tstudioz.fax.fme.models.interfaces

import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.models.data.User

interface PrisutnostInterface {

     suspend fun fetchPrisutnost(user: User): Result.PrisutnostResult

}