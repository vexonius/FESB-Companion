package com.tstudioz.fax.fme.feature.iksica.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.tstudioz.fax.fme.feature.iksica.models.ReceiptRoom
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataRoom


@Dao
interface IksicaDao {
    @Query("DELETE FROM receiptroom")
    fun deleteAll()

    @Insert(onConflict = REPLACE)
    fun insert(studentData: StudentDataRoom)

    @Insert(onConflict = REPLACE)
    fun insert(receipts: List<ReceiptRoom>)

    @Query("SELECT * FROM studentdataroom")
    fun readData(): StudentDataRoom?

    @Query("SELECT * FROM receiptroom")
    fun readReceipts(): List<ReceiptRoom>?
}