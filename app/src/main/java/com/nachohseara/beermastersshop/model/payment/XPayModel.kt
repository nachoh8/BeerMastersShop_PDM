package com.nachohseara.beermastersshop.model.payment

import android.content.Context
import android.util.Log
import it.nexi.xpay.CallBacks.FrontOfficeCallbackQP
import it.nexi.xpay.Models.WebApi.Requests.FrontOffice.ApiFrontOfficeQPRequest
import it.nexi.xpay.Utils.EnvironmentUtils
import it.nexi.xpay.Utils.Exceptions.DeviceRootedException
import it.nexi.xpay.Utils.Exceptions.MacException
import it.nexi.xpay.Utils.QPUtils.CurrencyUtilsQP
import it.nexi.xpay.XPay
import java.io.UnsupportedEncodingException

class XPayModel(c: Context) {
    companion object {
        const val TIMEOUT = 2000 //2sec
        const val ALIAS = "ALIAS_WEB_00023966"
        const val SECRET_KEY = "I0PMPJNQYFWW0XCKDMK2V1RCAU94EKK2"
        val ENVIRONMENT = EnvironmentUtils.Environment.TEST
        const val TAG = "XPAY"
    }

    private val ctxt = c
    private lateinit var xpay: XPay

    fun setXpay() {
        try {
            xpay = XPay(ctxt, SECRET_KEY)
            Log.d(TAG, "Xpay created")
        } catch (e: DeviceRootedException) {
            Log.e(TAG, "Root Device detected: ${e.message}")
        }
    }

    fun pay (tranCode: String, total: Int, handler: FrontOfficeCallbackQP) {
        setXpay()
        Log.d(TAG, "TranCode: $tranCode, Amount: $total")
        val foRequest = getFrontOfficeRequest(tranCode, total)
        xpay.FrontOffice.setEnvironment(ENVIRONMENT)
        xpay.FrontOffice.paga(foRequest, true, handler)
    }

    private fun getFrontOfficeRequest(tranCode: String, total: Int) : ApiFrontOfficeQPRequest? {
        var request: ApiFrontOfficeQPRequest? = null
        try {
            request = ApiFrontOfficeQPRequest(ALIAS, tranCode, CurrencyUtilsQP.EUR, total.toLong())
            Log.d(TAG, "ApiFrontOfficeQPRequest completed")
        } catch (e: MacException) {// error in mac control or calculate mac
            e.printStackTrace()
            Log.e(TAG, "MacException: ${e.message}")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            Log.e(TAG, "UnsupportedEncodingException: ${e.message}")
        }

        return request
    }

}