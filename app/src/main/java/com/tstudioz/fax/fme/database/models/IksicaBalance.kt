package com.tstudioz.fax.fme.database.models

import io.realm.kotlin.types.RealmObject

data class IksicaBalance(
    val balance: Double,
    val spentToday: Double,
)

class IksicaBalanceRealm: RealmObject {
    var balance: Double = 0.0
    var spentToday: Double = 0.0
}

fun IksicaBalance.toRealmObject(): IksicaBalanceRealm {
    val iksicaBalance = this
    return IksicaBalanceRealm().apply {
        balance = iksicaBalance.balance
        spentToday = iksicaBalance.spentToday
    }
}

fun IksicaBalanceRealm.fromRealmObject(): IksicaBalance {
    return IksicaBalance(
        balance = balance,
        spentToday = spentToday
    )
}
