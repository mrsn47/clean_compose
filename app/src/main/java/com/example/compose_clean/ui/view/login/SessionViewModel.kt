package com.example.compose_clean.ui.view.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_clean.data.dao.model.UserData
import com.example.compose_clean.domain.usecase.session.AuthUseCase
import com.example.compose_clean.domain.usecase.session.LoginUseCase
import com.example.compose_clean.domain.usecase.session.RegisterUseCase
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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

    val authError = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            authUseCase.invoke().collectLatest {
                _state.value = it
                Log.d("LOL1", "got state ${it?.uid}")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = loginUseCase.invoke(email,password)
                result.error?.let{
                    authError.postValue(it.localizedMessage)
                }
            }
        }
    }

    fun createAccount(email: String, password: String, userName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userData = UserData(null, email, userName)
                registerUseCase.invoke(userData,password)
            }
        }
    }
}