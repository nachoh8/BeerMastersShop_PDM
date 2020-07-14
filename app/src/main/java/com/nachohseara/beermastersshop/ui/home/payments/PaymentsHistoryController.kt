package com.nachohseara.beermastersshop.ui.home.payments

import android.os.Handler
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.entity.DateOrder
import com.nachohseara.beermastersshop.model.entity.PaymentList
import com.nachohseara.beermastersshop.model.entity.PaymentOrder
import com.nachohseara.beermastersshop.model.entity.StornoRimborsoOrder
import com.nachohseara.beermastersshop.model.payment.IXpayController
import com.nachohseara.beermastersshop.model.payment.XPayBackOffice

class PaymentsHistoryController(private val myView: PaymentsHistoryFragment) : IXpayController.BackOffice {
    private lateinit var myActivity: BaseActivity
    private var payments = PaymentList(listOf())

    private var dateFrom = DateOrder.Empty()
    private var dateTo = DateOrder.Empty()
    private var fromSwipe = false

    private var onRefund = false

    fun onAttach() {
        myActivity = myView.getBaseActivity()
    }

    fun onCreate(dateFrom: String, dateTo: String) {
        if (myActivity.notOnlineAndGo()) return
        this.dateFrom = DateOrder.toDateOrder(dateFrom)
        this.dateTo = DateOrder.toDateOrder(dateTo)
        getPayments(false)
    }

    private fun loadPayments() {
        myView.loadPayments(payments.payments)
    }

    fun isEmpty() : Boolean = payments.payments.isEmpty()

    fun getPayments(fromSwipe: Boolean) {
        this.fromSwipe = fromSwipe
        XPayBackOffice(myView.requireContext()).getOrders(this, dateFrom, dateTo)
    }

    private fun endGetPayments() {
        loadPayments()
        if (fromSwipe) {
            Handler().postDelayed({
                myView.offSwipeRefresh()
                myView.actionPostComplete()
            }, 500L)
        } else {
            myView.loadInfoComplete()
        }
    }

    fun onLongClick(order: PaymentOrder) {
        if (onRefund) return
       if (order.stato == "Contabilizzato" || order.stato == "Autorizzato") myView.alertDialogStornoRimborso(order)
    }

    fun onConfirmRefund(order: PaymentOrder) {
        if (myActivity.onlineOrMsg()) {
            onRefund = true
            myActivity.startLoading()

            val orderRefund = StornoRimborsoOrder(order.codiceTransazione, order.importo)
            XPayBackOffice(myView.requireContext()).refund(this, orderRefund)
        }
    }

    private fun endRefund(msg: String) {
        onRefund = false
        myActivity.endLoading()
        myView.msgSnackBar(msg)
    }

    // XPayBackoffice
    override fun onSuccess(cod: Int, data: Any?) {

        when (cod) {
            XPayBackOffice.GET_ORDERS_OK -> {
                if (data != null && data is PaymentList) {
                    payments = data
                    endGetPayments()
                } else {
                    onFaillure(XPayBackOffice.GET_ORDERS_FAILLURE, "")
                }
            }
            XPayBackOffice.REFUND_OK -> {
                if (data != null && data is String) { // KO
                    endRefund(data)
                } else { // OK
                    endRefund(myView.getString(R.string.refund_ok))
                    getPayments(false)
                }
            }
        }
    }

    override fun onFaillure(cod: Int, error: String) {
        when (cod) {
            XPayBackOffice.GET_ORDERS_FAILLURE -> {
                myView.msgSnackBar(myView.getString(R.string.error_data))
                endGetPayments()
            }
            XPayBackOffice.REFUND_FAILLURE -> {
                endRefund(myView.getString(R.string.error_data))
            }
        }
    }
}