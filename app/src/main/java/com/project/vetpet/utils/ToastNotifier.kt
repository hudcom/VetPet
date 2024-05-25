package com.project.vetpet.utils

interface ToastNotifier {
    fun showToast(message: String)

    fun writeLogs(message: String, type: TypeLogs = TypeLogs.INFO)
}

