package com.uberando.hipasar.data.source.remote

import com.uberando.hipasar.entity.request.*
import com.uberando.hipasar.entity.response.*
import retrofit2.http.*

interface AuthService {

  @POST("auth/signup")
  suspend fun signUp(
    @Body request: SignUpRequest
  ): SuccessStateResponse

  /**
   * Verify email with given code from user email
   */
  @POST("auth/{code}/verify")
  suspend fun emailVerified(
    @Path("code") hash: String
  ): AuthTokenOnlyResponse

  /**
   * Check whether email is verified from other source (website)
   */
  @POST("auth/check/verified")
  suspend fun checkEmailVerification(
    @Body request: EmailOnlyRequest
  ): AuthTokenOnlyResponse

  @POST("auth/signin/with/phone")
  suspend fun signIn(
    @Body request: SignInRequest
  ): AuthSignInPhoneResponse

  @POST("auth/signin/with")
  suspend fun signInOAuth(
    @Body request: SignInOAuthRequest
  ): OAuthResponse

  /**
   * Set hisijang password for OAuth only
   */
  @PUT("auth/password/set")
  suspend fun setPassword(
    @Header("Authorization") token: String,
    @Body request: SetPasswordRequest
  ): SuccessStateResponse

 @PUT("auth/phone/verification/compare")
 suspend fun compareVerificationCode(
   @Body request: CompareVerificationCodeRequest
 ): CompareVerificationCodeResponse

 @POST("auth/phone/verification/require")
 suspend fun requireSendVerificationCode(
   @Body request: SendPhoneVerificationRequest
 ): SuccessStateResponse

}