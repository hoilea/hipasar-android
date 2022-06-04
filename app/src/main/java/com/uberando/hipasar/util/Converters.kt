package com.uberando.hipasar.util

import com.uberando.hipasar.entity.request.SignInOAuthRequest
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.kakao.sdk.user.model.User

object Converters {

  fun convertGoogleSignInAccountToOAuthRequest(account: GoogleSignInAccount) =
    SignInOAuthRequest(
      id = account.id!!,
      name = account.displayName!!,
      email = account.email!!,
      accountType = Constants.AUTH_METHOD_GOOGLE
    )

  fun convertKakaoAccountToOAuthRequest(user: User) =
    SignInOAuthRequest(
      id = user.id?.toString()!!,
      name = user.kakaoAccount?.profile?.nickname!!,
      email = user.kakaoAccount?.email!!,
      accountType = Constants.AUTH_METHOD_KAKAO
    )

}