package com.tstudioz.fax.fme.feature.studomat.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Year(
    @PrimaryKey
    val id: String = "",
    var title: String = "",
    var href: String = ""
)