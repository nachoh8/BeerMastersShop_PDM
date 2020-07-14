package com.nachohseara.beermastersshop.model.entity

import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.model.db.DBProducts

class Product(d: ProductData) {
    companion object {
        fun getNoImgProduct() : Int = R.drawable.beer_no_imag
        const val MAX_LENGTH_PROD_NAME = 30

        /// Price
        fun formattedPrice(p: Int, withSymbol: Boolean = true) : String {
            val ud = (p / 100).toString()
            val dec = p % 100
            val decStr = if (dec < 10) "0$dec" else dec.toString()

            return if (withSymbol) "$ud.$decStr€" else "$ud.$decStr"
        }

        fun strToPrice(str: String) : Int {
            val fPrice = str.toFloat()
            return (fPrice * 100).toInt()
        }

        private val regexPrice = Regex("\\d+\\.\\d{2}")

        fun checkFormattedPrice(str: String) : Boolean = regexPrice.matches(str)

        /// Transform
        fun toProduct(doc: DocumentSnapshot) : Product? {
            if (doc.exists()) {
                return Product(toProductData(doc))
            }
            return null
        }

        fun toProductData(doc: DocumentSnapshot) : ProductData {
            val numReview = doc.get(DBProducts.FIELD_NUM_REVIEWS, Int::class.java)!!
            val acum = doc.get(DBProducts.FIELD_ACUM_RATING, Int::class.java)!!
            val rating = if (numReview > 0) acum.toFloat()/numReview.toFloat() else 0f

            return ProductData(
                doc.id,
                doc.getString(DBProducts.FIELD_NAME).toString(),
                doc.getString(DBProducts.FIELD_IMGURL).toString(),
                doc.get(DBProducts.FIELD_PRICE, Int::class.java)!!,
                numReview,
                rating,
                doc.getString(DBProducts.FIELD_BRAND_ID).toString(),
                doc.getString(DBProducts.FIELD_BRAND).toString(),
                doc.getString(DBProducts.FIELD_COUNTRY).toString(),
                doc.getBoolean(DBProducts.FIELD_ACTIVE)!!
            )
        }

        fun toJSON(prod: Product) : String {
            val gson = Gson()
            return gson.toJson(prod)
        }

        fun toProduct(json: String) : Product {
            val gson = Gson()
            if (json.isEmpty()) return emptyProduct()
            return gson.fromJson(json, Product::class.java)
        }

        fun toActive(str: String) : Boolean = str == "Enabled"

        /// Empty Product Creation
        fun emptyProduct() : Product = Product(dataEmpty())

        private fun dataEmpty() : ProductData = ProductData("", "", "", 0, 0, 0f, "", "", "", true)
    }

    private val data = d
    //private val strPrice = formattedPrice(d.price)

    fun getActive() : Boolean = data.acitve
    fun setActive(enabled: Boolean) {
        data.acitve = enabled
    }
    fun getDocId() : String = data.docId
    fun setDocId(id: String) {
        data.docId = id
    }
    fun getName() : String = data.name
    fun setName(newName: String)  {
        if (newName.isNotEmpty()) data.name = newName
    }
    fun getFormattedPrice(withSymbol: Boolean = true) : String = formattedPrice(data.price, withSymbol)
    fun getPrice() : Int = data.price
    fun setPrice(str: String) {
        data.price = strToPrice(str)
    }
    fun getNumReviews() : Int = data.reviews
    fun getRating() : Float = data.rating
    fun getImgUrl() : String = data.imgUrl
    fun setImgUrl(url: String) {
        data.imgUrl = url
    }
    fun getBrandId() : String = data.brandId
    fun getBrand() : String = data.brand
    fun setBrandCountry(brand: Brand) {
        data.country = brand.country
        data.brand = brand.name
        data.brandId = brand.id
    }
    fun getCountry() : String = data.country

    fun getDisplayBrandCountry() : String = "${getBrand()}, ${getCountry()}"
    fun getProductData() : ProductData = data

    fun toUpItem(docId: String = getDocId()) : HashMap<String, Any> {
        val item = hashMapOf<String, Any>()
        item[DBProducts.FIELD_ACTIVE] = getActive()
        item[DBProducts.FIELD_NAME] = getName()
        item[DBProducts.FIELD_PRICE] = getPrice()
        item[DBProducts.FIELD_BRAND_ID] = getBrandId()
        item[DBProducts.FIELD_BRAND] = getBrand()
        item[DBProducts.FIELD_COUNTRY] = getCountry()
        item[DBProducts.FIELD_ID] = docId
        item[DBProducts.FIELD_IMGURL] = getImgUrl()
        item[DBProducts.FIELD_NUM_REVIEWS] = getNumReviews()
        //item[DBProducts.FIELD_RATING] = getRating()
        item[DBProducts.FIELD_ACUM_RATING] = 0

        return item
    }

    fun isEmpty() : Boolean = getDocId().isEmpty()

    fun sameAs(other: Product) : Boolean {
        return getDocId() == other.getDocId() &&
                getActive() == other.getActive() &&
                getImgUrl() == other.getImgUrl() &&
                getName() == other.getName() &&
                getBrandId() == other.getBrandId() &&
                getPrice() == other.getPrice()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Product) return false
        return data == other.getProductData()
    }
}