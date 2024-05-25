package com.project.vetpet.view.dialog

import android.app.Activity
import android.app.AlertDialog
import com.project.vetpet.R


class CustomDialog(private val listener: DialogListener) {

    fun createDialog(activity: Activity, title:String, message:String) {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        builder.setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton("Так") { _, _ ->
                listener.onPositiveClick()
            }
            .setNegativeButton("Ні") { _, _ ->
                listener.onNegativeClick()
            }
            .show()
    }
}

interface DialogListener {
    fun onPositiveClick()
    fun onNegativeClick()
}