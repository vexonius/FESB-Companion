package com.tstudioz.fax.fme.feature.login.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.theme_dark_secondaryContainer
import com.tstudioz.fax.fme.compose.theme_dark_errorContainer
import com.tstudioz.fax.fme.compose.theme_dark_onErrorContainer
import com.tstudioz.fax.fme.compose.theme_dark_onSurface
import com.tstudioz.fax.fme.feature.login.models.TextFieldModel

@Composable
fun LoginCompose(
    showLoading: MutableLiveData<Boolean>,
    snackbarHostState: SnackbarHostState,
    username: MutableLiveData<String>,
    password: MutableLiveData<String>,
    passwordHidden: MutableLiveData<Boolean>,
    tryUserLogin: () -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val showLoadingObserved = showLoading.observeAsState().value

    fun onDone() {
        keyboardController?.hide()
        tryUserLogin()
    }

    val usernameModel = TextFieldModel(
        text = username,
        label = stringResource(id = R.string.login_email_or_username),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
    )
    val passwordModel = TextFieldModel(
        text = password,
        label = stringResource(id = R.string.login_password),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        textHidden = passwordHidden,
        trailingIcon = {
            IconButton(onClick = { passwordHidden.value = passwordHidden.value?.not() ?: false }) {
                val iconId = if (passwordHidden.observeAsState().value == true) R.drawable.visibility_show else R.drawable.visibility_hide
                Icon(
                    painter = painterResource(iconId),
                    contentDescription = "Visibility Icon",
                    modifier = Modifier.padding(7.dp)
                )
            }
        }
    )

    Scaffold(
        Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    it,
                    containerColor = theme_dark_errorContainer,
                    contentColor = theme_dark_onErrorContainer,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(40.dp, 16.dp, 40.dp, 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(128.dp))
            Text(
                text = stringResource(id = R.string.login_login_title),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.login_safe_data),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            CustomTextField(usernameModel)
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(passwordModel)
            Spacer(modifier = Modifier.height(24.dp))
            ButtonCircularLoading(showLoadingObserved, ::onDone)
            Spacer(modifier = Modifier.height(64.dp))

        }
    }
}

@Composable
fun CustomTextField(textFieldModel: TextFieldModel) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = theme_dark_secondaryContainer,
        focusedLabelColor = theme_dark_secondaryContainer,
        cursorColor = theme_dark_secondaryContainer,
    )
    val textFieldShape = RoundedCornerShape(10.dp)

    OutlinedTextField(
        value = textFieldModel.text.observeAsState().value ?: "",
        onValueChange = { textFieldModel.text.value = it },
        colors = textFieldColors,
        shape = textFieldShape,
        label = { Text(text = textFieldModel.label) },
        keyboardOptions = textFieldModel.keyboardOptions,
        keyboardActions = textFieldModel.keyboardActions,
        visualTransformation = if (textFieldModel.textHidden?.observeAsState()?.value == true) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = textFieldModel.trailingIcon,
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
fun ButtonCircularLoading(
    showLoadingObserved: Boolean?, onDone: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.End
    ) {
        Box(
            Modifier
                .height(40.dp)
                .width(120.dp)
                .align(Alignment.CenterVertically)
        ) {
            if (showLoadingObserved == true) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(35.dp)
                        .padding(5.dp)
                        .align(Alignment.Center),
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Round
                )
            } else {
                Button(
                    onClick = onDone,
                    colors = ButtonDefaults.buttonColors(theme_dark_secondaryContainer),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.login_action_submit),
                        color = theme_dark_onSurface
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun LoginComposePreview() {
    LoginCompose(
        MutableLiveData(false),
        SnackbarHostState(),
        MutableLiveData(""),
        MutableLiveData(""),
        MutableLiveData(true),
        {})
}