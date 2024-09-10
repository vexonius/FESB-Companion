package com.tstudioz.fax.fme.feature.studomat.models

import io.realm.kotlin.types.RealmObject


open class Year(
    var title: String = "",
    var href: String = ""
): RealmObject {
    constructor() : this("", "")
    override fun toString(): String {
        return "Year(title='$title', href='$href')"
    }
}