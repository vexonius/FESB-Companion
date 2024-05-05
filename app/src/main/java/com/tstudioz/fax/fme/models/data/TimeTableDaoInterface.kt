package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.models.Event

interface TimeTableDaoInterface {

    suspend fun insert(classes: List<Event> )

}