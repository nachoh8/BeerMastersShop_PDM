package com.nachohseara.beermastersshop.ui.account

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseFragment
import com.nachohseara.beermastersshop.ui.home.HomeActivity


class EditDataFragment : BaseFragment() {

    private lateinit var mView: View
    private val controller = EditDataController(this)
    private lateinit var txtTitle: TextView
    private lateinit var txtEditName: EditText
    private lateinit var txtEditLastname: EditText
    private lateinit var progBar: ProgressBar

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
        mView = inflater.inflate(R.layout.fragment_editdata, container, false)

        blockMenu(true)

        if (context is MainActivity) {
            (context as MainActivity).onCreateAcc = true
        }

        loadViews()
        controller.onCreate()

        return mView
    }

    fun blockMenu(block: Boolean) {
        if (context is HomeActivity) {
            (context as HomeActivity).setMenu(block)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.empty, menu)
    }

    fun loadViews() {
        val btSave: Button = mView.findViewById(R.id.btSave)
        btSave.setOnClickListener {
            if (context is MainActivity) {
                (context as MainActivity).onCreateAcc = false
            }
            controller.saveData()
        }

        txtTitle = mView.findViewById(R.id.txtTitle)
        txtEditName = mView.findViewById(R.id.txtEditName)
        txtEditLastname = mView.findViewById(R.id.txtEditLastname)

        progBar = mView.findViewById(R.id.dProgBar)
    }

    fun setInfo(title: String, name: String, lastname: String) {
        txtTitle.text = title
        txtEditName.hint = name
        txtEditLastname.hint = lastname
        if (!controller.onCreateAccount()) {
            txtEditName.setText(name, TextView.BufferType.EDITABLE)
            txtEditLastname.setText(lastname, TextView.BufferType.EDITABLE)
        }
    }

    fun getName() : String = txtEditName.text.toString()
    fun getLastname() : String = txtEditLastname.text.toString()

    fun errorName() {
        txtEditName.error = getString(R.string.name_is_required)
    }

    fun errorLastname() {
        txtEditName.error = getString(R.string.lastname_is_required)
    }


    fun hideKeyboard() {
        txtEditName.onEditorAction(EditorInfo.IME_ACTION_DONE)
        txtEditLastname.onEditorAction(EditorInfo.IME_ACTION_DONE)
    }

    override fun getMyView(): View = mView
}