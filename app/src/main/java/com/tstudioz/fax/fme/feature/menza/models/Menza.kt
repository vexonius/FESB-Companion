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
    val mealTime: MealTime,
    val name: String,
    val soupOrTea: String,
    val mainCourse: String,
    val sideDish: String,
    val salad: String,
    val dessert: String,
    val price: String,
){
    fun isNotEmpty(): Boolean {
        return soupOrTea.isNotEmpty() || mainCourse.isNotEmpty() || sideDish.isNotEmpty() || salad.isNotEmpty() || dessert.isNotEmpty()
    }
}
@Serializable
data class MeniSpecial(
    val type: String,
    val mealTime: MealTime,
    val meal: String,
    val price: String,
)

enum class MealTime(val value: String) {
    LUNCH("RUČAK"),
    DINNER("VEČERA"),
}
