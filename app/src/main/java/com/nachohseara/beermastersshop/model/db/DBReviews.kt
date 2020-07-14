package com.nachohseara.beermastersshop.model.db

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nachohseara.beermastersshop.model.entity.Review

class DBReviews(private val con: IFBConnector) {
    companion object {
        const val COLLECTION = "Reviews"
        const val FIELD_USER_ID = "userId"
        const val FIELD_USER_NAME = "userName"
        const val FIELD_PROD_NAME = "prodName"
        const val FIELD_PROD_ID = "prodId"
        const val FIELD_RATING = "rating"
        const val FIELD_TEXT_REVIEW = "text"
        const val FIELD_DATE = "date"

        const val TAG = "DB_REVIEWS"
        const val GET_REVIEWS_OK = 300
        const val GET_REVIEWS_FAILURE = 101
        const val ADD_REVIEW_OK = 302
        const val ADD_REVIEW_FAILURE = 303
        const val GET_REVIEW_OK = 304
        const val GET_REVIEW_FAILURE = 305
    }
    private val db = FirebaseFirestore.getInstance()
    private val colRef = db.collection(COLLECTION)

    fun getUserReviews(userId: String) {
        getReviews(true, userId)
    }

    fun getProdReviews(prodId: String) {
        getReviews(false, prodId)
    }

    private fun getReviews(inProfile: Boolean, id: String) {
        val field = if (inProfile) FIELD_USER_ID else FIELD_PROD_ID
        colRef.whereEqualTo(field, id).orderBy(FIELD_DATE, Query.Direction.DESCENDING).get()
            .addOnSuccessListener {docs ->
                con.onSuccessFB(GET_REVIEWS_OK, Review.toReviewList(docs, inProfile))
                Log.d(TAG, "Get Reviews OK")
            }
            .addOnFailureListener { exception ->
                con.onFailureFB(GET_REVIEWS_FAILURE, exception.message.toString())
                Log.d(TAG, exception.toString())
            }
    }

    fun addReview(review: Review) {
        colRef.add(Review.toItems(review))
            .addOnSuccessListener { doc ->
                con.onSuccessFB(ADD_REVIEW_OK, doc)
                Log.d(TAG, "Review upload succesfully")
            }.addOnFailureListener { exception ->
                con.onFailureFB(ADD_REVIEW_FAILURE, exception.message.toString())
                Log.d(TAG, exception.toString())
            }
    }

    fun getReview(userId: String, prodId: String) {
        colRef.whereEqualTo(FIELD_USER_ID, userId).whereEqualTo(FIELD_PROD_ID, prodId).get()
            .addOnSuccessListener {docs ->
                var review: Review? = null
                for (doc in docs) {
                    if (doc != null && doc.exists()) {
                        review = Review.toReview(doc)
                        break
                    }
                }
                con.onSuccessFB(GET_REVIEW_OK, review)
                Log.d(TAG, "Review upload succesfully")
            }.addOnFailureListener { exception ->
                con.onFailureFB(GET_REVIEW_FAILURE, exception.message.toString())
                Log.d(TAG, exception.toString())
            }
    }
}