package com.nachohseara.beermastersshop.model.entity

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.nachohseara.beermastersshop.model.db.DBReviews
import com.nachohseara.beermastersshop.model.db.FBModel

data class ReviewsList(val reviews: List<Review>, val myReview: Review?)

class Review(val id: String, val prodId: String, val prodName: String,
             val userId: String, val userName: String,
             val rating: Int, val text: String, val date: String) {
    companion object {
        const val MAX_TEXT_LENGTH = 500

        fun deleteCharsIllegal(str: String) : String {
            val str2 = str.trim()
            val regex = Regex("[\r\n]+")
            return str2.replace(regex, "\n")
        }

        fun toReviewList(docs: QuerySnapshot, inProfile: Boolean) : ReviewsList {
            val res = mutableListOf<Review>()
            var myReview: Review? = null
            val userId = FBModel.getUserId()!!

            for (doc in docs) {
                if (doc != null && doc.exists()) {
                    val review = toReview(doc)
                    if (inProfile) {
                        res.add(review)
                    } else {
                        if (review.userId == userId && myReview == null) {
                            myReview = review
                        } else {
                            res.add(review)
                        }
                    }
                }
            }

            return ReviewsList(res.toList(), myReview)
        }

        fun toReview(doc: QueryDocumentSnapshot) : Review {
            val prodId = doc.getString(DBReviews.FIELD_PROD_ID).toString()
            val userId = doc.getString(DBReviews.FIELD_USER_ID).toString()
            val prodName = doc.getString(DBReviews.FIELD_PROD_NAME).toString()
            val userName = doc.getString(DBReviews.FIELD_USER_NAME).toString()
            val rating = doc.get(DBReviews.FIELD_RATING, Int::class.java)!!
            val text = doc.getString(DBReviews.FIELD_TEXT_REVIEW).toString()
            val date = doc.getString(DBReviews.FIELD_DATE).toString()
            return Review(doc.id, prodId, prodName, userId, userName, rating, text, date)
        }

        fun toItems(review: Review) : HashMap<String, Any> {
            val items: HashMap<String, Any> = hashMapOf()
            items[DBReviews.FIELD_PROD_ID] = review.prodId
            items[DBReviews.FIELD_PROD_NAME] = review.prodName
            items[DBReviews.FIELD_USER_ID] = review.userId
            items[DBReviews.FIELD_USER_NAME] = review.userName
            items[DBReviews.FIELD_RATING] = review.rating
            items[DBReviews.FIELD_TEXT_REVIEW] = review.text
            items[DBReviews.FIELD_DATE] = review.date

            return items
        }
    }
}
