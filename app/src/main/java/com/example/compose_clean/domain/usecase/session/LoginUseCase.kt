package com.example.compose_clean.domain.usecase.session

import com.example.compose_clean.common.GenericResult
import com.google.firebase.auth.FirebaseUser

interface LoginUseCase {

    suspend fun invoke(email: String, password: String): GenericResult<FirebaseUser>

}