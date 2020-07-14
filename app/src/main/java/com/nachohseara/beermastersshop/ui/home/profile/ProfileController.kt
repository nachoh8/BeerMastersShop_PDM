package com.nachohseara.beermastersshop.ui.home.profile

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.FBModel
import com.nachohseara.beermastersshop.model.entity.User

class ProfileController(mV: ProfileFragment)  {
    private val myView = mV
    private lateinit var myAct: BaseActivity
    private lateinit var user: User

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate() {
        user = User(myView.requireContext())
        myView.loadViews()
        if (user.isAdmin()) myView.adminUI()
    }

    fun getEmail() : String = user.getEmail()

    fun getName() : String = user.getName()

    fun getLastname() : String = user.getLastname()

    fun onChangePwd() {
        if (online()) myView.findNavController().navigate(R.id.action_profile_to_change_pwd)
    }

    fun onEditData() {
        if (online()) myView.findNavController().navigate(R.id.action_profile_to_editdata)
    }

    fun onDeleteAccount() {
        if (!online()) return
        val args = Bundle()
        args.putString("goto", "delacc")
        myView.findNavController().navigate(R.id.action_profile_to_pwd, args)
    }

    fun onMyReviews() {
        if (myAct.onlineOrMsg()) {
            val args = Bundle()
            args.putBoolean("userReview", true)
            myView.findNavController().navigate(R.id.action_nav_profile_to_reviewListFragment, args)
        }
    }

    fun onLogoutClick() {
        myView.showChoiceDialog("Are you sure you want to exit?", "")
    }

    fun onLogOut() {
        User(myView.requireContext()).deleteData()
        FBModel.logOut()
        myView.findNavController().navigate(R.id.action_nav_profile_to_mainActivity)
        myView.requireActivity().finish()
    }

    fun online() : Boolean {
        if (myAct.isOnline()) {
            return true
        }
        myView.msgSnackBar(myView.getString(R.string.no_connection))
        return false
    }
}