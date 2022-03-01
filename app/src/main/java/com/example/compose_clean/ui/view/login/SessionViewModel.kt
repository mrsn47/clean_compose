package com.example.compose_clean.ui.view.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_clean.common.model.UserData
import com.example.compose_clean.domain.usecase.session.*
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val authError = MutableLiveData<String>()

    init {
        // todo: use globalscope?
        Log.d("SessionViewModel","Init")
        viewModelScope.launch {
            Log.d("SessionViewModel","launch")
            authUseCase.invoke().collectLatest {
                _state.value = it
                Log.d("LOL1", "got state ${it?.uid}")
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
            }
        }

    }

    private fun signUp(email: String, password: String, username: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userData = UserData(email, username)
                val result = registerUseCase.invoke(userData, password)
                result.error?.let {
                    authError.postValue(it.localizedMessage)
                }
            }
        }
    }

    private fun logIn(email: String, password: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = loginUseCase.invoke(email, password)
                result.error?.let {
                    authError.postValue(it.localizedMessage)
                }
            }
        }
    }

    sealed class Event {
        data class SignUpButtonIsClicked(val email: String, val password: String, val username: String) : Event()
        data class LogInButtonIsClicked(val email: String, val password: String) : Event()
    }

}