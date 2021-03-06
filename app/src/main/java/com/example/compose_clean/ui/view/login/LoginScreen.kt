package com.example.compose_clean.ui.view.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.compose_clean.nav.Screen
import com.example.compose_clean.ui.composables.util.CreateSnackbar
import com.example.compose_clean.ui.theme.Typography
import com.example.compose_clean.ui.view.login.SessionViewModel.Event

@ExperimentalComposeUiApi
@Composable
fun LoginScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel = hiltViewModel(),
) {

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val error = sessionViewModel.errorMessage.collectAsState()

    // todo: clean up
    Scaffold(
        scaffoldState = scaffoldState,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp), contentAlignment = Alignment.TopCenter
        )
        {
            error.value.CreateSnackbar(scope = scope, scaffoldState = scaffoldState)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = "Log in", style = Typography.h5)
                Spacer(modifier = Modifier.padding(8.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 48.dp)
                ) {
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                            },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.padding(12.dp))
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            sessionViewModel.sendEvent(
                                Event.LogInButtonIsClicked(email, password)
                            )
                        }, modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Log in now")
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    Row {
                        Text(
                            text = "Sign up", Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .align(alignment = Alignment.CenterVertically)
                                .clickable {
                                    sessionViewModel.sendEvent(Event.NavigateToRouteAndClearBackstack(navController, Screen.SignUp.route))
                                }
                                .padding(10.dp), textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}