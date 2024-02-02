package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.Predavanja
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query

class TimeTableDao {

    fun insertOrUpdateTimeTable(svaFreshPredavanja: MutableList<Predavanja> ){
        val mainRealmConfig = RealmConfiguration.Builder(setOf(Predavanja::class))
            .name("glavni.realm")
            .schemaVersion(3)
            .deleteRealmIfMigrationNeeded()
            .build()

        val realm = Realm.open(mainRealmConfig)

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
        val mainRealmConfig = RealmConfiguration.Builder(setOf(Predavanja::class))
            .name("temporary.realm")
            .schemaVersion(12)
            .deleteRealmIfMigrationNeeded()
            .build()

        val realm = Realm.open(mainRealmConfig)

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
        val tmpRealmConfig = RealmConfiguration.Builder(setOf(Predavanja::class))
            .name("temporary.realm")
            .schemaVersion(12)
            .deleteRealmIfMigrationNeeded()
            .build()

        val tempRealm = Realm.open(tmpRealmConfig)

        try {
            tempRealm.writeBlocking {
                val svaPredavanja = this.query<Predavanja>().find()
                for (pred in svaPredavanja) {
                    this.delete(pred)
                }
            }
        } finally {
            tempRealm.close()
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