package com.nachohseara.beermastersshop.ui.cart

import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.*
import com.nachohseara.beermastersshop.model.entity.*
import com.nachohseara.beermastersshop.model.payment.IXpayController
import com.nachohseara.beermastersshop.model.payment.XPayModel
import it.nexi.xpay.CallBacks.FrontOfficeCallbackQP
import it.nexi.xpay.Models.WebApi.Errors.ApiErrorResponse
import it.nexi.xpay.Models.WebApi.Responses.FrontOffice.ApiFrontOfficeQPResponse

class ConfirmOrderController(mV: ConfirmOrderFragment) : IFBConnector, IXpayController.Payment {
    private val myView = mV
    private lateinit var myAct : BaseActivity
    private lateinit var fb : DBOrders
    private var prods: Cart = Cart(hashMapOf(), 0)
    private lateinit var xpay: XPayModel
    private var tranCode = ""

    private var terminationCod = -1

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate() {
        fb = DBOrders(this, User(myView.requireContext()).getOrdersId())
        getProds()
    }

    private fun getProds() {
        val c = CartModel.loadSessionCart(myView.requireContext())
        if (c != null) {
            if (!c.products.isNullOrEmpty()) {
                prods = c
                myView.loadInfo(OrderList.cartProdToOrderProd(prods.products.values.toList()))
                return
            }
        }

        myView.msgToast("Error, try it later")
        onPositiveButton()
    }

    fun onPay() {
        if (myAct.onlineOrMsg() && prods.total > 0 && prods.products.isNotEmpty()) {
            (myView.requireActivity() as CartActivity).onWebView = true
            cancelTimer()

            tranCode = "BMS${System.currentTimeMillis()}"
            xpay = XPayModel(myView.requireActivity())
            xpay.pay(tranCode, prods.total, getFrontOfficeCallbackQp())
        }
    }

    fun onCancel() {
        myView.showChoiceDialog("Are you sure you want to cancel?", "")
    }

    fun onPositiveButton() {
        (myAct as CartActivity).endShoppingSession(false)
    }

    private fun upData(success: Boolean) {
        val order = OrderData(tranCode, prods.total, myView.address, OrderList.getDate(), "Payment", success, listOf())
        val items = OrderList.toItems(order)
        val lProd = OrderList.toProdsItems(prods.products.values.toList())

        fb.addOrder(items, lProd)
        if (success) {
            DBCart(this, User(myView.requireContext()).getCartId()).clearCart()
        }
    }

    private fun cancelTimer() {
        (myView.requireActivity() as CartActivity).cancelShoppingSession(false)
    }

    fun getTotal() :  String = Product.formattedPrice(prods.total)

    private fun end() {
        OrderCompletedActivity.saveState(terminationCod, myView.requireContext())
        myAct.endLoading()
        /*val args = Bundle()
        args.putInt("cod", terminationCod)*/
        myView.findNavController().navigate(R.id.action_confirmOrder_to_orderCompletedActivity)
        myView.requireActivity().finish()
    }

    override fun onSuccessFB(cod: Int, data: Any?) {
        if (cod == DBOrders.ORDER_UPLOAD_OK) {
            end()
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        when(cod) {
            DBOrders.ORDER_UPLOAD_FAILURE -> {
                end()
                //myView.msgSnackBar("Pedido completado, pero ha habido un problema al guardar el pedido")
            }
        }
    }

    //Xpay
    override fun getFrontOfficeCallbackQp(): FrontOfficeCallbackQP {
        return object : FrontOfficeCallbackQP {
            override fun onConfirm(response: ApiFrontOfficeQPResponse) { // user do the payment
                myAct.startLoading()
                if (response.isValid) {
                    (myView.requireActivity() as CartActivity).onWebView = false
                    cancelTimer()
                    upData(true)
                    terminationCod = 1
                    Log.d(XPayModel.TAG, "${response.result} Operation complete: ${response.extraParameters} {TranCode: ${response.codTrans}, Card Type: ${response.brand}}")
                } else { // security error has occurred
                    (myView.requireActivity() as CartActivity).onWebView = false
                    cancelTimer()
                    upData(false)
                    terminationCod = 0
                    if (response.error != null) {
                        Log.w(XPayModel.TAG, "Error ${response.result} ${response.error.code}: ${response.error.message}:\n${response.extraParameters}")
                    } else {
                        Log.w(XPayModel.TAG, "Error ${response.result}: ${response.extraParameters}")
                    }
                }
            }

            override fun onError(error: ApiErrorResponse) {
                Log.e(XPayModel.TAG, "ApiResponseError: ${error.error.message}")
            }

            override fun onCancel(response: ApiFrontOfficeQPResponse) { // user cancel payment
                myView.msgSnackBar("Payment canceled")
                Log.d(XPayModel.TAG, "${response.result} Opetation canceled by user")
            }
        }
    }
}