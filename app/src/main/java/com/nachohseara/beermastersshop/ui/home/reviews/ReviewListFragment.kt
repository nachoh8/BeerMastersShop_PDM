package com.nachohseara.beermastersshop.ui.home.reviews

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.LoaderFragment
import com.nachohseara.beermastersshop.model.entity.Product
import com.nachohseara.beermastersshop.model.entity.Review
import com.nachohseara.beermastersshop.model.entity.User
import com.nachohseara.beermastersshop.ui.home.HomeActivity

class ReviewListFragment : LoaderFragment() {
    private lateinit var mView: View
    private val controller = ReviewListController(this)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mView =  inflater.inflate(R.layout.fragment_review_list, container, false)

        blockMenu(true)

        var userReview = true
        var json = ""
        if (arguments != null) {
            userReview = requireArguments().getBoolean("userReview")
            json = requireArguments().getString("prod", "")
        }
        val prod = Product.toProduct(json)

        controller.onCreate(userReview, prod)

        return mView
    }

    private fun blockMenu(block: Boolean) {
        if (context is HomeActivity) {
            (context as HomeActivity).setMenu(block)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.empty, menu)
    }

    fun showReviews(reviews: List<Review>) {
        if (reviews.isNotEmpty()) {
            val rView: RecyclerView = mView.findViewById(R.id.recyclerView)
            rView.setHasFixedSize(true)

            val lManager = LinearLayoutManager(requireContext())
            rView.layoutManager = lManager

            val adapter = ReviewAdapter(reviews, requireContext(), controller.getOnProfile())
            rView.adapter = adapter
        }
    }

    override fun actionPostComplete() {
        val rView: RecyclerView = mView.findViewById(R.id.recyclerView)
        val progBar: ProgressBar = mView.findViewById(R.id.progBar)
        progBar.visibility = View.GONE
        if (controller.isEmpty()) {
            val txt: TextView = mView.findViewById(R.id.txtNotReviews)
            txt.text = if (controller.getOnProfile()) getString(R.string.no_reviews_user) else getString(R.string.no_reviews_prod)
            txt.visibility = View.VISIBLE
        } else {
            rView.visibility = View.VISIBLE
        }
        if (!controller.getOnProfile() && !User(requireContext()).isAdmin() && controller.myReview == null) {
            val cardWriteReview: CardView = mView.findViewById(R.id.cardWriteReview)
            val btWriteReview: Button = mView.findViewById(R.id.btWriteReview)
            cardWriteReview.visibility = View.VISIBLE
            btWriteReview.setOnClickListener {
                controller.onWriteReview()
            }
        }
    }

    override fun getMyView(): View = mView
}
