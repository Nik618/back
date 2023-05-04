package com.example.demo.dto.yookassa.request

import com.google.gson.annotations.SerializedName


data class PaymentMethodData (

  @SerializedName("type" ) var type : String? = null

)