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