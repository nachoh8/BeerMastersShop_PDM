package com.nachohseara.beermastersshop.ui.home.editproduc

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.DBProducts
import com.nachohseara.beermastersshop.model.db.IFBConnector
import com.nachohseara.beermastersshop.model.entity.Brand
import com.nachohseara.beermastersshop.model.entity.BrandsCountries
import com.nachohseara.beermastersshop.model.entity.Product

class BrandsController(private val myView: BrandsFragment) : IFBConnector {
    private lateinit var myAct: BaseActivity
    private var prod = Product.emptyProduct()
    private lateinit var bc : BrandsCountries
    private val db = DBProducts(this)

    private var btPressed = false

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate(json: String) {
        db.getBrandsCountries(true)
        if (json.isNotEmpty()) {
            prod = Product.toProduct(json)
        }
    }

    private fun loadBrands() {
        myView.showList(BrandsCountries.getBrandsSorted(bc))
    }

    fun isEmpty() : Boolean = bc.brands.isEmpty()

    fun prodBrandChecked() : String = prod.getBrand()

    fun onAddBrand() {
        if(myAct.onlineOrMsg() && !btPressed) myView.showAlertDialogAddBrand()
    }

    fun addBrand(name: String, country: String) {
        if (myAct.onlineOrMsg()) {
            btPressed = true
            myAct.startLoading()

            if (bc.brands.containsKey(name)) {
                myView.msgSnackBar(myView.getString(R.string.brand_alredy_exists))
                endLoad()
            } else {
                db.addBrand(Brand("", name, country, 0))
            }
        }
    }

    fun selectBrand(brand: Brand) {
        prod.setBrandCountry(brand)
        val args = Bundle()
        val ba = myView.getByteArray()
        if (ba != null) args.putByteArray("img", ba)
        args.putString("prod", Product.toJSON(prod))
        myView.findNavController().navigate(R.id.action_brandsFragment_to_editProductFragment, args)
    }

    private fun endLoad() {
        myAct.endLoading()
        btPressed = false
    }

    override fun onSuccessFB(cod: Int, data: Any?) {
        when (cod) {
            DBProducts.GET_DATA_OK -> {
                bc = if (data != null && data is BrandsCountries) {
                    data
                } else {
                    BrandsCountries(hashMapOf(), hashMapOf())
                }
                loadBrands()
                myView.loadInfoComplete()
            }
            DBProducts.UP_BRAND_OK -> {
                if (data != null && data is Brand) {
                    bc.brands[data.name] = data
                    loadBrands()
                }
                endLoad()
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        when (cod) {
            DBProducts.GET_DATA_FAILURE -> {
                myView.msgSnackBar(myView.getString(R.string.error_data))
                myView.requireActivity().onBackPressed()
            }
            DBProducts.UP_BRAND_ERROR -> {
                myView.msgSnackBar(myView.getString(R.string.error_data))
                endLoad()
            }
        }
    }
}