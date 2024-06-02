package com.project.vetpet.view.veterinarian

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.vetpet.adapters.MoreButtonClickListener
import com.project.vetpet.databinding.ActivityFindVeterinarianBinding
import com.project.vetpet.model.Veterinarian
import com.project.vetpet.view.factory

class FindVeterinarianActivity : AppCompatActivity(),MoreButtonClickListener {

    private lateinit var binding: ActivityFindVeterinarianBinding
    private val viewModel: FindVeterinarianViewModel by viewModels{ factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindVeterinarianBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeComponent()
    }


    private fun initializeComponent(){
        createToolbar()
        startLoadingAnimation()
        initAdapter()
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

    private fun initAdapter(){
        val veterinarianList: MutableList<Veterinarian> = intent.getSerializableExtra("veterinaries") as MutableList<Veterinarian>

        val recView = binding.vetRecyclerView
        recView.layoutManager = LinearLayoutManager(this)
        recView.adapter = viewModel.createVetAdapter(this,veterinarianList,this)
        stopLoadingAnimation()

        if ((recView.adapter?.itemCount ?: 0) <= 0) {
            binding.errorText.visibility = View.VISIBLE
            binding.vetRecyclerView.visibility = View.INVISIBLE
        }

    }

    override fun onMoreButtonClick(veterinarian:Veterinarian) {
        val intent = Intent(this, VeterinarianActivity::class.java)
        intent.putExtra("veterinarian", veterinarian)
        startActivity(intent)
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
            vetRecyclerView.visibility = View.INVISIBLE
        }
    }
    private fun stopLoadingAnimation() {
        with(binding){
            progressBar.visibility = View.GONE
            vetRecyclerView.visibility = View.VISIBLE
        }
    }

}