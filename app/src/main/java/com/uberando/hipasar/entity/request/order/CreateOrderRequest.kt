package com.uberando.hipasar.entity.request.order

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
  @SerializedName("orders") val products: List<OrderProductRequest>,
  @SerializedName("address_id") val addressId: Int,
  @SerializedName("tax_fee") val taxFee: Int,
  @SerializedName("shipment_fee") val shipmentFee: Int,
  @SerializedName("subtotal") val subtotal: Int,
  @SerializedName("total") val total: Int,
  @SerializedName("delivery_time_id") val deliveryTimeId: Int,
  @SerializedName("delivery_date") val deliveryDate: String,
  @SerializedName("payment_type_id") val paymentTypeId: Int?
)
