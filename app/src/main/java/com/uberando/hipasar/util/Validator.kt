package com.uberando.hipasar.util

import android.util.Patterns

object Validator {

  fun validateInputName(source: String): Boolean {
    return source != "" && source != "null" && source.length >= Constants.MINIMUM_NAME_LENGTH
  }

  fun validateInputPhone(source: String): Boolean {
    return source != "" && source != "null" && Patterns.PHONE.matcher(source).matches()
  }

}