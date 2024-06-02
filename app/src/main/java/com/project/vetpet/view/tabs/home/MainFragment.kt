package com.project.vetpet.view.tabs.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.project.vetpet.view.veterinarian.FindVeterinarianActivity
import com.project.vetpet.R
import com.project.vetpet.databinding.FragmentMainBinding
import com.project.vetpet.view.BaseFragment
import com.project.vetpet.view.TAG
import com.project.vetpet.view.factory

class MainFragment : BaseFragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainFragmentViewModel by viewModels{ factory() }
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
        setOnclickListeners(view)
    }

    private fun setOnclickListeners(view:View){
        setOnBackPressedKeyListener(view)
        binding.searchBtn.setOnClickListener { search() }
    }

    private fun search(){
        val category = binding.categoryFindSpinner.selectedItem.toString()
        val findCategoryItems = resources.getStringArray(R.array.find_category_items)

        when(category){
            findCategoryItems[0] -> { showToast("Оберіть категорію пошуку") }
            findCategoryItems[1] -> { findVeterinarian() }
            findCategoryItems[2] -> { findClinic() }
            else -> { Log.e(TAG,"Error with choose category search") }
        }

    }

    private fun findClinic(){
        showToast("В процесі розробки")
    }

    private fun findVeterinarian(){
        startLoadingAnimation()
        val name = binding.searchEditText.text.toString()
        viewModel.searchVeterinarianByName(name,
            onResult = { veterinarians ->
                if (veterinarians!!.isNotEmpty()) {
                    for (veterinarian in veterinarians) {
                        Log.d(TAG, "Found veterinarian: ${veterinarian.fullName}, ${veterinarian.workplace}")
                    }
                } else {
                    Log.d(TAG, "No veterinarian found with the given name.")
                }
                val intent = Intent(requireContext(), FindVeterinarianActivity::class.java)
                intent.putExtra("veterinaries", ArrayList(veterinarians))
                startActivity(intent)
            },
            onError = { exception ->
                Log.e(TAG, "Error searching for veterinarian", exception)
            }
        )
        stopLoadingAnimation()
    }

    private fun setSpinnerAdapter(){
        val spinner = binding.categoryFindSpinner
        spinner.adapter = viewModel.createSpinner(requireActivity())
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

    private fun showToast(message:String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun startLoadingAnimation() {
        binding.progressBar.bringToFront()
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun stopLoadingAnimation() {
        binding.progressBar.visibility = View.GONE
    }

}












