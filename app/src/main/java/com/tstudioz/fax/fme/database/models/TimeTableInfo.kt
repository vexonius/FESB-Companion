package com.tstudioz.fax.fme.database.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class TimeTableInfo (
    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("Name")
    var name: String = "",
    @SerializedName("StartDate")
    var startDate: LocalDate? = null,
    @SerializedName("EndDate")
    var endDate: LocalDate?= null,
    @SerializedName("StartDateText")
    var startDateText: String = "",
    @SerializedName("EndDateText")
    var endDateText: String = "",
    @SerializedName("Category")
    var category: String = "",
    @SerializedName("ColorCode")
    var colorCode: Long = 0x00FFFFFF,
    @SerializedName("IsWorking")
    var isWorking: Boolean = false,
)