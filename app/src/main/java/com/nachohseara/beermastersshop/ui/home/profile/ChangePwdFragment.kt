package com.nachohseara.beermastersshop.ui.home.profile

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseFragment
import com.nachohseara.beermastersshop.ui.home.HomeActivity

class ChangePwdFragment : BaseFragment() {
    private lateinit var mView: View
    private val controller = ChangePwdController(this)

    private lateinit var eCPwd: EditText
    private lateinit var eNPwd1: EditText
    private lateinit var eNPwd2: EditText

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_changepwd, container, false)

        blockMenu(true)

        controller.onCreate()

        return mView
    }

    private fun blockMenu(block: Boolean) {
        if (context is HomeActivity) {
            (context as HomeActivity).setMenu(block)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.empty, menu)
    }

    fun loadViews() {
        eCPwd = mView.findViewById(R.id.inputCurrentPwd)
        eNPwd1 = mView.findViewById(R.id.inputNewPwd1)
        eNPwd2 = mView.findViewById(R.id.inputNewPwd2)

        val btSavePwd: Button = mView.findViewById(R.id.btSave)
        btSavePwd.setOnClickListener {

            if (checkFields()) {
                controller.onChangePwd()
            }
        }
    }

    private fun checkFields() : Boolean {
        if (getCPwd().isEmpty()) {
            eCPwd.error = "Current Password is required"
            return false
        }
        if (getNPwd1().length < 6) {
            eNPwd1.error = "Password must be at least 6 characters"
            return false
        }
        if (getCPwd() == getNPwd1()) {
            eNPwd1.error = "La contraseña no puede ser igual que la actual"
            return false
        }
        if (getNPwd1() != getNPwd2()) {
            eNPwd2.error = "The password does not match"
            return false
        }

        return true
    }

    fun getCPwd() :  String = eCPwd.text.toString()
    fun getNPwd1() :  String = eNPwd1.text.toString()
    fun getNPwd2() :  String = eNPwd2.text.toString()

    fun hideKeyboard() {
        eCPwd.onEditorAction(EditorInfo.IME_ACTION_DONE)
        eNPwd1.onEditorAction(EditorInfo.IME_ACTION_DONE)
        eNPwd2.onEditorAction(EditorInfo.IME_ACTION_DONE)
    }

    override fun getMyView(): View = mView
}