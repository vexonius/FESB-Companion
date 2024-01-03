package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.Predavanja
import io.realm.Realm
import io.realm.RealmConfiguration

class TimeTableDao {
    fun insertOrUpdateTimeTable( svaFreshPredavanja: MutableList<Predavanja> ){
        val mainRealmConfig = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .name("glavni.realm")
            .schemaVersion(3)
            .deleteRealmIfMigrationNeeded()
            .build()
        val realm = Realm.getInstance(mainRealmConfig)

        try {
            realm.executeTransaction { rlm ->
                val svaPredavanja = rlm.where(Predavanja::class.java).findAll()
                for (freshpred in svaFreshPredavanja) {
                    if (!isPredavanjeInRealm(rlm, freshpred.objectId)) {
                        rlm.copyToRealm(freshpred)
                    }
                }
                for (pred in svaPredavanja) {
                    if (!isPredavanjeInFresh(pred, svaFreshPredavanja)) {
                        pred.deleteFromRealm()
                    }
                }
            }
        } finally {
            realm.close()
        }
    }fun insertTempTimeTable( svaFreshTempPredavanja: MutableList<Predavanja> ){
        val mainRealmConfig = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .name("temporary.realm")
            .schemaVersion(12)
            .deleteRealmIfMigrationNeeded()
            .build()
        val realm = Realm.getInstance(mainRealmConfig)

        try {
            realm.executeTransaction { rlm ->
                for (freshpred in svaFreshTempPredavanja) {
                    rlm.copyToRealm(freshpred)
                }
            }
        } finally {
            realm.close()
        }
    }
    fun deleteTempTimeTable(){
        val tmpRealmConfig = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .name("temporary.realm")
            .schemaVersion(12)
            .deleteRealmIfMigrationNeeded()
            .build()
        val tempRealm = Realm.getInstance(tmpRealmConfig)

        try {
            tempRealm.executeTransaction { rlm ->
                val svaPredavanja = rlm.where(Predavanja::class.java).findAll()
                for (pred in svaPredavanja) {
                    pred.deleteFromRealm()
                }
            }
        } finally {
            tempRealm.close()
        }
    }
    private fun isPredavanjeInRealm(realm: Realm, objectId: Int): Boolean {
        val results = realm.where(Predavanja::class.java)
            .equalTo("objectId", objectId)
            .findAll()

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