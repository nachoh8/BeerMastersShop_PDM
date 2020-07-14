package com.nachohseara.beermastersshop.ui.home.mainhome

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.DBProducts
import com.nachohseara.beermastersshop.model.db.IFBConnector
import com.nachohseara.beermastersshop.model.entity.FilterProduct
import com.nachohseara.beermastersshop.model.entity.Product
import com.nachohseara.beermastersshop.model.entity.ProductList
import com.nachohseara.beermastersshop.model.entity.User

class MainHomeController(mV: MainHomeFragment) : IFBConnector {
    private val myView = mV
    private lateinit var myAct: BaseActivity
    private val db = DBProducts(this)
    private var prodL: List<Product> = listOf()
    private var filter = FilterProduct.default()
    private var getProds = DBProducts.GETPRODS_TOPSALES

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate(_getProds: Int = DBProducts.GETPRODS_TOPSALES, json: String) {
        if (myAct.notOnlineAndGo()) return
        getProds = _getProds
        filter = FilterProduct.toFilter(json)

        myView.setFloattingButton(isAdmin())

        getProducts()
    }

    fun isAdmin() : Boolean = User(myView.requireContext()).isAdmin()

    // Local sorted for topsales/newest
    /*private fun sortBy() {
        var l = listOf<Product>()
        when (filter.sortBy) {
            FilterProduct.SORTBY_DEFAULT -> l = prodL
            FilterProduct.SORTBY_NAME -> l = prodL.sortedBy { it.getName()}
            FilterProduct.SORTBY_PRICE_ASC -> l = prodL.sortedBy { it.getPrice()}
            FilterProduct.SORTBY_PRICE_DESC -> l = prodL.sortedByDescending { it.getPrice()}
            FilterProduct.SORTBY_RATING -> l = prodL.sortedByDescending { it.getRating() }
        }
        loadProducts(l)
    }*/
    
    fun getProducts() {
        when(getProds) {
            DBProducts.GETPRODS_TOPSALES -> db.getTopSales(filter)
            DBProducts.GETPRODS_ALL -> {
                if (isAdmin()) {
                    db.getAllProducts(filter)
                } else {
                    db.getActiveProducts(filter)
                }
            }
        }
    }

    fun loadProducts(l: List<Product>) {
        myView.showProducts(l)
    }

    override fun onSuccessFB(cod: Int, data: Any?) {
        when(cod) {
            DBProducts.GET_DATA_OK ->{
                if (data is ProductList) {
                    prodL = data.products.toList()
                    loadProducts(ProductList.sortBy(prodL, filter))
                    /*if (getProds == DBProducts.GETPRODS_TOPSALES) {
                        sortBy()
                    } else {
                        loadProducts(prodL)
                    }*/
                }
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        if (cod == DBProducts.GET_DATA_FAILURE) {
            myView.msgSnackBar(myView.getString(R.string.error_data))
            loadProducts(listOf())
        }
    }

    fun goToPage(action: Int, args: Bundle) {
        if(online()) myView.findNavController().navigate(action, args)
    }

    fun goToCart() {
        if (online()) {
            myView.findNavController().navigate(R.id.action_shop_to_cart)
        }
    }

    fun onFilter() {
        val args = Bundle()
        args.putString("page", myView.getLabel())
        val json = FilterProduct.toJSON(filter)
        args.putString("cfilter", json)
        when (myView.getLabel()) {
            myView.getString(R.string.n_all) -> myView.findNavController().navigate(R.id.action_nav_all_to_filterFragment, args)
            else -> myView.findNavController().navigate(R.id.action_nav_top_sales_to_filterFragment, args)
        }
    }

    fun online() : Boolean {
        if (myAct.isOnline()) {
            return true
        }
        myView.msgSnackBar(myView.getString(R.string.no_connection))
        return false
    }

    fun onAddProduct() {
        if (online()) {
            myView.findNavController().navigate(R.id.action_navall_to_addProduct)
        }
    }
}