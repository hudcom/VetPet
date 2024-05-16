package com.project.vetpet.view.tabs.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.project.vetpet.R
import com.project.vetpet.databinding.FragmentMainBinding
import com.project.vetpet.view.BaseFragment
import com.project.vetpet.view.TAG

class MainFragment : BaseFragment() {

    private lateinit var binding: FragmentMainBinding
    private var doubleBackToExitPressedOnce = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSpinnerAdapter()
        setOnBackPressedKeyListener(view)
    }

    // Setting up an event handler for the Back key
    override fun setOnBackPressedKeyListener(view: View) {
        /* Thats a code for set focus in current fragment, not on Activity*/
        view.isFocusableInTouchMode = true
        view.requestFocus()

        view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                Log.d(TAG,"Back button pressed.")

                if (doubleBackToExitPressedOnce) {
                    activity?.finish()
                }

                doubleBackToExitPressedOnce = true
                Toast.makeText(requireContext(), "Натисніть НАЗАД ще раз для виходу", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)

                true
            } else {
                Log.e(TAG,"Error. Back button work incorrectly")
                false
            }
        }
    }


    private fun setSpinnerAdapter(){
        val spinner = binding.categoryFindSpinner

        spinner.adapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.find_category_items,
            R.layout.custom_spinner_adapter
        )
    }

}












