package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.Dolazak
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults

class PrisutnostDao {

    fun insertOrUpdatePrisutnost(svaFreshPrisutnost: MutableList<Dolazak>) {
        val mainRealmConfig = RealmConfiguration.Builder(setOf(Dolazak::class))
        .name("prisutnost.realm")
        .schemaVersion(10)
        .deleteRealmIfMigrationNeeded()
        .build()

        val realm = Realm.open(mainRealmConfig)

        try {
            realm.writeBlocking {
                val svaPris: RealmResults<Dolazak> = this.query<Dolazak>().find()
                for (fpris in svaFreshPrisutnost) {
                    if (fpris.id?.let { isPrisutnostInRealm(this, it) } == false) {
                        this.copyToRealm(fpris)
                    }
                }
                for (pris in svaPris) {
                    if (!isPrisutnostInFresh(pris, svaFreshPrisutnost)) {
                        this.delete(pris)
                    }
                }
            }
        } finally {
            realm.close()
        }
    }

    private fun isPrisutnostInRealm(realm: MutableRealm, id: String): Boolean {
        val results = realm.query<Dolazak>("id = $0", id)
            .find()

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