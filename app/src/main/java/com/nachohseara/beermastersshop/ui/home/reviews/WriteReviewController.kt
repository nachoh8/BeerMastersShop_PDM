package com.nachohseara.beermastersshop.ui.home.reviews

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.DBReviews
import com.nachohseara.beermastersshop.model.db.IFBConnector
import com.nachohseara.beermastersshop.model.entity.OrderList
import com.nachohseara.beermastersshop.model.entity.Product
import com.nachohseara.beermastersshop.model.entity.Review
import com.nachohseara.beermastersshop.model.entity.User

class WriteReviewController(private val myView: WriteReviewFragment) : IFBConnector {
    private var prod = Product.emptyProduct()
    private lateinit var myAct : BaseActivity

    private var btPressed = false

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate(_prod: Product) {
        if (_prod.isEmpty()) {
            myView.msgSnackBar(myView.getString(R.string.error_data))
            myView.requireActivity().onBackPressed()
            return
        }
        prod = _prod
    }

    fun onPost() {
        myView.hideKeyboard()
        if (myAct.onlineOrMsg() && !btPressed) {
            btPressed = true
            myAct.startLoading()
            val opinion = Review.deleteCharsIllegal(myView.getOpinion())
            val user = User(myView.requireContext())
            val review = Review("", prod.getDocId(), prod.getName(), user.getDocId(),
                user.getName(), myView.getRating().toInt(), opinion, OrderList.getDateDay())
            DBReviews(this).addReview(review)
        }
    }

    override fun onSuccessFB(cod: Int, data: Any?) {
        if (cod == DBReviews.ADD_REVIEW_OK) {
            myAct.endLoading()
            myView.msgSnackBar(myView.getString(R.string.review_post_succesfully))
            val args = Bundle()
            args.putBoolean("userReview", false)
            args.putString("prod", Product.toJSON(prod))
            myView.findNavController().navigate(R.id.action_writeReviewFragment_to_reviewListFragment, args)
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        if (cod == DBReviews.ADD_REVIEW_FAILURE) {
            myAct.endLoading()
            btPressed = false
            myView.msgSnackBar(myView.getString(R.string.error_data))
        }
    }
}