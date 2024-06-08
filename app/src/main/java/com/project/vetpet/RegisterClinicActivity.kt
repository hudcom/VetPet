package com.project.vetpet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.project.vetpet.databinding.ActivityRegisterClinicBinding
import com.project.vetpet.databinding.FragmentMoreBinding
import com.project.vetpet.model.Clinics
import com.project.vetpet.view.clinic.ClinicActivity
import com.project.vetpet.view.clinic.ClinicViewModel
import com.project.vetpet.view.factory

class RegisterClinicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterClinicBinding
    private val viewModel: ClinicViewModel by viewModels{ factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterClinicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponent()
    }

    private fun initComponent() {
        createToolbar()
        binding.linkToAuth.setOnClickListener { linkToAuthClinicActivity() }
        binding.regBtn.setOnClickListener { addClinicToDB() }
    }

    private fun fieldValidation(): Boolean{
        var isAllFieldFilled = true
        val errorText = getString(R.string.fill_the_field)

        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val name = binding.nameEditText.text.toString().trim()
        val address = binding.addressEditText.text.toString().trim()


        if (email.isBlank()){
            binding.emailEditText.error = errorText
            isAllFieldFilled = false
        }
        if(password.isBlank()){
            binding.passwordEditText.error = errorText
            isAllFieldFilled = false
        }
        if(name.isBlank()){
            binding.nameEditText.error = errorText
            isAllFieldFilled = false
        }
        if(address.isBlank()){
            binding.addressEditText.error = errorText
            isAllFieldFilled = false
        }


        return isAllFieldFilled
    }

    private fun addClinicToDB() {
        if (fieldValidation()){
            startLoadingAnimation()
            val clinic = createClinicObject()
            viewModel.addClinic(clinic){
                stopLoadingAnimation()
                if (it){
                    val intent = Intent(this, ClinicAccountActivity::class.java)
                    intent.putExtra("clinic", clinic)
                    startActivity(intent)
                } else {
                    Toast.makeText(this,"Виникла помилка при реєстрації клініки",Toast.LENGTH_SHORT).show()
                }
            }
        } else{
            Toast.makeText(this,"Заповніть всі поля", Toast.LENGTH_SHORT).show()
        }

    }

    private fun createClinicObject(): Clinics {
        return Clinics(
            email = binding.emailEditText.text.toString().trim(),
            password = binding.passwordEditText.text.toString().trim(),
            name = binding.nameEditText.text.toString().trim(),
            address = binding.addressEditText.text.toString().trim()
        )
    }

    private fun linkToAuthClinicActivity() {
        finalizeActivity()
    }

    private fun createToolbar(){
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.register)
        toolbar.setNavigationOnClickListener {
            finalizeActivity()
        }
    }

    private fun finalizeActivity(){
        finish()
    }

    private fun startLoadingAnimation(){
        binding.registerBlock.visibility = View.INVISIBLE
        binding.regProgressBar.visibility = View.VISIBLE
    }

    private fun stopLoadingAnimation(){
        binding.registerBlock.visibility = View.VISIBLE
        binding.regProgressBar.visibility = View.GONE
    }

}