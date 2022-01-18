package com.example.compose_clean.ui.view.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose_clean.nav.Screen
import com.example.compose_clean.ui.theme.Typography

@Composable
fun LoginScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp), contentAlignment = Alignment.TopCenter
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "Log in", style = Typography.h5)
            Spacer(modifier = Modifier.padding(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 48.dp)) {
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
                    onClick = { login(sessionViewModel, email, password) }, modifier = Modifier
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
                            .clickable(onClick = {
                                navController.navigate(Screen.SignUp.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            })
                            .padding(10.dp), textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun login(sessionViewModel: SessionViewModel, email: String, password: String) {
    sessionViewModel.signIn(email, password)
}