package com.flacrow.showtracker.utils

import android.widget.RadioGroup

object Extensions {
    fun RadioGroup.setChildrenEnabled(enabled: Boolean) {
        for (i in 0 until childCount) {
            getChildAt(i).isEnabled = enabled
        }
    }
}