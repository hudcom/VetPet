package com.project.vetpet.view

import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.vetpet.R

open class BaseFragment: Fragment() {

    /*
    *   view.isFocusableInTouchMode = true
    *   view.requestFocus()
    *   That`s code for set focus in current fragment, not on Activity
    */
    open fun setOnBackPressedKeyListener(view: View) {
        view.isFocusableInTouchMode = true
        view.requestFocus()

        view.setOnKeyListener { _, keyCode, event ->
            if (checkKeyListeners(event,keyCode)) {
                with(activity as MainActivity){
                    findNavController().popBackStack(R.id.mainFragment,false)
                    updateBottomNavigationMenu(R.id.mainFragment)
                }
                true
            } else {
                Log.e(TAG,"Error. Back button work incorrectly")
                false
            }
        }
    }

    private fun checkKeyListeners(event: KeyEvent, keyCode: Int):Boolean =
        event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK

}