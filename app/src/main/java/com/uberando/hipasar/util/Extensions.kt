package com.uberando.hipasar.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uberando.hipasar.BuildConfig
import com.uberando.hipasar.entity.*
import com.uberando.hipasar.entity.request.AddCartProductRequest
import com.uberando.hipasar.entity.request.UpdateCartQuantityRequest
import com.uberando.hipasar.entity.response.RequestPaymentResponse
import com.uberando.hipasar.entity.response.RequestPaymentResponseWrapper
import java.lang.Exception
import java.util.*

fun Int?.determineActionButtonVisibility() : Boolean {
  return when (this) {
    Constants.ORDER_STATUS_ON_DELIVERY -> true
    Constants.ORDER_STATUS_FINISHED -> true
    else -> false
  }
}

fun Int?.determineCancelButtonVisibility() : Boolean {
  return when (this) {
    Constants.ORDER_STATUS_PENDING -> true
    else -> false
  }
}

fun String?.produceNameFromEmail(): String {
  return if (this != null) {
    var tempString = ""
    run stop@ {
      this.forEach { char ->
        if (char != '@') {
          tempString += char
        } else {
          return@stop
        }
      }
    }
    tempString
  } else {
    "Name not provided"
  }
}

fun String.makeBearerToken(): String =
  "Bearer $this"

fun <T> LiveData<T>.toStringOrEmpty(): String {
  return if (this.value == null) {
    ""
  } else {
    this.value.toString()
  }
}

fun Collection<Any>?.getSizeOrZero(): Int {
  return this?.size ?: 0
}

fun List<OrderProduct>?.computeTotalPrice(): Int {
  var tempPrice = 0
  this?.forEach { product ->
    tempPrice += product.finalPrice
  }
  return tempPrice
}

fun MutableLiveData<User>.updateImageProperty(newImage: Image?) {
  this.postValue(
    this.value.also { it?.image = newImage }
  )
}

fun LiveData<Int>.valueOrZero(): Int {
  return this.value ?: 0
}

fun String?.findOrderId(): Int {
  return this?.makeListPair()?.findRequiredValue(Constants.KEY_ORDER_ID)!!.toInt()
}

fun String?.findOrderPrice(): Int {
  return this?.makeListPair()?.findRequiredValue(Constants.KEY_ORDER_PRICE)!!.toInt()
}

fun String?.findOrderCode(): String {
  return this?.makeListPair()?.findRequiredValue(Constants.KEY_ORDER_CODE)!!
}

fun String.makeListPair(): List<String> =
  this.split('{', '}', ',', ignoreCase = false).filter { it.isNotEmpty() }

fun List<String>.findRequiredValue(key: String): String =
  this.find { it.contains(key) }!!.split('=')[1]

fun List<Order>.asOrderType(): List<OrderType> {
  return this.map { order ->
    when (order.status) {
      Constants.ORDER_STATUS_PENDING -> OrderType.TypePending(order)
      Constants.ORDER_STATUS_ADMIN_CONFIRMATION -> OrderType.TypeAdminConfirmation(order)
      Constants.ORDER_STATUS_ON_PROCESS -> OrderType.TypeOnProcess(order)
      Constants.ORDER_STATUS_ON_DELIVERY -> OrderType.TypeOnDelivery(order)
      Constants.ORDER_STATUS_ARRIVED -> OrderType.TypeArrived(order)
      Constants.ORDER_STATUS_FINISHED -> OrderType.TypeFinished(order)
      Constants.ORDER_STATUS_CANCELLATION_REQUEST -> OrderType.TypeCancellationRequest(order)
      Constants.ORDER_STATUS_CANCELLED -> OrderType.TypeCancelled(order)
      else -> throw IllegalArgumentException("this should never thrown")
    }
  }
}

fun Order.buildPaymentArguments(): String {
  return mapOf(
    Constants.KEY_ORDER_ID to this.id,
    Constants.KEY_ORDER_CODE to this.orderCode,
    Constants.KEY_ORDER_PRICE to this.total
  ).toString()
}

