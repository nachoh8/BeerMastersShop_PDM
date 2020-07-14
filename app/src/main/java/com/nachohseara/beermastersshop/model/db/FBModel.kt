package com.nachohseara.beermastersshop.model.db

import android.util.Log
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.nachohseara.beermastersshop.model.entity.User
import com.nachohseara.beermastersshop.model.entity.UserData
import kotlin.collections.HashMap

class FBModel(c: IFBConnector) {

    companion object {
        fun islogin() : Boolean = FirebaseAuth.getInstance().currentUser != null

        fun getUserId() : String? = FirebaseAuth.getInstance().currentUser?.uid

        fun logOut() {
            FirebaseAuth.getInstance().signOut()
            Log.d(TAG, "Logout")
        }
        const val TAG = "FB_MODEL"

        const val EMPTY_CODE = -1
        const val LOGIN_OK = 0
        const val LOGIN_FAILURE = 1
        const val EMAIL_VERIFICATION_OK = 2
        const val EMAIL_VERIFICATION_FAILURE = 3
        const val HAS_DATA_OK = 4
        const val HAS_DATA_FAILURE = 5
        const val ACCOUNT_CREATED = 6
        const val ACCOUNT_FAILURE = 7
        const val DATA_UPLOAD_OK = 8
        const val DATA_UPLOAD_FAILURE = 9
        const val DATA_UPDATE_OK = 10
        const val DATA_UPDATE_FAILURE = 10
        const val REAUTHENTICATION_OK = 11
        const val REAUTHENTICATION_FAILURE = 12
        const val PWD_UPDATED_OK = 13
        const val PWD_UPDATED_FAILURE = 14
        const val DELETE_ACC_OK = 15
        const val DELETE_ACC_FAILURE = 16
        const val DATA_DELETED_FAILURE = 17
        const val SEND_RESET_PWD_OK = 18
        const val SEND_RESET_PWD_FAILURE = 19
    }

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val con = c

    private fun getUser() : FirebaseUser? {
        return mAuth.currentUser
    }

    private fun setDisplayName(name: String, lastname: String) {
        val user = getUser()
        val profileUpdate = UserProfileChangeRequest.Builder()
            .setDisplayName("$name $lastname")
            .build()
        user?.updateProfile(profileUpdate)
            ?.addOnSuccessListener {
                Log.d(TAG, "User DisplayName Updated to $name $lastname")
            }?.addOnFailureListener {exception ->
                Log.w(TAG, exception)
            }
    }

    fun getEmail() : String {
        return getUser()?.email ?: ""
    }

    fun isLogin() = getUser() != null

    fun isVerified() : Boolean {
        return getUser()?.isEmailVerified ?: false
    }

    fun logout() {
        logOut()
    }

    fun login(email: String, pwd: String) {
        mAuth.signInWithEmailAndPassword(email, pwd)
            .addOnSuccessListener {
                con.onSuccessFB(LOGIN_OK, null)
                Log.d(TAG, "Login complete")
            }
            .addOnFailureListener { exception ->
                con.onFailureFB(LOGIN_FAILURE, exception.message.toString())
                Log.w(TAG, exception)
            }
    }

    fun hasData() {
        val userID = getUser()?.uid
        if (userID.isNullOrEmpty()) {
            con.onFailureFB(EMPTY_CODE, "")
        } else {
            db.collection("Users").document(userID).get()
                .addOnSuccessListener {doc ->
                    con.onSuccessFB(HAS_DATA_OK, User.toUserData(doc)) // doc puede ser null si no existe
                    Log.d(TAG, "Doc: ${doc.id}")
                }
                .addOnFailureListener { exception ->
                    con.onFailureFB(HAS_DATA_FAILURE, exception.message.toString())
                    Log.w(TAG, exception)
                }
        }
    }

    fun createAccount(email: String, pwd: String) {
        mAuth.createUserWithEmailAndPassword(email, pwd)
            .addOnSuccessListener {
                con.onSuccessFB(ACCOUNT_CREATED, null)
                Log.d(TAG, "Account Created: $email")
            }.addOnFailureListener { exception ->
                con.onFailureFB(ACCOUNT_FAILURE, exception.message.toString())
                Log.w(TAG, exception)
            }
    }

    fun sendEmailVerification() {
        val user = getUser()
        user?.sendEmailVerification()
            ?.addOnSuccessListener {
                con.onSuccessFB(EMAIL_VERIFICATION_OK, null)
                Log.d(TAG, "VERIFICATION EMAIL sent")
            }?.addOnFailureListener {exception ->
                con.onFailureFB(EMAIL_VERIFICATION_FAILURE, exception.message.toString())
                Log.w(TAG, exception)
            }
    }

