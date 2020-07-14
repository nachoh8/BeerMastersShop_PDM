package com.nachohseara.beermastersshop.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class Permission {
    companion object {
        const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1

        fun checkPermissionReadExternalStorage(ctxt: Context) : Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(ctxt, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((ctxt as Activity),
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showDialog(ctxt,"External storage", Manifest.permission.READ_EXTERNAL_STORAGE)
                    } else {
                        ActivityCompat.requestPermissions(ctxt, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                    }
                    false
                } else {
                    true
                }
            } else {
                true
            }
        }

        private fun showDialog(ctxt: Context, msg: String, permission: String) {
            val builder = AlertDialog.Builder(ctxt)
            builder.setCancelable(true)
            builder.setTitle("Permission necessary")
            builder.setMessage("$msg permission is necessary")
            builder.setPositiveButton("Accept") { dialog, which ->
                ActivityCompat.requestPermissions((ctxt as Activity), arrayOf(permission), MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            }
            val alert: AlertDialog = builder.create()
            alert.show()
        }
    }
}