package com.tstudioz.fax.fme.database

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.Date

open class LeanTask : RealmObject {

    @PrimaryKey
    var id: String? = null
    var taskTekst: String? = null
    @Ignore
    var dateCreated: Date? = null
    @Ignore
    var dateReminder: Date? = null
    var reminder: Boolean? = null
    var checked: Boolean = false

}
