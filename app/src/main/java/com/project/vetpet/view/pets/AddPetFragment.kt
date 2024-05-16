package com.project.vetpet.view.pets

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.vetpet.R
import com.project.vetpet.databinding.FragmentAddPetBinding
import com.project.vetpet.view.TAG
import com.project.vetpet.view.factory

class AddPetFragment : Fragment() {

    private lateinit var binding: FragmentAddPetBinding
    private val viewModel: AddPetViewModel by viewModels{ factory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPetBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTypeSpinner()
        initAgeSpinner()
        setOnClickListeners()
    }

    private fun setOnClickListeners(){
        binding.addPetBtn.setOnClickListener { addPetInDB() }
    }

    private fun addPetInDB(){
        if (fieldValidation()){
            startLoadAnimation()
            createPet()

            Handler(Looper.getMainLooper()).postDelayed({
                stopLoadAnimation()
                Log.d(TAG, "Register pet in db complete")
                findNavController().popBackStack()
            },2000)

        } else
            Log.d(TAG,"Not all fields are filled in")
    }

    private fun fieldValidation():Boolean{
        var isAllFieldFilled = true
        val name = binding.petNameEditText
        val breed = binding.petBreedEditText

        val age = binding.petAge.selectedItem.toString()
        val type = binding.petType.selectedItem.toString()
        val errorText = getString(R.string.fill_the_field)

        if (name.text?.trim()?.isBlank() == true) { name.error = errorText; isAllFieldFilled = false}
        if (breed.text?.trim()?.isBlank() == true) { breed.error = errorText; isAllFieldFilled = false}


        if (!checkAge(age)) isAllFieldFilled = false
        if (!checkType(type)) isAllFieldFilled = false

        return isAllFieldFilled
    }

    private fun checkType(type: String): Boolean{
        var checkType = true
        if (type == getString(R.string.choose)) {
            Toast.makeText(requireContext(),"Оберіть вид вашого домашнього улюбленця.",Toast.LENGTH_LONG).show()
            checkType = false
        }
        return checkType
    }

    private fun checkAge(age: String): Boolean{
        var checkType = true
        if (age == "0") {
            Toast.makeText(requireContext(),"Оберіть вік вашого домашнього улюбленця.",Toast.LENGTH_LONG).show()
            checkType = false
        }
        return checkType
    }

    private fun createPet(){
        viewModel.addPet(
            name = binding.petNameEditText.text.toString(),
            age = Integer.parseInt(binding.petAge.selectedItem.toString()),
            type = binding.petType.selectedItem.toString(),
            breed = binding.petBreedEditText.text.toString()
        )
    }

    private fun initTypeSpinner(){
        val spinner = binding.petType
        spinner.adapter = viewModel.createTypeSpinner(requireActivity())
    }

    private fun initAgeSpinner(){
        val spinner = binding.petAge
        spinner.adapter = viewModel.createAgeSpinner(requireActivity())
    }

    private fun startLoadAnimation(){
        binding.addPetProgressBar.visibility = View.VISIBLE
        binding.addPetBtn.isEnabled = false
        binding.addPetBtn.background.alpha = 128
    }

    private fun stopLoadAnimation(){
        binding.addPetProgressBar.visibility = View.GONE
        binding.addPetBtn.isEnabled = true
        binding.addPetBtn.background.alpha = 255
    }
}