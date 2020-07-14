package com.nachohseara.beermastersshop.ui.account

import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.model.db.DBCart
import com.nachohseara.beermastersshop.model.db.FBModel
import com.nachohseara.beermastersshop.model.db.IFBConnector
import com.nachohseara.beermastersshop.model.entity.User
import com.nachohseara.beermastersshop.ui.home.HomeActivity

class DeleteAccountController(mV: DeleteAccountActivity) : IFBConnector {
    private val myView = mV
    private val fb = FBModel(this)

    private var btPressed = false

    fun onCreate() {
        if (!myView.notOnlineAndGo()) {
            myView.loadViews()
        }
    }

    fun onConfirm() {
        if (myView.onlineOrMsg() && !btPressed) myView.showChoiceDialog(myView.getString(R.string.dialog_tittle_delete_acc)
            ,myView.getString(R.string.dialog_msg_delete_acc))
    }

    fun onPositiveBtDialog() {
        if (myView.onlineOrMsg() && !btPressed) {
            btPressed = true
            myView.startLoading()
            deleteAccount()
        }
    }

    private fun deleteAccount() {
        DBCart(this, User(myView).getCartId()).clearCart()
        fb.deleteAccount(User(myView).getCartId())
    }

    private fun delLocalData() {
        User(myView).deleteData()
    }

    private fun goToHome() {
        myView.openActivity(MainActivity::class.java, true)
    }

    fun onCancel() {
        myView.openActivity(HomeActivity::class.java, true)
    }

    override fun onSuccessFB(cod: Int, data: Any?) {
        if (cod == FBModel.DELETE_ACC_OK) {
            delLocalData()
            myView.endLoading()
            myView.msgSnackBar("Account Deleted Succesfully")
            goToHome()
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        btPressed = false
        myView.endLoading()
        when(cod) {
            FBModel.EMPTY_CODE -> {
                myView.msgSnackBar(myView.getString(R.string.error_data))
            }
            FBModel.REAUTHENTICATION_FAILURE -> {
                myView.msgSnackBar("Error: $error")
            }
            FBModel.DATA_DELETED_FAILURE -> {
                myView.msgSnackBar("Error: $error")
            }
            FBModel.DELETE_ACC_FAILURE -> {
                delLocalData()
                myView.msgSnackBar("Error: $error")
                goToHome()
            }
        }
    }

}