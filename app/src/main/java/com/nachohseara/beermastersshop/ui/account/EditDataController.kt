package com.nachohseara.beermastersshop.ui.account

import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.FBModel
import com.nachohseara.beermastersshop.model.db.IFBConnector
import com.nachohseara.beermastersshop.model.entity.User
import com.nachohseara.beermastersshop.model.entity.UserData
import java.util.HashMap

class EditDataController(mV: EditDataFragment) : IFBConnector {
    private val myView = mV
    private lateinit var myAct :  BaseActivity
    private lateinit var user: User
    private val fb = FBModel(this)

    private var btPressed = false
    private var preName = ""
    private var preLastname = ""

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate() {
        user = User(myView.requireContext())
        loadDataUser()
    }

    private fun loadDataUser() {
        val title: String
        val name: String
        val lastname: String

        if (onCreateAccount()) {
            title = myView.getString(R.string.welcome_tittle)
            name = myView.getString(R.string.t_name)
            lastname = myView.getString(R.string.t_lastname)
        } else {
            title = myView.getString(R.string.edit_data_tittle)
            name = user.getName()
            preName = name
            lastname = user.getLastname()
            preLastname = lastname
        }

        myView.setInfo(title, name, lastname)
    }

    fun saveData() {
        if (checkFields()) {
            myView.hideKeyboard()
            if (myAct.onlineOrMsg() && !btPressed) {
                btPressed = true
                myAct.startLoading()

                val items = HashMap<String, Any>()
                items["email"] = fb.getEmail()
                items["name"] = myView.getName()
                items["lastname"] = myView.getLastname()

                if (onCreateAccount()) {
                    createAccout(items)
                } else {
                    updateAccount(items)
                }
            }
        }
    }

    private fun checkFields() : Boolean {
        val name = myView.getName()
        val lastname = myView.getLastname()

        if (name.isEmpty()) {
            myView.errorName()
            return false
        }
        if (lastname.isEmpty()) {
            myView.errorLastname()
            return false
        }

        if (!onCreateAccount()) {
            if (name == preName && lastname == preLastname) {
                myView.msgSnackBar(myView.getString(R.string.no_changes))
                return false
            }
        }

        return true
    }

    private fun createAccout(items: HashMap<String, Any>) {
        fb.addPersonalData(items)
    }

    private fun updateAccount(items: HashMap<String, Any>) {
        fb.updatePersonalData(items)
    }

    fun onCreateAccount() : Boolean = myAct.getActName() == BaseActivity.MAIN_ACT

    override fun onSuccessFB(cod: Int, data: Any?) {
        myAct.endLoading()
        btPressed = false
        when(cod) {
            FBModel.DATA_UPLOAD_OK ->{
                if (data is UserData) user.setFromData(data)
                myView.findNavController().navigate(R.id.action_editdata_to_home)
            }
            FBModel.DATA_UPDATE_OK -> {
                user.changeInfo(myView.getName(), myView.getLastname())
                myView.msgSnackBar(myView.getString(R.string.data_ok))
                myView.requireActivity().onBackPressed()
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        btPressed = false
        myAct.endLoading()
        when(cod) {
            FBModel.EMPTY_CODE -> {
                myView.msgSnackBar(myView.getString(R.string.error_data))
            }
            FBModel.DATA_UPLOAD_FAILURE -> {
                myView.msgSnackBar(myView.getString(R.string.error_data))
            }
            FBModel.DATA_UPDATE_FAILURE -> {
                myView.msgSnackBar(myView.getString(R.string.error_data))
            }
        }
    }
}