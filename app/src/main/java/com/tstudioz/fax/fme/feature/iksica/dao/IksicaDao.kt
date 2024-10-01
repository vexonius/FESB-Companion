package com.tstudioz.fax.fme.feature.iksica.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.feature.iksica.models.IksicaBalance
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.ReceiptRealm
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataIksica
import com.tstudioz.fax.fme.feature.iksica.models.fromRealmObject
import com.tstudioz.fax.fme.feature.iksica.models.toRealmObject
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import java.time.LocalTime

class IksicaDao(private val dbManager: DatabaseManagerInterface) : IksicaDaoInterface {


    override suspend fun insert(receipts: List<Receipt>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            val oldReceipts = this.query<ReceiptRealm>().find()
            this.delete(oldReceipts)
            receipts.forEach {
                this.copyToRealm(it.toRealmObject(), updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    override suspend fun insert(iksicaBalance: IksicaBalance, studentDataIksica: StudentDataIksica) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            val oldIksicaBalance = this.query<IksicaBalance>().find()
            this.delete(oldIksicaBalance)
            this.copyToRealm(iksicaBalance, updatePolicy = UpdatePolicy.ALL)

            val oldStudentDataIksica = this.query<StudentDataIksica>().find()
            this.delete(oldStudentDataIksica)
            this.copyToRealm(studentDataIksica, updatePolicy = UpdatePolicy.ALL)
        }
    }

    override suspend fun read(): Triple<List<Receipt>, IksicaBalance?, StudentDataIksica?> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val receipts = realm.query<ReceiptRealm>().find().map { it.fromRealmObject() }
            .sortedByDescending { LocalTime.parse(it.time) }
            .sortedByDescending { it.date }
        val iksicaBalance = realm.query<IksicaBalance>().find().firstOrNull()
        val studentDataIksica = realm.query<StudentDataIksica>().find().firstOrNull()

        return Triple(receipts, iksicaBalance, studentDataIksica)
    }

}