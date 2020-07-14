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
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseFragment
import com.nachohseara.beermastersshop.ui.home.HomeActivity

class PwdFragment : BaseFragment() {
    private lateinit var mView: View
    private val controller = PwdController(this)
    lateinit var inActivity: String

    private lateinit var ePwd: EditText
    private lateinit var progBar: ProgressBar

    override fun getMyView(): View = mView

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
        mView = inflater.inflate(R.layout.fragment_pwd, container, false)
        inActivity = if (arguments != null) {
            requireArguments().getString("goto", "")
        } else {
            ""
        }

        blockMenu(true)

        requireActivity().title = "Authentication"

        controller.onCreate()

        return mView
    }

    private fun blockMenu(block: Boolean) {
        if (context is HomeActivity) {
            (context as HomeActivity).setMenu(block)
        }
    }

    fun loadViews() {
        ePwd = mView.findViewById(R.id.inputPwd)

        progBar = mView.findViewById(R.id.progBar)

        val btContinue: Button = mView.findViewById(R.id.btContinue)
        btContinue.setOnClickListener {
            controller.onContinue()
        }
    }

    fun getPwd() : String = ePwd.text.toString()
    fun errorPwd() {
        ePwd.error = getString(R.string.pwd_is_required)
    }

    fun hideKeyboard() {
        ePwd.onEditorAction(EditorInfo.IME_ACTION_DONE)
    }
}