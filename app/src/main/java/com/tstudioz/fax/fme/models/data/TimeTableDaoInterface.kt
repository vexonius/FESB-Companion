package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.Predavanja

interface TimeTableDaoInterface {

    suspend fun insert(classes: List<Event> )

}