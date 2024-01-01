package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.Dolazak
import io.realm.Realm
import io.realm.RealmConfiguration

class PrisutnostDao {
fun insertOrUpdatePrisutnost(svaFreshPrisutnost: List<Dolazak>){
        val mainRealmConfig = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("prisutnost.realm")
        .schemaVersion(10)
        .deleteRealmIfMigrationNeeded()
        .build()
        val realm = Realm.getInstance(mainRealmConfig)

        realm.executeTransaction { rlm ->
            val svaPris = rlm.where(Dolazak::class.java).findAll()
            for (freshpris in svaFreshPrisutnost){
                if (!isPrisutnostInRealm(rlm, freshpris.id)){
                    rlm.copyToRealm(freshpris)
                }
            }
            for (pris in svaPris){
                if (!isPrisutnostInFresh(pris, svaFreshPrisutnost)){
                    pris.deleteFromRealm()
                }
            }
        }
    }

    private fun isPrisutnostInRealm(realm: Realm, objectId: String): Boolean {
        val results = realm.where(Dolazak::class.java)
            .equalTo("objectId", objectId)
            .findAll()

        return results.isNotEmpty()
    }
    private fun isPrisutnostInFresh(prisutnost: Dolazak, freshPris: List<Dolazak>): Boolean {
        for (pris in freshPris){
            if (prisutnost.id == pris.id){
                return true
            }
        }
        return false
    }
}