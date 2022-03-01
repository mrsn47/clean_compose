package com.example.compose_clean.domain.usecase.session

import com.example.compose_clean.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : LoginUseCase {
    override suspend fun invoke(email: String, password: String) = authRepository.logIn(email, password)
}