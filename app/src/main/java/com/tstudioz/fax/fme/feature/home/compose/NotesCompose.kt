package com.tstudioz.fax.fme.feature.home.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.notesContainer
import com.tstudioz.fax.fme.database.models.Note
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun NotesCompose(
    notes: List<Note>,
    insertNote: (note: Note) -> Unit,
    deleteNote: (note: Note) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp, 12.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(notesContainer)
            .padding(20.dp, 12.dp)
            .animateContentSize()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        Row {
            Text(
                text = stringResource(id = R.string.notes),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
        AddNoteCompose(insertNote)
        notes.sortedByDescending { it.dateCreated }.forEach { note ->
            key(note.id) {
                NoteItem(
                    note = note,
                    delete = { deleteNote(note) },
                    markDone = { isDone ->
                        insertNote(note.apply { checked = isDone })
                    }
                )
            }
        }
    }
}