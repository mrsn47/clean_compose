package com.example.compose_clean.domain.usecase.session

import com.example.compose_clean.common.model.UserData
import com.example.compose_clean.common.GenericResult
import com.example.compose_clean.domain.repository.AuthRepository
import javax.inject.Inject

interface RegisterUseCase {

    suspend fun invoke(userData: UserData, password: String) : GenericResult<Void>

}

class RegisterUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : RegisterUseCase {
    override suspend fun invoke(userData: UserData, password: String) = authRepository.createAccount(userData, password)
}