package com.nachohseara.beermastersshop.ui.home.orders

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.LoaderFragment
import com.nachohseara.beermastersshop.model.entity.OrderData

class OrdersFragment : LoaderFragment() {
    private lateinit var mView: View
    private val controller = OrdersController(this)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mView = inflater.inflate(R.layout.fragment_orders, container, false)

        controller.onCreate()

        return mView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.orders, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_sort_recent -> {
                controller.sortOrders(true)
                true
            }
            R.id.action_sort_older -> {
                controller.sortOrders(false)
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    fun showOrders(orders: List<OrderData>) {
        if (orders.isNotEmpty()) {
            val rView: RecyclerView = mView.findViewById(R.id.recyclerView_orders)
            rView.setHasFixedSize(true)

            val lManager = LinearLayoutManager(requireContext())
            rView.layoutManager = lManager

            val adapter = OrderAdapter(orders, requireContext())
            rView.adapter = adapter
        }
    }

    override fun actionPostComplete() {
        val progBar: ProgressBar = mView.findViewById(R.id.progBar)
        progBar.visibility = View.GONE
        if (controller.isEmpty()) {
            val txt: TextView = mView.findViewById(R.id.txtNoOrders)
            txt.visibility = View.VISIBLE
        } else {
            val rView: RecyclerView = mView.findViewById(R.id.recyclerView_orders)
            rView.visibility = View.VISIBLE
        }
    }

    override fun getMyView(): View = mView
}