package com.project.vetpet.view.pets

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SpinnerAdapter
import androidx.activity.viewModels
import androidx.navigation.fragment.findNavController
import com.project.vetpet.R
import com.project.vetpet.databinding.ActivityEditPetBinding
import com.project.vetpet.view.MainActivity
import com.project.vetpet.view.TAG
import com.project.vetpet.view.factory

class EditPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPetBinding
    private val viewModel: EditPetViewModel by viewModels{ factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeComponent()
        setListeners()
    }


    /*
    *
    * Initialize component methods
    *
     */
    private fun initializeComponent() {
        createToolbar()
        initTypeSpinner()
        initAgeSpinner()
        getBundle()
    }

    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setListeners() {
        binding.editPetBtn.setOnClickListener {
            editBtnClicked()
        }
        binding.deletePetBtn.setOnClickListener {
            deleteBtnClicked()
        }
    }

    private fun createToolbar(){
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finalizeActivity()
        }
    }

    private fun initTypeSpinner(){
        val spinner = binding.petType
        spinner.adapter = viewModel.createTypeSpinner(this)
    }

    private fun initAgeSpinner(){
        val spinner = binding.petAge
        spinner.adapter = viewModel.createAgeSpinner(this)
    }

    private fun getBundle() {
        val bundle = intent.getBundleExtra("pet")
        if (bundle != null) {
            viewModel.convertBundle(bundle)

            binding.petNameEditText.text = viewModel.getName()?.let { Editable.Factory.getInstance().newEditable(it) }
            binding.petBreedEditText.text = viewModel.getBreed()?.let { Editable.Factory.getInstance().newEditable(it) }

            binding.petAge.setSelection(getPosition(binding.petAge.adapter, viewModel.getAge().toString()))
            binding.petType.setSelection(getPosition(binding.petType.adapter, viewModel.getType()))

        }
    }

    private fun editBtnClicked(){
        startLoadingAnimation()
        if (viewModel.editElement(
            binding.petNameEditText.text.toString(),
            binding.petAge.selectedItem.toString(),
            binding.petType.selectedItem.toString(),
            binding.petBreedEditText.text.toString()
        ){ finalizeActivity() })
            else finalizeActivity()
    }

    private fun deleteBtnClicked(){
        startLoadingAnimation()
        viewModel.deleteElement(
            binding.petNameEditText.text.toString()
        ){
            finalizeActivity()
        }
    }

    private fun getPosition(adapter: SpinnerAdapter, value: String?): Int {
        for (i in 0 until adapter.count) {
            val currentItem = adapter.getItem(i) as String

            if (currentItem == value) {
                return i
            }
        }
        return -1
    }

    private fun finalizeActivity(){
        hideKeyboard(binding.editBlock)
        stopLoadingAnimation()
        finish()
    }

    /*
    * Animation Methods
    */
    private fun startLoadingAnimation() {
        binding.progressBar.bringToFront()
        binding.editBlock.alpha = 0.6f
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun stopLoadingAnimation() {
        binding.editBlock.alpha = 1f
        binding.progressBar.visibility = View.GONE
    }
}