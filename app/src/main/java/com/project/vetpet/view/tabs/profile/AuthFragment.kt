package com.project.vetpet.view.tabs.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.vetpet.R
import com.project.vetpet.databinding.FragmentAuthBinding
import com.project.vetpet.view.BaseFragment
import com.project.vetpet.view.TAG
import com.project.vetpet.view.factory


class AuthFragment : BaseFragment(){

    private lateinit var binding: FragmentAuthBinding
    private val viewModel: AuthViewModel by viewModels{ factory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()
        setOnBackPressedKeyListener(view)
        writeLogs("[onViewCreated]: Auth fragment")
    }

    private fun setOnClickListeners() {
        binding.passwordEditText.setOnEditorActionListener{v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onAuthButtonPressed()
                hideKeyboard(v)
                true
            } else {
                false
            }
        }

        binding.authBtn.setOnClickListener {
            hideKeyboard(requireView())
            onAuthButtonPressed()
        }
        binding.linkToReg.setOnClickListener { findNavController().navigate(R.id.registerFragment) }
    }

    /*
    * Methods for Auth
    */
    private fun onAuthButtonPressed(){
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if(fieldValidation(email, password)){
            startLoadingAnimation()
            loginUser(email,password)
        } else
           writeLogs("Not all fields are filled in")
    }

    private fun fieldValidation(login:String, password:String): Boolean{
        var isAllFieldFilled = true
        val errorText = getString(R.string.fill_the_field)

        if(login.isBlank()){
            binding.emailEditText.error = errorText
            isAllFieldFilled = false
        }
        if(password.isBlank()){
            binding.passwordEditText.error = errorText
            isAllFieldFilled = false
        }

        return isAllFieldFilled
    }

    private fun loginUser(login:String, password:String){
        writeLogs( "[AuthFragment] Check entered login and password in DB")
        // Завантаження даних та виклик колбека після завершення операції
        viewModel.loginUser(login,password) {
            checkUser()
            stopLoadingAnimation()
        }
    }

    private fun checkUser(){
        if (viewModel.hasCurrentUser()){
            writeLogs("Authorized")
            showShortToast("Користувач авторизований")
            findNavController().navigate(R.id.accountFragment)
        }
        else {
            showShortToast("Невірний логін або пароль")
            writeLogs("[checkUser] Authorization Error")
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /*
    * Animation Methods
    * */
    private fun startLoadingAnimation() {
        with(binding){
            progressBar.visibility = View.VISIBLE
            authBlock.visibility = View.INVISIBLE
        }
    }
    private fun stopLoadingAnimation() {
        with(binding){
            progressBar.visibility = View.GONE
            authBlock.visibility = View.VISIBLE
        }
    }

    /*
    * Support methods
    * */
    private fun writeLogs(text:String){
        Log.d(TAG,text)
    }

    private fun showShortToast(text:String){
        Toast.makeText(requireContext(),text,Toast.LENGTH_SHORT).show()
    }
}