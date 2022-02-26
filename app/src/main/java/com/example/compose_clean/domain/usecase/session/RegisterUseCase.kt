package com.example.compose_clean.domain.usecase.session

import com.example.compose_clean.common.model.UserData
import com.example.compose_clean.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository

) {
    suspend fun invoke(userData: UserData, password: String) = authRepository.createAccount(userData, password)

}