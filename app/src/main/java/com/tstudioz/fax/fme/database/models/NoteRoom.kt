package com.tstudioz.fax.fme.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

data class Note(
    val id: String,
    val noteTekst: String,
    val dateCreated: LocalDateTime,
    var checked: Boolean
) {
    constructor(noteRoom: NoteRoom) : this(
        id = noteRoom.id,
        noteTekst = noteRoom.noteTekst,
        dateCreated = noteRoom.dateCreated.let {
            try {
                LocalDateTime.parse(it)
            } catch (e: DateTimeParseException) {
                LocalDateTime.MIN
            }
        },
        checked = noteRoom.checked
    )
}

@Entity
open class NoteRoom(
    @PrimaryKey
    var id: String,
    var noteTekst: String,
    var dateCreated: String,
    var checked: Boolean
) {
    constructor(note: Note) : this(
        id = note.id,
        noteTekst = note.noteTekst,
        dateCreated = note.dateCreated.toString(),
        checked = note.checked
    )
}