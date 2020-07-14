package com.nachohseara.beermastersshop.ui.home.mainhome

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.model.entity.Product
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ProductAdapter(l: List<Product>, c: Context, con: MainHomeController) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private val pList = l
    private val ctxt = c
    private val controller = con

    class ProductViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        lateinit var adapter: ProductAdapter

        val name: TextView = v.findViewById(R.id.txtNameProd_home)
        val price: TextView = v.findViewById(R.id.txtPriceProd_home)
        val img: ImageView = v.findViewById(R.id.imgProdCard)
        val ratingBar: RatingBar = v.findViewById(R.id.ratingProdCard)
        val numReviews: TextView = v.findViewById(R.id.txtNumRevCard)
        val txtBrand: TextView = v.findViewById(R.id.txtBrand)
        val txtCountry: TextView = v.findViewById(R.id.txtCountry)
        val txtProdState: TextView = v.findViewById(R.id.txtProdState)
        var bitmap: Bitmap? = null

        init {
            v.setOnClickListener {
                adapter.onClick(adapterPosition)
            }
        }

        fun setData(p: Product) {
            name.text = p.getName()
            price.text = p.getFormattedPrice()
            ratingBar.rating = p.getRating()
            numReviews.text = p.getNumReviews().toString()
            txtBrand.text = p.getBrand()
            txtCountry.text = p.getCountry()
            if (adapter.isAdmin()) {
                txtProdState.visibility = View.VISIBLE
                if (p.getActive()) {
                    txtProdState.text = v.resources.getString(R.string.prod_enabled)
                    txtProdState.setTextColor(v.resources.getColor(R.color.prod_enabled))
                } else {
                    txtProdState.text = v.resources.getString(R.string.prod_disabled)
                    txtProdState.setTextColor(v.resources.getColor(R.color.prod_disabled))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.home_product_card, parent, false)
        return ProductViewHolder(v)
    }

    override fun getItemCount(): Int {
        return pList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val prod = pList[position]
        holder.adapter = this
        holder.setData(prod)
        if (holder.bitmap != null) {
            holder.img.setImageBitmap(holder.bitmap)
        } else {
            if (prod.getImgUrl().isEmpty()) return
            Picasso.get().load(prod.getImgUrl()).into(holder.img, object: Callback {
                override fun onSuccess() {
                    holder.bitmap = holder.img.drawable.toBitmap()
                }

                override fun onError(e: Exception?) {
                    holder.img.setImageResource(Product.getNoImgProduct())
                }

            })
        }
    }

    fun onClick(pos: Int) {
        val prod = pList[pos]
        val args = Bundle()
        args.putString("prodId", prod.getDocId())//setBundle(prod, img)
        controller.goToPage(R.id.action_home_to_prod_page, args)
    }

    fun isAdmin() = controller.isAdmin()
}