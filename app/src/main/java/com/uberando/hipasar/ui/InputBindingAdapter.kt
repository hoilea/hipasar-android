package com.uberando.hipasar.ui

import android.util.Patterns
import androidx.databinding.BindingAdapter
import com.uberando.hipasar.R
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Validator
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter(requireAll = false, value = ["setErrorRule_target", "setErrorRule_source", "setErrorRule_realPassword"])
fun TextInputLayout.bindErrorRule(target: String?, source: String?, realPassword: String?) {
  when (target) {
    Constants.INPUT_TARGET_USERNAME -> validateUsernameInput(this, source)
    Constants.INPUT_TARGET_PASSWORD -> validatePasswordInput(this, source)
    Constants.INPUT_TARGET_PASSWORD_CONFIRM -> validatePasswordConfirmInput(this, source, realPassword)
    Constants.INPUT_TARGET_EMAIL -> validateEmailInput(this, source)
    Constants.INPUT_TARGET_NAME -> validateNameInput(this, source)
    Constants.INPUT_TARGET_PHONE -> validatePhoneInput(this, source)
  }
}

fun validatePasswordConfirmInput(inputLayout: TextInputLayout, source: String?, realPassword: String?) {
  if (source != realPassword) {
    inputLayout.error = inputLayout.context.getString(R.string.str_error_password_confirm)
  } else {
    inputLayout.error = null
  }
}

private fun validateUsernameInput(inputLayout: TextInputLayout, source: String?) {
  source?.let {
    if (source.length < Constants.MINIMUM_USERNAME_LENGTH) {
      inputLayout.error = inputLayout.context.getString(R.string.str_error_username, Constants.MINIMUM_USERNAME_LENGTH)
    } else {
      inputLayout.error = null
    }
  }
}

private fun validatePasswordInput(inputLayout: TextInputLayout, source: String?) {
  val regex = Regex(Constants.PATTERN_ALPHANUMERIC)
  source?.let {
    when {
      source.length < Constants.MINIMUM_PASSWORD_LENGTH || !regex.matches(it) -> {
        inputLayout.error = inputLayout.context.getString(R.string.str_error_password, Constants.MINIMUM_PASSWORD_LENGTH)
      }
      else -> {
        inputLayout.error = null
      }
    }
  }
}

private fun validateEmailInput(inputLayout: TextInputLayout, source: String?) {
  source?.let {
    if (!Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
      inputLayout.error = inputLayout.context.getString(R.string.str_error_email)
    } else {
      inputLayout.error = null
    }
  }
}

private fun validateNameInput(inputLayout: TextInputLayout, source: String?) {
  source?.let {
    if (source.length < Constants.MINIMUM_NAME_LENGTH) {
      inputLayout.error =  inputLayout.context.getString(R.string.str_error_name, Constants.MINIMUM_NAME_LENGTH)
    } else {
      inputLayout.error = null
    }
  }
}

private fun validatePhoneInput(inputLayout: TextInputLayout, source: String?) {
  source?.let {
    if (Validator.validateInputPhone(source)) {
      inputLayout.error = null
    } else {
      inputLayout.error = inputLayout.context.getString(R.string.str_error_phone)
    }
  }
}