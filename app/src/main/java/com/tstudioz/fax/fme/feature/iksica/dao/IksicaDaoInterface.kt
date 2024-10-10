package com.tstudioz.fax.fme.feature.iksica.dao

import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataRealm

interface IksicaDaoInterface {

    suspend fun insert(studentData: StudentDataRealm)

    suspend fun read(): StudentDataRealm?

}