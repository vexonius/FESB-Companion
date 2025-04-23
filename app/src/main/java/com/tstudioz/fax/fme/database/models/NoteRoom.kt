package com.tstudioz.fax.fme.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

data class Note(
    val id: String?,
    val noteTekst: String?,
    val dateCreated: LocalDateTime?,
    var checked: Boolean?
)

@Entity
open class NoteRoom(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var noteTekst: String? = null,
    var dateCreated: String? = null,
    var checked: Boolean = false
)


fun NoteRoom.toNote(): Note {
    return Note(
        id = id,
        noteTekst = noteTekst,
        dateCreated = dateCreated?.let { LocalDateTime.parse(it) },
        checked = checked
    )
}

fun Note.toNoteRoom(): NoteRoom {
    val note = this
    return NoteRoom().apply {
        id = note.id ?: UUID.randomUUID().toString()
        dateCreated = note.dateCreated?.let { it.toString() }
        noteTekst = note.noteTekst
        checked = note.checked == true
    }
}