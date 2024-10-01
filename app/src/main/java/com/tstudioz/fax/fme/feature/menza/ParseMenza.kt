package com.tstudioz.fax.fme.feature.menza

import android.util.Log
import com.google.gson.JsonArray
import com.tstudioz.fax.fme.database.models.Meni
import org.json.JSONObject

fun parseMenza(json: String?): MutableList<Meni>? {
    val menies = mutableListOf<Meni>()

    try {
        val jsonResponse = json?.let { JSONObject(it) }
        val array = jsonResponse?.getJSONArray("values")

        for (j in 0..(array?.length() ?: 0)) {
            try {
                val itemsArray = array?.getJSONArray(j)
                if (itemsArray?.getString(0) == "R-MENI") {
                    val meni = Meni()
                    meni.id = itemsArray.getString(0)
                    meni.type = itemsArray.getString(1)
                    meni.jelo1 = itemsArray.getString(2)
                    meni.jelo2 = itemsArray.getString(3)
                    meni.jelo3 = itemsArray.getString(4)
                    meni.jelo4 = itemsArray.getString(5)
                    meni.desert = itemsArray.getString(6)
                    meni.cijena = itemsArray.getString(7)
                    menies.add(meni)
                } else if(itemsArray?.getString(0) == "R-JELO PO IZBORU") {
                    val izborniMeni = Meni()
                    izborniMeni.id = itemsArray.getString(0)
                    izborniMeni.jelo1 = itemsArray.getString(1)?.split(Regex("(?=\\d)"))?.firstOrNull()
                    izborniMeni.cijena = itemsArray.getString(1)?.split(" ")?.lastOrNull()
                    menies.add(izborniMeni)
                }
            } catch (ex: Exception) {
                Log.d("Menza activity", ex.toString())
                //return null
            }
        }

    } catch (ex: Exception) {
        ex.message?.let { Log.d("MenzaActivity", it) }
        return null
    }

    return menies
}