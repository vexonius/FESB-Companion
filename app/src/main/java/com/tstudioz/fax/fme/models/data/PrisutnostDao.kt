package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.Dolazak
import io.realm.Realm
import io.realm.RealmConfiguration

class PrisutnostDao {
fun insertOrUpdatePrisutnost(svaFreshPrisutnost: MutableList<Dolazak>){
        val mainRealmConfig = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("prisutnost.realm")
        .schemaVersion(10)
        .deleteRealmIfMigrationNeeded()
        .build()
        val realm = Realm.getInstance(mainRealmConfig)

        try{
            realm.executeTransaction { rlm ->
                val svaPris = rlm.where(Dolazak::class.java).findAll()
                for (fpris in svaFreshPrisutnost) {
                    if (fpris.id?.let { isPrisutnostInRealm(rlm, it) }==false) {
                        rlm.copyToRealm(fpris)
                    }
                }
                for (pris in svaPris) {
                    if (!isPrisutnostInFresh(pris, svaFreshPrisutnost)) {
                        pris.deleteFromRealm()
                    }
                }
            }
        } finally {
            realm.close()
        }
    }

    private fun isPrisutnostInRealm(realm: Realm, id: String): Boolean {
        val results = realm.where(Dolazak::class.java)
            .equalTo("id", id)
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