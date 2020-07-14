package com.nachohseara.beermastersshop.ui.cart

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.DBCart
import com.nachohseara.beermastersshop.model.db.IFBConnector
import com.nachohseara.beermastersshop.model.entity.*

class CartListController(mV: CartListFragment) : IFBConnector {
    private val myView = mV
    private lateinit var myAct : BaseActivity
    private lateinit var myCart: DBCart
    private lateinit var prods: Cart

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate() {
        if (myAct.notOnlineAndGo()) return

        (myAct as CartActivity).cancelShoppingSession()

        prods = Cart(hashMapOf(), 0)
        myCart = DBCart(this, User(myView.requireContext()).getCartId())
        myCart.getCart()
    }

    fun onContinue() {
        if (!myAct.onlineOrMsg()) return
        if (prods.total > 0 && prods.products.isNotEmpty()) {
            CartModel.saveSessionCart(myView.requireContext(), prods)

            (myAct as CartActivity).startShoppingSession()

            val args = Bundle()
            args.putString("goto", "address")
            myView.findNavController().navigate(R.id.action_cart_to_pwd, args)
        } else {
            myView.msgSnackBar("Cart is empty")
        }
    }

    fun getTotal() : String = Product.formattedPrice(prods.total)

    private fun loadInfo() {
        loadProducts(prods.products.values.toList())
        myView.loadTotal()
        myView.loadInfoComplete()
    }

    private fun loadProducts(list: List<CartProduct>) {
        myView.showProducts(list)
    }

    fun addToCart(id: String, p: Product) : Boolean {
        if (!myAct.onlineOrMsg()) return false
        val prod = prods.products[id]
        var num = 1
        if (prod != null) {
            num += prod.numUd
            prod.numUd = num
            myView.loadTotal()
        } else {
            prods.products[id] = CartProduct(p, 1)
        }
        prods.total += p.getPrice()
        myCart.addToCart(id, getItem(id, num))
        myView.loadTotal()
        return true
    }

    fun removeFromCart(id: String) : Boolean {
        if (!myAct.onlineOrMsg()) return false
        val prod = prods.products[id]
        if (prod != null) {
            val num = prod.numUd - 1
            prods.total -= prod.data.getPrice()
            val item = getItem(id, num)
            if (num == 0) {
                prods.products.remove(id)
                myCart.removeFromCart(id, item, true)
            } else {
                prod.numUd = num
                myCart.removeFromCart(id, item, false)
            }
            myView.loadTotal()
            return true
        }
        return false
    }

    private fun getItem(id: String, num: Int) : HashMap<String,Any> {
        val res: HashMap<String, Any> = hashMapOf()
        res[DBCart.ID_FIELD] = id
        res[DBCart.UD_FIELD] = num
        return res
    }

    fun goToPage(args: Bundle) {
        if (myAct.onlineOrMsg()) myView.findNavController().navigate(R.id.action_cart_to_prod_page, args)
    }

    override fun onSuccessFB(cod: Int, data: Any?) {
        when (cod) {
            DBCart.GET_CART_OK -> {
                if (data != null && data is Cart) {
                    prods = data
                }
                loadInfo()
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        if (cod == DBCart.GET_CART_FAILURE) {
            loadInfo()
        }
    }
}