package com.ikami.simplepay

import com.ikami.simplepay.util.Utils
import junit.framework.Assert
import org.junit.Test

class UtilsUnitTest {
    @Test
    fun getCountryCodeShouldReturnTheRightCountryCode () {
        var utils =Utils
        var countryCode = utils.getCountryCode("Kenya")
        Assert.assertEquals("+254", countryCode)
    }

    @Test
    fun convertDecimalToBinaryShouldReturnTheRightBinaryNo () {
        var utils =Utils
        var binary = utils.convertDecimalToBinary(2)
        Assert.assertEquals("10", binary)
    }
    @Test
    fun convertBinaryToDecimalShouldReturnTheRightDecimal () {
        var utils =Utils
        var decimal = utils.convertBinaryToDecimal(10)
        Assert.assertEquals(2.toFloat(), decimal)
    }
    @Test
    fun isBinaryNumberShouldReturnTrue () {
        var utils =Utils
        var isbinary = utils.isBinaryNumber(10)
        Assert.assertEquals(true, isbinary)
    }
    @Test
    fun getCurrencyForCountryShouldReturnTheRightCurrency () {
        var utils =Utils
        var countryCode = utils.getCurrencyForCountry("Uganda")
        Assert.assertEquals("UGX", countryCode)
    }
}