package com.project.vetpet.view.tabs.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.vetpet.R
import com.project.vetpet.databinding.FragmentRegisterBinding
import com.project.vetpet.view.BaseFragment
import com.project.vetpet.view.TAG
import com.project.vetpet.view.factory


class RegisterFragment : BaseFragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels{ factory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setOnBackPressedKeyListener(view)
    }

    private fun setClickListeners() {
        binding.linkToAuth.setOnClickListener { findNavController().popBackStack() }
        binding.regBtn.setOnClickListener {
            hideKeyboard(requireView())
            onRegButtonPressed()
        }
    }

    private fun onRegButtonPressed(){
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val repeatPassword = binding.passwordRepeatEditText.text.toString().trim()

        if (fieldValidation(email, password, repeatPassword)) {
            if (password == repeatPassword){
                startLoadingAnimation()
                registerUser(email,password)
            }
            else{
                Toast.makeText(requireContext(), "Паролі не співпадають $password $repeatPassword", Toast.LENGTH_SHORT).show()
                Log.d(TAG,"The passwords do not match ")
            }
        } else
            Toast.makeText(requireContext(), "Заповніть всі поля", Toast.LENGTH_SHORT).show()
            Log.d(TAG,"Not all fields are filled in")
    }

    private fun fieldValidation(email: String, password: String, repeatPassword: String): Boolean{
        var isAllFieldFilled = true
        val errorText = getString(R.string.fill_the_field)

        if (email.isBlank()){
            binding.emailEditText.error = errorText
            isAllFieldFilled = false
        }

        if (password.isBlank()){
            binding.passwordEditText.error = errorText
            isAllFieldFilled = false
        }

        if (repeatPassword.isBlank()){
            binding.passwordRepeatEditText.error = errorText
            isAllFieldFilled = false
        }

        return isAllFieldFilled
    }

    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun registerUser(email: String, password: String){
        Log.d(TAG,"Register user in DB.")
        viewModel.registerUser(email,password){
            Toast.makeText(requireContext(), "Користувач створений", Toast.LENGTH_SHORT).show()
            Log.d(TAG,"User created")
            stopLoadingAnimation()
            findNavController().navigate(R.id.action_registerFragment_to_accountFragment)
        }



    }

    private fun startLoadingAnimation(){
        binding.regProgressBar.visibility = View.VISIBLE
        binding.registerBlock.visibility = View.INVISIBLE
    }

    private fun stopLoadingAnimation(){
        binding.regProgressBar.visibility = View.GONE
        binding.registerBlock.visibility = View.VISIBLE
    }
}












