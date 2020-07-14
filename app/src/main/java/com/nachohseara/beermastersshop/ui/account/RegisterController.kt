package com.nachohseara.beermastersshop.ui.account

import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.FBModel
import com.nachohseara.beermastersshop.model.db.IFBConnector


class RegisterController(mV: RegisterFragment) : IFBConnector {
    private val myView = mV
    private lateinit var myAct: BaseActivity
    private val fb = FBModel(this)

    private var btPressed = false

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate() {
        myView.loadViews()
    }

    fun onLoginClick() {
        if (!btPressed) {
            myView.requireActivity().onBackPressed()
        }
    }

    fun createAccout() {
        if (checkFields()) {
            myView.hideKeyboard()
            if (myAct.onlineOrMsg() && !btPressed) {
                btPressed = true
                myAct.startLoading()
                fb.createAccount(myView.getEmail(), myView.getPwd())
            }
        }
    }

    private fun checkFields() : Boolean {
        if (myView.getEmail().isEmpty()) {
            myView.errorEmail()
            return false
        }
        if (myView.getPwd().length < 6) {
            myView.errorPwd()
            return false
        }
        return true
    }

    private fun sendEmailVerification() {
        fb.sendEmailVerification()
    }

    override fun onSuccessFB(cod: Int, data: Any?) {
        when(cod) {
            FBModel.ACCOUNT_CREATED -> sendEmailVerification()
            FBModel.EMAIL_VERIFICATION_OK -> {
                end(myView.getString(R.string.user_created_ok))
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        btPressed = false
        when(cod) {
            FBModel.ACCOUNT_FAILURE -> {
                myAct.endLoading()
                myView.msgSnackBar("Error: $error")
            }
            FBModel.EMAIL_VERIFICATION_FAILURE -> {
                end("User created, but verification email has not been sent, try login later")
            }
        }
    }

    private fun end(msg: String) {
        fb.logout()
        myAct.endLoading()
        myView.msgSnackBar(msg)
        myView.requireActivity().onBackPressed()
    }
}