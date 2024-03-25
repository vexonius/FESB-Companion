package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.models.Predavanja
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import org.koin.core.KoinComponent
import org.koin.core.inject

class TimeTableDao: KoinComponent {

    private val dbManager: DatabaseManager by inject()

    fun insertOrUpdateTimeTable(svaFreshPredavanja: MutableList<Predavanja> ){
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        try {
            realm.writeBlocking {
                val svaPredavanja = this.query<Predavanja>().find()
                for (freshpred in svaFreshPredavanja) {
                    if (!isPredavanjeInRealm(this, freshpred.id)) {
                        this.copyToRealm(freshpred)
                    }
                }
                for (pred in svaPredavanja) {
                    if (!isPredavanjeInFresh(pred, svaFreshPredavanja)) {
                        this.delete(pred)
                    }
                }
            }
        } finally {
            realm.close()
        }
    }

    fun insertTempTimeTable( svaFreshTempPredavanja: MutableList<Predavanja> ){
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        try {
            realm.writeBlocking {
                for (freshpred in svaFreshTempPredavanja) {
                    this.copyToRealm(freshpred)
                }
            }
        } finally {
            realm.close()
        }
    }
    fun deleteTempTimeTable(){
        val realm = Realm.open(dbManager.getDefaultConfiguration())

        try {
            realm.writeBlocking {
                val svaPredavanja = this.query<Predavanja>().find()
                for (pred in svaPredavanja) {
                    this.delete(pred)
                }
            }
        } finally {
            realm.close()
        }
    }
    private fun isPredavanjeInRealm(realm: MutableRealm, id: String?): Boolean {
        val results = realm.query<Predavanja>("id = $0", id)
            .find()

        return results.isNotEmpty()
    }
    private fun isPredavanjeInFresh(predavanje: Predavanja, freshPredavanja: MutableList<Predavanja>): Boolean {
        for (pred in freshPredavanja){
            if (predavanje.id == pred.id){
                return true
            }
        }
        return false
    }
}