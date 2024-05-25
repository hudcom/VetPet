package com.project.vetpet.view.tabs.profile


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.vetpet.R
import com.project.vetpet.view.MainActivity
import com.project.vetpet.adapters.MenuItemsAdapter
import com.project.vetpet.databinding.FragmentAccountBinding
import com.project.vetpet.model.User
import com.project.vetpet.view.BaseFragment
import com.project.vetpet.view.dialog.DialogListener
import com.project.vetpet.view.dialog.CustomDialog
import com.project.vetpet.view.factory

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
        setOnBackPressedKeyListener(view)
    }

    override fun onResume() {
        super.onResume()

        if (User.currentUser == null){
            (activity as MainActivity).updateBottomNavigationMenu(R.id.mainFragment)
            findNavController().popBackStack(R.id.mainFragment,false)
        }

        initTextView()
        setOnClickListener()
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
        } else
            binding.accountUserFullName.setTextColor(Color.BLACK)

        binding.accountUserLogin.text = viewModel.getUserEmail()
    }

    private fun setOnClickListener(){
        binding.accountEditBtn.setOnClickListener{ openEditPanel() }
        binding.accountCloseBtn.setOnClickListener { showCloseAccDialog() }
    }

    private fun openEditPanel(){
        val intent = Intent(requireContext(), EditAccountActivity::class.java)
        intent.putExtra("user", viewModel.getUser())
        startActivity(intent)
    }

    private fun showCloseAccDialog() {
        CustomDialog(this).createDialog(requireActivity(),"Підтвердження","Ви дійсно хочете вийти з аккаунту?")
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