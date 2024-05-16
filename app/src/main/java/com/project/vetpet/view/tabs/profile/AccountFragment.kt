package com.project.vetpet.view.tabs.profile

import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.vetpet.R
import com.project.vetpet.view.MainActivity
import com.project.vetpet.adapters.MenuItemsAdapter
import com.project.vetpet.databinding.FragmentAccountBinding
import com.project.vetpet.view.BaseFragment
import com.project.vetpet.view.dialog.DialogListener
import com.project.vetpet.view.dialog.CloseDialog
import com.project.vetpet.view.factory
import com.project.vetpet.view.tabs.profile.AccountViewModel

class AccountFragment : BaseFragment(), DialogListener, MenuItemsAdapter.AccountFragmentCallback {
    private lateinit var binding: FragmentAccountBinding
    private val viewModel: AccountViewModel by viewModels{ factory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTextView()
        setOnClickListener()
        setOnBackPressedKeyListener(view)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).initTopBar()
        initRecyclerView()
    }

    override fun onMyPetsSelected() {
        Navigation.findNavController(requireView()).navigate(R.id.myPetsFragment)
    }

    private fun initRecyclerView(){
        val recView = binding.accountRecyclerView

        recView.layoutManager = LinearLayoutManager(requireContext())
        recView.adapter = MenuItemsAdapter(viewModel.getMenuItemsList(), this, requireContext())
    }

    private fun initTextView(){
        val userName = viewModel.getUserFullName()
        binding.accountUserFullName.text = userName
        if (userName == "Ім'я користувача"){
            binding.accountUserFullName.setTextColor(Color.RED)
        }

        binding.accountUserLogin.text = viewModel.getUserEmail()
    }

    private fun setOnClickListener(){
        binding.accountEditBtn.setOnClickListener{ openEditPanel() }
        binding.accountCloseBtn.setOnClickListener { showCloseAccDialog() }
    }

    private fun openEditPanel(){
        Toast.makeText(requireContext(), "Відкрито панель редагування профілю", Toast.LENGTH_SHORT).show()
        // Todo
    }

    private fun showCloseAccDialog() {
        CloseDialog(this).createDialog(requireActivity())
    }

    override fun onPositiveClick() {
        viewModel.deleteUser()
        if (findNavController().popBackStack(R.id.authFragment,false)) return
        else {
            findNavController().popBackStack(R.id.mainFragment,false)
            findNavController().navigate(R.id.authFragment)
            (activity as MainActivity).initTopBar()
        }

    }

    override fun onNegativeClick() {
        Log.d("Project log", "Choose 'Ні'")
    }

}