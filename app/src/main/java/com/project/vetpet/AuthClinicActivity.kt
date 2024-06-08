package com.project.vetpet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.project.vetpet.databinding.ActivityAuthClinicBinding
import com.project.vetpet.databinding.ActivityRegisterClinicBinding
import com.project.vetpet.view.clinic.ClinicViewModel
import com.project.vetpet.view.factory

class AuthClinicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthClinicBinding
    private val viewModel: ClinicViewModel by viewModels{ factory() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthClinicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponent()
    }

    private fun initComponent() {
        createToolbar()
        binding.linkToReg.setOnClickListener { linkToRegClinicActivity() }
        binding.authBtn.setOnClickListener { authBtnClickListener() }
    }

    private fun fieldValidation(): Boolean{
        var isAllFieldFilled = true
        val errorText = getString(R.string.fill_the_field)

        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (email.isBlank()){
            binding.emailEditText.error = errorText
            isAllFieldFilled = false
        }
        if(password.isBlank()){
            binding.passwordEditText.error = errorText
            isAllFieldFilled = false
        }
        return isAllFieldFilled
    }

    private fun authBtnClickListener() {
        if (fieldValidation()){
            startLoadingAnimation()
            viewModel.authClinic(
                binding.emailEditText.text.toString().trim(),
                binding.passwordEditText.text.toString().trim()
            ){ result, clinic ->
                if (result){
                    val intent = Intent(this, ClinicAccountActivity::class.java)
                    intent.putExtra("clinic", clinic)
                    startActivity(intent)
                    stopLoadingAnimation()
                } else {
                    Toast.makeText(this,"Виникла помилка при авторизації клініки", Toast.LENGTH_SHORT).show()
                    stopLoadingAnimation()
                }
            }
        }

    }

    private fun linkToRegClinicActivity() {
        val intent = Intent(this, RegisterClinicActivity::class.java)
        startActivity(intent)
    }

    private fun createToolbar(){
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.authentication)
        toolbar.setNavigationOnClickListener {
            finalizeActivity()
        }
    }

    private fun finalizeActivity(){
        finish()
    }

    private fun startLoadingAnimation(){
        binding.authBlock.visibility = View.INVISIBLE
        binding.authProgressBar.visibility = View.VISIBLE
    }

    private fun stopLoadingAnimation(){
        binding.authBlock.visibility = View.VISIBLE
        binding.authProgressBar.visibility = View.GONE
    }

}