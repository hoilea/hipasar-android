package com.uberando.hipasar.util

import com.uberando.hipasar.entity.AddressPicked
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Parser {
  
  suspend fun parseToAddress(jsonString: String): AddressPicked {
    var address: AddressPicked
    withContext(Dispatchers.Default) {
      JsonParser.parseString(jsonString).asJsonObject.also { jsonObject ->
        address = AddressPicked(
          basicAddress = jsonObject["address"].asString,
          detailAddress = jsonObject["roadname"].asString,
          postCode = jsonObject["zonecode"].asString,
          reference = jsonObject["buildingName"].asString
        )
      }
    }
    return address
  }
  
}