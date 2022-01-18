package com.example.compose_clean.common.di

import android.content.Context
import com.example.compose_clean.domain.repository.AuthRepository
import com.example.compose_clean.domain.usecase.PostUseCase
import com.example.compose_clean.domain.usecase.session.AuthUseCase
import com.example.compose_clean.domain.usecase.session.LoginUseCase
import com.example.compose_clean.domain.usecase.session.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  // Repository
  @Provides
  @Singleton
  fun provideAuthRepository(
  ): AuthRepository {
    return AuthRepository()
  }

  // Use Case

  @Provides
  @Singleton
  fun providePostUseCase(
  ): PostUseCase {
    return PostUseCase()
  }

  @Provides
  @Singleton
  fun provideAuthUseCase(
    authRepository: AuthRepository
  ): AuthUseCase {
    return AuthUseCase(authRepository)
  }

  @Provides
  @Singleton
  fun provideLoginUseCase(
    authRepository: AuthRepository
  ): LoginUseCase {
    return LoginUseCase(authRepository)
  }

  @Provides
  @Singleton
  fun provideRegisterUseCase(
    authRepository: AuthRepository
  ): RegisterUseCase {
    return RegisterUseCase(authRepository)
  }
}