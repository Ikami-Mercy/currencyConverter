package com.ikami.simplepay.data

import com.ikami.simplepay.domain.models.CurrencyConverterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {

    @GET("/latest.json")
    suspend fun getRates(
        @Query("app_id") appId: String
    ): Response<CurrencyConverterResponse>
}

