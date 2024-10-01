package com.tstudioz.fax.fme.feature.login.dao

import com.tstudioz.fax.fme.database.models.Korisnik

interface UserDaoInterface {

    suspend fun insert(user: Korisnik)

}
