package com.example.compose_clean.domain.usecase.session

import com.example.compose_clean.common.GenericResult
import com.example.compose_clean.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

interface LoginUseCase {

    suspend fun invoke(email: String, password: String): GenericResult<FirebaseUser>

}

class LoginUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : LoginUseCase {
    override suspend fun invoke(email: String, password: String) = authRepository.logIn(email, password)
}