package com.nachohseara.beermastersshop.model.entity

import com.google.firebase.firestore.QuerySnapshot
import com.nachohseara.beermastersshop.model.db.DBProducts

class ProductList(docs: QuerySnapshot) {
    companion object{
        fun toList(l: List<ProductData>) : List<Product> {
            val resL : MutableList<Product> = mutableListOf()
            for (pd in l) {
                resL.add(Product(pd))
            }

            return resL.toList()
        }

        fun filter(pList: List<Product>, filter: FilterProduct) : List<Product> {
            val res  = when (filter.filterBy) {
                FilterProduct.S_COUNTRY -> {
                    pList.filter { p -> filter.fList.contains(p.getCountry())}
                }
                FilterProduct.S_BRAND -> {
                    pList.filter { p -> filter.fList.contains(p.getBrand())}
                }
                else -> {
                    pList
                }
            }

            return sortBy(res, filter)
        }

        fun sortBy(pList: List<Product>, filter: FilterProduct) : List<Product> {
            return when (filter.sortBy) {
                FilterProduct.SORTBY_NAME -> {
                    pList.sortedBy { it.getName() }
                }
                FilterProduct.SORTBY_PRICE_DESC -> {
                    pList.sortedByDescending { it.getPrice() }
                }
                FilterProduct.SORTBY_PRICE_ASC -> {
                    pList.sortedBy { it.getPrice() }
                }
                FilterProduct.SORTBY_RATING -> {
                    pList.sortedByDescending { it.getRating() }
                }
                else -> pList
            }
        }
    }

    var products: MutableList<Product> = mutableListOf()
    var prices =  HashMap<String, Int>()

    init {
        for (doc in docs) {
            val prod = Product.toProduct(doc)
            if (prod != null) {
                prices[prod.getDocId()] = prod.getPrice()
                products.add(prod)
            }
        }
    }
}