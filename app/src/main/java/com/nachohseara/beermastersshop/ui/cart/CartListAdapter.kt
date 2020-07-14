package com.nachohseara.beermastersshop.ui.cart

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.model.entity.CartProduct
import com.nachohseara.beermastersshop.model.entity.Product
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class CartListAdapter(l: List<CartProduct>, c: Context, con: CartListController) :
    RecyclerView.Adapter<CartListAdapter.ProductViewHolder>() {
    private val pList = l
    private val ctxt = c
    private val control = con

    class ProductViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        lateinit var adapter: CartListAdapter
        private var num = 0

        val name: TextView = v.findViewById(R.id.txtNameProd_home)
        val price: TextView = v.findViewById(R.id.txtPriceProd_home)
        val img: ImageView = v.findViewById(R.id.imgProdCard)
        val btAdd: Button = v.findViewById(R.id.btAddToCart)
        val btRemove: Button = v.findViewById(R.id.btRemoveToCart)
        val txtNumElem: TextView = v.findViewById(R.id.txtNumElem)

        init {
            btAdd.setOnClickListener {
                if (adapter.addToCart(adapterPosition)) {
                    txtNumElem.text = (++num).toString()
                }
            }
            btRemove.setOnClickListener {
                if (num > 0) {
                    if (adapter.removeFromCart(adapterPosition)) {
                        txtNumElem.text = (--num).toString()
                    }
                }
            }
            v.setOnClickListener {
                adapter.onClick(adapterPosition, img.drawable)
            }
        }

        fun setData(p: Product, ud: Int) {
            name.text = p.getName()
            price.text = p.getFormattedPrice()
            num = ud
            txtNumElem.text = num.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.cart_product_card, parent, false)
        return ProductViewHolder(v)
    }

    override fun getItemCount(): Int {
        return pList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val prod = pList[position]
        holder.setData(prod.data, prod.numUd)
        holder.adapter = this
        Picasso.get().load(prod.data.getImgUrl()).into(holder.img)
    }

    fun addToCart(position: Int) : Boolean {
        val prod = pList[position].data
        return control.addToCart(prod.getDocId(), prod)
    }

    fun removeFromCart(position: Int) : Boolean {
        val prod = pList[position]
        return control.removeFromCart(prod.data.getDocId())
    }

    fun onClick(pos: Int, img: Drawable) {
        val prod = pList[pos]
        val args = Bundle() //setBundle(prod.data, img)
        args.putString("prodId", prod.data.getDocId())
        control.goToPage(args)
    }
}