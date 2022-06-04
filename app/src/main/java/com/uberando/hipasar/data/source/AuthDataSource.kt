package com.uberando.hipasar.data.source

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AuthRepository
import com.uberando.hipasar.data.source.local.dao.AccountDao
import com.uberando.hipasar.data.source.local.entity.AccountEntity
import com.uberando.hipasar.data.source.remote.AuthService
import com.uberando.hipasar.entity.request.*
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.makeBearerToken
import retrofit2.HttpException
import javax.inject.Inject

class AuthDataSource @Inject constructor(
  private val authService: AuthService,
  private val accountDao: AccountDao
) : AuthRepository {
  
  private var cachedToken = ""

  override suspend fun signUp(request: SignUpRequest): Resource<Nothing> {
    return try {
      authService.signUp(request).let { response ->
        if (response.success) {
          Resource.Success()
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: HttpException) {
      Resource.Failed(code = e.code(), message = Constants.CODE_DUPLICATE_ENTRY)
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun verifyEmail(hash: String): Resource<Nothing> {
    return try {
      authService.emailVerified(hash).let { response ->
        if (insertToDatabase(response.token, Constants.AUTH_METHOD_BASIC) == Constants.SUCCESS) {
          Resource.Success()
        } else {
          Resource.Failed()
        }
      }
    } catch (e: HttpException) {
      Resource.Failed(code = e.code())
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun checkEmailVerification(request: EmailOnlyRequest): Resource<Nothing> {
    return try {
      authService.checkEmailVerification(request).let { response ->
        if (insertToDatabase(response.token, Constants.AUTH_METHOD_BASIC) == Constants.SUCCESS) {
          Resource.Success()
        } else {
          Resource.Failed()
        }
      }
    } catch (e: HttpException) {
      Resource.Failed(code = e.code())
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun compareCodeVerification(request: CompareVerificationCodeRequest): Resource<Nothing> {
    return try {
      authService.compareVerificationCode(request).let { response ->
        when (response.code) {
          Constants.CODE_VERIFICATION_OK ->  {
            if (insertToDatabase(response.token!!, Constants.AUTH_METHOD_BASIC) == Constants.SUCCESS) {
              Resource.Success()
            } else {
              Resource.Failed()
            }
          }
          else -> Resource.Failed(code = response.code)
        }
      }
    }  catch (e: HttpException) {
      Resource.Failed(code = e.code())
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun signIn(request: SignInRequest): Resource<Nothing> {
    return try {
      authService.signIn(request).let { response ->
        if (response.phoneVerified) {
          if (insertToDatabase(response.token, Constants.AUTH_METHOD_BASIC) == Constants.SUCCESS) {
            Resource.Success()
          } else {
            Resource.Failed()
          }
        } else {
          Resource.Failed(
            code = Constants.CODE_PHONE_NOT_VERIFIED
          )
        }
      }
    } catch (e: HttpException) {
      Resource.Failed(code = e.code())
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun signInOAuth(request: SignInOAuthRequest): Resource<Boolean> {
    return try {
      authService.signInOAuth(request).let { response ->
        if (insertToDatabase(response.token, request.accountType) == Constants.SUCCESS) {
          cachedToken = response.token
          Resource.Success(response.passwordAvailable)
        } else {
          Resource.Failed()
        }
      }
    } catch (e: HttpException) {
      Resource.Failed(code = e.code())
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun setPassword(request: SetPasswordRequest): Resource<Nothing> {
    return try {
      authService.setPassword(cachedToken.makeBearerToken(), request).let {
        Resource.Success()
      }
    } catch (e: HttpException) {
      Resource.Failed(code = e.code())
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun signOut(): Resource<String> {
    val cd = clearDatabase()
    return if (cd != null) {
      Resource.Success(cd)
    } else {
      Resource.Failed()
    }
  }

  override suspend fun sendPhoneVerification(request: SendPhoneVerificationRequest): Resource<Nothing> {
    return try {
      authService.requireSendVerificationCode(request).let { response ->
        if (response.success) {
          Resource.Success()
        } else {
          Resource.Failed()
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  private suspend fun insertToDatabase(token: String, method: String): Int {
    return try {
      accountDao.insert(AccountEntity(0, token, method))
      Constants.SUCCESS
    } catch(e: Exception) {
      Constants.FAILED
    }
  }

  private suspend fun clearDatabase(): String? {
    return try {
      requireSignInMethod().let { method ->
        if (method != null) {
          accountDao.clearAllAccount()
          method
        } else {
          null
        }
      }
    } catch (e: Exception) {
      null
    }
  }

  private suspend fun requireSignInMethod(): String? =
    accountDao.getSingleAccount()?.method

}