package com.nachohseara.beermastersshop.ui.cart

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseFragment

class AddressFragment : BaseFragment() {
    private lateinit var mView: View
    private val controller = AddressController(this)

    private lateinit var eAddress: EditText

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_address, container, false)
        eAddress = mView.findViewById(R.id.eAddress)

        val btContinue: Button = mView.findViewById(R.id.btContinue)
        btContinue.setOnClickListener {
            if (checkFields()) controller.onContinue()
        }

        requireActivity().title = "Shipping Address"

        return mView
    }

    private fun checkFields() : Boolean {
        if (getAddress().isEmpty()) {
            eAddress.error = "Address required"
            return false
        }
        return true
    }

    fun getAddress() : String = eAddress.text.toString()

    fun hideKeyboard() {
        eAddress.onEditorAction(EditorInfo.IME_ACTION_DONE)
    }

    override fun getMyView(): View = mView
}