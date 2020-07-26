package com.nachohseara.beermastersshop.model.db

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nachohseara.beermastersshop.model.entity.Brand
import com.nachohseara.beermastersshop.model.entity.FilterProduct
import com.nachohseara.beermastersshop.model.entity.Product
import com.nachohseara.beermastersshop.model.entity.ProductList

class DBProducts(c: IFBConnector) {

    companion object {
        const val PRODUCTS_COLLECTION = "Products"
        const val BRANDS_COLLECTION = "Brands"
        const val FIELD_ACTIVE = "active"
        const val FIELD_BRAND = "brand"
        const val FIELD_BRAND_ID = "brandId"
        const val FIELD_COUNTRY = "country"
        const val FIELD_ID = "id"
        const val FIELD_IMGURL = "imgUrl"
        const val FIELD_NAME = "name"
        const val FIELD_PRICE = "price"
        const val FIELD_NUM_REVIEWS = "numReviews"
        const val FIELD_RATING = "rating"
        const val FIELD_SALES = "sales"
        const val FIELD_ACUM_RATING = "ratingAcum"

        const val FIELD_BRAND_NAME = "name"
        const val FIELD_BRAND_COUNTRY = "country"
        const val FIELD_BRAND_NUM_ACTIVE = "numActive"
        const val FIELD_BRAND_NUM_TOTAL = "numTotal"

        const val GETPRODS_TOPSALES = 10
        const val GETPRODS_ALL = 11

        const val TAG = "DB_PRODUCTS"
        const val GET_DATA_OK = 100
        const val GET_DATA_FAILURE = 101
        const val UP_DATA_OK = 102
        const val UP_DATA_FAILURE = 103
        const val DELETE_PRODUCT_OK = 104
        const val DELETE_PRODUCT_FAILURE = 105
        const val UP_BRAND_OK = 106
        const val UP_BRAND_ERROR = 107
    }
    private val db = FirebaseFirestore.getInstance()
    private val con = c

    private val allProds = db.collection(PRODUCTS_COLLECTION)
    private val activeProds = db.collection(PRODUCTS_COLLECTION).whereEqualTo(FIELD_ACTIVE, true)
    private val topSales = allProds.orderBy(FIELD_SALES, Query.Direction.DESCENDING)

    fun getTopSales(filter: FilterProduct = FilterProduct.default(), limit: Long = 8) {
        val query = topSales.limit(limit)
        if (filter.isDefault()) {
            execute(query)
        } else {
            filterProducts(query, filter, GETPRODS_TOPSALES)
        }
    }

    fun getAllProducts(filter: FilterProduct = FilterProduct.default()) {
        val query = allProds as Query
        if (filter.isDefault()) execute(query)
        else filterProducts(query, filter, GETPRODS_ALL)
    }

    fun getActiveProducts(filter: FilterProduct = FilterProduct.default()) {
        val query = activeProds
        if (filter.isDefault()) execute(query)
        else filterProducts(query, filter, GETPRODS_ALL)
    }

    fun getProduct(prodId: String, admin: Boolean) {
        if (prodId.isEmpty()) {
            con.onFailureFB(GET_DATA_FAILURE, "prodId empty")
            Log.e(TAG, "prodId empty")
        } else {
            db.collection(PRODUCTS_COLLECTION).document(prodId).get()
                .addOnSuccessListener { doc ->
                    var prod = Product.toProduct(doc)
                    if (prod != null && !prod.getActive() && !admin) {
                        prod = null
                    }
                    con.onSuccessFB(GET_DATA_OK, prod)
                    Log.d(TAG, "Get product completed")
                }.addOnFailureListener { exception ->
                    con.onFailureFB(GET_DATA_FAILURE, exception.message!!)
                    Log.e(TAG, exception.message!!)
                }
        }
    }

