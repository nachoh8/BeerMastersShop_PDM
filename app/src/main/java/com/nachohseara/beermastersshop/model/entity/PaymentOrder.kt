package com.nachohseara.beermastersshop.model.entity

import com.google.gson.Gson

data class DateOrder(var day: Int, var month: Int, var year: Int) {
    companion object {
        fun Empty() : DateOrder = DateOrder(-1, -1, -1)

        fun toJSON(dateOrder: DateOrder) : String {
            return Gson().toJson(dateOrder)
        }
        fun toDateOrder(json: String) : DateOrder {
            if (json.isEmpty()) return  DateOrder(-1, -1, -1)
            return Gson().fromJson(json, DateOrder::class.java)
        }
    }

    fun isSet() : Boolean = day > 0 && month > -1 && year > 0

    fun transformStr(inverse: Boolean = false) : String {
        val _m = month + 1
        val d = if (day < 10) "0${day}" else day.toString()
        val m = if (_m < 10) "0$_m" else _m.toString()
        return if (inverse) "$year/$m/$d" else "$d/$m/${year}"
    }
}

data class PaymentList(val payments: List<PaymentOrder>)

data class PaymentOrder(
    val codiceTransazione: String,
    val numeroMerchant: String,
    val importo: Int,
    val divisa: String,
    val codiceAutorizzazione: String,
    val brand: String,
    val tipoPagamento: String,
    val tipoTransazione: String,
    val transactionTypeExtended: String,
    val nazione: String,
    val tipoProdotto: String,
    val pan: String,
    val parametri: String,
    val stato: String,
    val dataTransazione: String,
    val operationDate: String,
    val serviceType: String,
    val name: String,
    val surname: String,
    val mail: String)