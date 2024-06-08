package com.project.vetpet

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.project.vetpet.databinding.FragmentMainBinding
import com.project.vetpet.databinding.FragmentMoreBinding
import com.project.vetpet.view.BaseFragment
import com.project.vetpet.view.clinic.ClinicActivity
import com.project.vetpet.view.factory
import com.project.vetpet.view.tabs.home.MainFragmentViewModel

class MoreFragment : BaseFragment() {

    private lateinit var binding: FragmentMoreBinding
    private val viewModel: MoreFragmentViewModel by viewModels{ factory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnBackPressedKeyListener(view)
        initComponent()
    }

    private fun initComponent(){
        binding.linkToAuth.setOnClickListener { linkToAuthClinicFragment() }
    }

    private fun linkToAuthClinicFragment() {
        val intent = Intent(requireActivity(), AuthClinicActivity::class.java)
        startActivity(intent)
    }


}