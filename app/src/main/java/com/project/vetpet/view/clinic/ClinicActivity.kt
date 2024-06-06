package com.project.vetpet.view.clinic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.vetpet.adapters.MenuItemAdapter
import com.project.vetpet.databinding.ActivityClinicBinding
import com.project.vetpet.model.Clinics
import com.project.vetpet.utils.ItemClickListener
import com.project.vetpet.view.MainActivity
import com.project.vetpet.view.TAG
import com.project.vetpet.view.factory
import com.project.vetpet.view.veterinarian.FindVeterinarianActivity

class ClinicActivity : AppCompatActivity(),ItemClickListener {

    private lateinit var binding: ActivityClinicBinding
    private val viewModel: ClinicViewModel by viewModels{ factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClinicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeComponent()
    }



    private fun initializeComponent(){
        initTextView()
        createToolbar()
        initRecyclerView()
    }

    private fun initRecyclerView(){
        val recView = binding.vetRecyclerView

        recView.layoutManager = LinearLayoutManager(this)
        recView.adapter = MenuItemAdapter(viewModel.getMenuItemsList(), this, this)
    }


    private fun initTextView(){
        viewModel.extractDataFromBundle((intent.getSerializableExtra("clinic") as? Clinics)!!)

        binding.clinicName.text = viewModel.getClinicName()
        binding.clinicAddress.text = viewModel.getClinicAddress()
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

    override fun onItemClickListener(title:String) {
        when(title){
            "Розташування клініки" -> { onFindClinicButtonClicked() }
            "Ветеринари" -> { onVeterinarianButtonClicked() }
            "Відгуки" -> { onReviewButtonClicked() }
        }
    }

    private fun onVeterinarianButtonClicked(){
        val intent = Intent(this, FindVeterinarianActivity::class.java)
        intent.putExtra("clinic", viewModel.getClinicName())
        startActivity(intent)
    }

    private fun onFindClinicButtonClicked(){
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("searchQuery", viewModel.getClinicAddress())
            Log.d(TAG, "VeterinarianActivity: ${viewModel.getClinicAddress()}")
        }
        startActivity(intent)
    }

    private fun onReviewButtonClicked(){
        Toast.makeText(this, "В стадії розробки. Відгуки", Toast.LENGTH_SHORT).show()
        /*TODO*/
    }
}