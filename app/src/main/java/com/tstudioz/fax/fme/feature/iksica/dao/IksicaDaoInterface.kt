package com.tstudioz.fax.fme.feature.iksica.dao

import com.tstudioz.fax.fme.feature.iksica.models.IksicaBalance
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataIksica

interface IksicaDaoInterface {
    suspend fun insert(receipts: List<Receipt>)

    suspend fun insert(iksicaBalance: IksicaBalance, studentDataIksica: StudentDataIksica)

    suspend fun read(): Triple<List<Receipt>, IksicaBalance?, StudentDataIksica?>
}