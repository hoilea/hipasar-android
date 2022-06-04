package com.uberando.hipasar.data.source.remote

import com.uberando.hipasar.entity.User
import com.uberando.hipasar.entity.request.EmailOnlyRequest
import com.uberando.hipasar.entity.request.account.ChangeDisplayProfileRequest
import com.uberando.hipasar.entity.request.account.ChangePasswordRequest
import com.uberando.hipasar.entity.request.account.PutFirebaseTokenRequest
import com.uberando.hipasar.entity.request.account.SetPasswordOnlyRequest
import com.uberando.hipasar.entity.response.GetHiCashResponse
import com.uberando.hipasar.entity.response.SuccessStateResponse
import retrofit2.http.*

interface AccountService {

  @GET("users/profile")
  suspend fun getProfile(
    @Header("Authorization") token: String
  ): User

  @PUT("users/")
  suspend fun changeProfile(
    @Header("Authorization") token: String,
    @Body request: ChangeDisplayProfileRequest
  ): User

  @POST("users/email/change")
  suspend fun changeEmail(
    @Header("Authorization") token: String,
    @Body request: EmailOnlyRequest
  ): SuccessStateResponse

  @PUT("users/{hash}/email/verified")
  suspend fun verifyNewUserEmail(
    @Header("Authorization") token: String,
    @Path("hash") hash: String
  ): User

  @PUT("users/password/set")
  suspend fun setPassword(
    @Header("Authorization") token: String,
    @Body request: SetPasswordOnlyRequest
  ): SuccessStateResponse

  @PUT("users/password/change")
  suspend fun changePassword(
    @Header("Authorization") token: String,
    @Body request: ChangePasswordRequest
  ): SuccessStateResponse

  @GET("users/hicash")
  suspend fun getHiCash(
    @Header("Authorization") token: String
  ): GetHiCashResponse

  @PUT("users/firebase/token")
  suspend fun putFirebaseToken(
    @Header("Authorization") token: String,
    @Body request: PutFirebaseTokenRequest
  ): SuccessStateResponse

}