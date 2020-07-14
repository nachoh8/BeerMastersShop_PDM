package com.nachohseara.beermastersshop.ui.home.payments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseFragment
import com.nachohseara.beermastersshop.ui.home.HomeActivity

class SearchPaymentsFragment : BaseFragment() {
    private lateinit var mView: View
    private val controller = SearchPaymentsController(this)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_search_payments, container, false)
        (requireActivity() as HomeActivity).setMenu(false)

        loadViews()
        controller.onCreate()

        return mView
    }

    fun loadViews() {
        val btSelectDateFrom: Button = mView.findViewById(R.id.btSelectDateFrom)
        btSelectDateFrom.setOnClickListener {
            calendarDialog(true)
        }
        val btSelectDateTo: Button = mView.findViewById(R.id.btSelectDateTo)
        btSelectDateTo.setOnClickListener {
            calendarDialog(false)
        }
        val btSearch: Button = mView.findViewById(R.id.btSearch)
        btSearch.setOnClickListener {
            controller.onSearch()
        }
    }

    fun setDateFrom(str: String) {
        setDate(R.id.btSelectDateFrom, str)
    }

    fun setDateTo(str: String) {
        setDate(R.id.btSelectDateTo, str)
    }

    private fun setDate(id: Int, str: String) {
        val txtDate: Button = mView.findViewById(id)
        txtDate.text = str
    }

    private fun calendarDialog(from: Boolean) {
        val date = if (from) controller.getDateFrom() else controller.getDateTo()
        val year = date.year
        val month = date.month
        val day = date.day

        val datePickerDialog = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, yearPick, monthOfYear, dayOfMonth ->
            controller.onPickDate(from, dayOfMonth, monthOfYear, yearPick)
        }, year, month, day)

        datePickerDialog.show()
    }

    override fun getMyView(): View = mView
}
