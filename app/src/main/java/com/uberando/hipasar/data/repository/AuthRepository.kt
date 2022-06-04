package com.uberando.hipasar.data.repository

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.entity.request.*

interface AuthRepository {
  suspend fun signUp(request: SignUpRequest): Resource<Nothing>
  suspend fun verifyEmail(hash: String): Resource<Nothing>
  suspend fun checkEmailVerification(request: EmailOnlyRequest): Resource<Nothing>
  suspend fun compareCodeVerification(request: CompareVerificationCodeRequest): Resource<Nothing>
  suspend fun signIn(request: SignInRequest): Resource<Nothing>
  suspend fun signInOAuth(request: SignInOAuthRequest): Resource<Boolean>
  suspend fun setPassword(request: SetPasswordRequest): Resource<Nothing>
  suspend fun signOut(): Resource<String>
  suspend fun sendPhoneVerification(request: SendPhoneVerificationRequest): Resource<Nothing>
}