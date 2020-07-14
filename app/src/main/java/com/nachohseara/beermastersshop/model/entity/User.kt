package com.nachohseara.beermastersshop.model.entity

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson

data class UserData(var docId: String, var email: String, var name: String, var lastname: String, var cart: String, var orders: String, var admin: Boolean = false)

class User(c: Context) {
    companion object {
        const val TAG = "USER_DATA"
        const val file = "UserDataFile"
        const val dataField = "UserData"

        fun toUserData(doc: DocumentSnapshot) : UserData? {
            var ud: UserData? = null
            if (doc.exists()) {
                ud = UserData(doc.id, doc.getString("email")!!, doc.getString("name")!!,
                    doc.getString("lastname")!!, "", "")
                if (doc.getBoolean("admin") == null) {
                    ud.cart = doc.getString("cart")!!
                    ud.orders = doc.getString("orders")!!
                } else {
                    ud.admin = doc.getBoolean("admin")!!
                }
            }

            return ud
        }
    }

    private lateinit var data: UserData
    private val sp = c.getSharedPreferences(file, Context.MODE_PRIVATE)
    private val gson = Gson()

    init {
        if (fileExists()) loadData()
    }

    constructor(c: Context, ud: UserData) : this(c) {
        data = ud
        saveData()
    }

    fun fileExists() : Boolean = sp.contains(dataField)

    private fun loadData() {
        val json = sp.getString(dataField, "")
        data = if (json.isNullOrEmpty()) {
            UserData("","","", "", "", "")
        } else {
            gson.fromJson(json, UserData::class.java)
        }
        Log.d(TAG, "User load: $json")
    }

    fun isAdmin() : Boolean = data.admin

    fun getCartId() : String = data.cart

    fun getOrdersId() : String = data.orders

    fun getDocId() : String = data.docId

    fun getEmail() : String = data.email

    fun getName() : String = data.name

    fun getLastname() : String = data.lastname

    fun display() : String = "${data.name}  ${data.lastname}"

    fun getData() : UserData = data

    fun setFromData(ud: UserData) {
        data = ud
        saveData()
    }

    fun changeInfo(newName: String, newLastname: String) {
        data.name = newName
        data.lastname = newLastname
        //data.display = display
        saveData()
    }

    fun saveData() {
        val editor = sp.edit()
        val json = gson.toJson(data)
        editor.putString(dataField, json)
        editor.commit()
        Log.d(TAG, "Save Data $json")
    }

    fun deleteData() {
        sp.edit().clear().commit()
        Log.d(TAG, "UserData Deleted")
    }
}