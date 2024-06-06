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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.vetpet.R
import com.project.vetpet.adapters.AppointmentAdapter
import com.project.vetpet.view.MainActivity
import com.project.vetpet.adapters.MenuItemAdapter
import com.project.vetpet.databinding.FragmentAccountBinding
import com.project.vetpet.model.MyAppointment
import com.project.vetpet.model.User
import com.project.vetpet.utils.ItemClickListener
import com.project.vetpet.view.BaseFragment
import com.project.vetpet.view.TAG
import com.project.vetpet.view.dialog.DialogListener
import com.project.vetpet.view.dialog.CustomDialog
import com.project.vetpet.view.factory
import com.project.vetpet.view.tabs.map.MapsFragment

class AccountFragment : BaseFragment(), DialogListener, ItemClickListener {
    private lateinit var binding: FragmentAccountBinding
    private val viewModel: AccountViewModel by viewModels{ factory() }
    private lateinit var adapter: AppointmentAdapter


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

    override fun onItemClickListener(title:String) {
        when(title){
            "Мої улюбленці" -> {Navigation.findNavController(requireView()).navigate(R.id.myPetsFragment)}
            "Прийоми" -> { getAllUserAppointment() }
            "Благодійність" -> { /*TODO*/ }
            "Клініки поруч" -> { findClinicsNearby() }
        }
    }

    private fun findClinicsNearby() {
        viewModel.findClinicNearby { clinicAddresses ->
            val bundle = Bundle().apply {
                /*putStringArrayList("clinicAddresses", ArrayList(clinicAddresses))*/
                Log.d(TAG,"ACCOUNT_FRAGMENT: $clinicAddresses")
                MapsFragment.clinicAddresses = clinicAddresses
            }
            findNavController().navigate(R.id.mapsFragment, bundle)
            (activity as MainActivity).updateBottomNavigationMenu(R.id.mapsFragment)
        }
    }

    private fun getAllUserAppointment(){
        viewModel.getAllUsersAppointment() {
            val appointments: List<MyAppointment> = it
            val intent = Intent(requireContext(), MyAppointmentActivity::class.java)
            intent.putParcelableArrayListExtra("list", ArrayList(appointments))
            startActivity(intent)
        }
    }

    private fun initRecyclerView(){
        val recView = binding.accountRecyclerView

        recView.layoutManager = LinearLayoutManager(requireContext())
        recView.adapter = MenuItemAdapter(viewModel.getMenuItemsList(), this, requireContext())
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
        Log.d(TAG, "Choose 'Ні'")
    }

}