    private fun filterProducts(baseQuery: Query, filter: FilterProduct, getProds: Int) {
        var query = baseQuery
        val list = filter.fList.toList()
        if (list.isNotEmpty()) {
            if (filter.filterBy == FilterProduct.S_BRAND) {
                query = query.whereIn(FIELD_BRAND, list)
            } else if (filter.filterBy == FilterProduct.S_COUNTRY) {
                query = query.whereIn(FIELD_COUNTRY, list)
            }
        }

        /*if (getProds == GETPRODS_ALL) {
            when (filter.sortBy) {
                FilterProduct.SORTBY_NAME -> query = query.orderBy(FIELD_NAME)
                FilterProduct.SORTBY_PRICE_ASC -> query = query.orderBy(FIELD_PRICE, Query.Direction.ASCENDING)
                FilterProduct.SORTBY_PRICE_DESC -> query = query.orderBy(FIELD_PRICE, Query.Direction.DESCENDING)
                FilterProduct.SORTBY_RATING -> query = query.orderBy(FIELD_RATING, Query.Direction.DESCENDING)
            }
        }*/
        execute(query)
    }

    fun getBrandsCountries(admin: Boolean) {
        db.collection(BRANDS_COLLECTION).get()
            .addOnSuccessListener { docs->
                con.onSuccessFB(GET_DATA_OK, FilterProduct.toBrandsCountries(docs, admin))
                Log.d(TAG, "Get BrandsCountries completed")
            }.addOnFailureListener { exception ->
                con.onFailureFB(GET_DATA_FAILURE, exception.message!!)
                Log.e(TAG, exception.message!!)
            }
    }

    private fun execute(query: Query) {
        query.get()
            .addOnSuccessListener { docs ->
                con.onSuccessFB(GET_DATA_OK, ProductList(docs))
                Log.d(TAG, "Get products completed")
            }.addOnFailureListener { exception ->
                con.onFailureFB(GET_DATA_FAILURE, exception.message!!)
                Log.e(TAG, exception.message!!)
            }
    }

    fun addBrand(brand: Brand) {
        val items =  brand.toUploadItems()
        val ref = db.collection(BRANDS_COLLECTION).document()
        ref.set(items)
            .addOnSuccessListener {
                con.onSuccessFB(UP_BRAND_OK, Brand(ref.id, brand.name, brand.country, 0))
                Log.d(TAG, "Brand upload succesfully")
            }.addOnFailureListener { exception ->
                con.onFailureFB(UP_BRAND_ERROR, exception.message.toString())
                Log.d(TAG, exception.toString())
            }
    }

    fun addProduct(prod: Product) {
        val ref = db.collection(PRODUCTS_COLLECTION).document()
        editProduct(ref.id, prod)
    }

    fun editProduct(docId: String, prod: Product) {
        val items = prod.toUpItem(docId)
        db.collection(PRODUCTS_COLLECTION).document(docId).set(items)
            .addOnSuccessListener {
                con.onSuccessFB(UP_DATA_OK, docId)
                Log.d(TAG, "Product $docId upload: $prod")
            }.addOnFailureListener { exception ->
                con.onFailureFB(UP_DATA_FAILURE, exception.message!!)
                Log.w(TAG, exception)
            }
    }

    fun deleteProduct(prodId: String) {
        db.collection(PRODUCTS_COLLECTION).document(prodId).delete()
            .addOnSuccessListener {
                con.onSuccessFB(DELETE_PRODUCT_OK, null)
                Log.d(TAG, "Product deleted succesfully")
            }.addOnFailureListener { exception ->
                con.onFailureFB(DELETE_PRODUCT_FAILURE, exception.message.toString())
                Log.d(TAG, exception.toString())
            }
    }

    /*fun getPrices(prods: List<String>) {
        db.collection(PRODUCTS_COLLECTION).whereEqualTo("active", true).whereIn("id", prods).get()
            .addOnSuccessListener { docs ->
                //val prices = HashMap<String, List<Any>>()
                val prices = HashMap<String, Int>()
                for (doc in docs) {
                    if (doc != null && doc.exists()) {
                        val price = doc.get("price", Int::class.java)!!
                        //val name = doc.getString("name").toString()
                        //val data = mutableListOf<Any>()
                        //data.add(name); data.add(price)
                        prices[doc.id] = price//data.toList()
                    }
                }
                con.onSuccessFB(GET_DATA_OK, prices)
                Log.d(TAG, "getBestProducts completed")
            }.addOnFailureListener { exception ->
                con.onFailureFB(GET_DATA_FAILURE, exception.message!!)
                Log.e(TAG, exception.message!!)
            }
    }*/
}