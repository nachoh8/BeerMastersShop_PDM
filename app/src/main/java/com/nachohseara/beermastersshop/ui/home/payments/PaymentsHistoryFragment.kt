package com.nachohseara.beermastersshop.ui.home.payments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.LoaderFragment
import com.nachohseara.beermastersshop.model.entity.PaymentOrder
import com.nachohseara.beermastersshop.model.entity.Product
import com.nachohseara.beermastersshop.ui.home.HomeActivity


class PaymentsHistoryFragment : LoaderFragment() {
    private lateinit var mView: View
    private val controller = PaymentsHistoryController(this)

    private lateinit var rView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_payments_history, container, false)

        (requireActivity() as HomeActivity).setMenu(true)

        var dateFrom = ""
        var dateTo = ""
        if (arguments != null) {
            dateFrom = requireArguments().getString("from", "")
            dateTo = requireArguments().getString("to", "")
        }

        loadViews()
        controller.onCreate(dateFrom, dateTo)

        return mView
    }

    private fun loadViews() {
        rView = mView.findViewById(R.id.recyclerView)

        swipeRefresh = mView.findViewById(R.id.refreshProducts)
        swipeRefresh.setOnRefreshListener {
            setNoPayments(false)
            rView.visibility = View.INVISIBLE
            controller.getPayments(true)
        }
    }

    fun loadPayments(list: List<PaymentOrder>) {
        rView.setHasFixedSize(true)

        val lManager = LinearLayoutManager(requireContext())
        rView.layoutManager = lManager

        val adapter = PaymentAdapter(list, requireContext(), controller)
        rView.adapter = adapter
    }

    fun offSwipeRefresh() {
        swipeRefresh.isRefreshing = false
    }

    private fun setNoPayments(set: Boolean) {
        val txtNoPayments: TextView = mView.findViewById(R.id.txtNoPayments)
        txtNoPayments.visibility = if (set) View.VISIBLE else View.GONE
    }

    fun alertDialogStornoRimborso(order: PaymentOrder) {
        val builder = AlertDialog.Builder(requireContext())
        val tittle = if (order.stato == "Contabilizzato") getString(R.string.order_tittle_refund) else
            getString(R.string.order_tittle_cancel)
        builder.setTitle(tittle)

        val viewInflated = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_cancel_refund, view as ViewGroup, false)
        val txtCodTrans:TextView = viewInflated.findViewById(R.id.txtCodTransDialog)
        val txtAmount: TextView = viewInflated.findViewById(R.id.txtTotalRefund)
        txtCodTrans.text = order.codiceTransazione
        txtAmount.text = Product.formattedPrice(order.importo)

        builder.setView(viewInflated)

        builder.setPositiveButton("Confirm") { dialog, which ->
            controller.onConfirmRefund(order)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    /// Loader Fragment
    override fun actionPostComplete() {
        val progBar: ProgressBar = mView.findViewById(R.id.progBar)
        progBar.visibility = View.GONE
        rView.visibility = View.VISIBLE

        if (controller.isEmpty()) {
            setNoPayments(true)
        }
    }

    override fun getMyView(): View = mView
}
