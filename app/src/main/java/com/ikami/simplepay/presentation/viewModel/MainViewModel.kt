package com.ikami.simplepay.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ikami.simplepay.BuildConfig
import com.ikami.simplepay.domain.models.NetworkResponse
import com.ikami.simplepay.domain.models.Rates
import com.ikami.simplepay.domain.repository.MainRepository
import com.ikami.simplepay.util.*
import com.ikami.simplepay.util.Utils.getCurrencyForCountry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatchers: DispatcherProvider,
    private val networkStatusTracker: NetworkStatusTracker,
) : ViewModel() {

    sealed class RatesEvent {
        class Success(val currentRate: String) : RatesEvent()
        class Failure(val errorText: String) : RatesEvent()
        object Loading : RatesEvent()
        object Empty : RatesEvent()
    }

    sealed class ConversionEvent {
        class Success(val convertedResultAmount: String, val currentRate: String) :
            ConversionEvent()

        class Failure(val errorText: String) : ConversionEvent()
        object Loading : ConversionEvent()
        object Empty : ConversionEvent()
    }

    sealed class SendMoneyEvent {
        class Success(val successMessage: String) : SendMoneyEvent()
        object Loading : SendMoneyEvent()
        object Empty : SendMoneyEvent()
    }

    sealed class NetworkStatus {
        object Fetched : NetworkStatus()
        object Error : NetworkStatus()
    }

    private val _ratesState = MutableStateFlow<RatesEvent>(RatesEvent.Empty)
    val ratesState: StateFlow<RatesEvent> = _ratesState

    private val _currentRates = MutableStateFlow(Rates(0.0, 0.0, 0.0, 0.0))
    private val currentRates: StateFlow<Rates> = _currentRates

    private val _conversionState = MutableStateFlow<ConversionEvent>(ConversionEvent.Empty)
    val conversionState: StateFlow<ConversionEvent> = _conversionState

    private val _sendMoneyState = MutableStateFlow<SendMoneyEvent>(SendMoneyEvent.Empty)
    val sendMoneyState: StateFlow<SendMoneyEvent> = _sendMoneyState


    val internetState = networkStatusTracker.networkStatus
        .map(
            onAvailable = { NetworkStatus.Fetched },
            onUnavailable = { NetworkStatus.Error },
        )
        .asLiveData(dispatchers.io)

    fun convert(
        amountToConvert: String,
        currentUserCurrency: String,
        receivingCountry: String
    ) {
        val amount = amountToConvert.toFloatOrNull()
        if (amount == null) {
            _conversionState.value = ConversionEvent.Failure("")
            return
        }
        val rate = getRateForCountry(receivingCountry)
        if (rate == null) {
            _conversionState.value = ConversionEvent.Failure("Unexpected error")
        } else {

            val amountToConvertInDecimal = Utils.convertBinaryToDecimal(amountToConvert.toLong())
            val convertedCurrency = round(amountToConvertInDecimal * rate)
            val convertedCurrencyInBinary = Utils.convertDecimalToBinary(convertedCurrency.toInt())
            _conversionState.value = ConversionEvent.Success(
                "$convertedCurrencyInBinary ${getCurrencyForCountry(receivingCountry)}",
                "1 $currentUserCurrency = $rate  ${getCurrencyForCountry(receivingCountry)}"
            )
        }
    }

    fun getRates(selectedCountry: String, currentUserCurrency: String) {

        viewModelScope.launch(dispatchers.io) {
            _ratesState.value = RatesEvent.Loading

            when (val ratesResponse = repository.getRates(BuildConfig.APP_ID)) {
                is NetworkResponse.Error -> {
                    _ratesState.value = RatesEvent.Failure(ratesResponse.message!!)
                }
                is NetworkResponse.Success -> {
                    val rates = ratesResponse.data!!.rates
                    _currentRates.value = rates
                    _ratesState.value = RatesEvent.Success(
                        "1 $currentUserCurrency = ${getRateForCountry(selectedCountry)} ${
                            getCurrencyForCountry(
                                selectedCountry
                            )
                        }",
                    )
                }
            }
        }
    }

    fun sendMoney() {
        viewModelScope.launch(dispatchers.io) {
            _sendMoneyState.value = SendMoneyEvent.Loading
            delay(1000)
            _sendMoneyState.value = SendMoneyEvent.Success("")
        }
    }

    private fun getRateForCountry(country: String) = when (country) {
        "Uganda" -> currentRates.value.UGX
        "Tanzania" -> currentRates.value.TZS
        "Kenya" -> currentRates.value.KES
        "Nigeria" -> currentRates.value.NGN
        else -> null
    }

}