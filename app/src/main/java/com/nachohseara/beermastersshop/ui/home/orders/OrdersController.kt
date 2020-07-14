package com.nachohseara.beermastersshop.ui.home.orders

import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.DBOrders
import com.nachohseara.beermastersshop.model.db.IFBConnector
import com.nachohseara.beermastersshop.model.entity.ListOrderProds
import com.nachohseara.beermastersshop.model.entity.OrderData
import com.nachohseara.beermastersshop.model.entity.OrderList
import com.nachohseara.beermastersshop.model.entity.User

class OrdersController(mV: OrdersFragment) : IFBConnector {
    private val myView = mV
    private lateinit var myAct: BaseActivity
    private lateinit var db : DBOrders
    private var orderL = OrderList()
    private var sortByNewest = true

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate() {
        if (myAct.notOnlineAndGo()) return
        db = DBOrders(this, User(myView.requireContext()).getOrdersId())
        if (orderL.isEmpty()) {
            db.getOrders()
        } else {
            loadOrders(orderL.getOrders())
        }
    }

    fun sortOrders(recent: Boolean) {
        if (orderL.isEmpty()) return

        if (sortByNewest && !recent) {
            loadOrders(orderL.reversedOrders())
            sortByNewest = false
        } else if(!sortByNewest && recent) {
            loadOrders(orderL.getOrders())
            sortByNewest = true
        }
    }

    private fun loadOrders(l: List<OrderData>) {
        myView.showOrders(l)
        myView.loadInfoComplete()
    }

    fun isEmpty() : Boolean = orderL.isEmpty()

    private fun getOrderProds() {
        for (orderId in orderL.getKeys()) {
            db.getProdsOrder(orderId)
        }
    }

    override fun onSuccessFB(cod: Int, data: Any?) {
        when(cod) {
            DBOrders.GET_ORDERS_OK ->{
                if (data is OrderList) {
                    orderL = data
                    if (orderL.isEmpty()) {
                        loadOrders(orderL.getOrders())
                    } else {
                        getOrderProds()
                    }
                }
            }
            DBOrders.GET_PRODS_OK -> {
                if (data != null && data is ListOrderProds) {
                    orderL.addProdsToOrder(data)
                    if (orderL.isProdsLoaded()) {
                        loadOrders(orderL.getOrders())
                    }
                }
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        if (cod == DBOrders.GET_ORDERS_FAILURE) {
            myView.msgSnackBar(myView.getString(R.string.error_data))
            loadOrders(listOf())
        }
    }
}