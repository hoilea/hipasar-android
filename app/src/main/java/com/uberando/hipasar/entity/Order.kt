package com.uberando.hipasar.entity

import com.google.gson.annotations.SerializedName

data class Order(
  @SerializedName("id") val id: String,
  @SerializedName("order_code") val orderCode: String,
  @SerializedName("status") var status: Int,
  @SerializedName("ordered_at") val orderedAt: String,
  @SerializedName("paid_at") val paidAt: String,
  @SerializedName("confirmed_at") val confirmedAt: String,
  @SerializedName("arrived_at") val arrivedAt: String,
  @SerializedName("finished_at") val finishedAt: String,
  @SerializedName("total_products") val totalProducts: Int,
  @SerializedName("subtotal") val subtotal: Int,
  @SerializedName("total") val total: Int,
  @SerializedName("payment_type") val paymentType: PaymentMethod,

  /** Detail purpose */
  @SerializedName("tax_fee") val taxFee: Int,
  @SerializedName("shipment_fee") val shipmentFee: Int,
  @SerializedName("address") val address: Address,
  @SerializedName("products") val products: List<OrderProduct>,
  @SerializedName("delivery_date") val deliveryDate: String,
  @SerializedName("delivery_time") val deliveryTime: DeliveryTime
) {
  fun taxPercentage() = subtotal / taxFee
}