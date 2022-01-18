package com.example.compose_clean.domain.usecase.session

import com.example.compose_clean.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository

) {
    suspend fun invoke() = authRepository.auth()

}