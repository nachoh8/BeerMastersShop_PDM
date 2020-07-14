package com.nachohseara.beermastersshop.model.entity

import com.nachohseara.beermastersshop.model.payment.XPayBackOffice
import com.nachohseara.beermastersshop.model.payment.XPayModel
import org.json.JSONObject

class StornoRimborsoOrder(
    val codTrans: String,
    val amount: Int,
    val currency: Int = XPayBackOffice.CURRENCY_EUR) {

    val timeStamp = System.currentTimeMillis()
    val mac: String

    init {
        val calculateMac = "apiKey=${XPayModel.ALIAS}" +
                "codiceTransazione=${codTrans}" +
                "divisa=${currency}" +
                "importo=$amount" +
                "timeStamp=${timeStamp}" +
                XPayModel.SECRET_KEY

        mac = XPayBackOffice.hashMac(calculateMac)
    }

    fun toJSONObject() : JSONObject {
        val json = JSONObject()
        json.put("apiKey", XPayModel.ALIAS)
        json.put("codiceTransazione", codTrans)
        json.put("importo", amount)
        json.put("divisa", currency)
        json.put("timeStamp", timeStamp)
        json.put("mac", mac)

        return json
    }
}