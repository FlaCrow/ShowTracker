package com.flacrow.showtracker.presentation.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.flacrow.showtracker.BuildConfig
import com.flacrow.showtracker.R
import com.flacrow.showtracker.databinding.FragmentSettingsBinding
import com.flacrow.showtracker.presentation.adapters.*

class SettingsFragment :
    Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    protected val binding: FragmentSettingsBinding
        get() = _binding!!
    private var settingsAdapter =
        SettingsAdapter(
            onActionItemClicked = { actionType -> onActionItemClicked(actionType) },
            onItemSwitched = { switchableType, state -> onItemSwitch(switchableType, state) })
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater)
        _binding ?: throw IllegalArgumentException("Binding is null")
        return binding.root
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun onActionItemClicked(actionType: ActionTypes) {
        when (actionType) {
            ActionTypes.DATABASE_EXPORT -> {
                Toast.makeText(
                    context,
                    getString(R.string.settings_database_export),
                    Toast.LENGTH_SHORT
                ).show()
            }
            ActionTypes.DATABASE_IMPORT -> {
                Toast.makeText(
                    context,
                    getString(R.string.settings_database_import),
                    Toast.LENGTH_SHORT
                ).show()
            }
            ActionTypes.DATABASE_CLEAR -> {
                Toast.makeText(
                    context,
                    getString(R.string.settings_database_clear),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun onItemSwitch(switchableType: SwitchableTypes, state: Boolean) {
        when (switchableType) {
            SwitchableTypes.UPDATE_ON_INTERACTION -> {
                Toast.makeText(
                    context,
                    "new state UPDATE_ON_INTERACTION: ".plus(state),
                    Toast.LENGTH_SHORT
                ).show()
                sharedPrefs.edit().putBoolean(switchableType.name, state).apply()
            }
        }
    }
}