package com.ikami.simplepay.domain.models

data class CurrencyConverterResponse(
    val base: String,
    val disclaimer: String,
    val license: String,
    val rates: Rates,
    val timestamp: Int
)