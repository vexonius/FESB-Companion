package com.tstudioz.fax.fme.feature.home.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.database.models.Note
import java.time.LocalDateTime
import java.util.UUID


@Composable
fun AddNoteCompose(insertNote: (note: Note) -> Unit) {
    val openDialog = remember { mutableStateOf(false) }
    if (!openDialog.value) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable { openDialog.value = true }
                .padding(4.dp, 4.dp, 8.dp, 4.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.note_add),
                contentDescription = stringResource(id = R.string.add_note),
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = stringResource(id = R.string.add_note),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 10.dp),
            )
        }
    } else {
        val editMessage = remember { mutableStateOf("") }
        val message = remember { mutableStateOf("") }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        ) {
            BasicTextField(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .fillMaxWidth()
                    .weight(0.5f),
                value = editMessage.value,
                onValueChange = { editMessage.value = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                cursorBrush = SolidColor(Color.White),
                decorationBox = { innerTextField ->
                    Row(
                        Modifier
                            .height(35.dp)
                            .padding(start = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.weight(1f)) {
                            if (editMessage.value.isEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.enter_note),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                painter = painterResource(id = R.drawable.note_cancel),
                contentDescription = stringResource(id = R.string.cancel_note),
                modifier = Modifier
                    .size(35.dp)
                    .clickable { openDialog.value = false },
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                painter = painterResource(id = R.drawable.note_save_button),
                contentDescription = stringResource(id = R.string.save_note),
                modifier = Modifier
                    .size(35.dp)
                    .clickable {
                        message.value = editMessage.value
                        openDialog.value = false
                        insertNote(
                            Note(
                                noteTekst = message.value,
                                checked = false,
                                dateCreated = LocalDateTime.now(),
                                id = UUID.randomUUID().toString(),
                            )
                        )
                    },
                tint = Color.Unspecified
            )
        }
    }
}

@Preview
@Composable
fun AddNotePreview() {
    AppTheme() {
        Surface {
            AddNoteCompose { }
        }
    }
}