    fun sendResetPwd(email: String) {
        mAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                con.onSuccessFB(SEND_RESET_PWD_OK, null)
                Log.d(TAG, "Send Reset Password Email ok")
            }.addOnFailureListener { exception ->
                con.onFailureFB(SEND_RESET_PWD_FAILURE, exception.message.toString())
                Log.w(TAG, exception)
            }
    }

    fun addPersonalData(items: HashMap<String, Any>) {
        val userID = getUser()?.uid
        if (userID == null) {
            con.onFailureFB(EMPTY_CODE, "")
        } else {
            val userRef = db.collection("Users").document(userID)
            val cartRef = db.collection("Cart").document()
            val ordersRef = db.collection("Orders").document()
            items["cart"] = cartRef.id
            items["orders"] = ordersRef.id
            val cartItem = HashMap<String, String>()
            cartItem["user"] = userID
            val orderItem = HashMap<String, String>()
            orderItem["user"] = userID
            orderItem["email"] = getEmail()
            db.runTransaction { transaction ->
                transaction.set(userRef, items)
                transaction.set(cartRef, cartItem)
                transaction.set(ordersRef, orderItem)
            }.addOnSuccessListener {
                setDisplayName(items["name"] as String, items["lastname"] as String)
                val userData = UserData(userID, getEmail(), items["name"] as String, items["lastname"] as String, cartRef.id, ordersRef.id)
                con.onSuccessFB(DATA_UPLOAD_OK, userData)
                Log.d(TAG, "Data upload to server")
            }.addOnFailureListener { exception ->
                con.onFailureFB(DATA_UPLOAD_FAILURE, exception.message.toString())
                Log.w(TAG, exception)
            }
        }
    }

    fun updatePersonalData(items: HashMap<String, Any>) {
        val userID = getUser()?.uid
        if (userID == null) {
            con.onFailureFB(EMPTY_CODE, "")
        } else {
            val docRef = db.collection("Users").document(userID)
            val name = items["name"] as String
            val lastname = items["lastname"] as String
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val cName = snapshot.getString("name")
                val cLastname = snapshot.getString("lastname")

                if (cName != name) {
                    transaction.update(docRef, "name", name)
                }
                if (cLastname != lastname) {
                    transaction.update(docRef, "lastname", lastname)
                }
            }.addOnSuccessListener {
                setDisplayName(name, lastname)
                con.onSuccessFB(DATA_UPDATE_OK, null)
                Log.d(TAG, "Transaction Update Profile success")
            }.addOnFailureListener { exception ->
                con.onFailureFB(DATA_UPDATE_FAILURE, exception.message.toString())
                Log.w(TAG, exception)
            }
        }
    }

    fun changePwd(pwd: String, newPwd: String) {
        val user = getUser()
        if (user == null) {
            con.onFailureFB(EMPTY_CODE, "")
        } else {
            val credential = EmailAuthProvider.getCredential(getEmail(), pwd)
            user.reauthenticate(credential)
                .addOnSuccessListener {
                    updatePwd(user, newPwd)
                    Log.d(TAG, "Reauthentication complete")
                }
                .addOnFailureListener { exception ->
                    con.onFailureFB(REAUTHENTICATION_FAILURE, exception.message.toString())
                    Log.w(TAG, exception.toString())
                }
        }
    }

    fun reauthentication(pwd: String) {
        val user = getUser()
        if (user == null) {
            con.onFailureFB(EMPTY_CODE, "")
        } else {
            val credential = EmailAuthProvider.getCredential(getEmail(), pwd)
            user.reauthenticate(credential)
                .addOnSuccessListener {
                    con.onSuccessFB(REAUTHENTICATION_OK, null)
                    Log.d(TAG, "Reauthentication complete")
                }
                .addOnFailureListener { exception ->
                    con.onFailureFB(REAUTHENTICATION_FAILURE, exception.message.toString())
                    Log.w(TAG, exception.toString())
                }
        }
    }

    private fun updatePwd(user: FirebaseUser, newPwd: String) {
        user.updatePassword(newPwd)
            .addOnSuccessListener {
                con.onSuccessFB(PWD_UPDATED_OK, null)
                Log.d(TAG, "Password updated complete")
            }.addOnFailureListener { exception ->
                con.onFailureFB(PWD_UPDATED_FAILURE, exception.message.toString())
                Log.d(TAG, "Password updated complete")
            }
    }

    fun deleteAccount(cartId: String) {
        val user = getUser()
        if (user == null) {
            con.onFailureFB(EMPTY_CODE, "")
        } else {
            deleteAllData(user, cartId)
        }
    }

    private fun deleteAllData(user: FirebaseUser, cartId: String) {
        val userRef = db.collection("Users").document(user.uid)
        val cartRef = db.collection("Cart").document(cartId)
        db.runTransaction { transaction ->
            transaction.delete(userRef)
            transaction.delete(cartRef)
        }.addOnSuccessListener {
                deleteUser(user)
                Log.d(TAG, "All data delete")
            }.addOnFailureListener { exception ->
                con.onFailureFB(DATA_DELETED_FAILURE, exception.message.toString())
                Log.w(TAG, exception)
            }
    }

    private fun deleteUser(user: FirebaseUser) {
        user.delete().addOnSuccessListener {
            logout()
            con.onSuccessFB(DELETE_ACC_OK, null)
            Log.d(TAG, "User delete")
        }.addOnFailureListener { exception ->
            logout()
            con.onFailureFB(DELETE_ACC_FAILURE, exception.message.toString())
            Log.w(TAG, exception)
        }
    }
}