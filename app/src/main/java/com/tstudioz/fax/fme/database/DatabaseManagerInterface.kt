package com.tstudioz.fax.fme.database

import io.realm.kotlin.RealmConfiguration

interface DatabaseManagerInterface {

    fun getDefaultConfiguration(): RealmConfiguration

}