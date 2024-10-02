package com.tstudioz.fax.fme.feature.menza.dao

import com.tstudioz.fax.fme.feature.menza.models.Menza

interface MenzaDaoInterface {

    suspend fun insert(classes: Menza?)

    suspend fun getCachedMenza(): Menza?

}