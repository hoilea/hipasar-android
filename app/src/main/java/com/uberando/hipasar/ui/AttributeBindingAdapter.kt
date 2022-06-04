package com.uberando.hipasar.ui

import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.uberando.hipasar.R

@BindingAdapter(
  value = [
    "setText_content",
    "setText_contentAlternative"
  ],
  requireAll = false
)
fun TextView.bindTextContent(content: String?, alternative: String?) {
  val contentAvailable = content != null || content != "null"
  val alternativeAvailable = alternative != null || alternative != "null"
  when {
    contentAvailable -> this.text = content
    alternativeAvailable -> this.text = alternative
    else -> this.context.getString(R.string.str_no_content_provided)
  }
}

@BindingAdapter("setText_contentPassword")
fun TextView.bindTextContentPassword(passwordAvailable: Boolean?) {
  if (passwordAvailable == true) {
    this.text = this.context.getString(R.string.str_password_dummy)
  } else {
    this.text = this.context.getString(R.string.str_password_not_set)
  }
}

@BindingAdapter(
  value = [
    "setButtonText_condition",
    "setButtonText_conditionTrue",
    "setButtonText_conditionFalse"
  ],
  requireAll = true
)
fun Button.bindTextButtonAction(
  condition: Boolean = false,
  textIfTrue: String?,
  textIfFalse: String?
) {
  if (condition) {
    this.text = textIfTrue
  } else {
    this.text = textIfFalse
  }
}