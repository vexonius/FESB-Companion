package com.tstudioz.fax.fme.feature.menza.models


data class Menza(
    val name: String,
    val datePosted: String,
    val dateFetched: String,
    var menies: MutableList<Menu>,
    var meniesSpecial: MutableList<MeniSpecial>,
)

data class Menu(
    val type: String,
    val mealTime: String,
    val name: String,
    val soupOrTea: String,
    val mainCourse: String,
    val sideDish: String,
    val salad: String,
    val dessert: String,
    val price: String,
)

data class MeniSpecial(
    val type: String,
    val mealTime: String,
    val meal: String,
    val price: String,
)
