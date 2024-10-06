package com.tstudioz.fax.fme.navigation

import kotlinx.serialization.Serializable


@Serializable
data object Iksica

@Serializable
data object Studomat

@Serializable
data object Home

@Serializable
data object Attendance

@Serializable
data object TimeTable


data class TopLevelRoute<T : Any>(val nameId: Int, val route: T, val iconId: Int)