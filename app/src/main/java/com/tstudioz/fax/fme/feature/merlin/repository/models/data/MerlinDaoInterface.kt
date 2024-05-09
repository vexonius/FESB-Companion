package com.tstudioz.fax.fme.feature.merlin.repository.models.data

import com.tstudioz.fax.fme.database.models.Korisnik

interface MerlinDaoInterface {

    suspend fun insert(user: Korisnik)

}
