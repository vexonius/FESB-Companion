package com.tstudioz.fax.fme.feature.studomat.data

import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject

fun List<StudomatSubject>.sortedByNameAndSemester(): List<StudomatSubject> {
    return this
        .sortedBy { it.name }
        .sortedBy { it.semester }
}