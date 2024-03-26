package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.models.Korisnik

interface UserDaoInterface {

    suspend fun insert(user: Korisnik)

}
