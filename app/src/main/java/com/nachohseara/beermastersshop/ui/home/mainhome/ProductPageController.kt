package com.nachohseara.beermastersshop.ui.home.mainhome

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.DBCart
import com.nachohseara.beermastersshop.model.db.DBCloud
import com.nachohseara.beermastersshop.model.db.DBProducts
import com.nachohseara.beermastersshop.model.db.IFBConnector
import com.nachohseara.beermastersshop.model.entity.Product
import com.nachohseara.beermastersshop.model.entity.User
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.lang.Exception

class ProductPageController(mV: ProductPageFragment) : IFBConnector {
    private val myView = mV
    private lateinit var myAct: BaseActivity
    var product = Product.emptyProduct()
    private lateinit var myCart: DBCart
    lateinit var user: User
    private var numUd = 0

    private var loadUd = -1
    private var loadProd = -1
    private var loadImg = -1
    private var bm : Bitmap? = null

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate(_prodId: String) {
        user = User(myView.requireContext())

        if (isAdmin()) {
            myView.adminUI()
            loadUd = DBCart.GET_NUM_OK
        } else {
            myCart = DBCart(this, user.getCartId())
            myCart.getNumUd(_prodId)
        }
        DBProducts(this).getProduct(_prodId, isAdmin())
    }

    private fun downloadImg() {
        if (product.getImgUrl().isNotEmpty()) {
            Picasso.get().load(product.getImgUrl()).into(myView.getImgView(), object: Callback {
                override fun onSuccess() {
                    loadImg = DBCloud.GET_IMAG_OK
                    infoLoaded()
                }

                override fun onError(e: Exception?) {
                    myView.getImgView().setImageResource(Product.getNoImgProduct())
                    loadImg = DBCloud.GET_IMAG_FAILURE
                    infoLoaded()
                }

            })
        } else {
            loadImg = DBCloud.GET_IMAG_FAILURE
            infoLoaded()
        }
    }

    fun getNumUd() : Int = numUd

    fun getActive() : Boolean = product.getActive()

    fun getBitmap() : Bitmap? = bm

    fun isAdmin() : Boolean = user.isAdmin()

    fun onAddToCart() {
        if (!myAct.onlineOrMsg()) return
        numUd++
        val item = HashMap<String, Any>()
        item[DBCart.ID_FIELD] = product.getDocId()
        item[DBCart.UD_FIELD] = numUd
        myCart.addToCart(product.getDocId(), item)
        myView.loadUd()
    }

    fun onRemoveToCart() {
        if (!myAct.onlineOrMsg()) return
        if (numUd > 0) {
            numUd--
            val item = HashMap<String, Any>()
            item[DBCart.ID_FIELD] = product.getDocId()
            item[DBCart.UD_FIELD] = numUd
            myCart.removeFromCart(product.getDocId(), item, numUd == 0)
            myView.loadUd()
        }
    }

    fun onEditProduct() {
        if (!myAct.onlineOrMsg()
            || product.isEmpty()) return

        val args = Bundle()
        val bm = myView.getImg()
        var ba = ByteArray(0)
        if (bm != null) {
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
            ba = baos.toByteArray()
        }
        args.putByteArray("img", ba)
        args.putString("prod", Product.toJSON(product))
        myView.findNavController().navigate(R.id.action_prodpage_to_editproduct, args)
    }

    fun onDeleteProduct() {
        if (myAct.onlineOrMsg() && !product.isEmpty()) {
            myAct.startLoading()
            DBProducts(this).deleteProduct(product.getDocId())
        }
    }

    fun onReviews() {
        if (myAct.onlineOrMsg()) {
            val args = Bundle()
            args.putBoolean("userReview", false)
            args.putString("prod", Product.toJSON(product))
            myView.findNavController().navigate(R.id.action_productPageFragment_to_reviewListFragment, args)
        }
    }

    fun infoLoaded() {
        val endUd = loadUd != -1
        val endProd = loadProd != -1
        val endImg = loadImg != -1

        val end = endImg && endProd && endUd
        Log.d("ProdPage", "$endUd $endProd $endImg $end")
        if (end) {
            myView.loadInfoComplete()
        }
    }

    fun getError() : Boolean = loadUd == DBCart.GET_NUM_FAILURE || loadProd == DBProducts.GET_DATA_FAILURE

    override fun onSuccessFB(cod: Int, data: Any?) {
        when(cod) {
            DBCart.GET_NUM_OK -> {
                if (data is Int) {
                    numUd = data
                    loadUd = DBCart.GET_NUM_OK
                    myView.loadUd()
                } else {
                    loadUd = DBCart.GET_NUM_FAILURE
                }
                infoLoaded()
            }
            DBProducts.GET_DATA_OK -> {
                if (data != null && data is Product) {
                    product = data
                    loadProd = DBProducts.GET_DATA_OK
                    myView.loadInfo()
                } else {
                    loadProd = DBProducts.GET_DATA_FAILURE
                }
                downloadImg()
                infoLoaded()
            }
            //Admin
            DBProducts.DELETE_PRODUCT_OK -> {
                myAct.endLoading()
                myView.findNavController().navigate(R.id.action_productPageFragment_to_nav_all)
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        when (cod) {
            DBCart.GET_NUM_FAILURE -> {
                //myView.msgSnackBar(myView.getString(R.string.error_data))
                loadUd = DBCart.GET_NUM_FAILURE
                infoLoaded()
            }
            DBProducts.GET_DATA_FAILURE -> {
                loadProd = DBProducts.GET_DATA_FAILURE
                infoLoaded()
            }
            //Admin
            DBProducts.DELETE_PRODUCT_FAILURE -> {
                myAct.endLoading()
                myView.msgSnackBar(myView.getString(R.string.error_data))
            }
        }
    }
}