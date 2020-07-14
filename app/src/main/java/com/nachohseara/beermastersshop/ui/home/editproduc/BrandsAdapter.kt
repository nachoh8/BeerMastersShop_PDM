package com.nachohseara.beermastersshop.ui.home.editproduc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.model.entity.Brand

class BrandsAdapter(val l: List<Brand>, private val ctxt: Context, private val controller: BrandsController) :
    RecyclerView.Adapter<BrandsAdapter.BrandViewHolder>() {

    class BrandViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        lateinit var adapter: BrandsAdapter

        val checkBox: CheckBox = v.findViewById(R.id.checkBoxList)

        init {
            checkBox.setOnClickListener { v->
                adapter.onSelectBrand(adapterPosition)
            }
        }

        fun setData(brand: Brand) {
            val text = brand.name + ", " + brand.country
            checkBox.text = text
            if (adapter.prodBrandChecked() == brand.name) {
                checkBox.isChecked = true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val inflater = ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.filter_card_list, parent, false)
        return BrandViewHolder(v)
    }

    override fun getItemCount(): Int {
        return l.size
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        val item = l[position]
        holder.adapter = this
        holder.setData(item)
    }

    fun prodBrandChecked() : String = controller.prodBrandChecked()

    fun onSelectBrand(pos: Int) {
        val brand = l[pos]
        controller.selectBrand(brand)
    }
}