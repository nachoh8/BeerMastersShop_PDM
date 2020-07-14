package com.nachohseara.beermastersshop.base

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment() {
    fun getBaseActivity() : BaseActivity = requireActivity() as BaseActivity

    fun msgSnackBar(msg: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(getMyView(), msg, duration).show()
    }

    fun msgToast(msg: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(requireContext(), msg, duration).show()
    }

    abstract fun getMyView() : View
}