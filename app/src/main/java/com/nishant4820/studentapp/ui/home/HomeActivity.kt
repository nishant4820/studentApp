package com.nishant4820.studentapp.ui.home

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nishant4820.studentapp.R
import com.nishant4820.studentapp.databinding.ActivityHomeBinding
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import com.nishant4820.studentapp.utils.NetworkResult
import com.nishant4820.studentapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        navController = findNavController(R.id.fragmentContainerView)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.noticesFragment,
                R.id.resultsFragment,
                R.id.attendanceFragment,
                R.id.profileFragment
            )
        )
        binding.bottomNavView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

        getSettings()

    }

    private fun getSettings() {
        Log.d(LOG_TAG, "Home Activity: getSettings()")
        mainViewModel.getSettings()
        mainViewModel.settingsResponse.observe(this) { response ->
            Log.d(
                LOG_TAG,
                "Home Activity: Settings response observer, response code: ${response.statusCode}"
            )
            when (response) {
                is NetworkResult.Success -> {}

                is NetworkResult.Error -> {}

                is NetworkResult.Loading -> {}
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}