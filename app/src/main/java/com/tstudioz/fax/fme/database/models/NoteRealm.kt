package com.tstudioz.fax.fme.database.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID

data class Note(
    val id: String?,
    val noteTekst: String?,
    val dateCreated: LocalDateTime?,
    var checked: Boolean?
)

open class NoteRealm : RealmObject {

    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var noteTekst: String? = null
    var dateCreated: String? = null
    var checked: Boolean = false

}

fun NoteRealm.toNote(): Note {
    return Note(
        id = id,
        noteTekst = noteTekst,
        dateCreated = dateCreated?.let { LocalDateTime.parse(it) },
        checked = checked
    )
}

fun Note.toNoteRealm(): NoteRealm {
    val note = this
    return NoteRealm().apply {
        id = note.id ?: UUID.randomUUID().toString()
        dateCreated = note.dateCreated?.toString()
        noteTekst = note.noteTekst
        checked = note.checked ?: false
    }
}