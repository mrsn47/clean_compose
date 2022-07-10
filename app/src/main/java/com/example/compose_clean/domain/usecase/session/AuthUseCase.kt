package com.example.compose_clean.domain.usecase.session

import com.example.compose_clean.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface AuthUseCase {

    suspend operator fun invoke(): Flow<FirebaseUser?>

}

class AuthUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : AuthUseCase {
    override suspend fun invoke() = authRepository.auth()
}