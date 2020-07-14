package com.nachohseara.beermastersshop.ui.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.model.entity.OrderProduct
import com.nachohseara.beermastersshop.model.entity.Product

class ProdListAdapter(private val pList: List<OrderProduct>, private val ctxt: Context) :
    RecyclerView.Adapter<ProdListAdapter.ProductViewHolder>() {

    class ProductViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val txtUd: TextView = v.findViewById(R.id.txtNumUd)
        val txtName: TextView = v.findViewById(R.id.txtProdName)
        val txtPrice: TextView = v.findViewById(R.id.txtProdPrice)

        fun setData(p: OrderProduct) {
            txtName.text = p.name
            txtPrice.text = Product.formattedPrice(p.price)
            val num = "x${p.numUd}"
            txtUd.text = num
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.card_prods_list, parent, false)
        return ProductViewHolder(v)
    }

    override fun getItemCount(): Int {
        return pList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val prod = pList[position]
        holder.setData(prod)
    }
}