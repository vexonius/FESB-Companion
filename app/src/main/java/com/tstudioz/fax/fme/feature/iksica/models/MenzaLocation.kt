package com.tstudioz.fax.fme.feature.iksica.models

import com.tstudioz.fax.fme.feature.iksica.MenzaLocationType

data class MenzaLocation (
    val name: String = "",
    val address: String = "",
    val meniName: MenzaLocationType,
    val cameraName: String = "",
)