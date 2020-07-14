package com.nachohseara.beermastersshop.model.entity

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.nachohseara.beermastersshop.model.db.DBCart
import com.nachohseara.beermastersshop.model.db.DBProducts
import com.nachohseara.beermastersshop.model.db.IFBConnector

class CartModel(ctxt: Context) : IFBConnector {
    companion object {
        const val file = "CartFile"
        const val cartField = "Cart"
        const val TAG = "CART_MODEL"

        fun toCart(docs: QuerySnapshot) : CartRAW {
            val c = HashMap<String, Int>()
            for (doc in docs) {
                if (doc != null && doc.exists()) {
                    val id = doc.getString("prodId")!!
                    val ud = doc.get("numUd", Int::class.java)!!
                    c[id] = ud
                }
            }

            return CartRAW(c)
        }

        fun toCartList(cw: CartRAW, docs: QuerySnapshot) : Cart {
            val products: HashMap<String, CartProduct> = hashMapOf()
            var total = 0
            for (doc in docs) {
                val cProd = toCartProduct(cw, doc)
                if (cProd != null) {
                    total += cProd.data.getPrice() * cProd.numUd
                    products[cProd.data.getDocId()] = cProd
                }
            }

            return Cart(products, total)
        }

        fun toCartProduct(cw: CartRAW, doc: DocumentSnapshot) : CartProduct? {
            if (doc.exists()) {
                val numUd = cw.products[doc.id]
                if (numUd != null) {
                    val prod = Product.toProduct(doc)
                    if (prod != null) {
                        return CartProduct(prod, numUd)
                    }
                }
            }
            return null
        }

        fun saveSessionCart(ctxt: Context, cart: Cart) {
            val gson = Gson()
            val sp = ctxt.getSharedPreferences(file, Context.MODE_PRIVATE)
            val editor = sp.edit()
            val json = gson.toJson(cart)
            editor.putString(cartField, json)
            editor.commit()
            Log.d(TAG, "Session Cart Saved: $json")
        }

        fun loadSessionCart(ctxt: Context) : Cart? {
            val gson = Gson()
            val sp = ctxt.getSharedPreferences(file, Context.MODE_PRIVATE)
            val json = sp.getString(cartField, "")
            Log.d(TAG, "Sessionc Cart Loadedd: $json")
            return if (json.isNullOrEmpty()) {
                null
            } else {
                gson.fromJson(json, Cart::class.java)
            }
        }

        fun clearSessionCart(ctxt: Context) {
            val sp = ctxt.getSharedPreferences(file, Context.MODE_PRIVATE)
            sp.edit().clear().commit()
            Log.d(TAG, "Session Cart Deleted")
        }

        fun showProductsOnConfirm(prods: List<CartProduct>) : String {
            var res = ""
            for (p in prods) {
                val pTxt = "x${p.numUd} ${p.data.getName()} ${Product.formattedPrice(p.data.getPrice())}"
                res += "$pTxt\n"
            }

            return res
        }
    }

    private val sp = ctxt.getSharedPreferences(file, Context.MODE_PRIVATE)
    private var total = 0

    private val cartId = User(ctxt).getCartId()
    private val fb = DBCart(this, cartId)
    private var cart: CartRAW = CartRAW(hashMapOf())
    private val gson = Gson()

    init {
        //if (fileExists()) loadLocalCart()
        fb.getCart()
    }

    fun inCart(id: String): Boolean = cart.products.containsKey(id)

    fun addProduct(id: String, p: Int) {
        var ud = 1
        if (inCart(id)) {
            ud = cart.products[id]!! + 1
            cart.products[id] = ud
        } else {
            cart.products[id] = ud
        }
        val item = HashMap<String, Any>()
        item["prodId"] = id
        item["numUd"] = ud
        total += p
        fb.addToCart(cartId, item)
        //saveLocalCart()
    }

    fun removeProduct(id: String, p:Int) {
        total -= p
        val ud = cart.products[id]!! - 1
        val item = HashMap<String, Any>()
        item["prodId"] = id
        item["numUd"] = ud
        if (ud == 0) {
            cart.products.remove(id)
            fb.removeFromCart(cartId, item, true)
        } else {
            cart.products[id] = ud
            fb.removeFromCart(cartId, item, false)
        }
        //saveLocalCart()
    }

    fun clearCart() {
        //fb.clearCart(cart.products.keys.toList())
        //clearLocalCart()
    }

    fun getNumUd(id: String) : Int = if (cart.products[id] != null) cart.products[id]!! else 0

    fun getTotal() : Int = total

    fun setTotal(prices: HashMap<String, Int>) {
        total = 0
        for (id in cart.products.keys){
            total += prices[id]!! * cart.products[id]!!
        }
    }

    fun getProducts() : List<String> = cart.products.keys.toList()

    override fun onSuccessFB(cod: Int, data: Any?) {
        if (cod == DBCart.GET_CART_OK) {
            if (data is CartRAW) {
                cart = data
                //saveLocalCart()
                Log.d(TAG, "$cart")
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        TODO("Not yet implemented")
    }

    // Local Cart
    fun fileExists(): Boolean = sp.contains(cartField)

    private fun loadLocalCart() {
        val json = sp.getString(cartField, "")
        cart = if (json.isNullOrEmpty()) {
            CartRAW(hashMapOf())
        } else {
            gson.fromJson(json, CartRAW::class.java)
        }
        Log.d(TAG, "Local Cart Loaded, NumProd: ${cart.products.size}")
    }

    private fun saveLocalCart() {
        val editor = sp.edit()
        val json = gson.toJson(cart)
        editor.putString(cartField, json)
        editor.commit()
        Log.d(TAG, "Local Cart Saved: $json")
    }

    private fun clearLocalCart() {
        cart.products.clear()
        total = 0
        sp.edit().clear().commit()
        Log.d(TAG, "Local Cart Deleted")
    }
}