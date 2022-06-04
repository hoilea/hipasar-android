package com.uberando.hipasar.ui

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.uberando.hipasar.R
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.DateUtil
import com.uberando.hipasar.util.produceNameFromEmail
import com.uberando.hipasar.util.safeCharAt

@BindingAdapter("set_resource_image")
fun ImageView.bindResourceImage(resource: Int) {
  this.setImageResource(resource)
}

@BindingAdapter(value = ["setText_price_int", "setText_price_string"], requireAll = false)
fun TextView.bindPriceText(intPrice: Int?, stringPrice: String?) {
  val tempStringPrice = intPrice?.toString() ?: stringPrice ?: "0"
  val revPrice = tempStringPrice.reversed().chunked(3)
  var tempPrice = ""
  for (i in revPrice.size - 1 downTo 0) {
    tempPrice += revPrice[i].reversed()
    if (i != 0) {
      tempPrice += "."
    }
  }
  this.text =
    this.context.getString(R.string.str_template_price, tempPrice)
}

@BindingAdapter(value = ["setText_charString", "setText_charPosition"], requireAll = true)
fun TextView.bindCharText(charString: String?, charPosition: Int?) {
  if (charString != null && charPosition != null) {
    this.text = charString.safeCharAt(charPosition).toString()
  }
}

@BindingAdapter("set_profile_image")
fun ImageView.bindProfileImage(url: String?) {
  if (url != null) {
    Glide
      .with(this)
      .load(url)
      .placeholder(R.drawable.avatar_1)
      .error(R.drawable.avatar_1)
      .into(this)
  } else {
    this.setImageResource(R.drawable.avatar_1)
  }
}

@BindingAdapter("setImage_product")
fun ImageView.bindProductImage(url: String?) {
  if (url != null) {
    Glide
      .with(this)
      .load(url)
      .placeholder(R.color.grey_100_main)
      .error(R.color.grey_100_main)
      .into(this)
  } else {
    this.setImageResource(R.color.grey_100_main)
  }
}

@BindingAdapter("setText_datetime")
fun TextView.bindTextDateTime(datetime: String?) {
  if (datetime != null) {
    this.text = DateUtil.parseToOrderFormat(datetime)
  } else {
    this.text = "####"
  }
}

@BindingAdapter("setText_date")
fun TextView.bindTextDate(datetime: String?) {
  if (datetime != null) {
    this.text = DateUtil.parseToDateOnlyFormat(datetime)
  } else {
    this.text = "####"
  }
}

@BindingAdapter("setText_orderStatus")
fun TextView.bindTextOrderStatus(status: Int) {
  when (status) {
    Constants.ORDER_TYPE_ALL -> this.setText(R.string.str_status_all)
    Constants.ORDER_STATUS_PENDING -> this.setText(R.string.str_status_pending)
    Constants.ORDER_STATUS_ADMIN_CONFIRMATION -> this.setText(R.string.str_status_waiting)
    Constants.ORDER_STATUS_ON_PROCESS -> this.setText(R.string.str_status_on_process)
    Constants.ORDER_STATUS_ON_DELIVERY -> this.setText(R.string.str_status_on_delivery)
    Constants.ORDER_STATUS_ARRIVED -> this.setText(R.string.str_status_arrived)
    Constants.ORDER_STATUS_FINISHED -> this.setText(R.string.str_status_finished)
    Constants.ORDER_STATUS_CANCELLATION_REQUEST -> this.setText(R.string.str_status_admin_cancel)
    Constants.ORDER_STATUS_CANCELLED -> this.setText(R.string.str_status_cancelled)
    else -> this.setText(R.string.str_status_unknown)
  }
}

@BindingAdapter("setText_orderStatusNonCap")
fun TextView.bindTextOrderStatusNonCap(status: Int) {
  when (status) {
    Constants.ORDER_TYPE_ALL -> this.setText(R.string.str_status_non_cap_all)
    Constants.ORDER_STATUS_PENDING -> this.setText(R.string.str_status_non_cap_pending)
    Constants.ORDER_STATUS_ADMIN_CONFIRMATION -> this.setText(R.string.str_status_non_cap_waiting)
    Constants.ORDER_STATUS_ON_PROCESS -> this.setText(R.string.str_status_non_cap_on_process)
    Constants.ORDER_STATUS_ON_DELIVERY -> this.setText(R.string.str_status_non_cap_on_delivery)
    Constants.ORDER_STATUS_ARRIVED -> this.setText(R.string.str_status_non_cap_arrived)
    Constants.ORDER_STATUS_FINISHED -> this.setText(R.string.str_status_non_cap_finished)
    Constants.ORDER_STATUS_CANCELLATION_REQUEST -> this.setText(R.string.str_status_non_cap_admin_cancel)
    Constants.ORDER_STATUS_CANCELLED -> this.setText(R.string.str_status_non_cap_cancelled)
    else -> this.setText(R.string.str_status_non_cap_unknown)
  }
}

