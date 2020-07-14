package com.nachohseara.beermastersshop.ui.home.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.LoaderFragment
import com.nachohseara.beermastersshop.model.entity.FilterProduct
import com.nachohseara.beermastersshop.model.entity.ListItem

class ListFragment : LoaderFragment() {
    private lateinit var mView: View
    private val controller = ListController(this)

    private lateinit var recyclerView: RecyclerView
    private var inL = 0
    private lateinit var toPage: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_list, container, false)


        var json = ""
        if (arguments != null) {
            toPage = requireArguments().getString("page", "")
            inL = requireArguments().getInt("inL")
            json = requireArguments().getString("cfilter", "")
        }

        val filter = FilterProduct.toFilter(json)

        loadViews()
        controller.onCreate(filter)

        return mView
    }

    fun loadViews() {
        recyclerView = mView.findViewById(R.id.recylerView)

        val btApply: Button = mView.findViewById(R.id.btApply)
        btApply.setOnClickListener {
            controller.onApply()
        }
    }

    fun showList(l: List<ListItem>) {
        if (l.isNotEmpty()) {
            recyclerView.setHasFixedSize(true)

            val lManager = LinearLayoutManager(requireContext())
            recyclerView.layoutManager = lManager

            val adapter = FilterListAdapter(l, requireContext())
            recyclerView.adapter = adapter
        }
    }

    fun getAdapter() = recyclerView.adapter as FilterListAdapter

    override fun actionPostComplete() {
        val progBar: ProgressBar = mView.findViewById(R.id.progBar)
        val containerLayout: ConstraintLayout = mView.findViewById(R.id.containerLayout)

        progBar.visibility = View.GONE
        if (controller.getContent().isNotEmpty()) {
            containerLayout.visibility = View.VISIBLE
        } else {
            val txt: TextView = mView.findViewById(R.id.txtError)
            txt.text = getString(R.string.error_data)
            txt.visibility = View.VISIBLE
        }
    }

    fun isOnCountries() : Boolean = inL == 0

    fun toPage() : String = toPage

    override fun getMyView(): View = mView
}
