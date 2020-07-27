package com.nachohseara.beermastersshop.ui.home.payments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.model.entity.PaymentOrder
import com.nachohseara.beermastersshop.model.entity.Product

class PaymentAdapter(private val orders: List<PaymentOrder>, private val ctxt: Context, private val controller: PaymentsHistoryController) :
    RecyclerView.Adapter<PaymentAdapter.PaymentOrderViewHolder>() {
    private var expanded: PaymentOrderViewHolder? = null
    private val expand: MutableList<Boolean> = mutableListOf()

    class PaymentOrderViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        lateinit var adapter: PaymentAdapter

        val txtTransCod: TextView = v.findViewById(R.id.txtTransCod)
        val txtDate: TextView = v.findViewById(R.id.txtDate)
        val txtState: TextView = v.findViewById(R.id.txtState)
        val txtTotal: TextView = v.findViewById(R.id.txtTotal)
        val imgArrow: ImageView = v.findViewById(R.id.imgArrow)
        val txtBrandCard: TextView = v.findViewById(R.id.txtBrandCard)
        val txtPan: TextView = v.findViewById(R.id.txtPan)

        init {
            v.setOnClickListener {
                expand_collapse()
                //adapter.anyExpand(this)
            }
            v.setOnLongClickListener {
                adapter.onLongClick(adapterPosition)
                true
            }
        }

        fun setData(order: PaymentOrder) {
            txtTransCod.text = order.codiceTransazione
            txtDate.text = order.dataTransazione
            txtState.text = order.stato
            txtTotal.text = Product.formattedPrice(order.importo)
            val txt = "${order.brand} - ${order.nazione}"
            txtBrandCard.text = txt
            txtPan.text = order.pan

            setExpand(adapter.expand[adapterPosition])
        }

        fun expand_collapse() {
            adapter.expand[adapterPosition] = !adapter.expand[adapterPosition]
            setExpand(adapter.expand[adapterPosition])
        }

        private fun setExpand(set: Boolean) {
            val expandLayout: ConstraintLayout = v.findViewById(R.id.expandLayout)
            if (set) {
                expandLayout.visibility = View.VISIBLE
                imgArrow.setImageResource(R.drawable.ic_expand_less_black_24dp)
            } else {
                expandLayout.visibility = View.GONE
                imgArrow.setImageResource(R.drawable.ic_expand_more_black_24dp)
            }
        }
    }

    init {
        for (i in orders.indices) {
            expand.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentOrderViewHolder {
        val inflater = ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.payment_card, parent, false)
        return PaymentOrderViewHolder(v)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: PaymentOrderViewHolder, position: Int) {
        val order = orders[position]
        holder.adapter = this
        holder.setData(order)
    }

    fun anyExpand(holder: PaymentOrderViewHolder) {
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

    fun onLongClick(pos: Int) {
        val order = orders[pos]
        controller.onLongClick(order)
    }
}