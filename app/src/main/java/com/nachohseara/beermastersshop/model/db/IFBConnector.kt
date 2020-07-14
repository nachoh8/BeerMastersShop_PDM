package com.nachohseara.beermastersshop.model.db

interface IFBConnector {
    fun onSuccessFB(cod: Int, data: Any?)
    fun onFailureFB(cod: Int, error: String)
}