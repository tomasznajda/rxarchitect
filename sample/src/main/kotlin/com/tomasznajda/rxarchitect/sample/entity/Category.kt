package com.tomasznajda.rxarchitect.sample.entity

import android.support.annotation.StringRes
import com.tomasznajda.rxarchitect.sample.R

enum class Category(@StringRes val nameResId: Int) {
    PINNED(R.string.notes_category_pinned),
    OTHERS(R.string.notes_category_others)
}