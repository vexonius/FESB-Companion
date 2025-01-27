package com.tstudioz.fax.fme.feature.login.models

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.MutableLiveData

data class TextFieldModel(
    val text: MutableLiveData<String>,
    val label: String,
    val keyboardOptions: KeyboardOptions = KeyboardOptions(),
    val keyboardActions: KeyboardActions = KeyboardActions(),
    val textHidden : MutableState<Boolean>? = null,
    val trailingIcon: @Composable() (() -> Unit)? = null,
    val onSubmit: () -> Unit
)