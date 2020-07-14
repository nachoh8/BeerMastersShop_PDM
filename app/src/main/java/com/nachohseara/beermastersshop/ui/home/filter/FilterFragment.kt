package com.nachohseara.beermastersshop.ui.home.filter

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseFragment
import com.nachohseara.beermastersshop.model.entity.FilterProduct
import com.nachohseara.beermastersshop.ui.home.HomeActivity

class FilterFragment : BaseFragment(), AdapterView.OnItemSelectedListener    {
    private lateinit var mView: View
    private val controller = FilterController(this)
    private lateinit var toPage: String

    private lateinit var spinnerSortBy: Spinner
    private lateinit var colorStateList: ColorStateList
    private lateinit var txtCountries: TextView
    private lateinit var txtBrands: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mView = inflater.inflate(R.layout.fragment_filter, container, false)

        blockMenu(true)

        var json =  ""
        toPage = ""
        if (arguments != null) {
            toPage = requireArguments().getString("page", "")
            json = requireArguments().getString("cfilter", "")
        }
        val filter = FilterProduct.toFilter(json)

        loadViews()
        controller.onCreate(filter)

        return mView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset_filter -> {
                controller.reset()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun blockMenu(block: Boolean) {
        if (context is HomeActivity) {
            (context as HomeActivity).setMenu(block)
        }
    }

    fun loadViews() {
        txtCountries = mView.findViewById(R.id.txtCountries)
        txtBrands = mView.findViewById(R.id.txtBrands)
        clearInfoList()

        spinnerSortBy = mView.findViewById(R.id.spinnerSortBy)
        spinnerSortBy.onItemSelectedListener = this

        val btApply: Button = mView.findViewById(R.id.btApplyFilter)
        btApply.setOnClickListener {
            controller.onApply()
        }
        val btCountries: Button = mView.findViewById(R.id.btCountriesList)
        colorStateList = btCountries.backgroundTintList!!
        btCountries.setOnClickListener {
            controller.showListOf(true)
        }
        val btBrands: Button = mView.findViewById(R.id.btBrandsList)
        btBrands.setOnClickListener {
            controller.showListOf(false)
        }
    }

    fun loadSortBy(sortby: Int) {
        spinnerSortBy.setSelection(sortby)
    }

    fun loadInfoList(inCountries: Boolean, string: String) {
        val txt = if (inCountries) txtCountries else txtBrands
        txt.text = string
    }

    fun clearInfoList() {
        txtCountries.text = ""
        txtBrands.text = ""
    }

    fun reset() {
        clearInfoList()
        unlockBt()
    }

    fun lockBt(inCountries: Boolean) {
        val bt: Button = if (inCountries) {
            mView.findViewById(R.id.btCountriesList)
        } else {
            mView.findViewById(R.id.btBrandsList)
        }
        bt.backgroundTintList = ContextCompat.getColorStateList(requireActivity(), R.color.bt_lock)
    }

    fun unlockBt() {
        val btCountries: Button = mView.findViewById(R.id.btCountriesList)
        val btBrands: Button = mView.findViewById(R.id.btBrandsList)
        btCountries.backgroundTintList = colorStateList
        btBrands.backgroundTintList = colorStateList
    }

    fun toPage() : String = toPage

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        controller.changeSortBy(position)
    }

    override fun getMyView(): View = mView
}
