package com.nachohseara.beermastersshop.model.db

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.nachohseara.beermastersshop.model.entity.OrderList

class DBOrders(private val con: IFBConnector, private val id: String) {
    companion object {
        const val COLLECTION = "Orders"

        const val SUBCOLLECTION = "MyOrders"
        const val COL_PRODS = "Products"
        const val FIELD_ORDER_ID = "id"
        const val FIELD_ORDER_TOTAL = "total"
        const val FIELD_ORDER_ADDRESS = "address"
        const val FIELD_ORDER_DATE = "date"
        const val FIELD_ORDER_CAUSAL = "causal"
        const val FIELD_ORDER_SUCCESS = "success"
        const val FIELD_PROD_ID = "docId"
        const val FIELD_PROD_NAME = "name"
        const val FIELD_PROD_PRICE = "price"
        const val FIELD_PROD_NUMUD = "numUd"

        const val COL_PRODS_PURCHASED = "ProductsPurchased"

        const val TAG = "DB_ORDERS"
        const val GET_ORDERS_OK = 201
        const val GET_ORDERS_FAILURE = 202
        const val ORDER_UPLOAD_OK = 203
        const val ORDER_UPLOAD_FAILURE = 204
        const val GET_PRODS_OK = 205
        const val GET_PRODS_FAILURE = 206
        const val GET_PROD_PURCHASED_OK = 207
        const val GET_PROD_PURCHASED_FAILURE = 208
    }

    private val db = FirebaseFirestore.getInstance()
    private val prodsPurchasedRef = db.collection(COLLECTION).document(id).collection(COL_PRODS_PURCHASED)
    private val myOrdersRef = db.collection(COLLECTION).document(id).collection(SUBCOLLECTION)

    fun addOrder(items: HashMap<String, Any>, prod: List<HashMap<String, Any>>) {
        val orderRef = myOrdersRef.document()
        db.runTransaction { transaction ->
            transaction.set(orderRef, items)
            for (p in prod) {
                transaction.set(orderRef.collection(COL_PRODS).document(), p)
            }
        }.addOnSuccessListener {
            con.onSuccessFB(ORDER_UPLOAD_OK, null)
            Log.d(TAG, "Order upload succesfully")
        }.addOnFailureListener { exception ->
            con.onFailureFB(ORDER_UPLOAD_FAILURE, exception.message.toString())
            Log.w(TAG, exception)
        }
    }

    fun getOrders() {
        myOrdersRef.orderBy(FIELD_ORDER_DATE, Query.Direction.DESCENDING).get()
            .addOnSuccessListener {docs ->
                con.onSuccessFB(GET_ORDERS_OK, OrderList.toOrderList(docs))
                Log.d(TAG, "Get Orders Complete")
            }
            .addOnFailureListener { exception ->
                con.onFailureFB(GET_ORDERS_FAILURE, exception.message.toString())
                Log.w(TAG, exception)
            }
    }

    fun getProdsOrder(orderId: String) {
        myOrdersRef.document(orderId).collection(COL_PRODS).get()
            .addOnSuccessListener {docs ->
                con.onSuccessFB(GET_PRODS_OK, OrderList.toProdList(orderId, docs))
                Log.d(TAG, "Get Products Orders Complete")
            }
            .addOnFailureListener { exception ->
                con.onFailureFB(GET_PRODS_FAILURE, exception.message.toString())
                Log.w(TAG, exception)
            }
    }

    fun prodPurchased(prodId: String) {
        prodsPurchasedRef.document(prodId).get()
            .addOnSuccessListener {doc ->
                val res = doc != null && doc.exists()
                con.onSuccessFB(GET_PROD_PURCHASED_OK, res)
                Log.d(TAG, "Product $prodId Purchased: $res")
            }
            .addOnFailureListener { exception ->
                con.onFailureFB(GET_PROD_PURCHASED_FAILURE, exception.message.toString())
                Log.w(TAG, exception)
            }
    }
}