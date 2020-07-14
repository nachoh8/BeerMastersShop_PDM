package com.nachohseara.beermastersshop.model.entity

import android.os.Parcel
import android.os.Parcelable

data class ProductData(
    var docId: String, var name: String, var imgUrl: String, var price: Int,
    val reviews: Int, val rating: Float, var brandId: String,
    var brand: String, var country: String, var acitve: Boolean) : Parcelable {

    companion object CREATOR : Parcelable.Creator<ProductData> {
        override fun createFromParcel(parcel: Parcel): ProductData {
            return ProductData(parcel)
        }

        override fun newArray(size: Int): Array<ProductData?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readBoolean()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(docId)
        parcel.writeString(name)
        parcel.writeString(imgUrl)
        parcel.writeInt(price)
        parcel.writeInt(reviews)
        parcel.writeFloat(rating)
        parcel.writeString(brandId)
        parcel.writeString(brand)
        parcel.writeString(country)
        parcel.writeBoolean(acitve)
    }

    override fun describeContents(): Int = 0

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is ProductData) return false
        return docId == other.docId
    }
}