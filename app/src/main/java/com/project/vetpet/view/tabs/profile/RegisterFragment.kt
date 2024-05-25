package com.project.vetpet.view.tabs.profile

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.project.vetpet.R
import com.project.vetpet.databinding.FragmentRegisterBinding
import com.project.vetpet.utils.ToastNotifier
import com.project.vetpet.utils.TypeLogs
import com.project.vetpet.view.BaseFragment
import com.project.vetpet.view.TAG
import com.project.vetpet.view.factory


class RegisterFragment : BaseFragment(), ToastNotifier {

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
        createNotifier()
        setOnBackPressedKeyListener(view)
    }

    private fun setClickListeners() {
        binding.linkToAuth.setOnClickListener { findNavController().popBackStack() }
        binding.regBtn.setOnClickListener {
            hideKeyboard(requireView())
            onRegButtonPressed()
        }

        setOnToggleButtonClickAction(binding.passwordToggle,binding.passwordEditText)
        setOnToggleButtonClickAction(binding.passwordRepeatToggle,binding.passwordRepeatEditText)
    }

    private fun setOnToggleButtonClickAction(toggle:ImageButton ,passwordEditText: TextInputEditText){
        toggle.setOnClickListener {
            // Перемикання видимості пароля
            val inputType = if (passwordEditText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            passwordEditText.inputType = inputType

            // Переміщення курсора в кінець тексту
            passwordEditText.setSelection(passwordEditText.text?.length ?: 0)
        }
    }

    private fun onRegButtonPressed(){
        val email = binding.emailEditText.text.toString().trim()
        val name = binding.nameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val repeatPassword = binding.passwordRepeatEditText.text.toString().trim()

        if (fieldValidation(email,name, password, repeatPassword)) {
            if (password == repeatPassword){
                startLoadingAnimation()
                registerUser(email,name,password)
            }
            else{
                showToast("Паролі не співпадають")
                writeLogs("The passwords do not match ")
            }
        } else {
            showToast("Заповніть всі поля")
            writeLogs("Not all fields are filled in")
        }
    }

    private fun fieldValidation(email: String, name: String, password: String, repeatPassword: String): Boolean{
        var isAllFieldFilled = true
        val errorText = getString(R.string.fill_the_field)

        if (email.isBlank()){
            binding.emailEditText.error = errorText
            isAllFieldFilled = false
        }

        if (name.isBlank()){
            binding.nameEditText.error = errorText
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

    private fun registerUser(email: String, name:String, password: String){
        viewModel.registerUser(email,name,password){
            if (it){
                findNavController().navigate(R.id.accountFragment)
            }
            stopLoadingAnimation()
        }
    }

    private fun createNotifier(){
        // Передаємо інтерфейс у ViewModel
        viewModel.toastNotifier = this
    }

    private fun startLoadingAnimation(){
        binding.regProgressBar.visibility = View.VISIBLE
        binding.registerBlock.visibility = View.INVISIBLE
    }

    private fun stopLoadingAnimation(){
        binding.regProgressBar.visibility = View.GONE
        binding.registerBlock.visibility = View.VISIBLE
    }

    /**
     *
     * Override methods
     *
     */
    override fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun writeLogs(message: String, type: TypeLogs) {
        when(type){
            TypeLogs.INFO -> Log.d(TAG,message)
            TypeLogs.ERROR -> Log.e(TAG,message)
            TypeLogs.WARNING -> Log.w(TAG,message)
        }
    }
}












