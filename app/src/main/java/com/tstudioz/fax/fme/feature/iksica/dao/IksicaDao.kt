package com.tstudioz.fax.fme.feature.iksica.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.IksicaBalance
import com.tstudioz.fax.fme.database.models.IksicaBalanceRealm
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.ReceiptRealm
import com.tstudioz.fax.fme.database.models.StudentDataIksica
import com.tstudioz.fax.fme.database.models.StudentDataIksicaRealm
import com.tstudioz.fax.fme.database.models.fromRealmObject
import com.tstudioz.fax.fme.database.models.toRealmObject
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
            val oldIksicaBalance = this.query<IksicaBalanceRealm>().find()
            this.delete(oldIksicaBalance)
            this.copyToRealm(iksicaBalance.toRealmObject(), updatePolicy = UpdatePolicy.ALL)

            val oldStudentDataIksica = this.query<StudentDataIksicaRealm>().find()
            this.delete(oldStudentDataIksica)
            this.copyToRealm(studentDataIksica.toRealmObject(), updatePolicy = UpdatePolicy.ALL)
        }
    }

    override suspend fun read(): Triple<List<Receipt>, IksicaBalance?, StudentDataIksica?> {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val receipts = realm.query<ReceiptRealm>().find().map { it.fromRealmObject() }
            .sortedByDescending { LocalTime.parse(it.vrijeme) }
            .sortedByDescending { it.datum }
        val iksicaBalance = realm.query<IksicaBalanceRealm>().find().map { it.fromRealmObject() }.firstOrNull()
        val studentDataIksica = realm.query<StudentDataIksicaRealm>().find().map { it.fromRealmObject() }.firstOrNull()
        return Triple(receipts, iksicaBalance, studentDataIksica)
    }

}