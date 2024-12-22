package com.tstudioz.fax.fme.feature.login.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.login.view.LoginViewModel
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class)
@Composable
fun LoginCompose(loginViewModel: LoginViewModel) {

    //var passwordVisibility by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = loginViewModel.snackbarHostState) {
                Snackbar(
                    it,
                    containerColor = colorResource(id = R.color.login_error_color_container),
                    contentColor = colorResource(id = R.color.login_error_color_content),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.login_login_title),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(id = R.string.login_safe_data),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    OutlinedTextField(
                        value = loginViewModel.username.observeAsState().value ?: "",
                        onValueChange = { loginViewModel.username.value = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.login_button),
                            focusedLabelColor = colorResource(id = R.color.login_button),
                            cursorColor = colorResource(id = R.color.login_button),
                        ),
                        shape = RoundedCornerShape(10.dp),
                        label = { Text(text = stringResource(id = R.string.login_email_or_username)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = loginViewModel.password.observeAsState().value ?: "",
                        onValueChange = { loginViewModel.password.value = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.login_button),
                            focusedLabelColor = colorResource(id = R.color.login_button),
                            cursorColor = colorResource(id = R.color.login_button),
                        ),
                        shape = RoundedCornerShape(10.dp),
                        label = { Text(text = stringResource(id = R.string.login_password)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation =/* if (passwordVisibility) VisualTransformation.None else */PasswordVisualTransformation(),
                        /* trailingIcon = {
                             IconButton(onClick = {
                                 passwordVisibility = !passwordVisibility
                             }) {
                                 Icon(
                                     painter = icon,
                                     contentDescription = "Visibility Icon",
                                     modifier = Modifier.padding(7.dp)
                                 )
                             }
                         },*/
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(40.dp)
                ) {
                    if (loginViewModel.showLoading.observeAsState().value == true) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .width(40.dp)
                                .padding(10.dp),
                            strokeWidth = 4.dp,
                            strokeCap = StrokeCap.Round
                        )
                    } else {
                        Button(
                            onClick = {
                                keyboardController?.hide()
                                loginViewModel.tryUserLogin()
                            },
                            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.login_button)),
                        ) {
                            Text(
                                text = stringResource(id = R.string.login_action_submit),
                                color = colorResource(id = R.color.white)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}