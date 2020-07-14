package com.nachohseara.beermastersshop.ui.home.filter

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.model.entity.FilterProduct

class FilterController(private val myView: FilterFragment) {
    private var filter = FilterProduct.default()
    private var firstLoad = false

    fun onCreate(f: FilterProduct) {
        if (!firstLoad) {
            filter = f
        }

        myView.loadSortBy(filter.sortBy)
        if (filter.filterBy != FilterProduct.S_EMPTY) myView.loadInfoList(filter.filterBy == FilterProduct.S_COUNTRY, filter.listToStr())
        if (!filter.isDefault() && filter.fList.isNotEmpty()) {
            myView.lockBt(filter.filterBy != FilterProduct.S_COUNTRY)
        }

        firstLoad = true
    }

    fun changeSortBy(cod: Int) {
        filter.changeSortBy(cod)
    }

    fun reset() {
        filter.reset()
        myView.loadSortBy(filter.sortBy)
        myView.reset()
    }

    fun showListOf(countries: Boolean) {
        if (filter.filterBy == FilterProduct.S_EMPTY ||
            (countries && filter.filterBy == FilterProduct.S_COUNTRY) ||
            (!countries && filter.filterBy == FilterProduct.S_BRAND)) {

            val args = Bundle()
            val json = FilterProduct.toJSON(filter)
            val inL = if (countries) 0 else 1
            args.putString("cfilter", json)
            args.putInt("inL", inL)
            args.putString("page", myView.toPage())

            myView.findNavController().navigate(R.id.action_filterFragment_to_listFragment, args)
        }
    }

    fun onApply() {
        filterFinish()
    }

    private fun filterFinish() {
        val args = setBundle()
        when (myView.toPage()) {
            myView.getString(R.string.n_all) -> myView.findNavController().navigate(R.id.action_filterFragment_to_nav_all, args)
            else -> myView.findNavController().navigate(R.id.action_filterFragment_to_nav_top_sales, args)
        }
    }

    private fun setBundle() : Bundle {
        val args = Bundle()
        val json = FilterProduct.toJSON(filter)
        args.putString("filter", json)
        return args
    }
}