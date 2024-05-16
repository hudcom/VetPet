package com.project.vetpet.view.dialog

import android.app.Activity
import android.app.AlertDialog


class CloseDialog(private val listener: DialogListener) {

    fun createDialog(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Підтвердження")
            .setMessage("Ви дійсно хочете вийти з аккаунту?")
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