@BindingAdapter("setText_actionName")
fun Button.bindTextActionName(status: Int) {
  when (status) {
    Constants.ORDER_STATUS_PENDING -> this.setText(R.string.str_pay)
    Constants.ORDER_STATUS_ON_DELIVERY -> this.setText(R.string.str_product_received)
    Constants.ORDER_STATUS_FINISHED -> this.setText(R.string.str_create_feedback)
    else -> this.setText(R.string.str_status_unknown)
  }
}

@BindingAdapter("setImage_paymentCallback")
fun ImageView.bindImagePaymentCallback(callback: String?) {
  when (callback) {
    Constants.CALLBACK_SUCCESS -> this.placeResourceImage(R.drawable.checked)
    Constants.CALLBACK_FAILED -> this.placeResourceImage(R.drawable.cancel)
    Constants.CALLBACK_CANCEL -> this.placeResourceImage(R.drawable.cancel)
    else -> Unit
  }
}

@BindingAdapter("setImage_authResult")
fun ImageView.bindImageAuthResult(callback: String?) {
  when (callback) {
    Constants.CALLBACK_SUCCESS -> this.placeResourceImage(R.drawable.checked)
    Constants.CALLBACK_FAILED -> this.placeResourceImage(R.drawable.cancel)
    Constants.CALLBACK_CANCEL -> this.placeResourceImage(R.drawable.cancel)
    else -> Unit
  }
}

@BindingAdapter("setText_paymentCallbackTitle")
fun TextView.bindTextPaymentCallbackTitle(callback: String?) {
  when (callback) {
    Constants.CALLBACK_SUCCESS -> this.setText(R.string.str_payment_title_succeed)
    Constants.CALLBACK_FAILED -> this.setText(R.string.str_payment_title_failed)
    Constants.CALLBACK_CANCEL -> this.setText(R.string.str_payment_title_canceled)
    else -> Unit
  }
}

@BindingAdapter("setText_authResult")
fun TextView.bindTextAuthResult(callback: String?) {
  when (callback) {
    Constants.CALLBACK_SUCCESS -> this.setText(R.string.str_auth_success)
    Constants.CALLBACK_FAILED -> this.setText(R.string.str_auth_failed)
    Constants.CALLBACK_CANCEL -> this.setText(R.string.str_auth_failed)
    else -> Unit
  }
}

@BindingAdapter(
  requireAll = false,
  value = [
    "setText_userProfileName",
    "setText_userProfileName_alternative"
  ]
)
fun TextView.bindTextUserProfileName(firstName: String?, alternative: String?) {
  val firstNameNotNull = firstName != null && firstName != "null"
  val alternativeNotNull = alternative != null && alternative != "null"
  when {
    firstNameNotNull ->
      this.text = firstName
    alternativeNotNull ->
      this.text = alternative.produceNameFromEmail()
    else ->
      this.text = this.context.getString(R.string.str_no_name)
  }
}

@BindingAdapter("setText_paymentCallbackDescription")
fun TextView.bindTextPaymentCallbackDescription(callback: String?) {
  when (callback) {
    Constants.CALLBACK_SUCCESS -> this.setText(R.string.str_payment_description_succeed)
    Constants.CALLBACK_FAILED -> this.setText(R.string.str_payment_description_failed)
    Constants.CALLBACK_CANCEL -> this.setText(R.string.str_payment_description_canceled)
    else -> Unit
  }
}


private fun ImageView.placeResourceImage(@DrawableRes resource: Int) {
  Glide.with(this).load(resource).into(this)
}

@BindingAdapter("setOnClick_navigationIcon")
fun Toolbar.bindOnClickNavigationIcon(onClick: () -> Unit?) {
  this.setNavigationOnClickListener { onClick() }
}

@BindingAdapter(
  requireAll = false,
  value = [
    "setOnClick_hideSoftKeyboard",
    "setOnClick_action"
  ]
)
fun Button.bindOnClickHideSoftKeyboard(hideKeyboard: Boolean? = false, action: () -> Unit) {
    this.setOnClickListener {
      action()
      if (hideKeyboard == true) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        this.rootView.also {
          imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
      }
  }
}

@BindingAdapter("setScrollBarEnabled")
fun HorizontalScrollView.bindScrollBarEnabled(scrollBarEnabled: Boolean) {
 this.isVerticalScrollBarEnabled = scrollBarEnabled
 this.isHorizontalScrollBarEnabled = scrollBarEnabled
}