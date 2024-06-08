package com.project.vetpet.view.clinicfunctional

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.project.vetpet.databinding.ActivityAddVeterinarianBinding
import com.project.vetpet.model.Clinics
import com.project.vetpet.view.clinic.ClinicViewModel
import com.project.vetpet.view.factory

class AddVeterinarianActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddVeterinarianBinding
    private val viewModel: ClinicViewModel by viewModels{ factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVeterinarianBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("clinic")){
            val clinic = intent.getSerializableExtra("clinic") as? Clinics
            if (clinic != null) {
                viewModel.extractDataFromBundle(clinic)
            }
        }

        initComponent()
    }

    private fun initComponent() {
        binding.addUserBtn.setOnClickListener { addEmployee(binding.userNameEditText.text.toString()) }
        createToolbar()
    }

    private fun createToolbar(){
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = viewModel.getClinicName()
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun addEmployee(name:String) {
        startLoadingAnimation()
        viewModel.addEmployee(name){
            stopLoadingAnimation()
            finish()
        }
    }

    private fun startLoadingAnimation(){
        binding.progressBar.visibility = View.VISIBLE
        binding.editBlock.visibility = View.GONE
    }

    private fun stopLoadingAnimation(){
        binding.progressBar.visibility = View.GONE
        binding.editBlock.visibility = View.VISIBLE
    }
}