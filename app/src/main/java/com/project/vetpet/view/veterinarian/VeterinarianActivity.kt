package com.project.vetpet.view.veterinarian

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.vetpet.adapters.MenuItemAdapter
import com.project.vetpet.databinding.ActivityVeterinarianBinding
import com.project.vetpet.model.User
import com.project.vetpet.model.Veterinarian
import com.project.vetpet.utils.ItemClickListener
import com.project.vetpet.view.factory

class VeterinarianActivity : AppCompatActivity(), ItemClickListener {

    private lateinit var binding: ActivityVeterinarianBinding
    private val viewModel: VeterinarianViewModel by viewModels{ factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVeterinarianBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeComponent()
    }

    private fun initializeComponent(){
        initTextView()
        createToolbar()
        initRecyclerView()
    }

    private fun createToolbar(){
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = viewModel.getFullName()
        toolbar.setNavigationOnClickListener {
            finalizeActivity()
        }
    }

    private fun initTextView(){
        viewModel.extractDataFromBundle(intent.getSerializableExtra("veterinarian") as? Veterinarian)

        binding.veterinaryName.text = viewModel.getFullName()
        binding.veterinaryWorkplace.text = viewModel.getWorkplace()
    }

    private fun initRecyclerView(){
        val recView = binding.vetRecyclerView

        recView.layoutManager = LinearLayoutManager(this)
        recView.adapter = MenuItemAdapter(viewModel.getMenuItemsList(), this, this)
    }

    override fun onItemClickListener(title:String) {
        when(title){
            "Записатися на прийом" -> { onMakeAppointmentButtonClicked() }
            "Знайти клініку" -> { onFindClinicButtonClicked() }
            "Відгуки" -> { onReviewButtonClicked() }
        }
    }

    private fun onMakeAppointmentButtonClicked(){
        if (User.currentUser != null){
            val intent = Intent(this, VeterinarianScheduleActivity::class.java)
            intent.putExtra("veterinarian", viewModel.getVeterinarian())
            startActivity(intent)
        }
        else {
            Toast.makeText(this,"Щоб записатися потрібно авторизуватися в додатку", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onFindClinicButtonClicked(){
        Toast.makeText(this, "В стадії розробки. Локація", Toast.LENGTH_SHORT).show()
        /*TODO*/
    }

    private fun onReviewButtonClicked(){
        Toast.makeText(this, "В стадії розробки. Відгуки", Toast.LENGTH_SHORT).show()
        /*TODO*/
    }

    private fun finalizeActivity(){
        //stopLoadingAnimation()
        finish()
    }

}