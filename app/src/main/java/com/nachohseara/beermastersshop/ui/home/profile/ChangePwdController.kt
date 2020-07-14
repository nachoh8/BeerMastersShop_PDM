package com.nachohseara.beermastersshop.ui.home.profile

import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.FBModel
import com.nachohseara.beermastersshop.model.db.IFBConnector

class ChangePwdController(mV: ChangePwdFragment) : IFBConnector{
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

    fun onChangePwd() {
        myView.hideKeyboard()
        if (myAct.onlineOrMsg() && !btPressed) {
            btPressed = true
            myAct.startLoading()
            fb.changePwd(myView.getCPwd(), myView.getNPwd1())
        }
    }

    override fun onSuccessFB(cod: Int, data: Any?) {
        when(cod) {
            FBModel.PWD_UPDATED_OK -> {
                myAct.endLoading()
                myView.msgSnackBar(myView.getString(R.string.pwd_ok))
                btPressed = false
                myView.requireActivity().onBackPressed()
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        btPressed = false
        myAct.endLoading()
        when(cod) {
            FBModel.REAUTHENTICATION_FAILURE -> {
                myView.msgSnackBar("Error: $error")
            }
            FBModel.PWD_UPDATED_FAILURE -> {
                myView.msgSnackBar("Error: $error")
            }
            FBModel.EMPTY_CODE -> {
                myView.msgSnackBar(myView.getString(R.string.error_data))
            }
        }
    }
}