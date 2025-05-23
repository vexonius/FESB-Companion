package com.tstudioz.fax.fme.feature.home.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.feature.home.compose.NoteItemState.Default
import com.tstudioz.fax.fme.feature.home.compose.NoteItemState.Edit
import java.time.LocalDateTime
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    note: Note,
    markDone: (isDone: Boolean) -> Unit,
    delete: () -> Unit
) {
    val isDone = remember { mutableStateOf(note.checked == true) }
    val noteItemState: MutableState<NoteItemState> = remember { mutableStateOf(Default) }
    val iconSize = Dp(MaterialTheme.typography.bodyMedium.lineHeight.value)

    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(onLongClick = {
                noteItemState.value = noteItemState.value.switch()
            }) {}
            .padding(4.dp, 4.dp, 8.dp, 4.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        when (noteItemState.value) {
            Edit -> {
                Icon(
                    painter = painterResource(id = R.drawable.note_delete),
                    contentDescription = stringResource(id = R.string.delete_note_desc),
                    modifier = Modifier
                        .size(iconSize)
                        .noRippleClickable {
                            noteItemState.value = noteItemState.value.switch()
                            delete()
                        }
                )
            }

            Default -> {
                Icon(
                    painter = painterResource(id = if (isDone.value) R.drawable.note_checkmark else R.drawable.note_circle),
                    contentDescription = stringResource(id = R.string.checkmark_note_desc),
                    modifier = Modifier
                        .size(iconSize)
                        .noRippleClickable {
                            isDone.value = !isDone.value
                            markDone(isDone.value)
                        }
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = iconSize)
        ) {
            Text(
                text = note.noteTekst,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 10.dp),
                textDecoration = if (isDone.value) TextDecoration.LineThrough else TextDecoration.None
            )
        }
    }
}

@Preview
@Composable
fun NoteItemPreview() {
    AppTheme {
        Surface {
            NoteItem(
                note = Note(
                    id = UUID.randomUUID().toString(),
                    noteTekst = "Test",
                    dateCreated = LocalDateTime.now(),
                    checked = false
                ),
                markDone = {},
                delete = {}
            )
        }
    }
}

enum class NoteItemState {
    Default, Edit;

    fun switch(): NoteItemState {
        return when (this) {
            Default -> Edit
            Edit -> Default
        }
    }

}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}
