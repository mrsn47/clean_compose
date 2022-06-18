package com.example.compose_clean.ui.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.compose_clean.common.model.UserData
import com.example.compose_clean.domain.usecase.session.AuthUseCase
import com.example.compose_clean.domain.usecase.session.LoginUseCase
import com.example.compose_clean.domain.usecase.session.RegisterUseCase
import com.example.compose_clean.common.GenericErrorMessage
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<FirebaseUser?>(null)
    val authState: StateFlow<FirebaseUser?> = _state

    // todo: handle auth errors
    private val _error = MutableStateFlow<GenericErrorMessage?>(null)
    val errorMessage: StateFlow<GenericErrorMessage?> = _error

    init {
        Timber.d("SessionViewModel", "Init")
        viewModelScope.launch {
            Timber.d("SessionViewModel", "launch")
            authUseCase.invoke().collectLatest {
                _state.value = it
            }
        }
    }

    fun sendEvent(event: Event) {
        event.run {
            when (this) {
                is Event.SignUpButtonIsClicked -> {
                    signUp(email, password, username)
                }
                is Event.LogInButtonIsClicked -> {
                    logIn(email, password)
                }
                is Event.NavigateToRouteAndClearBackstack -> {
                    // so that the error message doesn't show up on the next screen
                    _error.update { null }
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
        }

    }

    private fun signUp(email: String, password: String, username: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userData = UserData(email, username)
                val result = registerUseCase.invoke(userData, password)
                _error.value = result.error?.let {
                    GenericErrorMessage(
                        it
                    )
                }
            }
        }
    }

    private fun logIn(email: String, password: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = loginUseCase.invoke(email, password)
                _error.value = result.error?.let {
                    GenericErrorMessage(
                        it
                    )
                }
            }
        }
    }

    sealed class Event {
        data class SignUpButtonIsClicked(val email: String, val password: String, val username: String) : Event()
        data class LogInButtonIsClicked(val email: String, val password: String) : Event()
        data class NavigateToRouteAndClearBackstack(val navController: NavController, val route: String) : Event()
    }

}