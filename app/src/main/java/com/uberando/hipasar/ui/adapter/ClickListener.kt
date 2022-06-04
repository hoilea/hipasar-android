package com.uberando.hipasar.ui.adapter

class ClickListener<T>(
  val itemClicked: (data: T) -> Unit
) {
  fun onItemClicked(data: T) = itemClicked(data)
}