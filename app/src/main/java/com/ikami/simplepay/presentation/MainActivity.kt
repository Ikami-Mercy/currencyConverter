package com.ikami.simplepay.presentation

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.ikami.simplepay.R
import com.ikami.simplepay.databinding.ActivityMainBinding
import com.ikami.simplepay.presentation.viewModel.MainViewModel
import com.ikami.simplepay.util.Extensions.validateAmountToSend
import com.ikami.simplepay.util.Extensions.validateRecipientName
import com.ikami.simplepay.util.Extensions.validateRecipientPhone
import com.ikami.simplepay.util.ProgressDialog
import com.ikami.simplepay.util.Utils.checkForInternet
import com.ikami.simplepay.util.Utils.getCountryCode
import com.ikami.simplepay.util.Utils.isBinaryNumber
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val progressDialog = ProgressDialog()
    private val viewModel: MainViewModel by viewModels()
    private var initiallyConnectedToInternet = true
    private var isConnectedToInternet = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        processUIToDisplay()
        observeViewModel()

    }


    private fun convertCurrency() {
        viewModel.convert(
            if (binding.etSendAmount.text.isNullOrEmpty()) {
                0.toString()
            } else {
                binding.etSendAmount.text.toString()
            },
            "USD",
            binding.spCountry.selectedItem.toString(),
        )
    }

    private fun setCountryCode() {

        binding.etCountryCode.setText(getCountryCode(binding.spCountry.selectedItem.toString()))
    }

    private fun fetchRates() {
        // Check for internet connectivity before hitting the rates end point

        if (checkForInternet(this)) {
            viewModel.getRates(binding.spCountry.selectedItem.toString(), "USD")

        } else {
            initiallyConnectedToInternet = false
            isConnectedToInternet = false
            binding.tvInternetError.isVisible = true
            binding.tvInternetError.text = getString(R.string.check_internet)
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.ops))
                .setContentText(getString(R.string.check_internet))
                .show()

        }
    }

    private fun observeViewModel() {
        observeInternet()
        observeRates()
        observeConversion()
        observeSendMoneyEvent()
    }

    private fun processUIToDisplay() {
        setCountryCode()
        fetchRates()

        binding.spCountry.onItemSelectedListener = this
        binding.etRecipientName.doAfterTextChanged {
            binding.tvRecipientName.error = null
        }
        binding.etPhoneNo.doAfterTextChanged {
            binding.tvPhoneNo.error = null
        }
        binding.etSendAmount.doAfterTextChanged {
            binding.tvSendAmount.error = null
            if (it?.length!! > 0) {
                val isValidBinary = isBinaryNumber(it.toString().toLong())
                if (isValidBinary) {
                    convertCurrency()
                } else {
                    binding.tvSendAmount.error = getString(R.string.enter_valid_binary_amt)
                }
            } else {
                convertCurrency()
            }
        }

        binding.btnSendAmt.setOnClickListener {
            // form validation
            binding.tvPhoneNo.error = null
            binding.tvRecipientName.error = null
            binding.tvSendAmount.error = null

            val recipientNameValid = binding.tvRecipientName.validateRecipientName()
            val recipientPhoneValid = binding.tvPhoneNo.validateRecipientPhone(
                binding.spCountry.selectedItem.toString()
            )
            val recipientAmountValid = binding.tvSendAmount.validateAmountToSend()
            if (!recipientNameValid || !recipientPhoneValid || !recipientAmountValid) {
                return@setOnClickListener
            } else {
                if (isConnectedToInternet) {
                    viewModel.sendMoney()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.check_internet),
                        Toast.LENGTH_LONG
                    ).show()
                }


            }
        }
    }

    private fun observeConversion() {
        lifecycleScope.launchWhenStarted {
            viewModel.conversionState.collect { event ->
                when (event) {
                    is MainViewModel.ConversionEvent.Success -> {
                        binding.conversionProgress.isVisible = false
                        binding.tvResult.isVisible = true
                        binding.tvResult.text = event.currentRate
                        binding.etReceiveAmount.setText(event.convertedResultAmount)
                        binding.tvError.isVisible = false
                    }
                    is MainViewModel.ConversionEvent.Failure -> {
                        binding.conversionProgress.isVisible = false
                        binding.tvError.text = event.errorText

                    }
                    is MainViewModel.ConversionEvent.Loading -> {
                        binding.conversionProgress.isVisible = true
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun observeRates() {
        lifecycleScope.launchWhenStarted {
            viewModel.ratesState.collect { event ->
                when (event) {
                    is MainViewModel.RatesEvent.Success -> {
                        binding.progressBar.isVisible = false
                        binding.tvResult.text = event.currentRate
                        binding.tvResult.isVisible = true
                    }
                    is MainViewModel.RatesEvent.Failure -> {
                        binding.progressBar.isVisible = false
                        binding.tvResult.isVisible = false
                        binding.tvResult.text = event.errorText
                    }
                    is MainViewModel.RatesEvent.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun observeSendMoneyEvent() {
        lifecycleScope.launchWhenStarted {
            viewModel.sendMoneyState.collect { event ->
                when (event) {
                    is MainViewModel.SendMoneyEvent.Success -> {
                        progressDialog.dismiss()
                        resetUI()
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.send_money_success),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                    is MainViewModel.SendMoneyEvent.Loading -> {
                        progressDialog.show(this@MainActivity)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun observeInternet() {
        viewModel.internetState.observe(this) { state ->
            when (state) {
                MainViewModel.NetworkStatus.Fetched -> {
                    binding.tvInternetError.isVisible = false
                    isConnectedToInternet = true
                    if (!initiallyConnectedToInternet) {
                        viewModel.getRates(binding.spCountry.selectedItem.toString(), "USD")
                    }

                }
                MainViewModel.NetworkStatus.Error -> {
                    binding.tvInternetError.isVisible = true
                    binding.tvInternetError.text = getString(R.string.check_internet)
                    isConnectedToInternet = false
                }
                else -> Unit
            }
        }
    }

    private fun resetUI() {
        binding.etPhoneNo.setText("")
        binding.etRecipientName.setText("")
        binding.etSendAmount.setText("0")
        binding.spCountry.setSelection(0)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        convertCurrency()
        setCountryCode()

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}


