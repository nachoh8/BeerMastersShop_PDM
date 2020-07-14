package com.nachohseara.beermastersshop.ui.account

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseFragment

class LoginFragment : BaseFragment() {

    private val controller = LoginController(this)

    private lateinit var mView: View
    private lateinit var inputEmail: EditText
    private lateinit var inputPwd: EditText
    private lateinit var progBar: ProgressBar

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_login, container, false)

        loadViews()
        controller.onCreate()

        return mView
    }

    fun loadViews() {
        inputEmail = mView.findViewById(R.id.inputEmail)
        inputPwd = mView.findViewById(R.id.inputPwd)
        progBar = mView.findViewById(R.id.progBar)

        val btLogin: Button = mView.findViewById(R.id.btLogin)
        btLogin.setOnClickListener {
            controller.onLogin()
        }
        val btForgotPwd: Button = mView.findViewById(R.id.btForgotPwd)
        btForgotPwd.setOnClickListener {
            controller.onForgotPwd()
        }
        val btToRegister: Button = mView.findViewById(R.id.btToRegister)
        btToRegister.setOnClickListener {
            controller.onRegisterClick()
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

    fun setLoginLayout(set: Boolean) {
            val loginLayout: ConstraintLayout = mView.findViewById(R.id.loginLayout)
            //val splashLayout: ConstraintLayout = mView.findViewById(R.id.splashLayout)
            if (set) {
                loginLayout.visibility = View.VISIBLE
                //splashLayout.visibility = View.GONE
            } else {
                //splashLayout.visibility = View.VISIBLE
                loginLayout.visibility = View.GONE
            }
    }

    fun hideKeyboard() {
        inputEmail.onEditorAction(EditorInfo.IME_ACTION_DONE)
        inputPwd.onEditorAction(EditorInfo.IME_ACTION_DONE)
    }

    fun emailSnackBar(msg: String, action: String) {
        Snackbar.make(mView, msg, Snackbar.LENGTH_LONG).setActionTextColor(resources.getColor(R.color.snackbar_action))
            .setAction(action) {
                controller.sendEmailVerification()
            }.show()
    }

    fun alertDialogResetPwd() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.forgot_pwd))
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        input.hint = getString(R.string.t_email)
        builder.setView(input)
        builder.setPositiveButton("Send Reset Link") { dialog, which ->
            val email = input.text.toString()
            if (email.isEmpty()) {
                input.error = getString(R.string.email_is_required)
            } else {
                controller.resetPwd(email)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    override fun getMyView() : View = mView
}