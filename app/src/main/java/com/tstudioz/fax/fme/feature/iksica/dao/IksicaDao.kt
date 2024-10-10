package com.tstudioz.fax.fme.feature.iksica.dao

import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query

class IksicaDao(private val dbManager: DatabaseManagerInterface) : IksicaDaoInterface {

    override suspend fun insert(studentData: StudentDataRealm) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        realm.write {
            delete(StudentDataRealm::class)
            copyToRealm(studentData, updatePolicy = UpdatePolicy.ALL)
        }
    }

    override suspend fun read(): StudentDataRealm? {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val model = realm.query<StudentDataRealm>().find().firstOrNull()

        return model
    }

}