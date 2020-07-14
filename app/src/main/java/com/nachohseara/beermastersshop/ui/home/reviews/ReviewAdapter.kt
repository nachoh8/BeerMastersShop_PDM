package com.nachohseara.beermastersshop.ui.home.reviews

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.model.entity.Review

class ReviewAdapter(private val reviews: List<Review>, private val ctxt: Context, private val onProfile: Boolean) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        lateinit var adapter: ReviewAdapter

        val txtReview: TextView = v.findViewById(R.id.txtReview)
        val txtDate: TextView = v.findViewById(R.id.txtDate)
        val ratingBar: RatingBar = v.findViewById(R.id.ratingBar)
        val btGoProd: Button = v.findViewById(R.id.btGoProd)

        init {
            btGoProd.setOnClickListener {
                if (adapter.onProfile) {
                    val prodId = adapter.reviews[adapterPosition].prodId
                    val args = Bundle()
                    args.putString("prodId", prodId)
                    v.findNavController().navigate(R.id.action_reviewListFragment_to_productPageFragment, args)
                }
            }
        }

        fun setData(review: Review) {
            txtReview.text = review.text
            txtDate.text = review.date
            ratingBar.rating = review.rating.toFloat()
            if (adapter.onProfile) {
                btGoProd.text = review.prodName
            } else {
                btGoProd.text = review.userName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val inflater = ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.user_review_card, parent, false)
        return ReviewViewHolder(v)
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.adapter = this
        holder.setData(review)
    }
}