package com.flacrow.showtracker.presentation.adapters


sealed class SettingsItem {
    data class SwitchableItem(val switchable: SwitchableTypes, val state: Boolean) :
        SettingsItem()

    data class ActionItem(val action: ActionTypes) : SettingsItem()
}

enum class ActionTypes {
    DATABASE_CLEAR, DATABASE_EXPORT, DATABASE_IMPORT
}

enum class SwitchableTypes {
    UPDATE_ON_INTERACTION
}

data class SettingsPageItem(val type: SettingsItem, val title: String)
