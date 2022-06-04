package com.uberando.hipasar.ui.adapter.address

import com.uberando.hipasar.entity.Address

class AddressListener(
  val onAddressClicked: (address: Address) -> Unit,
  val onAddressSetPrimary: (address: Address) -> Unit,
  val onAddressRemoved: (address: Address) -> Unit
) {
  fun clickAddress(address: Address) = onAddressClicked(address)
  fun setPrimaryAddress(address: Address) = onAddressSetPrimary(address)
  fun removeAddress(address: Address) = onAddressRemoved(address)
}