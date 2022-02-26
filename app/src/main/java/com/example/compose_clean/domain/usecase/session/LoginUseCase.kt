package com.example.compose_clean.domain.usecase.session

import com.example.compose_clean.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository

) {
    suspend fun invoke(email: String, password: String) = authRepository.logIn(email, password)

}