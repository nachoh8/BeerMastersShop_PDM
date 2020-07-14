package com.nachohseara.beermastersshop.ui.cart

import android.content.Context
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
import com.nachohseara.beermastersshop.model.entity.CartProduct

class CartListFragment : LoaderFragment() {
    private lateinit var mView: View
    private val controller = CartListController(this)
    private lateinit var txtTotalAmount: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_cart, container, false)

        requireActivity().title = "Cart"

        loadViews()
        controller.onCreate()

        return mView
    }

    fun loadViews() {
        txtTotalAmount = mView.findViewById(R.id.txtTotalAmount)

        val btContinue: Button = mView.findViewById(R.id.btContinue)
        btContinue.setOnClickListener {
            controller.onContinue()
        }
    }

    fun loadTotal() {
        txtTotalAmount.text = controller.getTotal()
    }

    override fun actionPostComplete() {
        val progBar: ProgressBar = mView.findViewById(R.id.progBar)
        val contentLayout: ConstraintLayout = mView.findViewById(R.id.contentLayout)
        progBar.visibility = View.GONE
        contentLayout.visibility = View.VISIBLE
    }

    fun showProducts(products: List<CartProduct>) {
        val txtCartEmpty: TextView = mView.findViewById(R.id.txtCartEmpty)
        if (products.isNotEmpty()) {
            txtCartEmpty.visibility = View.GONE

            val rView: RecyclerView = mView.findViewById(R.id.recyclerView)
            rView.setHasFixedSize(true)

            val lManager = LinearLayoutManager(requireContext())
            rView.layoutManager = lManager

            val adapter = CartListAdapter(products, requireContext(), controller)
            rView.adapter = adapter
        }
    }

    override fun getMyView(): View = mView
}