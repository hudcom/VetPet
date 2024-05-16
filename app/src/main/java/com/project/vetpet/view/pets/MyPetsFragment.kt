package com.project.vetpet.view.pets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.vetpet.R
import com.project.vetpet.adapters.EditButtonClickListener
import com.project.vetpet.databinding.FragmentMyPetsBinding
import com.project.vetpet.view.BaseFragment
import com.project.vetpet.view.TAG
import com.project.vetpet.view.factory

class MyPetsFragment : BaseFragment(), EditButtonClickListener {

    private lateinit var binding: FragmentMyPetsBinding
    private val viewModel: MyPetsViewModel by viewModels{ factory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPetsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners(view)
    }

    override fun onResume() {
        super.onResume()
        getPetsListFromDB()
    }

    private fun getPetsListFromDB(){
        startLoadingAnimation()
        viewModel.getPetsList(){
            initPetRecyclerView()
        }
    }

    override fun setOnBackPressedKeyListener(view: View) {
        view.isFocusableInTouchMode = true
        view.requestFocus()

        view.setOnKeyListener { _, keyCode, event ->
            if (checkKeyListeners(event,keyCode)) {
                findNavController().popBackStack()
                true
            } else {
                Log.e(TAG,"Error. Back button work incorrectly")
                false
            }
        }
    }

    private fun checkKeyListeners(event: KeyEvent, keyCode: Int):Boolean =
        event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK

    private fun setOnClickListeners(view: View){
        setOnBackPressedKeyListener(view)
        binding.addBtn.setOnClickListener {
            findNavController().navigate(R.id.addPetFragment)
            hideTextAndButtonAdd()
        }
        binding.add2Btn.setOnClickListener { findNavController().navigate(R.id.addPetFragment) }
    }

    private fun initPetRecyclerView() {
        val recView = binding.petRecyclerView
        recView.layoutManager = LinearLayoutManager(requireContext())
        recView.adapter = viewModel.getPetAdapter(this,viewModel, requireContext())
        stopLoadingAnimation()

        if ((recView.adapter?.itemCount ?: 0) > 0){
            binding.scrollView.visibility = View.VISIBLE
            binding.add2Btn.visibility = View.VISIBLE
        } else {
            showTextAndButtonAdd()
        }
    }

    /*
    * Animation Methods
    * */
    private fun startLoadingAnimation() {
        binding.scrollView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun stopLoadingAnimation() {
        binding.scrollView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun showTextAndButtonAdd(){
        binding.errorText.visibility = View.VISIBLE
        binding.addBtn.visibility = View.VISIBLE
    }

    private fun hideTextAndButtonAdd(){
        binding.errorText.visibility = View.GONE
        binding.addBtn.visibility = View.GONE
    }


    override fun onEditButtonClick(position: Int, ) {
        Toast.makeText(requireContext(),"EDIT BUTTON PRESSED",Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), EditPetActivity::class.java)
        intent.putExtra("pet", viewModel.getSelectedItem())
        startActivity(intent)
    }

}
