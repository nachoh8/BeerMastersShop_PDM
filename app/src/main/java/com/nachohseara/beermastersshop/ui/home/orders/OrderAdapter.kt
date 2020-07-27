package com.nachohseara.beermastersshop.ui.home.orders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.model.entity.OrderData
import com.nachohseara.beermastersshop.model.entity.OrderList
import com.nachohseara.beermastersshop.model.entity.Product
import com.nachohseara.beermastersshop.ui.cart.ProdListAdapter

class OrderAdapter(private val orders: List<OrderData>, private val ctxt: Context) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private var expanded: OrderViewHolder? = null
    private val expand: MutableList<Boolean> = mutableListOf()

    class OrderViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        lateinit var adapter: OrderAdapter

        val txtOrderId: TextView = v.findViewById(R.id.txtOrderId)
        val txtDate: TextView = v.findViewById(R.id.txtDate)
        val txtAddress: TextView = v.findViewById(R.id.txtAddress)
        val txtCausal: TextView = v.findViewById(R.id.txtCausal)
        val txtTotal: TextView = v.findViewById(R.id.txtTotal)
        val txtSuccess: TextView = v.findViewById(R.id.txtSuccess)
        val imgArrow: ImageView = v.findViewById(R.id.imgArrow)

        init {
            v.setOnClickListener {
                expand_collapse()//adapter.anyExpand(this)
            }
        }

        fun setData(order: OrderData) {
            txtAddress.text = order.address
            txtCausal.text = order.causal
            txtOrderId.text = order.id
            txtDate.text = OrderList.getDateFormatted(order.date)
            txtTotal.text = Product.formattedPrice(order.total)
            if (order.success) {
                txtSuccess.text = "(Success)"
                txtSuccess.setTextColor(v.resources.getColor(R.color.prod_enabled))
            } else {
                txtSuccess.text = "(Failure)"
                txtSuccess.setTextColor(v.resources.getColor(R.color.prod_disabled))
            }
            if (order.prods.isNotEmpty()) {
                val rView: RecyclerView = v.findViewById(R.id.recyclerProdList)
                rView.setHasFixedSize(true)

                val lManager = LinearLayoutManager(adapter.ctxt)
                rView.layoutManager = lManager

                val adapter = ProdListAdapter(order.prods, adapter.ctxt)
                rView.adapter = adapter
            }

            setExpand(adapter.expand[adapterPosition])
        }

        fun expand_collapse() {
            adapter.expand[adapterPosition] = !adapter.expand[adapterPosition]
            setExpand(adapter.expand[adapterPosition])
        }

        private fun setExpand(set: Boolean) {
            val orderExpandLayout: ConstraintLayout = v.findViewById(R.id.orderExpandLayout)
            if (set) {
                orderExpandLayout.visibility = View.VISIBLE
                imgArrow.setImageResource(R.drawable.ic_expand_less_black_24dp)
            } else {
                orderExpandLayout.visibility = View.GONE
                imgArrow.setImageResource(R.drawable.ic_expand_more_black_24dp)
            }
        }
    }

    init {
        for (i in orders.indices) {
            expand.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.order_card, parent, false)
        return OrderViewHolder(v)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.adapter = this
        holder.setData(order)
    }

    fun anyExpand(holder: OrderViewHolder) {
        if (expanded == holder) {
            expanded!!.expand_collapse()
            expanded = null
        } else {
            if (expanded != null) {
                expanded!!.expand_collapse()
            }
            holder.expand_collapse()
            expanded = holder
        }
    }
}