package com.nachohseara.beermastersshop.ui.cart

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity

class AddressController(mV: AddressFragment) {
    private val myView = mV
    private lateinit var myAct : BaseActivity

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }


    fun onContinue() {
        myView.hideKeyboard()
        if (!myAct.onlineOrMsg()) return

        val args = Bundle()
        args.putString("address", myView.getAddress())
        myView.findNavController().navigate(R.id.action_address_to_confirm, args)
    }
}