fun DeliveryTime.buildSingleLineText(): String {
  return "${this.name} : ${this.sTime.split('.').first()} - ${this.eTime.split('.').first()}"
}

fun Date.plus(interval: Int): Date {
  return Calendar.getInstance().apply {
    time = this@plus
    add(Calendar.DATE, interval)
  }.time
}

fun List<Product>.asProductType(): List<ProductType> {
  return this.map { product ->
    if (product.stock > 0) {
      ProductType.ProductAvailableType(product)
    } else {
      ProductType.ProductOutOfStockType(product)
    }
  }
}

fun Product.asCartProductRequest() =
  AddCartProductRequest(
    productId = this.id,
    name = this.name,
    imageId = this.image?.id ?: "",
    unitPerGram = this.unitPerGram,
    price = this.price
  )

fun List<ProductSearch>.asProduct() =
  this.map { productSearch ->
    Product(
      id = productSearch.id,
      name = productSearch.name,
      nameEn = productSearch.name,
      description = productSearch.description,
      descriptionEn = productSearch.description,
      price = productSearch.price,
      unitPerGram = productSearch.unitPerGram,
      minimumQuantity = productSearch.minimumOrder,
      stock = productSearch.stock,
      image = Image(id = productSearch.imageId, path = productSearch.imagePath)
    )
  }

fun Product.asOnCartProduct() =
  this.also { it.productOnCart = true }

fun Int.asUpdateCartQuantityRequest() =
  UpdateCartQuantityRequest(quantity = this)

/**
 * Get active payment method only;
 *
 * 1    = ready.
 *
 * else = not ready.
 */
fun List<PaymentMethod>.activeOnly() =
  this.filter { it.status == 1 }

/**
 * Build payment method type for recycler view
 */
fun List<PaymentMethod>?.buildPaymentMethodType(): List<PaymentMethodType> {
  val paymentMethodTypes = mutableListOf<PaymentMethodType>()
  paymentMethodTypes.addCod()
  this?.groupBy { it.type }?.forEach { paymentMethodGroup ->
    paymentMethodTypes.add(PaymentMethodType.TitleType(paymentMethodGroup.key))
    paymentMethodGroup.value.forEach { paymentMethod ->
      paymentMethodTypes.add(PaymentMethodType.ContentType(paymentMethod))
    }
  }
  return paymentMethodTypes
}

fun MutableList<PaymentMethodType>.addCod() {
  this.add(PaymentMethodType.ContentType(PaymentMethod(id = null, type = Constants.PAYMENT_TYPE_COD, name = "COD", iconPath = COD_IMAGE)))
}

fun String.safeCharAt(index: Int): Char {
  return try {
    this.toCharArray()[index]
  } catch (e: Exception) {
    ' '
  }
}

fun String.setupPhonePrefix(): String {
  return if (this.startsWith("0")) {
    this.drop(1).let {
      "62$it"
    }
  } else if (!this.startsWith("62")) {
    "62$this"
  } else {
    this
  }
}

fun Int.asHumanReadableTime(): String {
  val minute = this / 60
  val second = this - (minute * 60)
  return "$minute:$second"
}

fun RequestPaymentResponseWrapper.buildPaymentUrl(walletType: String = ""): RequestPaymentResponseWrapper {
  return this.also { wrapper ->
    if (wrapper.type == Constants.PAYMENT_TYPE_EWALLET) {
      wrapper.response.paymentUrl = when (walletType) {
        Constants.WALLET_TYPE_DANA -> {
          wrapper.response.checkoutUrl
        }
        Constants.WALLET_TYPE_SHOPEEPAY -> {
          wrapper.response.checkoutUrl
        }
        Constants.WALLET_TYPE_LINKAJA -> {
          BuildConfig.DURIAN_PAY_CHECKOUT_URL.plus(wrapper.response.checkoutUrl)
        }
        else -> {
          null
        }
      }
    }
  }
}

private const val COD_IMAGE = "https://api.hipasar.com/storage/upload/payment_icon/cod.png"