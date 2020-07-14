package com.nachohseara.beermastersshop.ui.home.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.model.entity.ListItem

class FilterListAdapter(val l: List<ListItem>, private val ctxt: Context) :
    RecyclerView.Adapter<FilterListAdapter.ProductViewHolder>() {

    class ProductViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        lateinit var adapter: FilterListAdapter

        val checkBox: CheckBox = v.findViewById(R.id.checkBoxList)

        init {
            checkBox.setOnClickListener { v->
                val newState = !adapter.l[adapterPosition].checked
                adapter.l[adapterPosition].checked = newState
            }
        }

        fun setData(item: ListItem) {
            checkBox.isChecked = item.checked
            val txt = "${item.item} (${item.num})"
            checkBox.text = txt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.filter_card_list, parent, false)
        return ProductViewHolder(
            v
        )
    }

    override fun getItemCount(): Int {
        return l.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = l[position]
        holder.setData(item)
        holder.adapter = this
    }
}