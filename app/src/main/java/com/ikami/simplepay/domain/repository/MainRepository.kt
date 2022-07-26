package com.ikami.simplepay.domain.repository

import com.ikami.simplepay.domain.models.CurrencyConverterResponse
import com.ikami.simplepay.domain.models.NetworkResponse


interface MainRepository {

    suspend fun getRates(appId: String): NetworkResponse<CurrencyConverterResponse>
}