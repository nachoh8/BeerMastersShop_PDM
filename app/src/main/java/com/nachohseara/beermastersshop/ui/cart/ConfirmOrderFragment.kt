package com.nachohseara.beermastersshop.ui.cart

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseFragment
import com.nachohseara.beermastersshop.model.entity.CartProduct
import com.nachohseara.beermastersshop.model.entity.OrderProduct

class ConfirmOrderFragment : BaseFragment() {
    private lateinit var mView: View
    private val controller = ConfirmOrderController(this)
    lateinit var address: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_confirm_order, container, false)
        address = if (arguments != null) {
            requireArguments().getString("address", "")
        } else {
            ""
        }

        requireActivity().title = "Confirm Order"

        loadViews()
        controller.onCreate()

        return mView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.confirmorder, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_cancel -> {
                controller.onCancel()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun loadViews() {
        val txtAddress: TextView = mView.findViewById(R.id.txtAddress)
        txtAddress.text = address

        val btPay: Button = mView.findViewById(R.id.btPay)
        btPay.setOnClickListener {
            controller.onPay()
        }
    }

    fun loadInfo(list: List<OrderProduct>) {
        val txtTotal: TextView = mView.findViewById(R.id.txtTotalAmount)
        txtTotal.text = controller.getTotal()

        if (list.isNotEmpty()) {
            val rView: RecyclerView = mView.findViewById(R.id.recyclerProdList)
            rView.setHasFixedSize(true)

            val lManager = LinearLayoutManager(requireContext())
            rView.layoutManager = lManager

            val adapter = ProdListAdapter(list, requireContext())
            rView.adapter = adapter
        }
    }

    fun showChoiceDialog(title: String, msg: String) {
        AlertDialog.Builder(requireContext()).setTitle(title).setMessage(msg)
            .setNegativeButton("No", null)
            .setPositiveButton("Yes") { _, _ -> controller.onPositiveButton()
            }.show()
    }

    override fun getMyView(): View = mView
}
