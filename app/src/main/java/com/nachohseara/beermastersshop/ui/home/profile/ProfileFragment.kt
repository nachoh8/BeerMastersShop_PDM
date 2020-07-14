package com.nachohseara.beermastersshop.ui.home.profile

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseFragment
import com.nachohseara.beermastersshop.ui.home.HomeActivity

class ProfileFragment : BaseFragment() {
    private val controller = ProfileController(this)

    private lateinit var mView: View
    private lateinit var txtName: TextView
    private lateinit var txtLastname: TextView
    private lateinit var txtEmail: TextView

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
        mView = inflater.inflate(R.layout.fragment_profile, container, false)

        controller.onCreate()

        return mView
    }

    override fun onResume() {
        (requireActivity() as HomeActivity).setMenu(false)
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            controller.onLogoutClick()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun loadViews() {
        val btMyReviews:Button = mView.findViewById(R.id.btMyReviews)
        btMyReviews.setOnClickListener {
            controller.onMyReviews()
        }

        val btDeleteAcc:Button = mView.findViewById(R.id.btDeleteAcc)
        btDeleteAcc.setOnClickListener {
            controller.onDeleteAccount()
        }

        val btChngPwd:Button = mView.findViewById(R.id.btChangePwd)
        btChngPwd.setOnClickListener {
            controller.onChangePwd()
        }

        val btEditData:Button = mView.findViewById(R.id.btEditData)
        btEditData.setOnClickListener {
            controller.onEditData()
        }
        txtEmail = mView.findViewById(R.id.txtPan)
        txtName = mView.findViewById(R.id.sdfds)
        txtLastname = mView.findViewById(R.id.txtLastname)

        loadInfo()
    }

    private fun loadInfo() {
        txtEmail.text = controller.getEmail()
        txtName.text = controller.getName()
        txtLastname.text = controller.getLastname()
    }

    fun adminUI() {
        val btDeleteAcc:Button = mView.findViewById(R.id.btDeleteAcc)
        btDeleteAcc.visibility = View.GONE
        val btEditData:Button = mView.findViewById(R.id.btEditData)
        btEditData.visibility = View.GONE
        val btMyReviews:Button = mView.findViewById(R.id.btMyReviews)
        btMyReviews.visibility = View.GONE
    }

    fun showChoiceDialog(title: String, msg: String) {
        AlertDialog.Builder(requireContext()).setTitle(title).setMessage(msg)
            .setNegativeButton("No", null)
            .setPositiveButton("Yes") { _, _ -> controller.onLogOut()
            }.show()
    }

    override fun getMyView(): View = mView
}