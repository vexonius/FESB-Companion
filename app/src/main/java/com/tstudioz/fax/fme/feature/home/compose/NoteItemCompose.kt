package com.tstudioz.fax.fme.feature.home.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.feature.home.compose.NoteItemState.Default
import com.tstudioz.fax.fme.feature.home.compose.NoteItemState.Edit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    note: Note,
    markDone: (isDone: Boolean) -> Unit,
    delete: () -> Unit
) {
    val isDone = remember { mutableStateOf(note.checked == true) }
    val noteItemState = remember { mutableStateOf(NoteItemState.Default) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .padding(vertical = 10.dp)
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(onLongClick = {
                noteItemState.value = noteItemState.value.Switch()
            }) {}
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        when (noteItemState.value) {
            Edit -> {
                Image(
                        painter = painterResource(id = R.drawable.trash_can_icon),
                contentDescription = stringResource(id = R.string.delete_note_desc),
                modifier = Modifier
                    .size(25.dp)
                    .padding(2.dp)
                    .noRippleClickable {
                        noteItemState.value = noteItemState.value.Switch()
                        delete()
                    }
                )
            }
            Default -> {
                Image(
                        painter = painterResource(id = if (isDone.value) R.drawable.circle_checked else R.drawable.circle_white),
                contentDescription = stringResource(id = R.string.checkmark_note_desc),
                modifier = Modifier
                    .size(25.dp)
                    .noRippleClickable {
                        isDone.value = !isDone.value
                        markDone(isDone.value)
                    }
                )
            }
        }
        Text(
            text = note.noteTekst,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 10.dp),
            textDecoration = if (isDone.value) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}

enum class NoteItemState{
    Default, Edit;
    fun Switch(): NoteItemState {
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
