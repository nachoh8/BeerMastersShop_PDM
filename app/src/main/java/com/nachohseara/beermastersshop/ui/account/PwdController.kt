package com.nachohseara.beermastersshop.ui.account

import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.FBModel
import com.nachohseara.beermastersshop.model.db.IFBConnector

class PwdController(mV: PwdFragment) : IFBConnector {
    private val myView = mV
    private lateinit var myAct : BaseActivity
    private val fb = FBModel(this)

    private var btPressed = false

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate() {
        myView.loadViews()
    }

    fun onContinue() {
        if (checkFields()) {
            myView.hideKeyboard()
            if (myAct.onlineOrMsg() && !btPressed) {
                btPressed = true
                myAct.startLoading()
                fb.reauthentication(myView.getPwd())
            }
        }
    }

    private fun checkFields() : Boolean {
        if (myView.getPwd().length < 6) {
            myView.errorPwd()
            return false
        }
        return true
    }

    override fun onSuccessFB(cod: Int, data: Any?) {
        if (cod == FBModel.REAUTHENTICATION_OK) {
            myAct.endLoading()
            btPressed = false
            when(myView.inActivity) {
                "address" -> myView.findNavController().navigate(R.id.action_pwd_to_address)
                "delacc" -> {
                    myView.findNavController().navigate(R.id.action_pwd_to_delete_acc)
                    myView.requireActivity().finish()
                }
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
       myAct.endLoading()
        btPressed = false
        when(cod) {
            FBModel.EMPTY_CODE -> {
                myView.msgSnackBar(myView.getString(R.string.error_data))
            }
            FBModel.REAUTHENTICATION_FAILURE -> {
                myView.msgSnackBar("Error: $error")
            }
        }
    }
}