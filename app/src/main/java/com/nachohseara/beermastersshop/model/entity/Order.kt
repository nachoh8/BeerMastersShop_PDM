package com.nachohseara.beermastersshop.model.entity

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.nachohseara.beermastersshop.model.db.DBOrders
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

data class OrderData(var id: String, var total: Int, var address: String, var date: String,
                     var causal: String, var success: Boolean, var prods: List<OrderProduct>)
data class OrderProduct(val id: String, val name: String, val numUd: Int, val price: Int)
data class ListOrderProds(val orderId: String, val prods: List<OrderProduct>)

class OrderList {
    companion object {
        fun toOrderList(docs: QuerySnapshot) : OrderList {
            val ol = OrderList()
            for (doc in docs) {
                if (doc != null && doc.exists()) {
                    val order = OrderData(
                        doc.getString(DBOrders.FIELD_ORDER_ID).toString(),
                        doc.get(DBOrders.FIELD_ORDER_TOTAL, Int::class.java)!!,
                        doc.getString(DBOrders.FIELD_ORDER_ADDRESS).toString(),
                        doc.getString(DBOrders.FIELD_ORDER_DATE).toString(),
                        doc.getString(DBOrders.FIELD_ORDER_CAUSAL).toString(),
                        doc.getBoolean(DBOrders.FIELD_ORDER_SUCCESS)!!,
                        listOf()
                    )
                    ol.orders[doc.id] = order
                    ol.ordersOrdered.add(order)
                }
            }
            return ol
        }

        fun toItems(order: OrderData) : HashMap<String, Any> {
            val items = HashMap<String, Any>()
            items[DBOrders.FIELD_ORDER_ID] = order.id
            items[DBOrders.FIELD_ORDER_TOTAL] = order.total
            items[DBOrders.FIELD_ORDER_ADDRESS] = order.address
            items[DBOrders.FIELD_ORDER_DATE] = order.date
            items[DBOrders.FIELD_ORDER_CAUSAL] = order.causal
            items[DBOrders.FIELD_ORDER_SUCCESS] = order.success

            return items
        }

        fun toProdsItems(prods: List<CartProduct>) : List<HashMap<String, Any>> {
            val m : MutableList<HashMap<String, Any>> = mutableListOf()
            for (p in prods) {
                val items = HashMap<String, Any>()
                items[DBOrders.FIELD_PROD_ID] = p.data.getDocId()
                items[DBOrders.FIELD_PROD_NAME] = p.data.getName()
                items[DBOrders.FIELD_PROD_PRICE] = p.data.getPrice()
                items[DBOrders.FIELD_PROD_NUMUD] = p.numUd
                m.add(items)
            }

            return m.toList()
        }

        fun getDate() : String {
            val d = Calendar.getInstance().time
            val pattern = "yyyy-MM-dd HH:mm:ss"
            val simple = SimpleDateFormat(pattern)

            return simple.format(d)
        }

        fun getDateDay() : String {
            val d = Calendar.getInstance().time
            val pattern = "yyyy-MM-dd"
            val simple = SimpleDateFormat(pattern)

            return simple.format(d)
        }

        fun toProdList(orderId: String, docs: QuerySnapshot) : ListOrderProds {
            val l = mutableListOf<OrderProduct>()
            for (doc in docs) {
                if (doc != null && doc.exists()) {
                    val prod = toOrderProd(doc)
                    l.add(prod)
                }
            }

            return ListOrderProds(orderId, l.toList())
        }

        private fun toOrderProd(doc: QueryDocumentSnapshot) : OrderProduct {
            val id = doc.getString(DBOrders.FIELD_PROD_ID)!!
            val name = doc.getString(DBOrders.FIELD_PROD_NAME)!!
            val numUd = doc.get(DBOrders.FIELD_PROD_NUMUD, Int::class.java)!!
            val price = doc.get(DBOrders.FIELD_PROD_PRICE, Int::class.java)!!
            return OrderProduct(id, name, numUd, price)
        }

        fun cartProdToOrderProd(cart: List<CartProduct>) : List<OrderProduct> {
            val l = mutableListOf<OrderProduct>()
            for (prod in cart) {
                val op = OrderProduct(prod.data.getDocId(), prod.data.getName(), prod.numUd, prod.data.getPrice())
                l.add(op)
            }

            return l.toList()
        }

        //yyyy-MM-dd HH:mm:ss -> HH:mm:ss dd-MM-yyyy
        fun getDateFormatted(date: String) : String {
            val strs = date.split(" ")
            val hour = strs[strs.size - 1]
            val date = (strs[0].split("-")).reversed()
            val dateRes = date.joinToString("-")

            return "$hour $dateRes"
        }
    }

    private val orders: HashMap<String,OrderData> = hashMapOf()
    private val ordersOrdered: MutableList<OrderData> = mutableListOf()
    private var ordersWithProds = 0

    fun isEmpty() : Boolean = orders.isEmpty()

    fun getOrders() : List<OrderData> = ordersOrdered.toList()//orders.values.toList().sortedWith(orderComparator)

    fun getKeys() : List<String> = orders.keys.toList()

    fun reversedOrders() : List<OrderData> = ordersOrdered.toList().reversed()

    fun addProdsToOrder(prods: ListOrderProds) {
        val order = orders[prods.orderId]
        if (order != null) {
            order.prods = prods.prods
            orders[prods.orderId] = order
            ordersWithProds++
        }
    }

    fun isProdsLoaded() : Boolean = ordersWithProds == orders.size
}