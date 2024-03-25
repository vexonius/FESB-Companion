package com.tstudioz.fax.fme.database.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.Date
import java.util.UUID

open class LeanTask : RealmObject {

    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var taskTekst: String? = null
    @Ignore
    var dateCreated: Date? = null
    @Ignore
    var dateReminder: Date? = null
    var reminder: Boolean? = null
    var checked: Boolean = false

}
