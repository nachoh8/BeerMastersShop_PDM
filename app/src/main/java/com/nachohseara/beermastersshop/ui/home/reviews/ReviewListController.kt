package com.nachohseara.beermastersshop.ui.home.reviews

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.DBOrders
import com.nachohseara.beermastersshop.model.db.DBReviews
import com.nachohseara.beermastersshop.model.db.IFBConnector
import com.nachohseara.beermastersshop.model.entity.*

class ReviewListController(private val myView: ReviewListFragment) : IFBConnector {
    private lateinit var myAct: BaseActivity
    private val db = DBReviews(this)

    private var prod = Product.emptyProduct()
    private var isUserReview = true
    private var isEmpty = true
    private var btPressed = false
    var myReview: Review? = null

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate(_isUserReview: Boolean, _prod: Product) {
        isUserReview = _isUserReview
        if (isUserReview) {
            val id = User(myView.requireContext()).getDocId()
            db.getUserReviews(id)
        } else {
            if (_prod.isEmpty()) {
                myView.msgSnackBar(myView.getString(R.string.error_data))
                myView.requireActivity().onBackPressed()
            } else {
                prod = _prod
                db.getProdReviews(prod.getDocId())
            }
        }
    }

    fun onWriteReview() {
        if (myAct.onlineOrMsg() && !btPressed) {
            btPressed = true
            DBOrders(this, User(myView.requireContext()).getOrdersId()).prodPurchased(prod.getDocId())
        }
    }

    fun getOnProfile() : Boolean = isUserReview

    fun isEmpty() : Boolean = isEmpty

    private fun loadReviews(l: List<Review>) {
        isEmpty = l.isEmpty()
        myView.showReviews(l)
        myView.loadInfoComplete()
    }

    override fun onSuccessFB(cod: Int, data: Any?) {
        if (cod == DBReviews.GET_REVIEWS_OK) {
            if (data != null && data is ReviewsList) {
                val showList = data.reviews.toMutableList()
                if (data.myReview != null) {
                    myReview = data.myReview
                    showList.add(0, myReview!!)
                }
                loadReviews(showList)
            } else {
                loadReviews(listOf())
            }
        } else if (cod == DBOrders.GET_PROD_PURCHASED_OK) {
            btPressed = false
            if (data != null && data is Boolean && data) {
                val args = Bundle()
                val json = Product.toJSON(prod)
                args.putString("prod", json)
                myView.findNavController().navigate(R.id.action_reviewListFragment_to_writeReviewFragment, args)
            } else {
                myView.msgSnackBar("You have to buy this product")
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        myView.msgSnackBar(myView.getString(R.string.error_data))
        if (cod == DBReviews.GET_REVIEWS_FAILURE) {
            myView.requireActivity().onBackPressed()
        } else if (cod == DBOrders.GET_PROD_PURCHASED_FAILURE) {
            btPressed = false
        }
    }
}