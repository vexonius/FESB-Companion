package com.tstudioz.fax.fme.models


data class User(val username: String,
                val fullname: String,
                val fmail: String) {

    fun getFirstName() : String {
        return fullname.substringBefore(" ")
    }

}