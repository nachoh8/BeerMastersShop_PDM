package com.nachohseara.beermastersshop.model.payment

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.nachohseara.beermastersshop.model.entity.DateOrder
import com.nachohseara.beermastersshop.model.entity.PaymentList
import com.nachohseara.beermastersshop.model.entity.PaymentOrder
import com.nachohseara.beermastersshop.model.entity.StornoRimborsoOrder
import it.nexi.xpay.Utils.EnvironmentUtils
import org.json.JSONObject
import java.io.IOException
import java.security.MessageDigest


class XPayBackOffice(private val ctxt: Context) {
    companion object {
        const val TAG = "XPay_BackOffice"

        const val URL_TEST = "https://int-ecommerce.nexi.it/"
        const val URL_PRODUZIONE = "https://ecommerce.nexi.it/"
        const val URI_ORDERS = "ecomm/api/bo/reportOrdini"
        const val URI_ORDER_DETAIL = "ecomm/api/bo/situazioneOrdine"
        const val URI_REFUND = "ecomm/api/bo/storna"

        const val CURRENCY_EUR = 978

        const val GET_ORDERS_OK = 0
        const val GET_ORDERS_FAILLURE = 1
        const val GET_DETAIL_ORDER_OK = 2
        const val GET_DETAIL_ORDER_FAILLURE = 3
        const val REFUND_OK = 4
        const val REFUND_FAILLURE = 5

        fun hashMac(stringaMac: String) : String {
            val digest = MessageDigest.getInstance("SHA-1")
            val ba = digest.digest(stringaMac.toByteArray())
            val builder = StringBuilder()

            for (b in ba) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        }

        data class SendInfo(val apiKey: String, val codiceTransazione: String, val timeStamp: Long, val mac: String, val periodo: String,
                            val canale: String, val stato: Array<String>)
    }

    fun getOrders(connector: IXpayController.BackOffice, dateFrom: DateOrder, dateTo: DateOrder) {
        if (!dateFrom.isSet() || !dateTo.isSet()) {
            connector.onFaillure(GET_ORDERS_FAILLURE, "Invalid Dates")
        }

        val requestUrl = if (XPayModel.ENVIRONMENT == EnvironmentUtils.Environment.TEST) "$URL_TEST$URI_ORDERS"
        else "$URL_PRODUZIONE$URI_ORDERS"

        // Params
        val periodo = "${dateFrom.transformStr()} - ${dateTo.transformStr()}"
        val timeStamp = System.currentTimeMillis()
        val codTrans = ""
        val canale = "All"
        val stato: Array<String> = arrayOf("Autorizzato", "Negato", "Annullato", "Incassato", "rimborsato")

        val strMac = "apiKey=${XPayModel.ALIAS}" +
                "codiceTransazione=${codTrans}" +
                "periodo=${periodo}" +
                "canale=$canale" +
                "timeStamp=${timeStamp}" +
                XPayModel.SECRET_KEY

        val macCalculated = hashMac(strMac)

        // JSON
        val sendInfo = SendInfo(XPayModel.ALIAS, codTrans, timeStamp, macCalculated, periodo, canale, stato)
        var jsonStr = ""
        try {
            val mapper = ObjectMapper()
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            jsonStr = mapper.writeValueAsString(sendInfo)
        } catch (e: IOException) {
            connector.onFaillure(GET_ORDERS_FAILLURE, e.message.toString())
            Log.e(TAG, e.message.toString())
        }
        val json = JSONObject(jsonStr)

        // Call BackOffice
        val queue = Volley.newRequestQueue(ctxt)
        val request = JsonObjectRequest(Request.Method.POST, requestUrl, json,
        Response.Listener { response: JSONObject ->
            Log.d(TAG, "Response: $response")
            manageGetOrdersResponse(connector, response)
        }, Response.ErrorListener {error: VolleyError ->
                connector.onFaillure(GET_ORDERS_FAILLURE, error.message.toString())
                Log.d(TAG, "Error: $error")
            })

        queue.add(request)
    }

    private fun manageGetOrdersResponse(connector: IXpayController.BackOffice, response: JSONObject) {
        val esito = response.getString("esito")
        val paymentList = if (esito == "OK") {
            val payments = response.getJSONArray("report")
            val paymentsList = mutableListOf<PaymentOrder>()

            for (i in 0 until payments.length()) {
                val jsonObj = payments.getJSONObject(i)
                //Log.d(TAG, "jsonObj: $jsonObj")
                var pay: PaymentOrder
                try {
                    pay = Gson().fromJson(jsonObj.toString(), PaymentOrder::class.java)
                    //Log.d(TAG, "PaymentOrder: $pay")
                    paymentsList.add(pay)
                } catch (e: IOException) {
                    Log.d(TAG, "Error: $e")
                }
            }

            PaymentList(paymentsList)
        } else {
            PaymentList(listOf())
        }
        connector.onSuccess(GET_ORDERS_OK, paymentList)
    }

    fun getDetailOrder(codTrans: String) {
        val requestUrl = if (XPayModel.ENVIRONMENT == EnvironmentUtils.Environment.TEST) "$URL_TEST$URI_ORDER_DETAIL"
        else "$URL_PRODUZIONE$URI_ORDER_DETAIL"

        val timeStamp = System.currentTimeMillis()

        val strMac = "apiKey=${XPayModel.ALIAS}" +
                "codiceTransazione=${codTrans}" +
                "timeStamp=${timeStamp}" +
                XPayModel.SECRET_KEY

        val macCalculated = hashMac(strMac)

        val json = JSONObject()
        json.put("apiKey", XPayModel.ALIAS)
        json.put("codiceTransazione", codTrans)
        json.put("timeStamp", timeStamp)
        json.put("mac", macCalculated)
        Log.d(TAG, "json: $json")

        // Call BackOffice
        val queue = Volley.newRequestQueue(ctxt)
        val request = JsonObjectRequest(Request.Method.POST, requestUrl, json,
            Response.Listener { response: JSONObject ->
                try {
                    Log.d(TAG, "Response: $response")
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: $e")
                }
            }, Response.ErrorListener {error: VolleyError ->
                Log.d(TAG, "Error: $error")
            })

        queue.add(request)
    }

    fun refund(connector: IXpayController.BackOffice, rimborsoOrder: StornoRimborsoOrder) {
        val requestUrl = if (XPayModel.ENVIRONMENT == EnvironmentUtils.Environment.TEST) "$URL_TEST$URI_REFUND"
        else "$URL_PRODUZIONE$URI_REFUND"

        // To Send
        val json = rimborsoOrder.toJSONObject()

        // Call BackOffice
        val queue = Volley.newRequestQueue(ctxt)
        val request = JsonObjectRequest(Request.Method.POST, requestUrl, json,
            Response.Listener { response: JSONObject ->
                Log.d(TAG, "Response: $response")
                val esito = response.getString("esito")
                val res = if (esito == "KO") response.getJSONObject("errore").getString("messaggio") else null
                connector.onSuccess(REFUND_OK, res)
            }, Response.ErrorListener {error: VolleyError ->
                Log.d(TAG, "Error: $error")
                connector.onFaillure(REFUND_FAILLURE, "")
            })

        queue.add(request)
    }
}