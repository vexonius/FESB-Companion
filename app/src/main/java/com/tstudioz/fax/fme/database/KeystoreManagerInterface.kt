package com.tstudioz.fax.fme.database

interface KeystoreManagerInterface {

    fun getOrCreateEncryptionKey(): ByteArray

}