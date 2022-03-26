package com.example.compose_clean.domain.usecase.session

import com.example.compose_clean.common.model.UserData
import com.example.compose_clean.common.GenericResult

interface RegisterUseCase {

    suspend fun invoke(userData: UserData, password: String) : GenericResult<Void>

}