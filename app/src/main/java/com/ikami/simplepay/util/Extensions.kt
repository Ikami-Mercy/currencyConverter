package com.ikami.simplepay.util

import android.text.TextUtils
import com.google.android.material.textfield.TextInputLayout
import com.ikami.simplepay.R
import com.ikami.simplepay.util.Utils.isBinaryNumber
import java.util.regex.Matcher
import java.util.regex.Pattern

object Extensions {
    fun TextInputLayout.validateRecipientName(): Boolean {
        val name = this.editText?.text.toString().trim { it <= ' ' }
        val nameRegex = "^[a-zA-Z']{2,}(?: [a-zA-Z']+){1,2}\$"
        return when {
            TextUtils.isEmpty(this.editText?.text) -> {
                this.error = context.getString(R.string.required_field)
                false
            }
            nameRegex.toRegex().matches(name) -> {
                this.error = null
                true
            }
            else -> {
                this.error = this.context.getString(R.string.invalid_name)
                false
            }
        }
    }

    fun TextInputLayout.validateRecipientPhone(country: String): Boolean {
        // 9 for KE & TZ
        // 7 for UG & NG
        var requiredPhoneLength = 0
        if(country == "Kenya" || country == "Tanzania" ){
            requiredPhoneLength = 9
        }
        if(country == "Uganda" || country == "Nigeria" ){
            requiredPhoneLength = 7
        }

        val input = this.editText?.text.toString()
        val special: Pattern = Pattern.compile("[!@#$%&*()_+=/|<>?{}\\[\\]~-]")
        val matcher: Matcher = special.matcher(input)
        return when {
            TextUtils.isEmpty(this.editText?.text) -> {
                this.error = this.context.getString(R.string.phone_required)
                false
            }
            this.editText?.text!!.length > requiredPhoneLength || this.editText?.text!!.length < requiredPhoneLength-> {

                this.error = String.format(this.context.getString(R.string.long_phone), requiredPhoneLength)
                false
            }
            matcher.find()  -> {
                this.error = this.context.getString(R.string.invalid_phone)
                false
            }
            else -> {
                true
            }
        }
    }


    fun TextInputLayout.validateAmountToSend(): Boolean {
        return when {
            TextUtils.isEmpty(this.editText?.text) -> {
                this.error = context.getString(R.string.amount_required)
                false
            }
            this.editText?.text!!.toString() == "0" -> {
                this.error = this.context.getString(R.string.amount_required)
                false
            }
            !isBinaryNumber(this.editText?.text!!.toString().toLong()) -> {
                this.error = this.context.getString(R.string.enter_valid_binary_amt)
                false
            }
            else -> {
                true
            }
        }
    }
}