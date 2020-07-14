package com.nachohseara.beermastersshop.ui.home.mainhome

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.LoaderFragment
import com.nachohseara.beermastersshop.ui.home.HomeActivity

class ProductPageFragment : LoaderFragment() {
    private lateinit var mView: View
    private val controller = ProductPageController(this)

    private lateinit var img: ImageView
    private lateinit var prodName: TextView
    private lateinit var prodBrand: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var numReviews: Button
    private lateinit var priceText: TextView
    private lateinit var numUdTxt: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mView = inflater.inflate(R.layout.fragment_product_page, container, false)

        val prodId = if (arguments != null) {
            requireArguments().getString("prodId", "")
        } else {
            ""
        }

        blockMenu(true)

        loadViews()
        controller.onCreate(prodId)

        return mView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuRes = if (controller.isAdmin()) R.menu.prod_page_admin else R.menu.empty
        inflater.inflate(menuRes, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_edit_product-> {
                controller.onEditProduct()
                true
            }
            R.id.action_delete_product -> {
                showChoiceDialog("Are you sure you want to delete this product?", "")
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    private fun blockMenu(block: Boolean) {
        if (context is HomeActivity) {
            (context as HomeActivity).setMenu(block)
        }
    }

    fun loadViews() {
        img = mView.findViewById(R.id.imgProdPage)
        prodName = mView.findViewById(R.id.txtNameProdPage)
        prodBrand = mView.findViewById(R.id.txtProdBrand)
        ratingBar = mView.findViewById(R.id.ratingBarProdPage)
        numReviews = mView.findViewById(R.id.numRevProdPage)
        numReviews.paintFlags = numReviews.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        priceText = mView.findViewById(R.id.txtPriceProdPage)
        numUdTxt = mView.findViewById(R.id.txtNumUd)

        val btAddToCart: Button = mView.findViewById(R.id.btAddToCart)
        btAddToCart.setOnClickListener {
            controller.onAddToCart()
        }
        val btRemoveToCart: Button = mView.findViewById(R.id.btRemoveToCart)
        btRemoveToCart.setOnClickListener {
            controller.onRemoveToCart()
        }
        numReviews.setOnClickListener {
            controller.onReviews()
        }
    }

    fun adminUI() {
        val btAddToCart: Button = mView.findViewById(R.id.btAddToCart)
        btAddToCart.visibility = View.GONE
        val btRemoveToCart: Button = mView.findViewById(R.id.btRemoveToCart)
        btRemoveToCart.visibility = View.GONE
        numUdTxt.visibility = View.GONE

        val txtProdState: TextView = mView.findViewById(R.id.txtProdState)
        txtProdState.visibility = View.VISIBLE
    }

    fun loadInfo() {
        prodName.text = controller.product.getName()
        prodBrand.text = controller.product.getDisplayBrandCountry()
        ratingBar.rating = controller.product.getRating()
        numReviews.text = controller.product.getNumReviews().toString()
        priceText.text = controller.product.getFormattedPrice()

        val txtProdState: TextView = mView.findViewById(R.id.txtProdState)
        if (controller.getActive()) {
            txtProdState.text = resources.getString(R.string.prod_enabled)
            txtProdState.setTextColor(resources.getColor(R.color.prod_enabled))
        } else {
            txtProdState.text = resources.getString(R.string.prod_disabled)
            txtProdState.setTextColor(resources.getColor(R.color.prod_disabled))
        }
    }

    fun loadUd() {
        numUdTxt.text = controller.getNumUd().toString()
    }

    fun getImgView() : ImageView = img

    fun getImg() : Bitmap? = if (img.drawable != null) img.drawable.toBitmap() else null

    fun showChoiceDialog(title: String, msg: String) {
        AlertDialog.Builder(requireContext()).setTitle(title).setMessage(msg)
            .setNegativeButton("No", null)
            .setPositiveButton("Yes") { _, _ -> controller.onDeleteProduct()
            }.show()
    }

    /// Loader Fragment
    override fun actionPostComplete() {
        val progBar: ProgressBar = mView.findViewById(R.id.progBar)
        val contentView: ScrollView = mView.findViewById(R.id.contentView)
        progBar.visibility = View.GONE
        if (!controller.getError() || !controller.product.isEmpty() && controller.isAdmin()) {
            contentView.visibility = View.VISIBLE
        } else {
            val txt: TextView = mView.findViewById(R.id.txtProdNotExists)
            txt.text = getString(R.string.no_product)
            txt.visibility = View.VISIBLE
        }
    }

    override fun getMyView(): View = mView
}