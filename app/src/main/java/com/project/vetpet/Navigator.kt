package com.project.vetpet

interface Navigator {

    fun goBack()

    fun navigateTo(fragmentId: Int): Boolean

    fun writeLog(text:String)
}