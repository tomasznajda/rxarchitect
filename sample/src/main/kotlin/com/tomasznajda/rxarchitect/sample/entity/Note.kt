package com.tomasznajda.rxarchitect.sample.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(val id: Long,
                val name: String,
                val content: String,
                val pinned: Boolean,
                val updateInMillis: Long) : Parcelable