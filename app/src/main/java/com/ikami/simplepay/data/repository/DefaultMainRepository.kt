package com.ikami.simplepay.data.repository

import com.ikami.simplepay.data.CurrencyApi
import com.ikami.simplepay.domain.models.CurrencyConverterResponse
import com.ikami.simplepay.domain.repository.MainRepository
import com.ikami.simplepay.domain.models.NetworkResponse
import java.net.SocketException
import javax.inject.Inject

class DefaultMainRepository @Inject constructor(
    private val api: CurrencyApi
) : MainRepository {

    override suspend fun getRates(appId: String): NetworkResponse<CurrencyConverterResponse> {
        return try {
            val response = api.getRates(appId)
            val result = response.body()
            if(response.isSuccessful && result != null) {
                NetworkResponse.Success(result)
            } else {
                NetworkResponse.Error(response.message())
            }
        } catch(e: SocketException ) {
            NetworkResponse.Error(e.message ?: "You are currently not connected to the internet. Please try again")
        }
        catch(e: java.net.UnknownHostException ) {
            NetworkResponse.Error(e.message ?: "You are currently not connected to the internet. Please try again")
        }
        catch(e: Exception) {

            NetworkResponse.Error(e.message ?: "An error occurred")
        }
    }
}