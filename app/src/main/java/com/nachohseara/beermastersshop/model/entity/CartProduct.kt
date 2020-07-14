package com.nachohseara.beermastersshop.model.entity

data class CartProduct(val data: Product, var numUd: Int = 0)

data class Cart(val products: HashMap<String, CartProduct>, var total: Int = 0) //Map<docID, CartProduct>

data class CartRAW(val products: HashMap<String, Int>) //<prodId, numUd>, <prodId, docId>

/*
class CartList(cw: CartRAW, docs: QuerySnapshot) {
    var products: HashMap<String, CartProduct> = hashMapOf()
    var total = 0

    init {
        for (doc in docs) {
            if (doc != null && doc.exists()) {
                val numUd = cw.products[doc.id]
                if (numUd != null) {
                    val price = doc.get("price", Int::class.java)!!
                    total += price * numUd
                    val prod = Product(
                        ProductData(
                            doc.id,
                            doc.getString("name").toString(),
                            doc.getString("imgUrl").toString(),
                            price,
                            doc.get("numReviews", Int::class.java)!!,
                            doc.get("rating", Float::class.java)!!,
                            doc.getString("brand").toString(),
                            doc.getString("country").toString(),
                            doc.getBoolean(DBProducts.FIELD_ACTIVE)!!
                        ))
                    products[doc.id] = CartProduct(prod, numUd)
                }
            }
        }
    }
}*/