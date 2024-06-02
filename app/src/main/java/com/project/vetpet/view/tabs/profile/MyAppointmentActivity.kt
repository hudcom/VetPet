package com.project.vetpet.view.tabs.profile

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.vetpet.R
import com.project.vetpet.adapters.MyAppointmentAdapter
import com.project.vetpet.databinding.ActivityMyAppointmentBinding
import com.project.vetpet.model.MyAppointment
import com.project.vetpet.view.factory

class MyAppointmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyAppointmentBinding
    private val viewModel: MyAppointmentViewModel by viewModels{ factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeComponent()
    }

    private fun initializeComponent(){
        createToolbar()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val appointmentsRecyclerView: RecyclerView = findViewById(R.id.appointmentsRecyclerView)
        appointmentsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Отримуємо список з Intent
        val appointments = intent.getParcelableArrayListExtra<MyAppointment>("list")

        if (appointments != null) {
            val adapter = MyAppointmentAdapter(appointments)
            appointmentsRecyclerView.adapter = adapter
        }
    }

    private fun createToolbar(){
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        toolbar.setNavigationOnClickListener {
            finalizeActivity()
        }
    }

    private fun finalizeActivity(){
        //stopLoadingAnimation()
        finish()
    }
}