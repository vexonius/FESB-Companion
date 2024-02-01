package com.tstudioz.fax.fme.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.Date

open class LeanTask : RealmObject() {
    @PrimaryKey
    var id: String? = null
    var taskTekst: String? = null
    var dateCreated: Date? = null
    var dateReminder: Date? = null
    var reminder: Boolean? = null
    var checked: Boolean = false
}
