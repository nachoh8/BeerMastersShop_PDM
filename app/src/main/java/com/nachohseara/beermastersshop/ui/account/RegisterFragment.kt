package com.nachohseara.beermastersshop.ui.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseFragment

class RegisterFragment : BaseFragment() {
    private lateinit var inputEmail: EditText
    private lateinit var inputPwd: EditText
    private lateinit var progBar: ProgressBar

    private val controller = RegisterController(this)
    private lateinit var mView: View

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_register, container, false)

        controller.onCreate()

        return mView
    }


    fun loadViews() {
        inputEmail = mView.findViewById(R.id.inputEmail)
        inputPwd = mView.findViewById(R.id.inputPwd)

        progBar = mView.findViewById(R.id.progBar)

        val btRegister: Button = mView.findViewById(R.id.btRegister)
        btRegister.setOnClickListener {
            controller.createAccout()
        }

        val btToLogin: Button = mView.findViewById(R.id.btToLogin)
        btToLogin.setOnClickListener {
            controller.onLoginClick()
        }

    }

    fun getEmail() : String = inputEmail.text.toString()
    fun getPwd() : String = inputPwd.text.toString()

    fun errorEmail() {
        inputEmail.error = getString(R.string.email_is_required)
    }

    fun errorPwd() {
        inputPwd.error = getString(R.string.pwd_is_required)
    }

    fun hideKeyboard() {
        inputEmail.onEditorAction(EditorInfo.IME_ACTION_DONE)
        inputPwd.onEditorAction(EditorInfo.IME_ACTION_DONE)
    }

    override fun getMyView(): View = mView
}