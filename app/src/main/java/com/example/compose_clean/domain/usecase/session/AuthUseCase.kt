package com.example.compose_clean.domain.usecase.session

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {

    suspend operator fun invoke(): Flow<FirebaseUser?>

}