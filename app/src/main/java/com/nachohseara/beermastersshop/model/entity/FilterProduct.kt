package com.nachohseara.beermastersshop.model.entity

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.nachohseara.beermastersshop.model.db.DBProducts

//<nombre marca, num activo cervezas>,<nombre pais, num activo cervezas>

data class ListItem(val item: String, var checked: Boolean, val num: Int)
data class Brand(val id: String, val name: String, val country: String, val num: Int) {
    fun toUploadItems() : HashMap<String, Any> {
        val items: HashMap<String, Any> = hashMapOf()
        items[DBProducts.FIELD_BRAND_NAME] = name
        items[DBProducts.FIELD_BRAND_COUNTRY] = country
        items[DBProducts.FIELD_BRAND_NUM_ACTIVE] = 0
        items[DBProducts.FIELD_BRAND_NUM_TOTAL] = 0

        return items
    }
}
data class Country(val name: String, var num: Int)
data class BrandsCountries(val brands: HashMap<String, Brand>, val countries: HashMap<String, Country>) {
    companion object {
        fun getBrandsSorted(data: BrandsCountries) : List<Brand> {
            val brands = data.brands.values.toList()
            return brands.sortedBy { it.name }
        }
    }
}

class FilterProduct(var sortBy: Int, var filterBy: Int, var fList: MutableSet<String>) {
    companion object {
        const val SORTBY_DEFAULT = 0
        const val SORTBY_NAME = 1
        const val SORTBY_PRICE_ASC = 2
        const val SORTBY_PRICE_DESC = 3
        const val SORTBY_RATING = 4

        const val S_BRAND = 0
        const val S_COUNTRY = 1
        const val S_EMPTY = -1

        fun sortByToStr(cod: Int) : String {
            return when(cod) {
                SORTBY_NAME -> "SORTBY_NAME"
                SORTBY_PRICE_ASC -> "SORTBY_PRICE_ASC"
                SORTBY_PRICE_DESC -> "SORTBY_PRICE_DESC"
                SORTBY_RATING -> "SORTBY_RATING"
                else -> "SORTBY_DEFAULT"
            }
        }

        fun filterByToStr(cod: Int) : String {
            return when(cod) {
                S_BRAND -> "Brand"
                S_COUNTRY -> "Country"
                else -> "Empty"
            }
        }

        fun toJSON(filter: FilterProduct) : String {
            val gson = Gson()
            return gson.toJson(filter)
        }

        fun toFilter(json: String) : FilterProduct {
            val gson = Gson()
            if (json.isEmpty()) return default()
            return gson.fromJson(json, FilterProduct::class.java)
        }

        fun default() : FilterProduct = FilterProduct(SORTBY_DEFAULT, S_EMPTY, mutableSetOf())

        fun toBrandsCountries(docs: QuerySnapshot, admin: Boolean) : BrandsCountries {
            val bc = BrandsCountries(hashMapOf(), hashMapOf())
            for (doc in docs) {
                if (doc != null && doc.exists()) {
                    val nameB = doc.getString("name")!!
                    val nameC = doc.getString("country")!!
                    val num = if (admin) {
                        doc.get(DBProducts.FIELD_BRAND_NUM_TOTAL, Int::class.java)!!
                    } else {
                        doc.get(DBProducts.FIELD_BRAND_NUM_ACTIVE, Int::class.java)!!
                    }
                    if (admin || !admin && num > 0) {
                        val brand = Brand(doc.id, nameB, nameC, num)

                        bc.brands[nameB] = brand
                        if (bc.countries[nameC] == null) {
                            bc.countries[nameC] = Country(nameC, num)
                        } else {
                            bc.countries.getValue(nameC).num += num
                        }
                    }
                }
            }

            return bc
        }
    }

    fun isDefault() : Boolean {
        return sortBy == SORTBY_DEFAULT && filterBy == S_EMPTY && fList.isEmpty()
    }

    fun changeSortBy(cod: Int) {
        sortBy = cod
    }

    fun toListOfBrands(bc: BrandsCountries) : List<ListItem> {
        //return toListItem(bc.brands, fBrand)
        val l : MutableList<ListItem> = mutableListOf()
        for (c in bc.brands.keys) {
            val num = bc.brands.getValue(c).num
            val checked = fList.contains(c)
            l.add(ListItem(c, checked, num))
        }

        return l.toList().sortedBy { it.item }
    }

    fun toListOfCountries(bc: BrandsCountries) : List<ListItem> {
        //return toListItem(bc.countries, fCountry)
        val l : MutableList<ListItem> = mutableListOf()
        for (c in bc.countries.keys) {
            val num = bc.countries.getValue(c).num
            val checked = fList.contains(c)
            l.add(ListItem(c, checked, num))
        }

        return l.toList().sortedBy { it.item }
    }

    fun listToStr() : String {
        var res = ""
        if (fList.isEmpty()) return res

        for ((n, data) in fList.withIndex()) {
            if (n < 3) {
                if (res.isEmpty()) {
                    res = data
                } else {
                    res += ", $data"
                }
            } else {
                res += "..."
                break
            }
        }

        return res
    }

    fun reset() {
        sortBy = SORTBY_DEFAULT
        filterBy = S_EMPTY
        fList.clear()
    }

    fun setBrands(l: List<ListItem>) {
        setFilterList(l, S_BRAND)
    }

    fun setCountries(l: List<ListItem>) {
        setFilterList(l, S_COUNTRY)
    }

    override fun toString() : String {
        return "Sort by: ${sortByToStr(sortBy)}, Filter by: ${filterByToStr(filterBy)}, List: $fList"
    }

    private fun setFilterList(l: List<ListItem>, _filterBy: Int) {
        fList.clear()
        for (item in l) {
            if (item.checked) {
                fList.add(item.item)
            }
        }
        if (fList.isEmpty()) {
            filterBy = S_EMPTY
        } else {
            filterBy = _filterBy
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is FilterProduct) return false
        return sortBy == other.sortBy &&
                filterBy == other.filterBy &&
                fList == other.fList
    }
}