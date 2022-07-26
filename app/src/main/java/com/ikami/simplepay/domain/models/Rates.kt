package com.ikami.simplepay.domain.models

import com.google.gson.annotations.SerializedName

data class Rates(
    @SerializedName("TZS")
    val TZS: Double,
    @SerializedName("UGX")
    val UGX: Double,
    @SerializedName("KES")
    val KES: Double,
    @SerializedName("NGN")
    val NGN: Double,
)