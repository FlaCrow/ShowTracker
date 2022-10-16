package com.flacrow.showtracker.presentation.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.flacrow.showtracker.BuildConfig
import com.flacrow.showtracker.R
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.databinding.FragmentSettingsBinding
import com.flacrow.showtracker.presentation.adapters.*
import com.flacrow.showtracker.presentation.viewModels.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class SettingsFragment :
    BaseFragment<FragmentSettingsBinding, SettingsViewModel>(FragmentSettingsBinding::inflate) {



    //TODO: Add permission check, add file-check to prevent user to import unrelated file
    private val exportDb =
        registerForActivityResult(ActivityResultContracts.CreateDocument("*/*")) { uri ->
            if (uri == null) return@registerForActivityResult
            val outputStream = context?.contentResolver?.openOutputStream(uri)
            val inputStream = context?.getDatabasePath("AppDatabase")?.inputStream()
            if (outputStream != null && inputStream != null) {
                viewModel.exportBackupFile(inputStream, outputStream)
            }
        }

    private val importDb =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult
            val inputStream = context?.contentResolver?.openInputStream(uri)
            val outputStream = context?.getDatabasePath("AppDatabase")?.outputStream()
            if (outputStream != null && inputStream != null) {
                binding.dbImportProgressbar.isVisible = true
                viewModel.importBackupFile(inputStream, outputStream)
                //Restart an app to reopen database that was closed during import process
                //There has to be a better way to do this, right?
                lifecycleScope.launch {
                    viewModel.importDone.collect { done ->
                        if (!done) return@collect
                        val ctx = requireContext().applicationContext
                        val pm = ctx.packageManager
                        val intent = pm.getLaunchIntentForPackage(ctx.packageName)
                        val mainIntent = Intent.makeRestartActivityTask(intent?.component)
                        ctx.startActivity(mainIntent)
                        Runtime.getRuntime().exit(0)
                    }
                }
            }
        }
    override val viewModel: SettingsViewModel by viewModels {
        viewModelFactory
    }

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }

    private var settingsAdapter =
        SettingsAdapter(
            onActionItemClicked = { actionType -> onActionItemClicked(actionType) },
            onItemSwitched = { switchableType, state -> onItemSwitch(switchableType, state) })
    private lateinit var sharedPrefs: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingsAdapter.submitList(populateSettingsList())
        populateHeaderString()
        binding.settingsRecycler.run {
            adapter = settingsAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun populateHeaderString() {
        binding.settingsHeaderTv.text =
            getString(R.string.settings_info, BuildConfig.BUILD_TYPE, BuildConfig.VERSION_NAME)
    }

    private fun populateSettingsList(): List<SettingsPageItem> {
        sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        return listOf(
            SettingsPageItem(
                SettingsItem.SwitchableItem(
                    SwitchableTypes.UPDATE_ON_INTERACTION,
                    sharedPrefs.getBoolean(SwitchableTypes.UPDATE_ON_INTERACTION.name, true)
                ),
                getString(R.string.settings_update_on_interact)
            ),
            SettingsPageItem(
                SettingsItem.ActionItem(ActionTypes.DATABASE_IMPORT),
                getString(R.string.settings_database_import)
            ),
            SettingsPageItem(
                SettingsItem.ActionItem(ActionTypes.DATABASE_EXPORT),
                getString(R.string.settings_database_export)
            ),
            SettingsPageItem(
                SettingsItem.ActionItem(ActionTypes.DATABASE_CLEAR),
                getString(R.string.settings_database_clear)
            )
        )
    }


    private fun onActionItemClicked(actionType: ActionTypes) {
        when (actionType) {
            ActionTypes.DATABASE_EXPORT -> {
                exportDb.launch(

                    "ShowTracker_backup_" + SimpleDateFormat(
                        "ddmmyyyy-HHmmss",
                        Locale.getDefault()
                    ).format(
                        Calendar.getInstance().time
                    ) + ".STData"
                )
            }
            ActionTypes.DATABASE_IMPORT -> {
                importDb.launch("*/*")
            }
            ActionTypes.DATABASE_CLEAR -> {
                Toast.makeText(
                    context,
                    getString(R.string.settings_database_clear),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.nukeDatabase()
            }
        }
    }

    private fun onItemSwitch(switchableType: SwitchableTypes, state: Boolean) {
        when (switchableType) {
            SwitchableTypes.UPDATE_ON_INTERACTION -> {
                sharedPrefs.edit().putBoolean(switchableType.name, state).apply()
            }
        }
    }


}