package com.flacrow.showtracker.utils

import android.widget.ImageView
import android.widget.RadioGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flacrow.showtracker.R
import com.flacrow.showtracker.data.models.TvDetailed


object Extensions {
    fun RadioGroup.setChildrenEnabled(enabled: Boolean) {
        for (i in 0 until childCount) {
            getChildAt(i).isEnabled = enabled
        }
    }

    fun ImageView.setImageWithGlide(url: String) {
        Glide
            .with(this.context)
            .load(url)
            .placeholder(R.drawable.ic_placeholder_image_50)
            .error(R.drawable.ic_placeholder_image_50)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }

    inline fun <T> List<T>.allReverseIteration(predicate: (T) -> Boolean): Boolean {
        for (i in this.size - 1 downTo 0) if (!predicate(this[i])) return false
        return true
    }

    fun TvDetailed.isMutableFieldEqual(tvDetailed: TvDetailed): Boolean {

        if (this.seasons != tvDetailed.seasons) return false
        if (this.rating != tvDetailed.rating) return false
        if (this.status != tvDetailed.status) return false
        return true
    }

}