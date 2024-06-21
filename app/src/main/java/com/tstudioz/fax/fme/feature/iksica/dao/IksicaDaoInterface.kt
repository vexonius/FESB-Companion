package com.tstudioz.fax.fme.feature.iksica.dao

import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.IksicaBalance
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.StudentDataIksica

interface IksicaDaoInterface {
    suspend fun insert(receipts: List<Receipt>)

    suspend fun insert(iksicaBalance: IksicaBalance, studentDataIksica: StudentDataIksica)

    suspend fun read(): Triple<List<Receipt>, IksicaBalance?, StudentDataIksica?>
}