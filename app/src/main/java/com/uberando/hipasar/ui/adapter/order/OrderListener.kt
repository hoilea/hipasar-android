package com.uberando.hipasar.ui.adapter.order

import com.uberando.hipasar.entity.Order

class OrderListener(
  val itemClicked: (data: Order) -> Unit,
  val orderPay: (data: Order) -> Unit,
  val orderCancelled: (data: Order) -> Unit,
  val orderArrived: (data: Order) -> Unit,
  val orderFinished: (data: Order) -> Unit
) {
  fun onItemClick(data: Order) = itemClicked(data)
  fun onPay(data: Order) = orderPay(data)
  fun onCancel(data: Order) = orderCancelled(data)
  fun onArrive(data: Order) = orderArrived(data)
  fun onFinish(data: Order) = orderFinished(data)
}