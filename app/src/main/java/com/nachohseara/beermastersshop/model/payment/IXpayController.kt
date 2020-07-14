package com.nachohseara.beermastersshop.model.payment

import it.nexi.xpay.CallBacks.FrontOfficeCallbackQP

interface IXpayController {
    interface Payment {
        fun getFrontOfficeCallbackQp(): FrontOfficeCallbackQP
    }
    interface BackOffice {
        fun onSuccess(cod: Int, data: Any?)
        fun onFaillure(cod: Int, error: String)
    }
}