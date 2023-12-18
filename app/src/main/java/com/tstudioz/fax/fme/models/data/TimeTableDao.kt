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

        realm.executeTransaction { rlm ->
            var svaPredavanja = rlm.where(Predavanja::class.java).findAll()
            for (freshpred in svaFreshPredavanja){
                if (!isPredavanjeInRealm(rlm, freshpred.objectId)){
                    rlm.copyToRealm(freshpred)
                }
            }
            for (pred in svaPredavanja){
                if (!isPredFresh(pred, svaFreshPredavanja)){
                    pred.deleteFromRealm()
                }
            }
            svaPredavanja = rlm.where(Predavanja::class.java).findAll()
        }
    }
    fun isPredavanjeInRealm(realm: Realm, objectId: Int): Boolean {
        val results = realm.where(Predavanja::class.java)
            .equalTo("objectId", objectId)
            .findAll()
        val rezultati = realm.where(Predavanja::class.java).findAll()

        return results.isNotEmpty()
    }
    fun isPredFresh(predavanje: Predavanja, FreshPredavanja: MutableList<Predavanja>): Boolean {
        for (pred in FreshPredavanja){
            if (predavanje.id == pred.id){
                return true
            }
        }
        return false
    }
}