package com.tstudioz.fax.fme.networking.session

import kotlinx.coroutines.flow.Flow

interface SessionDelegateInterface {

    val isSessionActive: Boolean

    val onUserDeleted: Flow<Unit>

    fun clearSession()

}