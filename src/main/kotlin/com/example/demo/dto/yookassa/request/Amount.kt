package com.example.demo.dto.yookassa.request

import com.google.gson.annotations.SerializedName


data class Amount (

  @SerializedName("value"    ) var value    : String? = null,
  @SerializedName("currency" ) var currency : String? = null

)