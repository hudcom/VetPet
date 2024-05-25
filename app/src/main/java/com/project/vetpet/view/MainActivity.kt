package com.project.vetpet.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.vetpet.R
import com.project.vetpet.databinding.ActivityMainBinding

const val TAG = "Project log"


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var navController: NavController


    private val viewModel: MainActivityViewModel by viewModels{ factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.initFirebase()
    }

    override fun onStart() {
        super.onStart()
        initializeComponent()
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveSharedPreferences()
    }

    fun updateBottomNavigationMenu(selectedItemId: Int) {
        bottomNavView.selectedItemId = selectedItemId
    }


    /*
    * Initialize methods
    * created by Denis
    */
    private fun initializeComponent(){
        viewModel.initSharedPreferences(this)
        viewModel.initUser()

        initTopBar()
        initBottomNavigation()
    }

    fun initTopBar(){
        binding.topBar.location.text = viewModel.getTopBarLocation()
    }

    /*
    *
    * Bottom navigation methods
    * created by Denis
    *
    */
    private fun initBottomNavigation(){
        bottomNavView = binding.bottomBar
        navController = findNavController(R.id.main_fragment)

        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.mainFragment ->  {
                    navigateTo(R.id.mainFragment)
                    true
                }

                R.id.notificationFragment -> {
                    navigateTo(R.id.notificationFragment)
                    true
                }

                R.id.authFragment, R.id.accountFragment, R.id.registerFragment -> {
                    if (viewModel.hasCurrentUser())  navController.navigate(R.id.accountFragment)
                    else  navController.navigate(R.id.authFragment)
                    true
                }

                R.id.mapsFragment -> {
                    navigateTo(R.id.mapsFragment)
                    true
                }

                R.id.moreFragment -> {
                    navigateTo(R.id.moreFragment)
                    true
                }

                else -> false
            }
        }
    }
     private fun navigateTo(fragmentId: Int): Boolean {
        navController.navigate(fragmentId)
        return true
    }
}









