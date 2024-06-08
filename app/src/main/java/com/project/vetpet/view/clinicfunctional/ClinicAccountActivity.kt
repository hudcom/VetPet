package com.project.vetpet.view.clinicfunctional

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.vetpet.adapters.MoreButtonClickListener
import com.project.vetpet.adapters.VeterinarianAdapter
import com.project.vetpet.databinding.ActivityClinicAccountBinding
import com.project.vetpet.model.Clinics
import com.project.vetpet.model.Veterinarian
import com.project.vetpet.view.clinic.ClinicViewModel
import com.project.vetpet.view.factory
import com.project.vetpet.view.schedule.VeterinarianScheduleActivity

class ClinicAccountActivity : AppCompatActivity(), MoreButtonClickListener {

    private lateinit var binding: ActivityClinicAccountBinding
    private val viewModel: ClinicViewModel by viewModels{ factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClinicAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("clinic")){
            val clinic = intent.getSerializableExtra("clinic") as? Clinics
            if (clinic != null) {
                viewModel.extractDataFromBundle(clinic)
                Clinics.currentClinic = clinic
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initComponent()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.deleteClinicObject()
    }

    private fun initComponent() {
        startLoadingEmployeeList()
        createToolbar()
        binding.clinicName.text = viewModel.getClinicName()
        binding.clinicAddress.text = viewModel.getClinicAddress()
        binding.clinicEmail.text = viewModel.getClinicEmail()
        initRecyclerView()
        ViewCompat.setTooltipText(binding.addEmployeeBtn,"Додати працівника")
        binding.addEmployeeBtn.setOnClickListener { addVeterinarian() }
    }

    private fun addVeterinarian() {
        val intent = Intent(this, AddVeterinarianActivity::class.java)
        intent.putExtra("clinic", viewModel.getClinic())
        startActivity(intent)
    }

    private fun initRecyclerView(){
        val recView = binding.listVeterinarian
        recView.layoutManager = LinearLayoutManager(this)
        viewModel.getClinicListVeterinarian(){
            recView.adapter = VeterinarianAdapter(this, it.toMutableList() ,this)
            stopLoadingEmployeeList()
        }
    }


    private fun createToolbar(){
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = viewModel.getClinicName()
        toolbar.setNavigationOnClickListener {
            finalizeActivity()
        }
    }

    private fun finalizeActivity(){
        finish()
    }

    override fun onMoreButtonClick(veterinarian: Veterinarian) {
        val intent = Intent(this, VeterinarianScheduleActivity::class.java)
        intent.putExtra("veterinarian", veterinarian)
        startActivity(intent)
    }

    private fun startLoadingEmployeeList(){
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun stopLoadingEmployeeList(){
        binding.progressBar.visibility = View.GONE
    }
}