package com.project.vetpet.view.clinic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.vetpet.adapters.MoreClinicButtonClickListener
import com.project.vetpet.databinding.ActivityFindClinicsBinding
import com.project.vetpet.model.Clinics
import com.project.vetpet.view.factory

class FindClinicsActivity : AppCompatActivity(),MoreClinicButtonClickListener {

    private lateinit var binding: ActivityFindClinicsBinding
    private val viewModel: FindClinicsViewModel by viewModels{ factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindClinicsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeComponent()
    }

    private fun initializeComponent(){
        createToolbar()
        startLoadingAnimation()
        initAdapter()
    }

    private fun initAdapter(){
        val clinicsList: MutableList<Clinics> = intent.getSerializableExtra("clinics") as MutableList<Clinics>

        val recView = binding.clinicsRecyclerView
        recView.layoutManager = LinearLayoutManager(this)
        recView.adapter = viewModel.createClinicsAdapter(this,clinicsList,this)
        stopLoadingAnimation()

        if ((recView.adapter?.itemCount ?: 0) <= 0) {
            binding.errorText.visibility = View.VISIBLE
            binding.clinicsRecyclerView.visibility = View.INVISIBLE
        }
    }

    private fun createToolbar(){
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Результати пошуку"
        toolbar.setNavigationOnClickListener {
            finalizeActivity()
        }
    }


    private fun finalizeActivity(){
        stopLoadingAnimation()
        finish()
    }


    /*
* Animation Methods
* */
    private fun startLoadingAnimation() {
        with(binding){
            progressBar.visibility = View.VISIBLE
            clinicsRecyclerView.visibility = View.INVISIBLE
        }
    }
    private fun stopLoadingAnimation() {
        with(binding){
            progressBar.visibility = View.GONE
            clinicsRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onMoreButtonClick(clinic: Clinics) {
        val intent = Intent(this, ClinicActivity::class.java)
        intent.putExtra("clinic", clinic)
        startActivity(intent)
    }

}