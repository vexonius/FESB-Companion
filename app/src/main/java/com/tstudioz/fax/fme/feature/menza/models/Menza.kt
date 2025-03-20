package com.tstudioz.fax.fme.feature.menza.models

import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Menza(
    val name: String,
    val datePosted: String,
    val dateFetched: String,
    var menies: MutableList<Menu>,
    var meniesSpecial: MutableList<MeniSpecial>,
){
    fun toRealm(): MenzaRealm{
        return MenzaRealm(
            Json.encodeToJsonElement(serializer(), this).toString()
        )
    }
}
@Serializable
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
){
    fun isNotEmpty(): Boolean {
        return soupOrTea.isNotEmpty() || mainCourse.isNotEmpty() || sideDish.isNotEmpty() || salad.isNotEmpty() || dessert.isNotEmpty()
    }
}
@Serializable
data class MeniSpecial(
    val type: String,
    val mealTime: String,
    val meal: String,
    val price: String,
)

open class MenzaRealm(
    var menza: String? = null
) : RealmObject{
    constructor(): this(null)
    fun fromRealm(): Menza? {
        return menza?.let { Json.decodeFromString(Menza.serializer(), it) }
    }
}