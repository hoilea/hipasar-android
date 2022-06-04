package com.uberando.hipasar.data.repository

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.entity.User
import com.uberando.hipasar.entity.request.EmailOnlyRequest
import com.uberando.hipasar.entity.request.account.ChangeDisplayProfileRequest
import com.uberando.hipasar.entity.request.account.ChangePasswordRequest
import com.uberando.hipasar.entity.request.account.PutFirebaseTokenRequest
import com.uberando.hipasar.entity.request.account.SetPasswordOnlyRequest

interface AccountRepository {
  suspend fun getUserAccount(): Resource<User>
  suspend fun changeUserDisplayProfile(request: ChangeDisplayProfileRequest): Resource<User>
  suspend fun changeUserEmail(request: EmailOnlyRequest): Resource<Nothing>
  suspend fun verifyUserNewEmail(hash: String): Resource<User>
  suspend fun setUserAccountPassword(request: SetPasswordOnlyRequest): Resource<Nothing>
  suspend fun changeUserAccountPassword(request: ChangePasswordRequest): Resource<Nothing>
  suspend fun getUserHiCash(): Resource<Int>
  suspend fun putFirebaseToken(request: PutFirebaseTokenRequest): Resource<Nothing>
}