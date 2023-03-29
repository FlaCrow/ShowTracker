package com.flacrow.showtracker.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.flacrow.core.utils.ConstantValues.SERIES_ID_EXTRA
import com.flacrow.showtracker.NavGraphDirections
import com.flacrow.showtracker.R
import com.flacrow.showtracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        setupBottomNav()
        if (intent.action == Intent.ACTION_VIEW) {
            val seriesIdFromNotification = intent.getIntExtra(SERIES_ID_EXTRA, 0)
            binding.navHostFragmentContainer.findNavController().navigate(
                NavGraphDirections.actionGlobalSeriesDetailsFragment(seriesIdFromNotification)
            )
        }
    }

    private fun setupBottomNav() {
        val navController = binding.navHostFragmentContainer.findNavController()
        binding.bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.movieDetailsFragment -> hideBottomNav()
                R.id.seriesDetailsFragment -> hideBottomNav()
                R.id.settingsFragment -> hideBottomNav()
                else -> {
                    showBottomNav()
                    currentNavigationFragment?.apply {
                        exitTransition = null
                        reenterTransition = null
                    }
                }
            }
        }
    }

    private fun showBottomNav() {
        binding.bottomNav.isVisible = true
    }

    private fun hideBottomNav() {
        binding.bottomNav.isVisible = false
    }
}
