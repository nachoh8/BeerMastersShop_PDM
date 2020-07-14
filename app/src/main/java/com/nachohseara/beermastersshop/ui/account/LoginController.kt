package com.nachohseara.beermastersshop.ui.account

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.FBModel
import com.nachohseara.beermastersshop.model.db.IFBConnector
import com.nachohseara.beermastersshop.model.entity.User
import com.nachohseara.beermastersshop.model.entity.UserData

class LoginController(mV: LoginFragment) : IFBConnector{

    private val myView = mV
    private lateinit var myAct: BaseActivity
    private val fb = FBModel(this)

    private var btPressed = false

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate() {
        if (!myAct.notOnlineAndGo()) {
            checkIsLogin()
        }
    }

    private fun checkIsLogin() {
        if (fb.isLogin()) {
            if (fb.isVerified()) {
                fb.hasData()
            } else {
                fb.logout()
                myView.setLoginLayout(true)
            }
        } else {
            myView.setLoginLayout(true)
        }
    }

    fun onLogin() {
        if (checkFields()) {
            myView.hideKeyboard()
            if (!btPressed && myAct.onlineOrMsg()) {
                btPressed = true
                myAct.startLoading()
                fb.login(myView.getEmail(), myView.getPwd())
            }
        }
    }

    fun sendEmailVerification() {
        if (myAct.onlineOrMsg()) {
            myAct.startLoading()
            fb.sendEmailVerification()
        }
    }

    fun onForgotPwd() {
        myView.alertDialogResetPwd()
    }

    fun resetPwd(email: String) {
        myAct.startLoading()
        fb.sendResetPwd(email)
    }

    fun onRegisterClick() {
        if (myAct.onlineOrMsg() && !btPressed) myView.findNavController().navigate(R.id.action_login_to_register)
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

    /// Database operations
    override fun onSuccessFB(cod: Int, data: Any?) {
        when(cod) {
            FBModel.HAS_DATA_OK -> {
                myAct.endLoading()
                if (data != null && data is UserData) {
                    val user = User(myView.requireContext())
                    user.setFromData(data)
                    myView.findNavController().navigate(R.id.action_login_to_home)
                    myView.requireActivity().finish()
                } else {
                    btPressed = false
                    val args = Bundle()
                    args.putString("activity", "account")
                    myView.findNavController().navigate(R.id.action_login_to_editdata, args)
                }
            }
            FBModel.LOGIN_OK -> {
                if (fb.isVerified()) {
                    fb.hasData()
                } else {
                    btPressed = false
                    myAct.endLoading()
                    myView.emailSnackBar(myView.getString(R.string.msg_send_email), myView.getString(R.string.action_send_email))
                }
            }
            FBModel.EMAIL_VERIFICATION_OK -> {
                btPressed = false
                fb.logout()
                myAct.endLoading()
                myView.msgSnackBar(myView.getString(R.string.verification_email_sent))
            }
            FBModel.SEND_RESET_PWD_OK -> {
                myAct.endLoading()
                myView.msgSnackBar(myView.getString(R.string.send_reset_pwd_ok))
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        myAct.endLoading()
        btPressed = false
        when(cod) {
            FBModel.LOGIN_FAILURE -> {
                myView.msgSnackBar("Error: $error")
            }
            FBModel.HAS_DATA_FAILURE -> {
                fb.logout()
                myView.setLoginLayout(true)
                myView.msgSnackBar("Error: $error")
            }
            FBModel.EMAIL_VERIFICATION_FAILURE -> {
                fb.logout()
                myView.msgSnackBar("Error: $error")
            }
            FBModel.SEND_RESET_PWD_FAILURE -> {
                myView.msgSnackBar("Error: $error")
            }
            FBModel.EMPTY_CODE -> {
                fb.logout()
                myView.setLoginLayout(true)
                myView.msgSnackBar(myView.getString(R.string.error_data))
            }
        }
    }
}