package com.tstudioz.fax.fme.feature.iksica.models

import io.realm.kotlin.types.RealmObject

class IksicaBalance(
    var balance: Double,
    var spentToday: Double
) : RealmObject{
    constructor() : this(0.0, 0.0)
}


