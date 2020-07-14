package com.nachohseara.beermastersshop.model.db

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.nachohseara.beermastersshop.model.entity.CartModel
import com.nachohseara.beermastersshop.model.entity.CartRAW

class DBCart(c: IFBConnector, id: String) {
    companion object {
        const val TAG = "DB_CART"
        const val COLLECTION = "Cart"
        const val SUBCOLLECTION = "Products"
        const val UD_FIELD = "numUd"
        const val ID_FIELD = "prodId"

        const val GET_CART_OK = 0
        const val GET_CART_FAILURE = 1
        const val CLEAR_CART_OK = 2
        const val CLEAR_CART_FAILURE = 3
        const val OP_ADD_OK = 4
        const val OP_ADD_FAILURE = 5
        const val OP_REMOVE_OK = 6
        const val OP_REMOVE_FAILURE = 7
        const val GET_NUM_OK = 8
        const val GET_NUM_FAILURE = 9
    }

    private val db = FirebaseFirestore.getInstance()
    private val con = c
    private val cartId = id

    // Return products<id, numUd>
    fun getCart() {
        db.collection(COLLECTION).document(cartId).collection(SUBCOLLECTION).get()
            .addOnSuccessListener {docs->
                getProducts(CartModel.toCart(docs))
                //con.onSuccessFB(GET_CART_OK, CartModel.toCart(docs))
                Log.d(TAG, "Get Cart completed")
            }.addOnFailureListener { exception ->
                con.onFailureFB(GET_CART_FAILURE, exception.message!!)
                Log.w(TAG, exception)
            }
    }

    private fun getProducts(prods: CartRAW) {
        if (prods.products.isNullOrEmpty()) {
            con.onSuccessFB(GET_CART_OK, null)
        } else {
            db.collection(DBProducts.PRODUCTS_COLLECTION).whereEqualTo("active", true)
                .whereIn("id", prods.products.keys.toList()).get()
                .addOnSuccessListener { docs ->
                    val res = CartModel.toCartList(prods, docs)
                    con.onSuccessFB(GET_CART_OK, res)
                    Log.d(TAG, "Get Cart-Products completed: $res")
                }.addOnFailureListener { exception ->
                    con.onFailureFB(GET_CART_FAILURE, exception.message!!)
                    Log.e(TAG, exception.message!!)
                }
        }
    }

    // clear cart neccesary pass ids prods
    fun clearCart() {
        db.collection(COLLECTION).document(cartId).collection(SUBCOLLECTION).get()
            .addOnSuccessListener {docs->
                deleteProds(docs)
                Log.d(TAG, "Get docs ok")
            }.addOnFailureListener { exception ->
                con.onFailureFB(CLEAR_CART_FAILURE, exception.message!!)
                Log.w(TAG, exception)
            }
    }

    private fun deleteProds(docs: QuerySnapshot) {
        db.runTransaction { transaction ->
            for (doc in docs) {
                transaction.delete(doc.reference)
            }
        }.addOnSuccessListener {
            Log.d(TAG, "Cart Deleted")
        }.addOnFailureListener { exception ->
            con.onFailureFB(CLEAR_CART_FAILURE, exception.message!!)
            Log.w(TAG, exception)
        }
    }

    fun addToCart(prodId: String, prod: HashMap<String, Any>) {
        db.collection(COLLECTION).document(cartId).collection(SUBCOLLECTION).document(prodId).set(prod)
            .addOnSuccessListener {
                con.onSuccessFB(OP_ADD_OK, null)
                Log.d(TAG, "Add to Cart completed: $prod")
            }.addOnFailureListener { exception ->
                con.onFailureFB(OP_ADD_FAILURE, exception.message!!)
                Log.w(TAG, exception)
            }
    }

    fun removeFromCart(prodId: String, prod: HashMap<String, Any>, remove: Boolean) {
        val ref = db.collection(COLLECTION).document(cartId).
        collection(SUBCOLLECTION).document(prodId)
        db.runTransaction {transaction ->
            if (remove) {
                transaction.delete(ref)
            } else {
                transaction.update(ref, UD_FIELD, prod[UD_FIELD])
            }
        }.addOnSuccessListener {
            con.onSuccessFB(OP_REMOVE_OK, null)
            Log.d(TAG, "Remove from Cart completed: $prod")
        }.addOnFailureListener { exception ->
            con.onFailureFB(OP_REMOVE_FAILURE, exception.message!!)
            Log.w(TAG, exception)
        }
    }

    fun getNumUd(prodId: String) {
        db.collection(COLLECTION).document(cartId).collection(SUBCOLLECTION).document(prodId).get()
            .addOnSuccessListener {doc->
                var num = 0
                if (doc != null && doc.exists() && doc.contains(UD_FIELD)) {
                    num = doc.get(UD_FIELD, Int::class.java)!!
                    con.onSuccessFB(GET_NUM_OK, num)
                } else {
                    con.onSuccessFB(GET_NUM_OK, num)
                }
                Log.d(TAG, "$prodId numUd: $num")
            }.addOnFailureListener { exception ->
                con.onFailureFB(GET_NUM_FAILURE, exception.message.toString())
                Log.w(TAG, exception)
            }
    }
}