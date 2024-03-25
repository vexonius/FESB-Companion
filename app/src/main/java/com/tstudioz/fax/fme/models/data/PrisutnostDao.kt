package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.models.Dolazak
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import org.koin.android.ext.android.inject

class PrisutnostDao {

    // TODO: Remove this, temporary fix
    private val dbManager = DatabaseManager()

    fun insertOrUpdatePrisutnost(svaFreshPrisutnost: MutableList<Dolazak>) {
        val realm = Realm.open(dbManager.getDefaultConfiguration())

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
