package com.uberando.hipasar.data.source

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AccountRepository
import com.uberando.hipasar.data.source.local.dao.AccountDao
import com.uberando.hipasar.data.source.remote.AccountService
import com.uberando.hipasar.entity.User
import com.uberando.hipasar.entity.request.EmailOnlyRequest
import com.uberando.hipasar.entity.request.account.ChangeDisplayProfileRequest
import com.uberando.hipasar.entity.request.account.ChangePasswordRequest
import com.uberando.hipasar.entity.request.account.PutFirebaseTokenRequest
import com.uberando.hipasar.entity.request.account.SetPasswordOnlyRequest
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.makeBearerToken
import timber.log.Timber
import javax.inject.Inject

class AccountDataSource @Inject constructor(
  private val accountService: AccountService,
  private val accountDao: AccountDao
) : AccountRepository {

  override suspend fun getUserAccount(): Resource<User> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          accountService.getProfile(token.makeBearerToken()).let { response ->
            Resource.Success(response)
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Timber.e(e)
      Resource.Failed()
    }
  }

  override suspend fun changeUserDisplayProfile(request: ChangeDisplayProfileRequest): Resource<User> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          accountService.changeProfile(token.makeBearerToken(), request).let { response ->
            Resource.Success(response)
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun changeUserEmail(request: EmailOnlyRequest): Resource<Nothing> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          accountService.changeEmail(token.makeBearerToken(), request).let { response ->
            if (response.success) {
              Resource.Success()
            } else {
              Resource.Failed()
            }
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Timber.e(e)
      Resource.Failed()
    }
  }

  override suspend fun verifyUserNewEmail(hash: String): Resource<User> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          accountService.verifyNewUserEmail(token.makeBearerToken(), hash).let { response ->
            Resource.Success(response)
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun setUserAccountPassword(request: SetPasswordOnlyRequest): Resource<Nothing> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          accountService.setPassword(token.makeBearerToken(), request).let { response ->
            if (response.success) {
              Resource.Success()
            } else {
              Resource.Failed()
            }
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun changeUserAccountPassword(request: ChangePasswordRequest): Resource<Nothing> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          accountService.changePassword(token.makeBearerToken(), request).let { response ->
            if (response.success) {
              Resource.Success()
            } else {
              Resource.Failed()
            }
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun getUserHiCash(): Resource<Int> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          accountService.getHiCash(token.makeBearerToken()).let { response ->
            Resource.Success(response.hicash)
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun putFirebaseToken(request: PutFirebaseTokenRequest): Resource<Nothing> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          accountService.putFirebaseToken(token.makeBearerToken(), request).let { response ->
            if (response.success) {
              Resource.Success()
            } else {
              Resource.Failed()
            }
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  private suspend fun getTokenOrNull(): String? = accountDao.getSingleAccount()?.token

}