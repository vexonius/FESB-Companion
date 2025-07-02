package com.tstudioz.fax.fme.feature.menza.models

import com.tstudioz.fax.fme.feature.menza.MenzaLocationType

data class MenzaLocation (
    val name: String,
    val address: String,
    val meniName: MenzaLocationType,
    val cameraName: String,
)