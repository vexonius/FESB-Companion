package com.tstudioz.fax.fme.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.tstudioz.fax.fme.R
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class)
@Composable
fun IksicaLoginCompose(
    snackbarHostS: SnackbarHostState,
    login: (String, String) -> Unit,
    showLoading: LiveData<Boolean>
) {

    val snackbarHostState = remember { snackbarHostS }
    var textEmail by remember { mutableStateOf("") }
    var textPass by remember { mutableStateOf("") }

    var passwordVisibility by remember { mutableStateOf(false) }
    val icon = painterResource(
        id = if (passwordVisibility)
            R.drawable.invisible
        else
            R.drawable.view
    )

    Scaffold(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            Icon(
                painter = painterResource(R.drawable.iksica),
                contentDescription = "",
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Iksica")
            Spacer(modifier = Modifier.height(16.dp))
            val focusManager = LocalFocusManager.current
            Column {
                OutlinedTextField(
                    value = textEmail,
                    onValueChange = { textEmail = it },
                    shape = RoundedCornerShape(10.dp),
                    label = { Text(text = "Email") },
                    placeholder = { Text("Unesi email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = textPass,
                    onValueChange = { textPass = it },
                    shape = RoundedCornerShape(10.dp),
                    label = { Text(text = "Lozinka") },
                    placeholder = { Text("Unesi lozinku") },
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordVisibility = !passwordVisibility
                        }) {
                            Icon(
                                painter = icon,
                                contentDescription = "Visibility Icon",
                                modifier = Modifier.padding(7.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        login(textEmail.substringBefore("@"), textPass)
                    }),
                    visualTransformation = if (passwordVisibility) VisualTransformation.None
                    else PasswordVisualTransformation()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (showLoading.observeAsState().value == true) {
                CircularProgressIndicator(
                    modifier = Modifier.width(40.dp),
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Round
                )
            } else {
                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus()
                        login(textEmail.substringBefore("@"), textPass)
                    }
                ) {
                    Text(text = "Prijava")
                }
            }
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}