package com.nachohseara.beermastersshop.ui.home.filter

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.model.db.DBProducts
import com.nachohseara.beermastersshop.model.db.IFBConnector
import com.nachohseara.beermastersshop.model.entity.BrandsCountries
import com.nachohseara.beermastersshop.model.entity.FilterProduct
import com.nachohseara.beermastersshop.model.entity.ListItem
import com.nachohseara.beermastersshop.model.entity.User

class ListController(private val myView: ListFragment) : IFBConnector {
    private lateinit var filter : FilterProduct
    private val db = DBProducts(this)
    private lateinit var bc : BrandsCountries

    fun onCreate(f: FilterProduct) {
        db.getBrandsCountries(User(myView.requireContext()).isAdmin())
        filter = f
    }

    fun onApply() {
        val checkList = myView.getAdapter().l
        if (myView.isOnCountries()) {
            filter.setCountries(checkList)
        } else {
            filter.setBrands(checkList)
        }

        val args = Bundle()
        val json = FilterProduct.toJSON(filter)
        args.putString("cfilter", json)
        args.putString("page", myView.toPage())
        myView.findNavController().navigate(R.id.action_listFragment_to_filterFragment, args)
    }

    fun getContent() : List<ListItem> {
        return if (myView.isOnCountries()) {
            filter.toListOfCountries(bc)
        } else {
            filter.toListOfBrands(bc)
        }
    }

    private fun showList() {
        myView.showList(getContent())

        myView.loadInfoComplete()
    }

    override fun onSuccessFB(cod: Int, data: Any?) {
        if (cod == DBProducts.GET_DATA_OK) {
            if (data != null && data is BrandsCountries) {
                bc = data
                showList()
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        if (cod == DBProducts.GET_DATA_FAILURE) {
            myView.msgSnackBar(myView.getString(R.string.error_data))
            bc = BrandsCountries(hashMapOf(), hashMapOf())
            showList()
        }
    }
}